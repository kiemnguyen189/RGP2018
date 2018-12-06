import lejos.hardware.BrickFinder;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;

public class Main {
 
 // Color sensor
 static EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S4);
 static SampleProvider colorProvider = colorSensor.getRGBMode();
 static float[] colorSample = new float[3];
 // Ultrasonic sensor
 static EV3UltrasonicSensor ultraSensor = new EV3UltrasonicSensor(SensorPort.S2);
 static SampleProvider ultraProvider = ultraSensor.getDistanceMode();
 static float[] ultraSample = new float[ultraProvider.sampleSize()];
 
 // Large wheel motors and medium motor
 static RegulatedMotor mA = new EV3LargeRegulatedMotor(MotorPort.A);
 static RegulatedMotor mB = new EV3LargeRegulatedMotor(MotorPort.B);
 static RegulatedMotor mC = new EV3MediumRegulatedMotor(MotorPort.C);
 
 static double kP = 800;	// Proportional control
 static double kI = 500;	// Integral control
 static double kD = 15;		// Derivative control
 static double initialAngle = -90;
 static double tP = 160;
 static double initialTime = 0;
 static double lastError = 0;
 static double offset = 0.185;
 static double integral = 0;
 static double derivative = 0;
 static double setDistance = 0.05;
 
 public static void main(String[] args) {
  
  GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();
  String s;
  
  while(true) {
	  
//	  rotateHead();
   
   // Implementation of PID control.
   
   // We want to reset the integral back to 0 every time the program goes through a loop, 
   // as the integral is continuosly added to every run, and the speed of the wheels is partly decided by this value.
   // If the value was not reset, the integral would keep increasing in a linear fashion, and we would go to max speed on the wheels.
   colorProvider.fetchSample(colorSample, 0);

   if (determineColor(colorSample) == "red") {

	   mA.startSynchronization();
	   mA.stop();
	   mB.stop();
	   mA.endSynchronization();
	
   } else if ((getDistance() <= setDistance)) {
	   
	  ultraStop();
	  ultraTurn();
   
   } else {
	   
    mA.synchronizeWith(new RegulatedMotor[]{mB});
//    mC.synchronizeWith(new RegulatedMotor[]{mA});
    
    double turn = turnValue();
    double powerLeft = tP - turn;
    double powerRight = tP + turn;
    
    
    mA.startSynchronization();
    
    mA.setSpeed((int)powerLeft);
    mB.setSpeed((int)powerRight);
    mA.forward();
    mB.forward();
    
    mA.endSynchronization();
	rotateHead();
    
   }
   
  }
}

static double turnValue() {
	
	initialTime = System.currentTimeMillis();
	
	colorProvider.fetchSample(colorSample, 0);

	float redValue = colorSample[0];
    float greenValue = colorSample[1];
    float blueValue = colorSample[2];
    
    double totalError = (redValue - offset) + (greenValue - offset) + (blueValue - offset);
    double averageError = totalError/3;
    
    if (lastError == 0 || Math.abs((lastError - averageError)) > Math.abs(lastError)) {
     integral = 0;
    }
    
    double dT = System.currentTimeMillis() - initialTime;
    integral = ((1/8) * integral) + (averageError * dT);
//    System.out.println(integral);
    derivative = (averageError - lastError);
    
    double turn = (kP * averageError) + (kI * integral) + (kD * derivative);
    
    lastError = averageError;
    
    return turn;
	
}
 
 
static void ultraStop() {
 
	mA.startSynchronization();
	mA.stop();
	mB.stop();
	mA.endSynchronization();
	
	do {
		
		mA.startSynchronization();
		mA.setSpeed((270));
		mB.setSpeed((270));
		mA.forward();
		mB.backward();
		mA.endSynchronization();  
		
	} while (getDistance() >= setDistance);
	
	mC.rotateTo((int)initialAngle);
	 
}

static void ultraTurn() {
 
	 circleObject();
 
	 mA.startSynchronization();
	
	 mA.setSpeed(0);
	 mB.setSpeed(0);
	 
	 mA.endSynchronization();
	 mC.rotateTo(0);
	 
	 Delay.msDelay(100);
	 reCenter();
	  
// do {
//	 
//  colorProvider.fetchSample(colorSample,  0);
//  mA.startSynchronization();
//  
//  double distance = getDistance(); 
//  double error = distance - 0.045;
//  
//  if (error >= 0.245) {
//	  error = 0.245;
//  }
//  
//  if (error <= -0.245) {
//	  error = -0.245;
//  }
//  
//  double turn = kP * error;
//  mA.setSpeed((int)(tP - (turn)));
//  mB.setSpeed((int)(tP + (turn)));
//  
//  mA.forward();
//  mB.forward();
//
//  
//  mA.endSynchronization(); 
// } while (determineColor(colorSample) != "black");
	 
// do {
//
//  colorProvider.fetchSample(colorSample,  0);
//  mA.startSynchronization();
//  mA.setSpeed((270));
//  mB.setSpeed((270));
//  mA.forward();
//  mB.backward();
//  mA.endSynchronization();  
//  
// } while (determineColor(colorSample) != "white");
// 
}
 
