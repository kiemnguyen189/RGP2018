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
import lejos.robotics.navigation.MovePilot;
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
	
	static double[] locationProbability = new double[37];

	public static void main(String[] args) throws IOException {

		Arrays.fill(locationProbability, 1/37d);
		
		while(!locFin) {

			localise();
			
			
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
	
	
	static double moveWork = 0.9;
	static double sensorWork = 0.9;
	
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
	
	private static void localise() throws IOException {
		
		double totalProbability = calcTotal();
		
//		System.out.println(maxProbability());
		while (maxProbability() < 0.4) {
			
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
			
			totalProbability = calcTotal();
			System.out.printf("Total: %.3f %n", totalProbability);

			
			for (double prob : locationProbability) {
				prob *= (1 / totalProbability);
			}
			
//			Delay.msDelay(50);
			pilot.setLinearSpeed(4);
			pilot.travel(1.7);
			
//			for (int i = 1; i < 37; i++) {
//				locationProbability[i] = locationProbability[i] * moveWork + locationProbability[i-1] * (1 - moveWork);
//			}
			
			usingFileWriter();
			
			
		}
		
		System.out.println("MAX: " + maxProbability());
		locFin = true;
		
	}


	private static double maxProbability() {
		double max = 1/37;
		for (int i = 0; i < 37; i++) {
			if (locationProbability[i] > max) {
				max = locationProbability[i];
			}
		}
		
		return max;
	}
	
	// calculate the total probability of every element in the barcode array
	private static double calcTotal() {
		double total = 0;
		
		for (double prob: locationProbability) {
			total += prob;
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