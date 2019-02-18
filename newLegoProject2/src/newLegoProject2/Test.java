package newLegoProject2;

public class Test {
	public static void main(String[] args) {
		Obj rob = new Obj(0, 0, 1, null);
		Grid grid = new Grid(12, rob);
		System.out.println(rob.toString());
	}
}
