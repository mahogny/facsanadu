package facsanadu.gui.view.gate;

import facsanadu.gui.MainWindow;

/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public interface GateHandle
	{
	public double getX();
	public double getY();
	public void move2(MainWindow w, double nx, double ny);
	}
