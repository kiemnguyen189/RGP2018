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
	
	static Wheel wheel1 = WheeledChassis.modelWheel(motorA, 3).offset(4);
	static Wheel wheel2 = WheeledChassis.modelWheel(motorB, 3).offset(-4);
	
	static Chassis chassis = new WheeledChassis(new Wheel[]{wheel1, wheel2}, WheeledChassis.TYPE_DIFFERENTIAL);
	static MovePilot pilot = new MovePilot(chassis);
	
	public static void main(String[] args) {
		
		while(true) {
			
			pilot.setLinearSpeed(30);
			pilot.travel(42);
			pilot.setAngularSpeed(30);
			pilot.rotate(90);
			Delay.msDelay(1000);
			
			pilot.travel(30);
			pilot.rotate(-90);
			Delay.msDelay(1000);
			
			pilot.travel(40);
			Delay.msDelay(1000);
			break;
			
//			moveSquare();
			
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
