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
	public double[][] cumsum;

	public int getLength()
		{
		if(data.length>0)
			return data[0].length;
		else
			return 0;
		}

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
	}
