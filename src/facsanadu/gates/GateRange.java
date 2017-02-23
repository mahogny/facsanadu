package facsanadu.gates;

/**
 * 
 * Range gate, mainly for histograms
 * 
 * @author Johan Henriksson
 *
 */
public class GateRange extends Gate
	{
	public int indexX;

	public double x1, x2;
	
	public double ix1, ix2;

	public void updateInternal()
		{
		ix1=Math.min(x1, x2);
		ix2=Math.max(x1, x2);
		lastModified=System.currentTimeMillis();
		}
	
	public boolean classify(double[] obs)
		{
		return
				obs[indexX]>=ix1 && obs[indexX]<=ix2;
				
		}

	}
