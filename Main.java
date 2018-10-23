import lejos.hardware.BrickFinder;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;


public class Main {
	public static void main(String[] args) {
		
		GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();
		String s;
		
		// First touch sensor
		EV3TouchSensor touchSensor1 = new EV3TouchSensor(SensorPort.S1);
		SensorMode touch1 = touchSensor1.getTouchMode();
		float[] sample1 = new float[touch1.sampleSize()];
		
		// Second touch sensor
		EV3TouchSensor touchSensor2 = new EV3TouchSensor(SensorPort.S2);
		SensorMode touch2 = touchSensor2.getTouchMode();
		float[] sample2 = new float[touch2.sampleSize()];
		
		// Color sensor
		EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S4);
		SampleProvider colorProvider = colorSensor.getRGBMode();
		float[] colorSample = new float[3];
		
		// Large wheel motors
		RegulatedMotor mA = new EV3LargeRegulatedMotor(MotorPort.A);
		RegulatedMotor mB = new EV3LargeRegulatedMotor(MotorPort.B);
		
		// Medium rotating motor
//		RegulatedMotor mC = new EV3MediumRegulatedMotor(MotorPort.B);
		

		while(true) {
			
			// Touch sensors currently not in use, as we can make the robot run without pressing a button.
			touch1.fetchSample(sample1, 0);
			touch2.fetchSample(sample2, 0);
			
			colorProvider.fetchSample(colorSample, 0);
			String color = determineColor(colorSample);
			
//			if (color == "red") {
//				mA.stop();
//				mB.stop();
//			} else if (color == "black") {
//				mA.startSynchronization();
//				runMotor(mA, color);
//				runMotor(mB, color);
//				mA.endSynchronization();
//			} else if (color == "white") {
//				runMotor(mA, color);
//				runMotor(mB,color);
//			} else if (color == "unknown") {
//				mA.startSynchronization();
//				mA.stop(true);
//				mB.stop();
//				mA.endSynchronization();
//			}
			
			// Run motor spins wheels, and checks what the color is. If a certain color, does a certain thing.
			// Red stops motor, Black makes it run, White makes it slow down, and unknown color makes it spin to a halt.
			runMotor(mA, color);
			runMotor(mB, color);
			
		}
	}
	
	
static void runMotor(RegulatedMotor motor, String color) {

	// If motor is moving/not moving and red is detected, stop the motors
	// If motor is moving/not moving and black is detected, run motor.
	// If motor is running and white is detected, slow down.
	// If unknown color is found, go to a halt.
	
	if (color == "red") {
		System.out.println("Found red, stopping");
		motor.stop();
		Delay.msDelay(100);
	} else if (color == "black") {
		System.out.println("Found black, running");
		motor.setSpeed(180);
		motor.forward();
	} else if (motor.isMoving() && color == "white") {
		System.out.println("Found white, slowing down");
		motor.setSpeed(90);
		motor.forward();
		Delay.msDelay(500);
	} else {
		System.out.println("Found unknown color, spinning to a halt");
		motor.flt(true);
		Delay.msDelay(1000);
	}
	
//	if (color == "black") {
//		motor.setSpeed(360);
//		motor.forward();
//	}
//	
//	if (motor.isMoving() && color == "white") {
//		motor.setSpeed(180);
//		motor.forward();
//	}
		
}
	
static String determineColor(float[] sample) {
	
	String color = "";
	// ////////// VERY LARGE RANGES, MIGHT GIVE UNWANTED RESULTS //////////////
	// RED = Red between 0.2 and 0.3, Green and blue between 0.05 / 0.06 and 0.03 and 0.04
	// BLACK = Red between 0.055 and 0.65, Green and blue between 0.050 / 0.060 and 0.050 and 0.060
	// WHITE = Red between 0.2 and 0.3, Green and blue between 0.2 / 0.3 and 0.2 and 0.3
	// If not in above ranges, color is unknown. This may cause problems later if a slightly different hue of certain color is found.
	
	
	if ( 	(sample[0] > 0.200f) && (sample[0] < 0.300f)
			&&	(sample[1] > 0.020f) && (sample[1] < 0.060f)
			&& 	(sample[2] > 0.020f) && (sample[2] < 0.060f)
			) {
//			System.out.println("DETECTING RED");
			color = "red";
		} else if ( (sample[0] > 0.020f) && (sample[0] < 0.065f)
				&& 	(sample[1] > 0.020f) && (sample[1] < 0.060f)
				&& 	(sample[2] > 0.020f) && (sample[2] < 0.060f)
			) {
//			System.out.println("DETECTING BLACK");
			color = "black";
		} else if ((sample[0] > 0.150f) && (sample[0] < 0.400f)
				&& 	(sample[1] > 0.150f) && (sample[1] < 0.400f)
				&& 	(sample[2] > 0.150f) && (sample[2] < 0.400f)
			) {
//			System.out.println("DETECTING WHITE");
			color = "white";
		} else {
//			System.out.println("DETECTING RGB VALUES   " + sample[0] + "/" + sample[1] + "/" + sample[2]);
			color = "unknown";
		}
	
	return color;
}
	
