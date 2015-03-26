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
	
	/*
	public void autoscale(Dataset segment)
		{
		double maxx=getMaxForChannel(segment,indexX);
		double maxy=getMaxForChannel(segment,indexY);
		scaleX=1.0/maxx;
		scaleY=1.0/maxy;
		} */

	public void autoscale(double[] max)
		{
		double maxx=max[indexX];
		double maxy=max[indexY];
		scaleX=1.0/maxx;
		scaleY=1.0/maxy;
		} 


	public static double getMaxForChannel(Dataset segment, int chanid)
		{
		double max=-Double.MAX_VALUE;
		for(int i=0;i<segment.eventsFloat.size();i++)
			max=Math.max(max,segment.eventsFloat.get(i)[chanid]);
		return max;
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

	
	public static double[] getMaxForChannels(Collection<Dataset> dataset)
		{
		if(dataset.size()==0)
			return new double[0];
		else
			{
			int numchan=dataset.iterator().next().getNumChannels();
			
			double max[]=new double[numchan];
			for(int i=0;i<max.length;i++)
				max[i]=-Double.MAX_VALUE;
			for(Dataset ds:dataset)
				{
				double[] cmax=getMaxForChannel(ds);
				for(int j=0;j<numchan;j++)
					max[j]=Math.max(max[j],cmax[j]);
				}
			return max;
			}
		}
	
	
	}
