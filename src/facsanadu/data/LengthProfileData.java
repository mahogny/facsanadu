package facsanadu.data;

/**
 * 
 * Length profile data for one event (=worm)
 * 
 * @author Johan Henriksson
 *
 */
public class LengthProfileData
	{
	public double[][] data; //[channel][levels]
	
	/**
	 * Cumulative sum: Always kept up to date, correct after loading
	 * [channel][levels]
	 */
	public double[][] cumsum; 

	
	/**
	 * Has this profile been flipped?
	 * This feature is used to normalize worms if they come with their tail first
	 */
	public boolean isFlipped=false;
	
	
	public int getLength()
		{
		if(data.length>0)
			return data[0].length;
		else
			return 0;
		}

	/**
	 * Calculate the cumulative sum
	 */
	public void calcCumsum()
		{
		int len=getLength();
		cumsum=new double[data.length][len];
		for(int i=0;i<data.length;i++)
			{
			double[] arr=data[i];
			double[] outarr=cumsum[i];
			double last=0;
			for(int j=0;j<arr.length;j++)
				{
				outarr[j]=last+arr[j];
				last=outarr[j];
				}
			}
		}
	
	public double[][] getFlipped()
		{
		double[][] n=new double[data.length][];//[data[0].length];
		for(int i=0;i<data.length;i++)
			{
			double[] from=data[i];
			double[] to=new double[from.length];
			for(int j=0;j<from.length;j++)
				to[j]=from[from.length-1-j];
			n[i]=to;
			}
		return n;
		}
	
	public void flip(double[][] newdata)
		{
		isFlipped=true;
		data=newdata;
		calcCumsum();
		}
	}
