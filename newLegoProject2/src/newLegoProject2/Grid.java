package newLegoProject2;

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
		if (o.get_move_x() <= scale && o.get_move_y() <= scale)
			return true;
		return false;
	}
	
	private boolean collision()
	{
		for (Obj o: rob.get_obs()) {
			if (rob.intersect(o))
			    return true;
		}
		
		return false;
	}
	
	public void take_turn() 
	{
		if (can_move(rob))
			rob.move();
		else
			System.out.println("hit wall!");
		
		for (Obj o: objs)
			o.move();
	}
	
	public void add_obj(Obj o) {
		if ((o.get_x() < scale && o.get_x() > 0) &&
			(o.get_y() < scale && o.get_y() > 0))
			objs.add(o);
	}
}
