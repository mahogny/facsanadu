package facsanadu.gates;

/**
 * 
 * Root gate (passes everything)
 * 
 * @author Johan Henriksson
 *
 */
public class GateRoot extends Gate
	{
	public boolean classify(double[] obs)
		{
		return true;
		}
	
	public void updateInternal()
		{
		setUpdated();
		}


	}
