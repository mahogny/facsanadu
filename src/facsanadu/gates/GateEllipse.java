package facsanadu.gates;

/**
 * 
 * Rectangular gate
 * 
 * @author Johan Henriksson
 *
 */
public class GateEllipse extends Gate
	{
	public int indexX, indexY;

	public double x, y;
	public double rx, ry;

	public void updateInternal()
		{
		setUpdated();
		}
	
	
	public boolean classify(double[] obs)
		{
		double dx=(obs[indexX]-x)/rx;
		double dy=(obs[indexY]-y)/ry;
		return dx*dx+dy*dy<=1;
		}



	}
