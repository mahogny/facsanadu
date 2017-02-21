package facsanadu.data;

/**
 * 
 * One profile channel
 * 
 * @author Johan Henriksson
 *
 */
public class ProfChannel
	{
	public int channel;
	public int from=50;
	public int to=100;

	public boolean forNormalized=true;
	//TODO boolean, on normalized or not?
	
	public String getName()
		{
		return "profc"+channel+" "+from+"-"+to;
		}

	public double calc(LengthProfileData data)
		{
		int len=data.getLength();
		if(forNormalized)
			{
			int nfrom=from*data.getLength()/1050;
			int nto=to*data.getLength()/1050;
			return (data.cumsum[channel][nto]-data.cumsum[channel][nfrom])/(double)(nto-nfrom);		
			}		
		else
			{
			//Danger! might be out of range. Need to adjust
			int sfrom=from;
			int sto=to;
			if(sfrom>=len) sfrom=len-1;
			if(sto>=len) sto=len-1;
			return (data.cumsum[channel][sto]-data.cumsum[channel][sfrom])/(to-from);		
			}
		}
	}
