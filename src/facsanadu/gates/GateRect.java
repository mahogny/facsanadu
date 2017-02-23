package facsanadu.gates;

/**
 * 
 * Rectangular gate
 * 
 * @author Johan Henriksson
 *
 */
public class GateRect extends Gate
	{
	public int indexX, indexY;

	public double x1, x2, y1, y2;

	public double ix1, ix2, iy1, iy2;

	public void updateInternal()
		{
		ix1=Math.min(x1, x2);
		iy1=Math.min(y1, y2);
		
		ix2=Math.max(x1, x2);
		iy2=Math.max(y1, y2);
		setUpdated();
		}
	
	public boolean classify(double[] obs)
		{
		return
				obs[indexX]>=ix1 && obs[indexX]<=ix2 &&
				obs[indexY]>=iy1 && obs[indexY]<=iy2;
		}

	}
