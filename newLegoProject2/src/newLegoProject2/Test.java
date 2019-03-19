package newLegoProject2;

import lejos.utility.Delay;

public class Test {
	public static void main(String[] args) {

		Obj goal = new Obj(41, 42, 0, null);
		Obj rob = new Obj(76, 42, 1, goal);
		rob.set_speed(7);
		rob.set_angle(135);
//		rob.set_goal(get_goal());
		rob.set_goal(goal);
		Grid grid = new Grid(122, rob);
		
		Obj obs = new Obj(0, 3, 0, null);
		rob.add_obs(obs);
		grid.add_obj(obs);
		
		while (!rob.at_goal() && grid.can_move(rob)) {
			System.out.println(rob.toString());
//			System.out.println(rob.distance());
			System.out.println("angle: " + rob.get_angle());
			System.out.println();
			grid.take_turn();
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//setpath();
			//Delay.msDelay(500);
			
			if (rob.at_goal()){
				//rob.set_goal(get_goal());
			    System.out.println("rob at goal!");
			}
			else
				System.out.println("the future, remains the same!");
		}
		
//		if (rob.at_goal())
//		    System.out.println("rob at goal!");
//		else
//			System.out.println("the future, remains the same!");
		
		//Delay.msDelay(5000);

	}
	
}