package facsanadu.gates;

import facsanadu.data.Dataset;

/**
 * 
 * Calculation: Mean
 * 
 * @author Johan Henriksson
 *
 */
public class GateCalcMean extends GateCalcUnivariate
	{
	public double calc(Dataset ds, Gate g, GatingResult res)
		{
		IntArray arr=res.acceptedFromGate.get(g);
		
		double sum=0;
		for(int i=0;i<arr.size();i++)
			{
			int ind=arr.get(i);
			sum+=ds.getAsFloat(ind)[channelIndex];
			}
		return sum/arr.size();
		}
	
	
	
	}