static double getDistance() {
 
 ultraProvider.fetchSample(ultraSample, 0);
// System.out.println(ultraSample[0]);
 return ultraSample[0];
 
}
 
static void circleObject() {
	
	do {
		 
	  colorProvider.fetchSample(colorSample,  0);
	  
	  double distance = getDistance(); 
	  double error = distance - setDistance;
	  
	  if (error >= 0.25) {
		  error = 0.25;
	  }
	  
	  if (error <= -0.25) {
		  error = -0.25;
	  }
	  
	  double turn = kP * error;
	  
	  mA.startSynchronization();
	  
	  mA.setSpeed((int)(tP - (turn)));
	  mB.setSpeed((int)(tP + (turn)));
	  mA.forward();
	  mB.forward();
	  
	  mA.endSynchronization(); 
	  
	} while (determineColor(colorSample) != "black");
	
}

static void reCenter() {
	
	do {
	
		 colorProvider.fetchSample(colorSample,  0);
		 mA.startSynchronization();
		 mA.setSpeed((360));
		 mB.setSpeed((360));
		 mA.forward();
		 mB.backward();
		 mA.endSynchronization();  
		  
	} while (determineColor(colorSample) != "white");
	 
}

static int rotationDir = 1;
static void rotateHead() {
	
//	Delay.msDelay(20);
//	Delay.msDelay(20);
	mC.rotateTo(40 * rotationDir, true);
	if (rotationDir == 1) {
		rotationDir = -1;
	} else {
		rotationDir = 1;
	}
	
}
 
static String determineColor(float[] sample) {
 
 String color = "";
 
 // ////////// VERY LARGE RANGES, MIGHT GIVE UNWANTED RESULTS //////////////
 // RED = Red between 0.2 and 0.3, Green and blue between 0.05 / 0.06 and 0.03 and 0.04
 // BLACK = Red between 0.055 and 0.65, Green and blue between 0.050 / 0.060 and 0.050 and 0.060
 // WHITE = Red between 0.2 and 0.3, Green and blue between 0.2 / 0.3 and 0.2 and 0.3
 // If not in above ranges, color is unknown. This may cause problems later if a slightly different hue of certain color is found.
 
 
 if (  (sample[0] > 0.200f) && (sample[0] < 0.300f)
   && (sample[1] > 0.020f) && (sample[1] < 0.060f)
   &&  (sample[2] > 0.020f) && (sample[2] < 0.060f)
  ) {
   color = "red";
   
  } else if ( (sample[0] > 0.020f) && (sample[0] < 0.065f)
    &&  (sample[1] > 0.020f) && (sample[1] < 0.065f)
    &&  (sample[2] > 0.020f) && (sample[2] < 0.065f)
  ) {
   color = "black";
   
  } else if ((sample[0] > 0.150f) && (sample[0] < 0.400f)
    &&  (sample[1] > 0.150f) && (sample[1] < 0.400f)
    &&  (sample[2] > 0.150f) && (sample[2] < 0.400f)
  ) {
   color = "white";
   
//  } else if ((sample[0] > 0.065f) && (sample[0] < 0.150f)
//    &&  (sample[1] > 0.065f) && (sample[1] < 0.150f)
//    &&  (sample[2] > 0.065f) && (sample[2] < 0.150f)
//   ) {
//   color = "grey";
  } else {
   color = "unknown";
  }
 
 return color;
 
}


// OLD CODE MIGHT REUSE AT SOME POINT BUT NOT ATM 
/////////////////////////////////////////////////////////////////////////////
// Touch sensors currently not in use, as we can make the robot run without pressing a button.
//touch1.fetchSample(sample1, 0);
//touch2.fetchSample(sample2, 0);


// First touch sensor
//EV3TouchSensor touchSensor1 = new EV3TouchSensor(SensorPort.S1);
//SensorMode touch1 = touchSensor1.getTouchMode();
//float[] sample1 = new float[touch1.sampleSize()];

// Second touch sensor
//EV3TouchSensor touchSensor2 = new EV3TouchSensor(SensorPort.S2);
//SensorMode touch2 = touchSensor2.getTouchMode();
//float[] sample2 = new float[touch2.sampleSize()];


// Medium rotating motor
//RegulatedMotor mC = new EV3MediumRegulatedMotor(MotorPort.B);


