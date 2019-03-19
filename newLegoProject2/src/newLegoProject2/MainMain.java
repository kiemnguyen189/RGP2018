package newLegoProject2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
import lejos.robotics.navigation.MovePilot;
import lejos.utility.Delay;

public class MainMain {
	
	private static final int GRID_SIZE = 122;
	
	static RegulatedMotor motorA = new EV3LargeRegulatedMotor(MotorPort.A);
	static RegulatedMotor motorB = new EV3LargeRegulatedMotor(MotorPort.B);
	
	static EV3TouchSensor tSensor = new EV3TouchSensor(SensorPort.S2);
	static SensorMode tProvider = tSensor.getTouchMode();
	static float[] tSample = new float[tProvider.sampleSize()];

	static EV3ColorSensor cSensor = new EV3ColorSensor(SensorPort.S4);
	static SampleProvider cProvider = cSensor.getRedMode();
	static float[] cSample = new float[1];
	static Wheel wLeft = WheeledChassis.modelWheel(motorA, 3).offset(4.155);
	static Wheel wRight = WheeledChassis.modelWheel(motorB, 3).offset(-4.155);

	static Chassis chassis = new WheeledChassis(new Wheel[]{wLeft, wRight}, WheeledChassis.TYPE_DIFFERENTIAL);
	static MovePilot pilot = new MovePilot(chassis);

	static boolean isRed = false;
	static boolean locFin = false;	// SET ON / OFF
	
	static boolean pathFin = false;
	
	static double[] locationProbability = new double[37];
	
	static private double rotation = 135;
	
	static int lVal = 0;
	
	
	//POTENTIAL FIELD CONSTANTS
	static final int kRep = 0;
	static final int kAtt = 0;
	static final int qRad = 0;
	
	static final int obsPos = 1;
	static int iter_pos = 0;

	
	/* rob the robot */
	static Obj rob;
	
	/* grid system */
	static Grid grid;
	
	static ArrayList<Obj> goals;
	

	public static void main(String[] args) throws IOException {

		
		/* BEGIN LOCALISE */
		
		Arrays.fill(locationProbability, 1/37d);
		double init_coord = 0;
		
		while(!locFin) {

			lVal = localise(); 	//returns the block the robot localises at (should be 23)
			
//			init_coord = 102.5 - (lVal * 1.74); // x and y coordinate			
			
			locFin = true;
		}
		
		System.out.print("Localise Finished.");
		
		boolean left = true;
		
		if (!left) {	// between cylinders
			pilot.setAngularSpeed(45);
			pilot.setLinearSpeed(10);
			pilot.rotate(90);
			pilot.travel(33);
			Delay.msDelay(100);
			pilot.rotate(-45);
			pilot.travel(20);
			pilot.rotate(-45);
			pilot.travel(30);	// go past obstacle
			beep();
			pilot.setLinearSpeed(15);
			pilot.rotate(-45);
			pilot.travel(55);	// go into goal
		} else {	// between obs and barrier
			pilot.setAngularSpeed(45);
			pilot.setLinearSpeed(10);
			pilot.rotate(50);
			pilot.travel(16);
			Delay.msDelay(100);
			pilot.travel(16);
			pilot.rotate(-50);
			pilot.travel(38);	
			beep();
			pilot.setLinearSpeed(15);
			pilot.rotate(45);
			pilot.travel(7);
			pilot.rotate(-90);
			pilot.travel(35);	
		}
		touchEnd();

	}

	private static void touchEnd() {

		// true = red
		// false = green

		tSensor.fetchSample(tSample, 0);
		cSensor.fetchSample(cSample, 0);
		
		if (tSample[0] != 0) {
			if (cSample[0] > 0.5) {
				isRed = false;
				Sound.beep();
				
				pilot.travel(-40);
				pilot.rotate(-45);
				pilot.travel(70);
				pilot.rotate(-90);
				pilot.travel(40);
				pilot.rotate(-90);
				pilot.travel(20);
				pilot.rotate(90);
				pilot.travel(60);
				
			} else {
				isRed = true;
				Sound.twoBeeps();
				Sound.twoBeeps();
				
				pilot.travel(-40);
				pilot.rotate(-47);
				pilot.travel(94);
				pilot.rotate(-90);
				pilot.travel(40);
				pilot.rotate(-90);
				pilot.travel(20);
				pilot.rotate(90);
				pilot.travel(40);
				pilot.rotate(-90);
				pilot.travel(5);
				pilot.rotate(90);
				pilot.travel(20);
				
			}
		}
		
		pilot.travel(-23);
		
		///set_secondary_goals(isRed);
		
//		if (color == true) {
//			
//			// BETWEEN CYLINDER AND BARRIER
//			
//			Sound.beep();
//			Delay.msDelay(100);
//			
//			pilot.travel(-45);
//			pilot.rotate(-48); // MAKE 48
//			pilot.travel(59.4);
//			pilot.setAngularSpeed(40);
//			pilot.arc(-15, 135);
//			
//		} else {
//			
//			// BETWEEN CYLINDERS
//			
//			Sound.twoBeeps();
//			Delay.msDelay(100);
//			
//			pilot.travel(-45);
//			pilot.rotate(-48); // MAKE 48
//			pilot.travel(79.6);
//			pilot.setAngularSpeed(40);
//			pilot.arc(-15, 135);
//			
//		}
		
	}
	
	private static void set_secondary_goals(boolean color)
	{	
		
		add_goal(30, 52, 0);
		
		if (color == false) {
			add_goal(75, 84, 0);
			add_goal(90, 72, 0);
			add_goal(80, 62, 0);
			add_goal(110, 32, 0);
		} else {
			add_goal(90, 102, 0);
			add_goal(110, 82, 0);
			add_goal(90, 62, 0);
			add_goal(110, 32, 0);
		}
		
	}
	
	private static Obj get_goal()
	{
		Obj o = goals.get(iter_pos);
		iter_pos = (iter_pos < goals.size() -1) ? iter_pos + 1: 0;
		return o;
	}
	
	private static void add_goal(int x, int y, int radius)
	{
		goals.add(new Obj(x, y, radius, null));
	}
	
	private static void beep()
	{
		Sound.beep();
		Delay.msDelay(2000);
	}
	
	private static void set_angle(double angle)
	{
		double diff = (rotation - angle) % 360; 
		rotation = angle;
		pilot.rotate(-diff);
		rob.set_rotated(false);
	}
	
	
	private static void setpath()
	{
		pilot.setLinearSpeed(rob.get_speed());
		if (rob.get_rotated())
			set_angle(rob.get_angle());
		pilot.travel(rob.get_distance());
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
		
		return findIndex(maxProbability());
		
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
	
	private static int calcManhat(int x1, int y1, int x2, int y2) {
		int xDiff = x2 - x1;
		int yDiff = y2 - y1;
		int dist = xDiff + yDiff;
		return dist;
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