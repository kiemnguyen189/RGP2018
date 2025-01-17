package RGPsem2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.navigation.Pose;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.utility.Delay;

public class Main {
	
	static RegulatedMotor motorA = new EV3LargeRegulatedMotor(MotorPort.A);
	static RegulatedMotor motorB = new EV3LargeRegulatedMotor(MotorPort.B);

	static EV3TouchSensor touch1 = new EV3TouchSensor(SensorPort.S1);
	static SensorMode t1 = touch1.getTouchMode();
	static float[] tSample1 = new float[t1.sampleSize()];
	
	static EV3TouchSensor touch2 = new EV3TouchSensor(SensorPort.S2);
	static SensorMode t2 = touch2.getTouchMode();
	static float[] tSample2 = new float[t2.sampleSize()];

	static EV3ColorSensor cSensor = new EV3ColorSensor(SensorPort.S4);
	static SampleProvider cProvider = cSensor.getRedMode();
	static float[] cSample = new float[1];

	static Wheel wLeft = WheeledChassis.modelWheel(motorA, 3).offset(4.155);
	static Wheel wRight = WheeledChassis.modelWheel(motorB, 3).offset(-4.155);

	static Chassis chassis = new WheeledChassis(new Wheel[]{wLeft, wRight}, WheeledChassis.TYPE_DIFFERENTIAL);
	static MovePilot pilot = new MovePilot(chassis);

	static boolean isRed = false;
	static boolean locFin = false;
	
	static boolean pathFin = false;
	
	static double[] locationProbability = new double[37];
	
	static private double rotate = 0;
	
	static int lVal = 0;
	
	//REPULSION STUFF
	static Point leftObs1 = new Point((float)20.5, (float)101.5);
	static Point leftObs2 = new Point(35, 87);
	
	static Point rightObs1 = new Point(88, 34);
	static Point rightObs2 = new Point(103, 19);
	
	static Point leftBarrier = new Point(47, 75);
	static Point middleBarrier = new Point(61, 61);
	static Point rightBarrier = new Point(75, 47);
	
	//ATTRACTION STUFF
	static Point leftGoal1 = new Point((float)33.75, (float)88.25);
	static Point leftGoal2 = new Point((float)27.75, (float)94.25);	
	static Point garageGoal = new Point((float)10, (float)47);
	static Point rightGoal1 = new Point((float)81.5, (float)40.5);
	static Point rightGoal2 = new Point((float)95.5, (float)26.5);
	static Point endGoal = new Point((float)112.5, (float)101.5);
	
	
	//POTENTIAL FIELD CONSTANTS
	static final int kRep = 1;
	static final int kAtt = 1;
	static final int qRad = 1;
	
	static final int obsPos = 1;
	

	public static void main(String[] args) throws IOException {

		Arrays.fill(locationProbability, 1/37d);
		
		while(!locFin) {

			lVal = localise(); 	//returns the block the robot localises at (should be 23)
			
			double locCoords = 102.5 - (lVal * 1.74); // x and y coordinate
			
			Pose robPos = new Pose((float)locCoords, (float)locCoords, 225);
			
			
			
			double maxProb = maxProbability();
//			System.out.println("MAX MAX = " + maxProb);
//			String location = new String().indexOf(maxProb);
		
			int location = findIndex(maxProb);
//			System.out.println("WE ARE AT INDEX " + location);
			
			Delay.msDelay(2000);
			
			pilot.setAngularSpeed(45);
			pilot.setLinearSpeed(10);
			set_angle(90);
			//pilot.rotate(90);
			pilot.travel(20);
			
			set_angle(-90);
			//pilot.rotate(-90);
			pilot.travel(34);
			
			beep();
			/*Sound.beep();
			Delay.msDelay(2000);*/
			
			locFin = true;
			
//			List<String> lines = Arrays.asList();
//			Path file = Paths.get("arrayvals.txt");
//			Files.write(file, lines, Charset.forName("UTF-8"));
			
//			pilot.setLinearSpeed(15);
//			pilot.travel(43);
////			Delay.msDelay(1000);
//
//			pilot.setAngularSpeed(45);
//			pilot.rotate(90);
//			pilot.travel(13);
////			Delay.msDelay(1000);
//
//			pilot.arc(-15, 120);
//			pilot.arc(0, 45);
//
//			pilot.travel(23);
//			pilot.rotate(-64);
//			pilot.setLinearSpeed(15);
//			pilot.travel(31);
//
//			t1.fetchSample(tSample1, 0);
//			t2.fetchSample(tSample2, 0);
//			cProvider.fetchSample(cSample, 0);
//
//			
//			System.out.print("Test0");
//			System.out.println("");
//			System.out.println("");
//			System.out.println("");
//			System.out.println("");
//
//			System.out.println(tSample1[0] != 0);
//			System.out.println(tSample2[0] != 0);
//
//			if ((tSample1[0] != 0) || (tSample2[0] != 0)) {
//
////				System.out.print("Test1");
//
//				if ((cSample[0]) > 0.5) {
////					System.out.print("Test2");
//					isRed = true; 
//				}
//
//				touchEnd(isRed);				
//
//			} else {
//
//				Sound.buzz();
//				Delay.msDelay(500);
//				Sound.buzz();
//
//			}
//
//			Delay.msDelay(5000);
////			break;
//
		}
		
		System.out.print("Localise Finished.");
		
		

	}
	
