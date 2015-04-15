package facsanadu.data;

import java.io.File;
import java.util.ArrayList;

/**
 * 
 * One data block - an FCS file can contain multiple but the standard encourages against
 * 
 * Note: should separate this from FCS parsing!
 * 
 * @author Johan Henriksson
 *
 */
public class Dataset
	{
	//public ArrayList<int[]> eventsInt=new ArrayList<int[]>();
	public ArrayList<double[]> eventsFloat=new ArrayList<double[]>();
	public ArrayList<ChannelInfo> ci=new ArrayList<ChannelInfo>();
	public File source;
	
	public ArrayList<LengthProfile> lengthprofsInfo=new ArrayList<LengthProfile>();
	public ArrayList<LengthProfileData> lengthprofsData=new ArrayList<LengthProfileData>();
	
	public ArrayList<ChannelInfo> getChannelInfo()
		{
		return ci;
		}

	
	public int getNumObservations()
		{
		return eventsFloat.size();
		}


	public int getNumChannels()
		{
		if(getNumObservations()>0)
			return eventsFloat.get(0).length;
		else
			return 0;
		}


	public int getNumLengthProfiles()
		{
		return lengthprofsInfo.size();
		}


	public String getName()
		{
		return source.getName();
		}


	public double getAsFloat(int obs, int indexChan)
		{
		return eventsFloat.get(obs)[indexChan];
		}


	public double[] getAsFloat(int obs)
		{
		return eventsFloat.get(obs);
		}
	}