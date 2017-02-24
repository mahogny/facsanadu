package facsanadu.gates.measure;

import facsanadu.data.Dataset;
import facsanadu.gates.Gate;
import facsanadu.gates.GatingResult;
import facsanadu.gates.IntArray;
import facsanadu.gui.FacsanaduProject;

/**
 * 
 * Calculation: Mean
 * 
 * @author Johan Henriksson
 *
 */
public class GateMeasureMean extends GateMeasureUnivariate
	{
	public double calc(Dataset ds, Gate g, GatingResult res)
		{
		IntArray arr=res.getAcceptedFromGate(g);
		
		double sum=0;
		for(int i=0;i<arr.size();i++)
			{
			int ind=arr.get(i);
			sum+=ds.getAsFloatCompensated(ind)[channelIndex];
			}
		return sum/arr.size();
		}

	@Override
	public String getDesc(FacsanaduProject proj)
		{
		return "Mean: "+proj.getChannelInfo().get(channelIndex).formatName();
		}
	
	
	
	}
