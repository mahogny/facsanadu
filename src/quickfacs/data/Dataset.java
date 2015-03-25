package quickfacs.data;

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
	public ArrayList<int[]> eventsInt;
	public ArrayList<double[]> eventsFloat;
	public ArrayList<ChannelInfo> ci=new ArrayList<ChannelInfo>();
	public File source;
	
	
	public ArrayList<ChannelInfo> getChannelInfo()
		{
		return ci;
		}

	
	public int getNumObservations()
		{
		return eventsFloat.size();
		}
	}