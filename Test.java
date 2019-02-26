package RGPsem2;

public class Test {
	public static void main(String[] args) {
		Obj goal = new Obj(10, 30, 0, null);
		Obj rob = new Obj(120, 120, 1, goal);
		Grid grid = new Grid(122, rob);
		System.out.println(rob.toString());
		
		Obj obs = new Obj(2, 3, 0, null);
		grid.add_obj(obs);
		
		for (int i =  0; i < grid.get_scale(); ++i) {
			grid.take_turn();		
			System.out.println(rob.toString());
		}
	}
}