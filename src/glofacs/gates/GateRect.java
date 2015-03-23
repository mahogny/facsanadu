package glofacs.gates;

/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class GateRect extends Gate
	{
	public int indexX, indexY;

	public double x1, x2, y1, y2;

	public boolean classify(double[] obs)
		{
		return true;
		}

	}