//static void runMotors(RegulatedMotor motor1, RegulatedMotor motor2, float[] sample) {
// // If motor is moving/not moving and red is detected, stop the motors
// // If motor is moving/not moving and black is detected, run motor.
// // If motor is running and white is detected, slow down.
// // If unknown color is found, go to a halt.
// 
// motor1.synchronizeWith(new RegulatedMotor[]{motor2});
// 
// if (  (sample[0] > 0.200f) && (sample[0] < 0.300f)
//   && (sample[1] > 0.020f) && (sample[1] < 0.060f)
//   &&  (sample[2] > 0.020f) && (sample[2] < 0.060f)
//  ) {
//  
//   // RED
//   motor1.startSynchronization();
//   motor1.stop();
//   motor2.stop();
//   motor1.endSynchronization();
//   
//  } else if ( (sample[0] > 0.020f) && (sample[0] < 0.065f)
//    &&  (sample[1] > 0.020f) && (sample[1] < 0.065f)
//    &&  (sample[2] > 0.020f) && (sample[2] < 0.065f)
//  ) {
//   
//   // BLACK
//   motor1.startSynchronization();
//   motor1.setSpeed(18);
//   motor1.forward();
//   motor2.setSpeed(180);
//   motor2.forward();
//   motor1.endSynchronization();
//   
//  } else if ((sample[0] > 0.150f) && (sample[0] < 0.400f)
//    &&  (sample[1] > 0.150f) && (sample[1] < 0.400f)
//    &&  (sample[2] > 0.150f) && (sample[2] < 0.400f)
//  ) {
//   
//   // WHITE
//   motor1.startSynchronization();
//   motor1.setSpeed(180);
//   motor1.forward();
//   motor2.setSpeed(18);
//   motor2.forward();
//   motor1.endSynchronization();
//   
//  } else if ((sample[0] > 0.065f) && (sample[0] < 0.150f)
//    &&  (sample[1] > 0.065f) && (sample[1] < 0.150f)
//    &&  (sample[2] > 0.065f) && (sample[2] < 0.150f)
//   ) {
//   
//   // GREY
//   motor1.startSynchronization();
//   motor1.setSpeed(180);
//   motor1.forward();
//   motor2.setSpeed(180);
//   motor2.forward();
//   motor1.endSynchronization();
//  } else {
//   
//   // UNKNOWN
//   motor1.startSynchronization();
//   motor1.setSpeed(90);
//   motor1.forward();
//   motor2.setSpeed(90);
//   motor2.forward();
//   motor1.endSynchronization();
//  }
// 
// 
// if (color == "red") {
////  System.out.println("Found red, stopping");
//  motor1.startSynchronization();
//  motor1.stop();
//  motor2.stop();
//  motor1.endSynchronization();
////  Delay.msDelay(100);
//  
// } else if (color == "black") {
////  System.out.println("Found black, running");
//  motor1.startSynchronization();
//  motor1.setSpeed(24);
//  motor1.forward();
//  motor2.setSpeed(240);
//  motor2.forward();
//  motor1.endSynchronization();
//  
// } else if (color == "white") {
////  System.out.println("Found white, slowing down");
//  motor1.startSynchronization();
//  motor1.setSpeed(240);
//  motor1.forward();
//  motor2.setSpeed(24);
//  motor2.forward();
//  motor1.endSynchronization();
////  Delay.msDelay(500);
//  
// } else if (color == "grey") { 
////  System.out.println("Found unknown color, spinning to a halt");
//  motor1.startSynchronization();
//  motor1.setSpeed(240);
//  motor1.forward();
//  motor2.setSpeed(240);
//  motor2.forward();
//  motor1.endSynchronization();
////  Delay.msDelay(1000); 
//  
// } else {
//  motor1.startSynchronization();
//  motor1.setSpeed(120);
//  motor1.forward();
//  motor2.setSpeed(120);
//  motor2.forward();
//  motor1.endSynchronization();
//  
// }
  
//}

//String color = determineColor(colorSample);

// Run motor spins wheels, and checks what the color is. If a certain color, does a certain thing.
// Red stops motor, Black makes it run, White makes it slow down, and unknown color makes it spin to a halt.

// error multiplier

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

//			if (color == "red") {
//	mA.stop();
//	mB.stop();
//} else if (color == "black") {
//	mA.startSynchronization();
//	runMotor(mA, color);
//	runMotor(mB, color);
//	mA.endSynchronization();
//} else if (color == "white") {
//	runMotor(mA, color);
//	runMotor(mB,color);
//} else if (color == "unknown") {
//	mA.startSynchronization();
//	mA.stop(true);
//	mB.stop();
//	mA.endSynchronization();
//}


}
