package facsanadu.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;

import facsanadu.gui.FacsanaduProject;

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
	public ArrayList<double[]> eventsFloat=new ArrayList<double[]>();
	public ArrayList<double[]> eventsFloatCompensated=new ArrayList<double[]>();
	private int numChannel=0;
	private int numPc=0;
	public int numCompensated=0;
	
	public ArrayList<ChannelInfo> channelInfo=new ArrayList<ChannelInfo>();
	public File source;
	
	public ArrayList<LengthProfile> lengthprofsInfo=new ArrayList<LengthProfile>();
	public ArrayList<LengthProfileData> lengthprofsData=new ArrayList<LengthProfileData>();
	public TreeMap<String, String> metaKeyName=new TreeMap<String, String>();

	
	public ArrayList<ChannelInfo> getChannelInfo()
		{
		return channelInfo;
		}

	
	public int getNumObservations()
		{
		return eventsFloat.size();
		}


	public int getNumChannels()
		{
		return numCompensated;
//		return numChannel+numPc;
		}


	public int getNumLengthProfiles()
		{
		return lengthprofsInfo.size();
		}


	public String getName()
		{
		return source.getName();
		}


	public double getAsFloatCompensated(int obs, int indexChan)
		{
		return eventsFloatCompensated.get(obs)[indexChan];
		//return eventsFloat.get(obs)[indexChan];
		}


	public double[] getAsFloatCompensated(int obs)
		{
		return eventsFloatCompensated.get(obs);
//		return eventsFloat.get(obs);
		}
	
	/**
	 * Compute profile channels. or only one if not null
	 */
	public void computeProfChannel(FacsanaduProject proj, ProfChannel forPc)
		{
		//If deleting or adding a channel... I find it fine enough to recompute everything. But this should
		//not be done if just modifying a gate
		HashSet<ProfChannel> oldPc=new HashSet<ProfChannel>();
		for(ChannelInfo i:channelInfo)
			if(i.pc!=null)
				oldPc.add(i.pc);
		if(!oldPc.equals(new HashSet<ProfChannel>(proj.profchan)))
			{
			//System.out.println("Recomp all chan");
			
			//Recompute all channels
			for(ChannelInfo i:new ArrayList<ChannelInfo>(channelInfo))
				if(i.pc!=null)
					channelInfo.remove(i);
			for(ProfChannel pc:proj.profchan)
				{
				ChannelInfo i=new ChannelInfo();
				i.label=pc.getName();
				i.pc=pc;
				channelInfo.add(i);
				}
			//Add new channels
			numPc=proj.profchan.size();
			resizeEvents(numChannel+numPc);
			System.out.println("number of PC: "+numPc+" --------------------------------");
			for(int i=0;i<numPc;i++)
				{
				int toi=numChannel+i;
				ChannelInfo ci=channelInfo.get(toi);
				for(int j=0;j<getNumObservations();j++)
					{
					double[] d=eventsFloat.get(j);
					d[toi]=ci.pc.calc(lengthprofsData.get(j));
					}
				}
			}
		else if(forPc!=null)
			{
			//Update only one channel
			ChannelInfo ci=getChannelInfoForProf(forPc);
			int toi=channelInfo.indexOf(ci);
			for(int j=0;j<getNumObservations();j++)
				{
				double[] d=eventsFloat.get(j);
				d[toi]=forPc.calc(lengthprofsData.get(j));
				}
			}
		else
			{
			System.out.println("hmmm. prof chan nothing to do; "+proj.profchan);
			}
		}

	public ChannelInfo getChannelInfoForProf(ProfChannel pc)
		{
		for(ChannelInfo ci:channelInfo)
			if(ci.pc==pc)
				return ci;
		throw new RuntimeException("No channel info for prof channel");
		}

	public void setEvents(ArrayList<double[]> e)
		{
		eventsFloat=e;
		numChannel=0;
		if(getNumObservations()>0)
			numChannel=eventsFloat.get(0).length;
		}

	/**
	 * Resize the events. Used to make space for virtual channels
	 */
	private void resizeEvents(int newsize)
		{
		ArrayList<double[]> newEventsFloat=new ArrayList<double[]>(eventsFloat.size());
		for(double[] o:eventsFloat)
			{
			double[] n=new double[newsize];
			System.arraycopy(o, 0, n, 0, numChannel);
			newEventsFloat.add(n);
			}
		eventsFloat=newEventsFloat;
		}
	
	}

