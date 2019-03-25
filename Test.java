import java.util.ArrayList;

public class Test {
	public static void main(String[] args) {

		// Obj goal1 = new Obj(43, 43, 0, null);
		Obj goal1V2 = new Obj(40, 17, 0, null);
		Obj goal2V2 = new Obj(12, 42, 0, null);
		Obj goal2 = new Obj(20, 70, 0, null);
		Obj goal3 = new Obj(30, 52, 0, null);
		Obj goal4 = new Obj(90, 102, 0, null);
		Obj goal5 = new Obj(110, 82, 0, null);
		Obj goal6 = new Obj(90, 62, 0, null);
		Obj goal7 = new Obj(110, 32, 0, null);
		Obj rob = new Obj(76, 43, 1, null);

		ArrayList<Obj> goals = new ArrayList<Obj>();

		// goals.add(goal1);
		goals.add(goal1V2);
		goals.add(goal2V2);
		goals.add(goal2);
		goals.add(goal3);
		goals.add(goal4);
		goals.add(goal5);
		goals.add(goal6);
		goals.add(goal7);
		
		rob.set_speed(7);
		rob.set_angle(135);

		rob.set_goal(goals.get(0));
		Grid grid = new Grid(122, rob);
		
		Obj obs = new Obj(0, 3, 0, null);
		rob.add_obs(obs);
		grid.add_obj(obs);
		
		while (!rob.at_goal() && grid.can_move(rob)) {
			System.out.println(rob.toString());
			// System.out.println(rob.distance());
			// System.out.println("angle: " + rob.get_angle());
			System.out.println();
			grid.take_turn();

			if (rob.at_goal()) {
				System.out.println("REACHED GOAL");
				if (goals.size() > 1) {
					goals.remove(0);
					rob.set_goal(goals.get(0));
				} else {
					goals.remove(0);
					// Sound.beepSequence();
					return;
				}
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		
		if (rob.at_goal())
		    System.out.println("rob at goal!");
		else
			System.out.println("the future, remains the same!");

	}
}