package newLegoProject2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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

	static boolean isRed = false; // SET ON / OFF
	static boolean locFin = false;	// SET ON / OFF
	
	static double[] locationProbability = new double[37];
	
	static private double rotation = 135;
	
	static int lVal = 0;
	
	/* rob the robot */
	static Obj rob;
	
	/* grid system */
	static Grid grid;
	
	static ArrayList<Obj> goals;
	

	public static void main(String[] args) throws IOException {

		/* BEGIN LOCALISE */
		Arrays.fill(locationProbability, 1/37d);
		
		while(!locFin) {
			lVal = localise(); 	//returns the block the robot localises at (should be 23)		
			locFin = true;
		}
		
		System.out.print("Localise Finished.");
		
		double dist = lVal * 1.74;
		double xCoord = 105 - ( Math.cos(Math.PI / 4) * dist );
		double yCoord = 17 + ( Math.sin(Math.PI / 4) * dist );

		/* INIT ROB AND GRID */
		rob = new Obj((int)xCoord , (int)yCoord, 2, null);
		goals = new ArrayList<Obj>();
		
		/* START RUNNING THE GRID SYSYTEM */
		
		/* new goal for rob */
		rob.set_speed(7);
		rob.set_angle(135);
		grid = new Grid(GRID_SIZE, rob);
		
		set_initial_goals();
		rob.set_goal(get_goal());
		
		while (grid.can_move(rob) && (goals.size() != 0)) {

			grid.take_turn();
			setpath();
			
			if (rob.at_goal()) {
				System.out.println("GOAL REACHED");
				if (goals.size() > 1) {
					goals.remove(0);
					rob.set_goal(goals.get(0));
				} else {
					goals.remove(0);
					break;
				}
			}
			
		}
		
		// true = red
		// false = green
		boolean color = touchEnd();

		add_goal(30, 52, 0);

		if (color) {
			add_goal(90, 102, 0);
			add_goal(110, 82, 0);
			add_goal(90, 62, 0);
			add_goal(110, 32, 0);
		} else {
			add_goal(75, 84, 0);
			add_goal(90, 72, 0);
			add_goal(80, 62, 0);
			add_goal(110, 32, 0);
		}
		
		while (grid.can_move(rob) && (goals.size() != 0)) {
			
			Obj goal = rob.get_goal();
			System.out.println("Goal = (" + goal.get_x() + ", " + goal.get_y() + ")");

			grid.take_turn();
			setpath();

			if (rob.at_goal()) {
				System.out.println("GOAL REACHED");
				if (goals.size() > 1) {
					goals.remove(0);
					rob.set_goal(goals.get(0));
				} else {
					goals.remove(0);
					Sound.beepSequence();
					return;
				}	
			}
		}
		Delay.msDelay(5000);
	}
	
	
	private static void set_initial_goals()
	{	
		// Goal between barrier and 1st obstacle
		add_goal(43, 43, 0);
		
		// Goals between 1st and 2nd obstacle
//		add_goal(40, 17, 0);
//		add_goal(12, 42, 0);
		
		// GARAGE
		add_goal(20, 70, 0);
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
	
	// Set angle of robot to point in wanted direction
	// Rotate robot by difference in angle to target and current rotation
	// We want to rotate counterclockwise, as it is taken as the positive
	// rotation direction, hence the negative rotation in pilot.rotate()
	private static void set_angle(double angle)
	{
		double diff = (rotation - angle) % 360; 
		rotation = angle;
		pilot.rotate(-diff);
		rob.set_rotated(false);
	}
	
	// Sets path to next goal from current location
	private static void setpath()
	{
		pilot.setLinearSpeed(rob.get_speed());
		if (rob.get_rotated())
			set_angle(rob.get_angle());
		pilot.travel(rob.get_distance());
	}
	

	// Enter garage, touch wall, and leave garage
	private static boolean touchEnd() {

		// true = red
		// false = green
		// ~5 degree error by the time we reach garage, want to rotate by extra 5 degrees.
		pilot.rotate((90 - rotation) - 5);
		pilot.travel(23);
		tSensor.fetchSample(tSample, 0);
		cSensor.fetchSample(cSample, 0);
		
		if (tSample[0] != 0) {
			if (cSample[0] > 0.5) {
				isRed = true;
				Sound.beep();
			} else {
				isRed = false;
				Sound.twoBeeps();
			}
		}
		
		pilot.travel(-23);
		return isRed;
	}

	// HOW TO DO LOCALISATION
	// All probabilities start equal, 1/(number of grids) (let each grid be a 1.7cm x 1.7cm square)
	// Robot is placed on line
	// Robot takes sample from color sensor
	// Robot moves, and depending on readings, probability of that color in array increases
	// Probabilities are normalised, using new normalisation factor (1/total probabilities), so we have relative probabilities
	// When one of the probabilities reaches over 0.5 / 0.6, we know where the robot is, and then we can move around the track.
	// Robot can determine where it is depending on what probability certain color has
	
	// Probability of moving and sensor working (99%)
	static double moveWork = 0.99;
	static double sensorWork = 0.99;
	
	// Blue = TRUE, White = FALSE
	// 1 unit = 1.7cm
	// 2W, 3B, 1W, 2B, 2W, 3B, 1W, 2B, 2W, 3B, 2W, 3B, 1W, 2B, 2W, 3B, 1W, 2B
	// 37 blocks
	
	static boolean[] colorArray = new boolean[] {
		  false, false, true, true, true, false, true, true, false, false, 
          true, true, true, false, true, true, false, false, true, true, true,
          false, false, true, true, true, false, true, true, false, false,
          true, true, true, false, true, true
	};
	
	static boolean currentColor = false;
	
	private static int localise() throws IOException {
		
		double totalProbability = calcTotal();
		pilot.setLinearSpeed(4);

		while (maxProbability() < 0.6) {
			
			// If the reading is more red, we are on a white tile (in Red mode for color sensor)
			// If reading is less red, we are on blue
			
			double max = maxProbability();
			cProvider.fetchSample(cSample, 0);						
			if (cSample[0] > 0.5) {

				// we have sensed white
				currentColor = false;				
				for (int i = 0; i < 37; i++) {
					if (currentColor == colorArray[i]) {
						// If we see same color as in color array, increase probability for all of seen color
						locationProbability[i] = locationProbability[i] * sensorWork;	
					} 
					else {
						// Else we know that we see a different color, so decrease
						locationProbability[i] = locationProbability[i] * (1 - sensorWork);				
					}
				}
		
			} else {

				// we have sensed blue
				currentColor = true;
				for (int i = 0; i < 37; i++) {
					if (currentColor == colorArray[i]) {
						locationProbability[i] = locationProbability[i] * sensorWork;		
					} 
					else {
						locationProbability[i] = locationProbability[i] * (1 - sensorWork);
					}		
				}

			}
	
			totalProbability = calcTotal();
			for (int i = 0; i < 37; i++) {
				locationProbability[i] = locationProbability[i] * (1 / totalProbability);
			}

			// Travel 1 block forward
			pilot.travel(1.74);
			
			// Shifting probabilities one movement forward
			for (int i = 36; i > 0; i--) {
				locationProbability[i] = locationProbability[i-1] * moveWork + locationProbability[i] * (1-moveWork);
			}
		}
	
		Sound.twoBeeps();
		return findIndex(maxProbability());
	}

	// Find max probability in array
	private static double maxProbability() {
		double max = 0;
		for (int i = 0; i < 37; i++) {
			if (locationProbability[i] > max) {
				max = locationProbability[i];
			}
		}
		return max;
	}
	
	// Find index at which max probability is at
	private static int findIndex(double max) {
		int index = 0;
		for (int i = 0; i < locationProbability.length; i++) {
			if (locationProbability[i] == max) {
				index = i;
			}
		}
		return index;
	}
	
	// Calculate the total probability of every element in the barcode array
	private static double calcTotal() {
		double total = 0;
		for (int i = 0; i < 37; i++) {
			total += locationProbability[i];
		}
		return total;
	}
	
}

	
// OLD CODE MIGHT USE LATER
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

