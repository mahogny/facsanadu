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

	//public double ix1, ix2, iy1, iy2;

	public void updateInternal()
		{
		//For performance: worth a rect test first?
		/*
		ix1=Math.min(x1, x2);
		iy1=Math.min(y1, y2);
		
		ix2=Math.max(x1, x2);
		iy2=Math.max(y1, y2);
		*/
		}
	
	
	public boolean classify(double[] obs)
		{
		double dx=(obs[indexX]-x)/rx;
		double dy=(obs[indexY]-y)/ry;
		return dx*dx+dy*dy<=1;
		}

	}
