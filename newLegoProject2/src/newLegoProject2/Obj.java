package newLegoProject2;

import java.util.ArrayList;

public class Obj {
	private int x;
	private int y;
	private int angle;
	private int speed;
	
	private int radius;
	private Obj goal;
	private ArrayList<Obj> obs;
	
	public Obj(int x, int y, int speed, Obj goal) {
		this.x = x;
		this.y = y;
		this.speed = speed;
		this.goal = goal;
		this.obs = new ArrayList<>();
	}
	
	void move(int angle) {
		
	}
	
	void set_goal(Obj goal) {
		this.goal = goal;
	}
	
	void add_obs(Obj o) {
		obs.add(o);
	}
	
	int get_x() {
		return x;
	}
	
	int get_y() {
		return y;
	}
	
	public String toString() {
		return "x: " + x + "\n" +
			   "y: " + y + "\n";
	}
	
	
}