	private static void beep()
	{
		Sound.beep();
		
		Delay.msDelay(2000);
	}
	
	private static void set_angle(int angle)
	{
		rotate = angle;
		pilot.rotate(rotate);
	}
	
	
	private static void setpath()
	{
		
	}
	

	private static void touchEnd(boolean color) {

		// true = red
		// false = green

		System.out.print("Test3");

		if (color == true) {
			
			// BETWEEN CYLINDER AND BARRIER
			
			Sound.beep();
			Delay.msDelay(100);
			
			pilot.travel(-45);
			pilot.rotate(-48); // MAKE 48
			pilot.travel(59.4);
			pilot.setAngularSpeed(40);
			pilot.arc(-15, 135);
			
		} else {
			
			// BETWEEN CYLINDERS
			
			Sound.twoBeeps();
			Delay.msDelay(100);
			
			pilot.travel(-45);
			pilot.rotate(-48); // MAKE 48
			pilot.travel(79.6);
			pilot.setAngularSpeed(40);
			pilot.arc(-15, 135);
			
		}
		
	}
	
	
	// HOW TO DO LOCALISATION
	// All probabilities start equal, 1/(number of grids) (let each grid be a 1.7cm x 1.7cm square)
	// Robot is placed on line
	// Robot takes sample from color sensor
	// Robot moves, and depending on readings, probability of that color in array increases
	// Probabilities are normalised, using new normalisation factor (1/total probabilities), so we have relative probabilities
	// When one of the probabilities reaches over 0.5 / 0.6, we know where the robot is, and then we can move around the track.
	// Robot can determine where it is depending on what probability certain color has? (yes, rhetorical question)
	// ok im losing it safe
	
	
	static double moveWork = 0.99;
	static double sensorWork = 0.99;
	
	// Blue = TRUE, White = FALSE
	// 1 unit = 1.7cm
	// 2W, 3B, 1W, 2B, 2W, 3B, 1W, 2B, 2W, 3B, 2W, 3B, 1W, 2B, 2W, 3B, 1W, 2B
	// 37 grids
	
	static boolean[] colorArray = new boolean[] {
		  false, false, true, true, true, false, true, true, false, false, 
          true, true, true, false, true, true, false, false, true, true, true,
          false, false, true, true, true, false, true, true, false, false,
          true, true, true, false, true, true
};
	
	HashMap map = new HashMap();
	
	// normalisation factor
//	static double nFactor = 1;
	static boolean currentColor = false;
	
	public static void usingFileWriter() throws IOException
	{
	    String fileContent = locationProbability.toString();
	    
	    FileWriter fileWriter = new FileWriter("C:\\Users\\KiemPC\\eclipse-workspace\\RGPsem2\\src\\RGPsem2\\array.txt");
	    fileWriter.write(fileContent);
	    fileWriter.close();
	}
	
