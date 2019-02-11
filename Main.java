import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;
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

	public static void main(String[] args) {

		while(true) {

			pilot.setLinearSpeed(15);
			pilot.travel(43);
//			Delay.msDelay(1000);

			pilot.setAngularSpeed(45);
			pilot.rotate(90);
			pilot.travel(13);
//			Delay.msDelay(1000);

			pilot.arc(-15, 120);
			pilot.arc(0, 45);

			pilot.travel(23);
			pilot.rotate(-64);
			pilot.setLinearSpeed(15);
			pilot.travel(31);

			t1.fetchSample(tSample1, 0);
			t2.fetchSample(tSample2, 0);
			cProvider.fetchSample(cSample, 0);

			
			System.out.print("Test0");
			System.out.println("");
			System.out.println("");
			System.out.println("");
			System.out.println("");

			System.out.println(tSample1[0] != 0);
			System.out.println(tSample2[0] != 0);

			if ((tSample1[0] != 0) || (tSample2[0] != 0)) {

//				System.out.print("Test1");

				if ((cSample[0]) > 0.5) {
//					System.out.print("Test2");
					isRed = true; 
				}

				touchEnd(isRed);				

			} else {

				Sound.buzz();
				Delay.msDelay(500);
				Sound.buzz();

			}

			Delay.msDelay(5000);
//			break;

		}

	}
	

	static private void touchEnd(boolean color) {

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
	
	
	// how to do localisation
	// All probabilities start equal, 1/(number of grids) (let each grid be a 1.7cm x 1.7cm square)
	// Robot is placed on line
	// Robot takes sample from color sensor
	// Robot moves, and depending on readings, probability of that color in array increases
	// Probabilities are normalised, using new normalisation factor (1/total probabilities), so we have relative probabilities
	// When one of the probabilities reaches over 0.5 / 0.6, we know where the robot is, and then we can move around the track.
	// Robot can determine where it is depending on what probability certain color has? (yes, rhetorical question)
	// ok im losing it safe
	
	
	double[] locationProbability = new double[37];
	Arrays.fill(locationProbability, (1/37));
	
	double moveWork = 0.98;
	double sensorWork = 0.98;
	
	// Blue = TRUE, White = FALSE
	// 1 unit = 1.cm
	// 2W, 3B, 1W, 2B, 2W, 3B, 1W, 2B, 2W, 3B, 2W, 3B, 1W, 2B, 2W, 3B, 1W, 2B
	// 37 grids
	
	boolean[] colorArray = new boolean[
	                                   (false, false, true, true, true, false, true, true, false, false, 
	                                	true, true, true, false, true, true, false, false, true, true, true,
	                                	false, false, true, true, true, false, true, true, false, false
	                                	true, true, true, false, true, true)
	                                   ];
	
	HashMap map = new HashMap();
	
	// normalisation factor
	double nFactor = 0;
	
	static private void localisation() {
		
		// take sensor reading (color sensor)
		// 
		
		// if blue is read, probability of blue increases in whole array
		// if white is read, probability of white increases in whole array
		
		// if probability > 0.5 / 0.6, then something
		
		
		
		
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

}
