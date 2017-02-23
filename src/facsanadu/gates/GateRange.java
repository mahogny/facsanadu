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
	public int index;

	public double x1, x2;
	
	public double ix1, ix2;

	public void updateInternal()
		{
		ix1=Math.min(x1, x2);
		ix2=Math.max(x1, x2);
		setUpdated();
		}
	
	public boolean classify(double[] obs)
		{
		return
				obs[index]>=ix1 && obs[index]<=ix2;
				
		}

	}
