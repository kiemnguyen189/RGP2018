package newLegoProject2;

import java.util.ArrayList;

public class Grid {
	private int scale;
	private Obj rob;
	private ArrayList<Obj> objs;
	
	public Grid(int scale, Obj rob)
	{
		this.scale = scale;
		this.rob = rob;
	}
	
	public void add_obj(Obj o) {
		if ((o.get_x() < scale && o.get_x() > 0) &&
			(o.get_y() < scale && o.get_y() > 0))
			objs.add(o);
	}
}