// OLD CODE MIGHT REUSE AT SOME POINT BUT NOT ATM 
/////////////////////////////////////////////////////////////////////////////
// if pressing button 1, set speed of wheels to 2 RPS, and write to screen.
//if (sample1[0] != 0) {
//	
//	// COLOR ID MODE CHANGED TO RGB MODE, NOT USING ANYMORE.
//	// Colors have IDs in ColorIDMode. Colors with IDs are:
//	// 0 1 2 3 4 5 6 7
//	// None, Black, Blue, Green, Yellow, Red, White, Brown
//	// If the color detected is not red, run motor.
//	
//	
//	colorProvider.fetchSample(colorSample, 0);
//	String color = determineColor(colorSample);
//	
//	if (color == "red") {
//		mA.stop();
//		mB.stop();
//	} else if (color == "black") {
//		runMotor(mA);
//		runMotor(mB);
//	} else if (color == "white") {
//		runMotor(mA);
//		runMotor(mB);
//	} else if (color == "unknown") {
//		mA.stop();
//		mB.stop();
//	}
//		
//	
//} 
//// if pressing button 2, fetch the values in the colorprovider, and print them out on the screen. These are normalised between 0 and 1.
//else if (sample2[0] != 0) {
//	
//	
//	colorProvider.fetchSample(colorSample, 0);
//	// RED STRIP
//	// Red 0.23431373
//	// Green 0.46078432
//	// Blue 0.033333335
//	
//	// BLACK STRIP
//	// Red 0.0029411765
//	// Green 0.004901961
//	// Blue 0.003921569
//	
//	// WHITE STRIP
//	// Red 0.27058825
//	// Green 0.2617647
//	// Blue 0.17843138
//	
//	// ELSE OTHER COLOR, WE DONT NEED TO TEST FOR OTHERS FOR NOW
//	
//	
//	
////	System.out.println(colorSample[0]);
////	System.out.println(colorSample[1]);
////	System.out.println(colorSample[2]);
//	
////	if ( 	(colorSample[0] > 0.230f) && (colorSample[0] < 0.240)
////		&&	(colorSample[1] > 0.455) && (colorSample[1] < 0.470)
////		&& 	(colorSample[2] > 0.030) && (colorSample[2] < 0.040)
////		) {
////		System.out.println("DETECTING RED");
////	} else if ( (colorSample[0] > 0.02) && (colorSample[0] < 0.04)
////			&& 	(colorSample[1] > 0.04) && (colorSample[1] < 0.06)
////			&& 	(colorSample[2] > 0.03) && (colorSample[2] < 0.05)
////		) {
////		System.out.println("DETECTING BLACK");
////	} else if ((colorSample[0] > 0.265) && (colorSample[0] < 0.275)
////			&& 	(colorSample[1] > 0.255) && (colorSample[1] < 0.265)
////			&& 	(colorSample[2] > 0.170) && (colorSample[2] < 0.185)
////		) {
////		System.out.println("DETECTING WHITE");
////	} else {
////		System.out.println("DETECTING RGB VALUES   " + colorSample[0] + "/" + colorSample[1] + "/" + colorSample[2]);
////	}
////
//	// Print out pressing button 2 and hue of red in rgb spectrum.
////	g.drawString("PRESSING BUTTON 2 " +  colorSample[0], 0, 0, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
//	
//	
//} else {
//	
//	mA.stop();
//	mB.stop();
//	
//}
//touch2.fetchSample(sample2, 0);
//if (sample2[0] != 0) {
//	g.clear();
//	mB.setSpeed(90);
//	mB.setSpeed(90);
//	mB.forward();
//	g.drawString("Pressing B", 0, 20, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
//} else {
//	mB.stop();
//}
//
//gyro.fetchSample(gyroTest, 0);
//int valueTest = (int)gyroTest[0];
//
//g.clear();
//g.drawString("Gyro Angle = "  + Integer.toString(valueTest), 0, 50, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);

//g.setAutoRefresh(true);


// Gyro sensor
//EV3GyroSensor gyroSensor = new EV3GyroSensor(SensorPort.S3);
//SampleProvider gyro = gyroSensor.getAngleMode();
//float[] gyroTest = new float[gyro.sampleSize()];

}
