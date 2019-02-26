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
	
	private boolean can_move(Obj o)
	{
		if (o.get_x() < scale && o .get_y() < scale)
			return true;
		return false;
	}
	
	private boolean collision()
	{
		for (Obj o: rob.get_obs()) {
			if (rob.get_x() != o.get_x()) {
				System.out.println("hit obs!");
				return true;
			}
		}
		return false;
	}
	
	public void take_turn() 
	{
		if (can_move(rob) && !collision())
			rob.move(1);
		else
			System.out.println("hit wall!");
		
		for (Obj o: objs)
			o.move(0);
	}
	
	public void add_obj(Obj o) {
		if ((o.get_x() < scale && o.get_x() > 0) &&
			(o.get_y() < scale && o.get_y() > 0))
			objs.add(o);
//			System.out.println(o.toString());
	}
}
