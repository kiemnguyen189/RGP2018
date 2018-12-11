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
	// PID control to make the robot follow the line.
	
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

	derivative = (averageError - lastError);

	double turn = (kP * averageError) + (kI * integral) + (kD * derivative);

	lastError = averageError;

	return turn;
	
}
 
 
static void ultraStop() {
 	// ultraStop is used to stop the robot when it sees an object. The wheels are stopped, 
	// and the robot rotates around 90 degrees away from the object, with the head still facing the object.
	// The robot keeps rotating until the distance is far enough away from the obstacle.
	
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
 	// ultraTurn is used to both circle the object, and get back on the track.
	// It calls circleObject once, then stops and rotates the head back to 0 degrees, and then realigns the robot onto the track.
	
	circleObject();

	mA.startSynchronization();

	mA.setSpeed(0);
	mB.setSpeed(0);

	mA.endSynchronization();
	mC.rotateTo(0);

	Delay.msDelay(100);
	reCenter();

}
	
static double getDistance() {
	// Simple method to get the distance from an object. Used for the ultrasonic sensor.
	
	ultraProvider.fetchSample(ultraSample, 0);
	return ultraSample[0];
 
}
 
static void circleObject() {
	// circleObject uses P control to stay at a contstant distance away from the object. 
	// It calculates the current distance away from the object, and then adjusts the wheels speeds depending on that distance.
	// Error is calculated as the current distance - desired distance (in this case 5cm, or 0.05m).
	// If the robot is too far, the error is positive (i.e. 10cm away would make error 0.1 - 0.05 = 0.05), and negative in the other direction.
	// The max error is set to 0.25, although it should not get this far away from the obstacle anyway. 
	// This error is then mutiplied by kP, and then set as the turn value for the wheels.
	
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
	// reCenter is used to align the robot with the track after finding the track. After circling the object,
	// it should find a black line. It then rotates right until it finds white again, meaning that it gets back on track
	// by turning to the inside of the traack (i.e. right side of the line).
	
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
	// rotateHead is used to rotate the medium motor (mC). The current rotation amount is 40 degrees,
	// and rotateTo is used as we can rotate between +40 and -40 degrees. If rotate() was used instead,
	// we would have to rotate first by +40, then by -80, then by +120, -160, and so on. This would cause unnecessary trouble,
	// hence the rotateTo() instead. The second parameter of the method tells the robot that it should return immediately from the method,
	// and therefore it won't wait for the head to rotate in the main method. This is so that PID is run continuously,
	// and the head rotates to the given angles without slowing down the PID driving.
	
	mC.rotateTo(40 * rotationDir, true);
	if (rotationDir == 1) {
		rotationDir = -1;
	} else {
		rotationDir = 1;
	}
	
}
 
static String determineColor(float[] sample) {
 
 String color = "";
	
// Depending on the reading from the color sensor, we can determine what color is read.
// If the first value in the sensor array (sample[0]) is the highest (between 0.2 and 0.3), and the other 2 are low, we can tell the color is red.
// If all three values in the array are low (between 0.02 and 0.065), we can tell the color is black.
// If all three values in the array are high (between 0.15 and 0.4), we can tell the color is white.
// The last case is any other color and it is set as "unknown". This was previously used in non-PID control driving, but no longer used.
 
// Depending on the read color, we set the string color to the found color (red, black or white). This is then returned and used for driving.
	
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
  } else {
   color = "unknown";
  }
 
 return color;
 
}
