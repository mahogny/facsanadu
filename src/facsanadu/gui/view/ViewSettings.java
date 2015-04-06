package facsanadu.gui.view;

import java.util.Collection;
import java.util.LinkedList;

import facsanadu.data.Dataset;
import facsanadu.gates.Gate;
import facsanadu.gates.GatingResult;
import facsanadu.gates.IntArray;

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


	/**
	 * Autoscale several views according to several datasets
	 */
	public static void autoscale(LinkedList<Dataset> selds, LinkedList<ViewSettings> selviews)
		{
		if(!selds.isEmpty())
			{
			double[] max=ViewSettings.getMaxForChannels(selds);
			double[] min=ViewSettings.getMinForChannels(selds);
			for(ViewSettings vs:selviews)
				vs.autoscale(max,min);
			}
		}



	public String getName()
		{
		//Use gate name as name of view
		return fromGate.name;
		}



	public Histogram computeHistogram(Dataset data, GatingResult gr)
		{
		Histogram h=new Histogram();
		h.setup(0, 1.0/scaleX, 50);
		
		IntArray accepted=gr.acceptedFromGate.get(fromGate);
		if(accepted!=null)
			for(int i=0;i<accepted.size();i++)
				{
				int ind=accepted.get(i);
				h.countEvent(data.getAsFloat(ind, indexX));
				}
		return h;
		}
	
	
	
	
	}
