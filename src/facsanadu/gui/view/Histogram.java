package facsanadu.gui.view;

/**
 * 
 * One computed histogram
 * 
 * @author Johan Henriksson
 *
 */
public class Histogram
	{
	private double from, to;
	private int numbin;
	private int[] bins;
	private int eventcount;
	
	
	public void setup(double from, double to, int numbin)
		{
		this.from=from;
		this.to=to;
		this.numbin=numbin;
		bins=new int[numbin];
		}
	
	
	public void countEvent(double x)
		{
		double bin=numbin*(x-from)/(to-from);		
		if(bin>=numbin)
			{
			bin=numbin-1;
			}
		else if(bin<0)
			bin=0;
		bins[(int)bin]++;
		eventcount++;
		}
	
	public double getFrom()
		{
		return from;
		}
	public double getTo()
		{
		return to;
		}
	
	public int getCount(int index)
		{
		return bins[index];
		}
	public double getFrac(int index)
		{
		return getCount(index)/(double)eventcount;
		}
	
	public int getNumBins()
		{
		return numbin;
		}
	
	}
