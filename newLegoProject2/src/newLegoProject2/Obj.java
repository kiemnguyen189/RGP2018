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
	private double distance;
	
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
		this.distance = 0;
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
	
	double calc_angle(Obj o)
	{
		int dx = Math.abs(get_x() - o.get_x());
	    int dy = Math.abs(get_y() - o.get_y());
	    
	    return Math.atan(dy/dx);
	}
	
	private void calc_distance(int nx, int ny)
	{
		int dx = Math.abs(get_x() - nx);
	    int dy = Math.abs(get_y() - ny);
	    double range = Math.sqrt((dx * dx) + (dy * dy));
	    this.distance = range;
	}
	
	double get_distance()
	{
		return this.distance;
	}
	
	double get_object_distance(Obj o)
	{
		int dx = Math.abs(get_x() - o.get_x());
	    int dy = Math.abs(get_y() - o.get_y());
	    return Math.sqrt((dx * dx) + (dy * dy));
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
			int dx = (gx < x) ? (s * -1): s;
			int dy = (gy < y) ? (s * -1): s;
			
			int nx = this.x + dx * (this.angle / ANGLE);
			int ny = this.y + dy;
			
			calc_distance(nx, ny);
			this.x = nx;
			this.y = ny;
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
	
	Obj get_goal()
	{
		return this.goal;
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
