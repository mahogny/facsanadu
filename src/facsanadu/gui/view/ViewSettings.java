package facsanadu.gui.view;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import facsanadu.data.Dataset;
import facsanadu.gates.Gate;
import facsanadu.gates.GatingResult;
import facsanadu.gates.IntArray;
import facsanadu.transformations.TransformationStack;

/**
 * 
 * Settings for a view
 * 
 * @author Johan Henriksson
 *
 */
public class ViewSettings
	{
	//Which gate the view should pick data from
	public Gate gate;
	
	public int indexX=7;
	public int indexY=6;
	
	//FCS space * scale = screen space (almost)
	public double scaleX=1;
	public double scaleY=1;
	
	public double zoomX=1; //For speed, this is integrated into the scale whenever needed
	public double zoomY=1;

	public TransformationStack transformation=new TransformationStack();

	public int numHistBins=50;
	
	/**
	 * Set the scale to cover the given max and min values
	 */
	public void autoscale(double[] max, double[] min)
		{
		//Currently min values are not used
		double maxx=max[indexX];
		double maxy=max[indexY];
		scaleX=1.0/maxx*zoomX;
		scaleY=1.0/maxy*zoomY;
		} 


	/**
	 * Get the maximum for a channel
	 */
	public double[] getMaxForChannel(Dataset dataset)
		{
		double val[]=new double[dataset.getNumChannels()];
		for(int i=0;i<val.length;i++)
			val[i]=-Double.MAX_VALUE;
		for(int i=0;i<dataset.getNumObservations();i++)
			for(int j=0;j<val.length;j++)
					val[j]=Math.max(val[j],dataset.getAsFloatCompensated(i,j));
		val=transformation.perform(val);
		return val;
		}

	/**
	 * Get the minimum value for channel
	 */
	public double[] getMinForChannel(Dataset dataset)
		{
		double val[]=new double[dataset.getNumChannels()];
		for(int i=0;i<val.length;i++)
			val[i]=Double.MAX_VALUE;
		for(int i=0;i<dataset.getNumObservations();i++)
			for(int j=0;j<val.length;j++)
					val[j]=Math.min(val[j],dataset.getAsFloatCompensated(i,j));
		val=transformation.perform(val); //this is kind of cheating
		return val;
		}

	/**
	 * Get the maximum value for all channels
	 */
	public double[] getMaxForChannels(Collection<Dataset> dataset)
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
	
	/**
	 * Get the minimum value for all channels
	 */
	public double[] getMinForChannels(Collection<Dataset> dataset)
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
			for(ViewSettings vs:selviews)
				{
				//Might be possible to optimize this if there are many different kind of views
				double[] max=vs.getMaxForChannels(selds);
				double[] min=vs.getMinForChannels(selds);
				vs.autoscale(max,min);
				}
			}
		}


	/**
	 * Get the name of this view
	 */
	public String getName()
		{
		//Use gate name as name of view
		return gate.name;
		}


	/**
	 * Compute histogram from data
	 */
	public Histogram computeHistogram(Dataset data, GatingResult gr)
		{
		Histogram h=new Histogram();
		h.setup(0, 1.0/scaleX, numHistBins);
		IntArray accepted=gr.getAcceptedFromGate(gate);
		if(accepted!=null)
			for(int i=0;i<accepted.size();i++)
				{
				int ind=accepted.get(i);
				double x=transformation.perform(data.getAsFloatCompensated(ind, indexX), indexX);
				h.countEvent(x);
				}
		return h;
		}


	/**
	 * Check if the view settings is for a histogram
	 */
	public boolean isHistogram()
		{
		return indexX==indexY;
		}


	public void setHistogram(int chanid)
		{
		indexX=indexY=chanid;
		}


	public boolean coversXandY(int indexX2, int indexY2)
		{
		HashSet<Integer> ind=new HashSet<Integer>();
		ind.add(indexX);
		ind.add(indexY);
		return ind.contains(indexX2) && ind.contains(indexY2);
		}

	public boolean coversX(int indexX2)
		{
		HashSet<Integer> ind=new HashSet<Integer>();
		ind.add(indexX);
		ind.add(indexY);
		return ind.contains(indexX2);
		}


	public void swapAxis()
		{
		int axis=indexX;
		indexX=indexY;
		indexY=axis;
		
		double scale=scaleX;
		scaleX=scaleY;
		scaleY=scale;
		
		double zoom=zoomX;
		zoomX=zoomY;
		zoomY=zoom;
		}
	
	
	
	
	}
