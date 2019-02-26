package newLegoProject2;

import java.util.ArrayList;

public class Obj {
	private boolean at_goal;
	private int x;
	private int y;
	private int angle;
	private int speed;
	
	private int radius;
	private Obj goal; 
	private ArrayList<Obj> obs;
	
	public Obj(int x, int y, int speed, Obj goal) {
		this.at_goal = false;
		this.x = x;
		this.y = y;
		this.speed = speed;
		this.angle = 0;
		this.goal = goal;
		this.obs = new ArrayList<>();
	}
	
	void move(int angle) {
		if  (!at_goal) {
			angle = angle % 360;
			int dx = this.x += speed;
			int dy = this.y += speed;
			
			int ang = angle - this.angle;		
			
			this.x += speed;
			/* TODO */
			this.y +=  speed * (ang / 100);
			this.angle = angle;
		}
		
		int gx = goal.get_x();
		int gy = goal.get_y();
		
		if (x == gx && y == gy)
			at_goal = true;
	}
	
	public boolean at_goal()
	{
		return this.at_goal;
	}
	
	public int get_angle()
	{
		return this.angle;
	}
	
	void set_goal(Obj goal) {
		this.goal = goal;
	}
	
	void add_obs(Obj o) {
		obs.add(o);
	}
	
	public ArrayList<Obj> get_objs()
	{
		return this.obs;
	}
	
	int get_x() {
		return x;
	}
	
	int get_y() {
		return y;
	}
	
	public String toString() {
		return "x: " + x + "\n" +
			   "y: " + y + "\n" +
			   "speed: " + speed;
	}
	
	
}