	private static int localise() throws IOException {
		
		double totalProbability = calcTotal();
		pilot.setLinearSpeed(4);

//		System.out.println("TOTAL AT START = " + totalProbability);
		
//		System.out.println(maxProbability());
		while (maxProbability() < 0.6) {
			
//			System.out.printf("LP 0: %.3f %n", locationProbability[0]);
			double max = maxProbability();
//			System.out.printf("sMax: %.3f %n", max);

			
			// If the reading is more red, we are on a white tile (in Red mode for color sensor)
			// If reading is less red, we are on blue (assuming for now)
			
			cProvider.fetchSample(cSample, 0);
			
			// if value is over 0.5 (DON'T KNOW WHAT VALUE YET)
			// On track: White reads 0.6+, blue reads <0.1
			
			if (cSample[0] > 0.5) {

				// we have sensed white
//				System.out.println("WE SEE WHITE");
				currentColor = false;
//				System.out.println("WHITE PROB 0 = " + locationProbability[0]);
				
				for (int i = 0; i < 37; i++) {
					if (currentColor == colorArray[i]) {
			
						// If we see same color as in color array, increase probability for all of seen color
						locationProbability[i] = locationProbability[i] * sensorWork;
//						totalProbability += locationProbability[i];
						
					} 
					
					else {
						
						// Else we know that we see a different color, so decrease
						locationProbability[i] = locationProbability[i] * (1 - sensorWork);
//						totalProbability += locationProbability[i];
						
					}
				}
				
			} else {
				
				// we have sensed blue
//				System.out.println("WE SEE BLUE");
				currentColor = true;
//				System.out.println("BLUE PROB 0 = " + locationProbability[0]);
				
				for (int i = 0; i < 37; i++) {
					
					if (currentColor == colorArray[i]) {
						
						locationProbability[i] = locationProbability[i] * sensorWork;
//						totalProbability += locationProbability[i];
						
					} 
					
					else {

						locationProbability[i] = locationProbability[i] * (1 - sensorWork);
//						totalProbability += locationProbability[i];	

					}	
					
				}
			}
			
//			System.out.printf("Total: %.3f %n", totalProbability);
//			System.out.println("TOTAL PROB: " + totalProbability);
			
//			totalProbability = calcTotal();
//			System.out.printf("T before: %.3f %n", totalProbability);

			totalProbability = calcTotal();
			
			for (int i = 0; i < 37; i++) {
				locationProbability[i] = locationProbability[i] * (1 / totalProbability);
			}

//			totalProbability = calcTotal();
//			System.out.printf("T after: %.3f %n", totalProbability);
//			
//			System.out.println("MAX: " + maxProbability());
			
			pilot.travel(1.74);
			
			// Shifting probabilities one movement forward
			for (int i = 36; i > 0; i--) {
				locationProbability[i] = locationProbability[i-1] * moveWork + locationProbability[i] * (1-moveWork);
			}
			
			
			
		}
	
		Sound.twoBeeps();
		
		return (int)(1.74 * maxProbability());
		
	}

	private static double maxProbability() {
		double max = 0;
		for (int i = 0; i < 37; i++) {
			if (locationProbability[i] > max) {
				max = locationProbability[i];
			}
		}
		
		return max;
	}
	
	private static float[] calcManhat(float x1, float y1, float x2, float y2) {
		float xDiff = x2 - x1;
		float yDiff = y2 - y1;
		float[] dist = new float[2];
		dist[0] = xDiff;
		dist[1] = yDiff;
		return dist;
	}
	
	private static float calcEuclidean(int x1, int y1, int x2, int y2) {
		return ((x2 - x1)^2 + (y2 - y1)^2)^(1/2);
	}
	
	private static float[] calcFAtt(int robX, int robY, int goalX, int goalY) {
		int[] manhat = calcManhat(robX, robY, goalX, goalY);
		
		float[] attForces = new float[2];
		
		float forceX = kAtt * (manhat[0]);
		float forceY = kAtt * (manhat[1]);
		
		attForces[0] = forceX;
		attForces[1] = forceY;
		
		return attForces;
	}
	
	private static float[] calcFRep(int robX, int robY, int obsX, int obsY) {
		float[] manhat = calcManhat(obsX, obsY, robX, robY);
		float euclid = calcEuclidean(obsX, obsY, robX, robY);
		
		float[] repForces = new float[2];
		
		float forceX = kRep * ((1 / euclid) - (1 / qRad)) * (1 / euclid^3) * (manhat[0]);
		float forceY = kRep * ((1 / euclid) - (1 / qRad)) * (1 / euclid^3) * (manhat[1]);
		
		repForces[0] = forceX;
		repForces[1] = forceY;
		
		return repForces;
	}
	
	private static float[] calcForce(float[] att, float[] rep) {
		float[] forces = new float[2];
		
		forces[0] = att[0] + rep[0];
		forces[1] = att[1] + rep[1];
		
		return forces;
	}

	private static int findIndex(double max) {
		int index = 0;
		
		for (int i = 0; i < locationProbability.length; i++) {
			if (locationProbability[i] == max) {
				index = i;
			}
		}
		
		return index;
	}
	
	// calculate the total probability of every element in the barcode array
	private static double calcTotal() {
		double total = 0;
		
		for (int i = 0; i < 37; i++) {
			total += locationProbability[i];
		}
		
		return total;
	}
	
}

	
//	public static void moveSquare() {
//		
//		for (int i = 0; i < 4; i++) {
//			pilot.setLinearSpeed(30);
//			pilot.travel(10);
//			pilot.setAngularSpeed(6);
//			pilot.rotate(90, true);
//			
//			Delay.msDelay(4000);
//			if (i == 3) {
//				Delay.msDelay(10000);
//			}
//		}
//		
//	}

//}