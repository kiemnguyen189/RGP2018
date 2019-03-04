package newLegoProject2;

import java.util.ArrayList;

public class Obj {

    /* angle relative to direction */
    private static final int ANGLE = 90;
    
    /* if object has reached goal */
	private boolean at_goal;
	/* cords of the object on the grid */
	private int x;
	private int y;
	private int angle;
	private int speed;
	
	/* the radius or zone of the object */
	private int radius;
	private Obj goal;
	/* list of obstacles to be avoided */
	private ArrayList<Obj> obs;
	
	public Obj(int x, int y, int radius, Obj goal)
	{
		this.at_goal = false;
		this.x = x;
		this.y = y;
		this.speed = 0;
		this.angle = 0;
		this.radius = radius;
		this.goal = goal;
	}
	
	int get_radius()
	{
	    return this.radius;
	}
	
	int get_angle()
	{
	    return this.angle;
	}
	
	void set_angle(int angle)
	{   
	    if (angle > ANGLE || angle < -ANGLE)
			    angle = angle >= 0 ?
			        angle % ANGLE: angle % -ANGLE;
			        
	    this.angle = angle;
	}
	
	boolean intersect(Obj o)
	{
	    int dx = Math.abs(o.get_x() - get_move_x());
	    int dy = Math.abs(o.get_y() - get_move_y());
	    double range = Math.sqrt((dx * dx) + (dy * dy));
	    
	    return range <= o.get_radius() ? true: false;
	}
	
	int get_move_x()
	{
	    if (goal == null)
	        return 0;
	    
	    int gx = goal.get_x();
	    int s = this.speed;
	    int dx = (gx < s) ? (s * -1): s;
	    return this.x + dx * (this.angle / ANGLE);
	}
	
	int get_move_y()
	{
	    if (goal == null)
	        return 0;
	    
	    int gy = goal.get_y();
	    int s = this.speed;
	    int dy = (gy < s) ? (s * -1): s;
	    return this.y + dy;
	}
	
	void move()
	{
		if (goal != null && !at_goal) {
		    int gx = goal.get_x();
		    int gy = goal.get_y();
		
		    if (x == gx && y == gy){
			    at_goal = true;
			    return;
			}
			
			int s = this.speed;
			int dx = (gx < s) ? (s * -1): s;
			int dy = (gy < s) ? (s * -1): s;
			
			this.x += dx * (this.angle / ANGLE);
			this.y += dy;
		}
	}
	
	int get_speed()
	{
	    return this.speed;
	}
	
	void set_speed(int speed)
	{
	    this.speed = Math.abs(speed);
	}
	
	boolean at_goal()
	{
		return this.at_goal;
	}
	
	void set_goal(Obj goal) {
		this.goal = goal;
		this.at_goal = false;
	}
	
	void add_obs(Obj o)
	{
	    if (this.obs == null)
	        this.obs = new ArrayList<>();
		this.obs.add(o);
	}
	
	public ArrayList<Obj> get_obs()
	{
		return this.obs;
	}
	
	int get_x()
	{
		return this.x;
	}
	
	int get_y()
	{
		return this.y;
	}
	
	public String distance()
	{
	    if (goal == null)
	        return "(no goal)";
	    
	    int dx = Math.abs(goal.get_x() - this.x);
	    int dy = Math.abs(goal.get_y() - this.y);
	    return "distance x: " + dx + "\n" +
	           "distance y: " + dy;
	}
	
	public String toString()
	{
		return "x: " + x + "\n" +
			   "y: " + y + "\n" +
			   "speed: " + speed;
	}
	
	
}
