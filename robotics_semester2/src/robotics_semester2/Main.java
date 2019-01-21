package robotics_semester2;
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
	
	static Wheel wLeft = WheeledChassis.modelWheel(motorA, 3).offset(4.155);
	static Wheel wRight = WheeledChassis.modelWheel(motorB, 3).offset(-4.155);
	
	static Chassis chassis = new WheeledChassis(new Wheel[]{wLeft, wRight}, WheeledChassis.TYPE_DIFFERENTIAL);
	static MovePilot pilot = new MovePilot(chassis);
	
	public static void main(String[] args) {
		
		while(true) {
		
			pilot.setLinearSpeed(15);
			pilot.travel(40);
			Delay.msDelay(1000);
			
			pilot.setAngularSpeed(45);
			pilot.rotate(90);
			pilot.travel(13);
			Delay.msDelay(1000);
			
			pilot.arc(-15, 120);
			pilot.arc(0, 45);
			
			pilot.travel(25);
			pilot.rotate(-60);
			pilot.travel(24);
			
			break;
			
			
//			
//			pilot.travel(6);
//			pilot.setAngularSpeed(20);
//			pilot.rotate(90);
//			Delay.msDelay(1500);
//			
//			pilot.arc(3,45);
//			Delay.msDelay(2000);
//			pilot.arc(-3, 45);
//			Delay.msDelay(2000);
//			pilot.arc(3, -45);
//			Delay.msDelay(2000);
//			pilot.arc(-3, -45);
//			Delay.msDelay(2000);
//			Delay.msDelay(1500);
			
//			pilot.setLinearSpeed(30);
//			pilot.travel(42);
//			pilot.setAngularSpeed(30);
//			pilot.rotate(90);
//			Delay.msDelay(1000);
//			
//			pilot.travel(30);
//			pilot.rotate(-90);
//			Delay.msDelay(1000);
//			
//			pilot.travel(40);
//			Delay.msDelay(1000);
//			break;
			
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