// private static int calcManhat(int x1, int y1, int x2, int y2) {
// 	int xDiff = x2 - x1;
// 	int yDiff = y2 - y1;
// 	int dist = xDiff + yDiff;
// 	return dist;
// }
// public static void usingFileWriter() throws IOException
// {
//     String fileContent = locationProbability.toString();
	
//     FileWriter fileWriter = new FileWriter("C:\\Users\\KiemPC\\eclipse-workspace\\RGPsem2\\src\\RGPsem2\\array.txt");
//     fileWriter.write(fileContent);
//     fileWriter.close();
// }

// private static void beep()
// {
// 	Sound.beep();
// 	Delay.msDelay(2000);
// }

// private static void set_secondary_goals(boolean color)
// {	
	
	// add_goal(30, 52, 0);
	
	// if (color == false) {
	// 	add_goal(75, 84, 0);
	// 	add_goal(90, 72, 0);
	// 	add_goal(80, 62, 0);
	// 	add_goal(110, 32, 0);
	// } else {
	// 	add_goal(90, 102, 0);
	// 	add_goal(110, 82, 0);
	// 	add_goal(90, 62, 0);
	// 	add_goal(110, 32, 0);
	// }
	
// }

//POTENTIAL FIELD CONSTANTS
	// static final int kRep = 0;
	// static final int kAtt = 0;
	// static final int qRad = 0;
	
	// static final int obsPos = 1;
	// static int iter_pos = 0;
	

