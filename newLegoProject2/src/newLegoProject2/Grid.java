package RGPsem2;

import java.util.ArrayList;

public class Grid {

	private int scale;
	private Obj rob;
	private ArrayList<Obj> objs;
	
	public Grid(int scale, Obj rob)
	{
		objs = new ArrayList<Obj>();
		this.scale = scale;
		this.rob = rob;
	}
	
	public int get_scale()
	{
		return scale;
	}
	
	public boolean can_move(Obj o)
	{
		if ((o.get_move_x() <= scale) && (o.get_move_x() > -1) && (o.get_move_y() <= scale) && (o.get_move_y() > -1)) 
			return true;
		return false;
	}
	
	// Checks if the next move is safe to make (i.e doesn't collide with obstacle)
	private boolean collision()
	{
		for (Obj o: rob.get_obs()) {
			if (rob.intersect(o))
			    return true;
		}
		
		return false;
	}
	
	// Move every object on the grid if they can move
	public void take_turn() 
	{
		if (can_move(rob)){
			Obj goal = rob.get_goal();
			double ang = (rob.calc_angle(goal));
			if (ang != 0) {
				rob.set_rotated(true);
				rob.set_angle(ang);
			}
			rob.move();
		}
		else
			System.out.println("hit wall!");
		
		/*for (Obj o: objs)
			o.move();*/
	}
	
	public void add_obj(Obj o) {
		if ((o.get_x() < scale && o.get_x() > 0) &&
			(o.get_y() < scale && o.get_y() > 0))
			objs.add(o);
	}
}