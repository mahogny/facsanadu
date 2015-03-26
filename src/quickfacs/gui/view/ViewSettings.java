package quickfacs.gui.view;

import java.util.Collection;

import quickfacs.data.Dataset;
import quickfacs.gates.Gate;

/**
 * 
 * Settings for a view
 * 
 * @author Johan Henriksson
 *
 */
public class ViewSettings
	{
	public Gate fromGate;
	
	public int indexX=7;
	public int indexY=6;
	
	//FCS space * scale = screen space (almost)
	public double scaleX=1;
	public double scaleY=1;
	

	public void autoscale(double[] max, double[] min)
		{
		double maxx=max[indexX];
		double maxy=max[indexY];
		scaleX=1.0/maxx;
		scaleY=1.0/maxy;
		} 


	
	public static double[] getMaxForChannel(Dataset dataset)
		{
		double max[]=new double[dataset.getNumChannels()];
		for(int i=0;i<max.length;i++)
			max[i]=-Double.MAX_VALUE;
		for(int i=0;i<dataset.eventsFloat.size();i++)
			for(int j=0;j<max.length;j++)
					max[j]=Math.max(max[j],dataset.eventsFloat.get(i)[j]);
		return max;
		}


	public static double[] getMinForChannel(Dataset dataset)
		{
		double val[]=new double[dataset.getNumChannels()];
		for(int i=0;i<val.length;i++)
			val[i]=Double.MAX_VALUE;
		for(int i=0;i<dataset.eventsFloat.size();i++)
			for(int j=0;j<val.length;j++)
					val[j]=Math.min(val[j],dataset.eventsFloat.get(i)[j]);
		return val;
		}

	public static double[] getMaxForChannels(Collection<Dataset> dataset)
		{
		if(dataset.size()==0)
			return new double[0];
		else
			{
			int numchan=dataset.iterator().next().getNumChannels();
			
			double val[]=new double[numchan];
			for(int i=0;i<val.length;i++)
				val[i]=-Double.MAX_VALUE;
			for(Dataset ds:dataset)
				{
				double[] cmax=getMaxForChannel(ds);
				for(int j=0;j<numchan;j++)
					val[j]=Math.max(val[j],cmax[j]);
				}
			return val;
			}
		}
	
	
	public static double[] getMinForChannels(Collection<Dataset> dataset)
		{
		if(dataset.size()==0)
			return new double[0];
		else
			{
			int numchan=dataset.iterator().next().getNumChannels();
			
			double val[]=new double[numchan];
			for(int i=0;i<val.length;i++)
				val[i]=Double.MAX_VALUE;
			for(Dataset ds:dataset)
				{
				double[] cmax=getMinForChannel(ds);
				for(int j=0;j<numchan;j++)
					val[j]=Math.min(val[j],cmax[j]);
				}
			return val;
			}
		}
	
	
	
	
	}
