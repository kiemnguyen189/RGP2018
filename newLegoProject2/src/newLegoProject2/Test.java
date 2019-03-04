package newLegoProject2;

public class Test {

	public static void main(String[] args) {
	    Obj goal = new Obj(10, 10, 0, null);
		Obj rob = new Obj(12, 0, 1, goal);
		rob.set_speed(1);
		rob.set_angle(0);
		Grid grid = new Grid(12, rob);
		
		Obj obs = new Obj(0, 3, 0, null);
		rob.add_obs(obs);
		grid.add_obj(obs);
		
		while (!rob.at_goal() && grid.can_move(rob)) {
			System.out.println(rob.toString());
			System.out.println(rob.distance());
			System.out.println();
			grid.take_turn();
		}
		
		if (rob.at_goal())
		    System.out.println("rob at goal!");
	}
	
}
