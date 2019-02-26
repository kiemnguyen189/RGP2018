package newLegoProject2;

public class Test {
	public static void main(String[] args) {
		Obj rob = new Obj(0, 0, 1, null);
		Grid grid = new Grid(12, rob);
		System.out.println(rob.toString());
		
		Obj obs = new Obj(2, 3, 0, null);
		grid.add_obj(obs);
		
		for (int i =  0; i < grid.get_scale(); ++i) {
			grid.take_turn();		
			System.out.println(rob.toString());
		}
	}
}
