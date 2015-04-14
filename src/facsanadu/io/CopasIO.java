package facsanadu.io;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import facsanadu.data.ChannelInfo;
import facsanadu.data.Dataset;
import facsanadu.data.LengthProfile;
import facsanadu.data.LengthProfileData;





/**
 * The Union Bio COPAS stores data in a non-standard way.
 * 
 * There is an .lmd-file which follows the FCS format but it only contains a subset of the information - there is more in the .txt-file.
 * 
 * There is optionally a .dat-file with profiles. The exported _profile-file has a subset of this information.
 * 
 * @author Johan Henriksson
 *
 */
public class CopasIO
	{
	//NOTE: should probably split this dataset by well!!!
/*
	private static int maxlen=4200;
	
	public static List<String> profchans(int numchan)
		{
		LinkedList<String> list=new LinkedList<String>();
		list.add("ProfLength");
		for(int j=0;j<numchan;j++)
			for(int i=0;i<maxlen;i++)
				list.add("Prof"+j+"_"+i);
		return list;
		}*/
	
	public static List<String> chans=Arrays.asList("TOF","EXT","Green","Yellow","Red", 
			"PH Ext", "PW Ext", "PC Ext", 
			"PH Green, PW Green", "PCGreen",
			"PH Yellow, PW Yellow", "PCYellow",
			"PH Red, PW Red", "Red"
			);

	public static class CopasEvent
		{
		public int id;
		public int plate;
		public String row;
		public int col;
		public boolean clog;
		public int scanrate;
		public int sortStatus;
		public int statusSelect;
		/*
		public int tof;
		public int ext;
		public Integer chanGreen;
		public Integer chanYellow;
		public Integer chanRed;
		*/
		public int timestamp;
		
		//TODO. Support double? fcs does. make it an array to save space
		public Map<String,Integer> prop=new HashMap<String, Integer>(); 
		
		
		
		
		//// There are a lot more columns here!!!!
		
		public double[][] levels; //[chan][pos]
		public int wormLength; //Original length, not modified by normalization
		
		
		
		
		//Maybe add another flag?
		
		public int getBasicSortStatus()
			{
			if(sortStatus>30)
				return sortStatus-30;
			else
				return sortStatus;
			}
		
		public boolean isCloseToPrevious()
			{
			return sortStatus>=30;
			}
		
		public final static int SORTSTATUS_NODISPENSE=0;
		public final static int SORTSTATUS_UNKNOWN=1;
		public final static int SORTSTATUS_OUTOFREGION=2;
		public final static int SORTSTATUS_COINCIDENCEWITHFLOWNOTSORTABLEEVENT3=3;
		public final static int SORTSTATUS_COICIDENCEINPUREMODE=4;
		public final static int SORTSTATUS_LOSTSYNCH=5;
		public final static int SORTSTATUS_DISPENSED=6;
		public final static int SORTSTATUS_DISPENSEDINSUPERDROP=7;
		public final static int SORTSTATUS_COINCIDENCEWITHPREVNOTSORTABLEEVENT=8;
		public final static int SORTSTATUS_SORTINGTOOSLOW=9;
		
		public final static int SELECTSTATUS_OUTSIDETOF=0;
		public final static int SELECTSTATUS_OUTSIDEGATING=1;
		public final static int SELECTSTATUS_OUTSIDESORTING=2;
		//3-10 conditions, not included
		public final static int SELECTSTATUS_INCLUDED=40;
		
		
		
		public CopasEvent copy() 
			{
			CopasEvent e=new CopasEvent();
			
			
			e.id=id;
			e.plate=plate;
			e.row=row;
			e.col=col;
			e.clog=clog;
			e.scanrate=scanrate;
			e.sortStatus=sortStatus;
			e.statusSelect=statusSelect;
			e.timestamp=timestamp;
	
			e.prop.putAll(prop);
	
			e.levels=new double[levels.length][];
			for(int i=0;i<levels.length;i++)
				{
				if(e.levels[i]!=null)
					{
					e.levels[i]=new double[e.levels[i].length];
					for(int j=0;j<levels.length;j++)
						e.levels[i][j]=levels[i][j];
					}
				}
			
			e.wormLength=wormLength;
		
			return e;
			}

		public double[] toDoubleArr(List<String> chans, boolean hasProfile)
			{
			int len=chans.size();
			double[] arr=new double[len];
			for(int i=0;i<chans.size();i++)
				arr[i]=prop.get(chans.get(i));
			return arr;
			}
	
		
		}

	public static boolean isCopasFile(File path)
		{
		return path.getName().endsWith(".lmd") || path.getName().endsWith(".txt") || path.getName().endsWith(".dat");
		}

	
	/**
	 * Read all files belong together. Supply basename with .txt or .dat or .lmd
	 */
	public static Dataset readAll(File f) throws IOException
		{
		if(f.getName().length()<4)
			throw new IOException("Not a proper filename!");
		String name=f.getName();
		name=name.substring(0,name.length()-4);
		
		File fOverview=new File(f.getParent(),name+".txt");
		File fBinaryProfile=new File(f.getParent(),name+".dat");
		File fTextProfile=new File(f.getParent(),name+"_profile.txt");
		
		System.out.println(fOverview);
		TreeMap<Integer,CopasEvent> destOverview=readTextOverview(fOverview);
		
		boolean hasProfile=false;
		if(fBinaryProfile.exists())
			{
			TreeMap<Integer,CopasEvent> sourceProfiles=readBinaryProfile(fBinaryProfile);
			joinProfiles(sourceProfiles, destOverview);
			hasProfile=true;
			}
		else if(fTextProfile.exists())
			{
			//This is a bad export format. The dat contains everything so no reason to create this file!
			TreeMap<Integer,CopasEvent> sourceProfiles=readTextProfile(fTextProfile);
			
			joinProfiles(sourceProfiles, destOverview);
			hasProfile=true;
			}
		else
			System.out.println("No profile present, not loading");
		Dataset ds=convertEvents(destOverview, hasProfile);
		ds.source=f;
		return ds;
		}

	
	/**
	 * XXX_profile.txt
	 */
	private static TreeMap<Integer,CopasEvent> readTextProfile(File f) throws IOException
		{
		TreeMap<Integer,CopasEvent> profiles=new TreeMap<Integer, CopasEvent>();
		
		CsvFileReader csv=new CsvFileReader(f, '\t');
		ArrayList<ArrayList<String>> lines=new ArrayList<ArrayList<String>>();
		ArrayList<String> line=new ArrayList<String>();
		while((line=csv.readLine())!=null)
			lines.add(line);
		
		//ID is at the bottom. Can be used to figure out how many columns there are for one worm
		ArrayList<String> lastLine=lines.get(lines.size()-1);
		int numColumnsPerWorm=0;
		while(numColumnsPerWorm<lastLine.size() && lastLine.get(numColumnsPerWorm).equals(lastLine.get(0)))
			numColumnsPerWorm++;
		
		//Read all profiles
		int numProfiles=(lastLine.size()-1)/numColumnsPerWorm;
		for(int curProf=0;curProf<numProfiles;curProf++)
			{
			int thisid=Integer.parseInt(lastLine.get(curProf*numColumnsPerWorm));
			
			CopasEvent prof=new CopasEvent();
			prof.id=thisid;
			profiles.put(thisid,prof);
			
			//How long is the profile?
			int numNotNull=lines.size()-2;
			foundnotnull: while(numNotNull>0)
				{
				for(int j=0;j<numColumnsPerWorm;j++)
					if(!lines.get(numNotNull).get(curProf*numColumnsPerWorm+j).equals("0"))
						break foundnotnull;
				numNotNull--;
				}
			
			//Pull out this profile
			prof.levels=new double[numColumnsPerWorm][];
			for(int j=0;j<numColumnsPerWorm;j++)
				{
				double[] levels=new double[numNotNull];
				prof.levels[j]=levels;
				for(int i=0;i<numNotNull;i++)
					{
					String sval=lines.get(i).get(curProf*numColumnsPerWorm+j);
					int level=Integer.parseInt(sval);
					levels[i]=level;
					}
				prof.wormLength=numNotNull;
				}
			}
		
		return profiles;
		}


	/**
	 * 
	 * Convert COPAS even to a regular event
	 */
	private static Dataset convertEvents(TreeMap<Integer, CopasEvent> profiles, boolean hasProfile)
		{
		//Create channel info
		Dataset fcs=new Dataset();
		if(profiles.size()==0)
			return fcs;
		ArrayList<String> chans=new ArrayList<String>(profiles.get(0).prop.keySet());
		for(int i=0;i<chans.size();i++)
			{
			ChannelInfo ci=new ChannelInfo();
			ci.name=chans.get(i);
			fcs.ci.add(ci);
			}
		//Convert events
		for(CopasEvent e:profiles.values())
			fcs.eventsFloat.add(e.toDoubleArr(chans,hasProfile));
		if(hasProfile)
			{
			int numchan=profiles.firstEntry().getValue().levels.length;
			for(int i=0;i<numchan;i++)
				{
				LengthProfile prof=new LengthProfile();
				prof.name="prof "+(i+i);
				fcs.lengthprofsInfo.add(prof);
				}
			for(CopasEvent e:profiles.values())
				{
				LengthProfileData d=new LengthProfileData();
				d.data=e.levels;
				fcs.lengthprofsData.add(d);
				}
			}
			
			
		return fcs;
		}


	private static int readInt(DataInputStream is) throws IOException
		{
		return (is.read()) + (is.read()<<8) + (is.read()<<16) + (is.read()<<24);
		}
	
	private static int readShort(DataInputStream is) throws IOException
		{
		return (is.read()) + (is.read()<<8);
		}
		
	/**
	 * .dat
	 */
	private static TreeMap<Integer,CopasEvent> readBinaryProfile(File f) throws IOException
		{
		TreeMap<Integer,CopasEvent> profiles=new TreeMap<Integer, CopasEvent>();
		
		DataInputStream is=new DataInputStream(new FileInputStream(f));
		
		// 1-Extinction, 2- Flu1, 3- Flu2, 4- Flu3
		String[] chanName=new String[]{"EXT","Flu1","Flu2","Flu3"};
		int channels=readInt(is);
		int numChannels=0;
		LinkedList<String> levelsName=new LinkedList<String>();
				
		for(int i=0;i<4;i++)
			if((channels&(1<<i))!=0)
				{
				numChannels++;
				levelsName.add(chanName[i]);
				}
		
		for(;;)
			{
			//Get ID and figure out if this is the end of the file
			int thisid=is.read();
			if(thisid==-1)
				break;
			thisid+=is.read()<<8;
			thisid+=is.read()<<16;
			thisid+=is.read()<<24;

			CopasEvent prof=new CopasEvent();
			prof.levels=new double[numChannels][];

			//Read channels
			int numPoint=readShort(is);
			for(int i=0;i<numChannels;i++)
				{
				double levels[]=prof.levels[i]=new double[numPoint];
				for(int j=0;j<numPoint;j++)
					levels[j]=readShort(is);
				prof.wormLength=numPoint;
				prof.prop.put("LENGTH", prof.wormLength);
				}
			
			profiles.put(thisid,prof);
			}
		
		return profiles;
		}
	
	/**
	 * Handle n/a texts and parse ints
	 */
	private static Integer parseInt(String s)
		{
		if(s.equals("n/a"))
			return null;
		else
			return Integer.parseInt(s);
		}

	
	private static void removeNulls(CopasEvent prof)
		{
		for(String s:new LinkedList<String>(prof.prop.keySet()))
			if(prof.prop.get(s)==null)
				prof.prop.remove(s);
		}
	
	/**
	 * .txt
	 * 
	 * It has some things that are not in the LMD-file (FCS). 
	 * 
	 */
	private static TreeMap<Integer,CopasEvent> readTextOverview(File f) throws IOException
		{
		TreeMap<Integer,CopasEvent> profiles=new TreeMap<Integer, CopasEvent>();
		
		CsvFileReader csv=new CsvFileReader(f,'\t');
		ArrayList<String> titles=csv.readLine(); //Skip titles
		
		System.out.println(titles.size());
		if(titles.size()!=27)
			throw new IOException("Wrong number of headers in file; is this really a COPAS file?");
		
		ArrayList<String> line;
		while((line=csv.readLine())!=null)
			{
			CopasEvent prof=new CopasEvent();
			String id=line.get(0);
			if(id.equals(""))
				break;
			prof.id=Integer.parseInt(id);
			prof.plate=Integer.parseInt(line.get(1));
			prof.row=line.get(2);
			prof.col=Integer.parseInt(line.get(3));
			prof.clog=line.get(4).equals("Y");
			prof.scanrate=Integer.parseInt(line.get(5));
			prof.sortStatus=Integer.parseInt(line.get(6));
			prof.statusSelect=Integer.parseInt(line.get(7));
			
			prof.prop.put("TOF", Integer.parseInt(line.get(8)));
			prof.prop.put("EXT", Integer.parseInt(line.get(9)));
			
			prof.prop.put("Green",parseInt(line.get(10)));
			prof.prop.put("Yellow",parseInt(line.get(11)));
			prof.prop.put("Red",parseInt(line.get(12)));

			prof.prop.put("PH Ext",parseInt(line.get(13)));
			prof.prop.put("PW Ext",parseInt(line.get(14)));
			prof.prop.put("PC Ext",parseInt(line.get(15)));
			
			prof.prop.put("PH Green",parseInt(line.get(16)));
			prof.prop.put("PW Green",parseInt(line.get(17)));
			prof.prop.put("PCGreen", parseInt(line.get(18)));

			prof.prop.put("PH Yellow",parseInt(line.get(19)));
			prof.prop.put("PW Yellow",parseInt(line.get(20)));
			prof.prop.put("PCYellow", parseInt(line.get(21)));

			prof.prop.put("PH Red",parseInt(line.get(22)));
			prof.prop.put("PW Red",parseInt(line.get(23)));
			prof.prop.put("PCRed", parseInt(line.get(24)));

			prof.timestamp=parseInt(line.get(25));
			//prof.prop.put("Time Stamp", );

			
//			prof.tof=Integer.parseInt(line.get(8));
			//prof.ext=Integer.parseInt(line.get(9));
			//prof.chanGreen=parseInt(line.get(10));
			//prof.chanYellow=parseInt(line.get(11));
			//prof.chanRed=parseInt(line.get(12));
			
			removeNulls(prof);
			
			profiles.put(prof.id,prof);
			}
		
		return profiles;
		
		
		/* Id	
		 * Plate	
		 * Row	
		 * Column	
		 * Clog	
		 * Scan rate	
		 * Status sort	
		 * Status sel	
		 * TOF	
		 * EXT	
		 * Green	
		 * Yellow	
		 * Red	
		 * PH Ext	
		 * PW Ext	
		 * PC Ext	
		 * PH Green	
		 * PW Green	
		 * PCGreen	
		 * PH Yellow	
		 * PW Yellow	
		 * PCYellow	
		 * PH Red	
		 * PW Red	
		 * PCRed	
		 * Time Stamp 
		 * */	
		
		
		}
	
	/**
	 * Take levels from source (profile file) and put them in destination (overview txt file)
	 */
	private static void joinProfiles(TreeMap<Integer,CopasEvent> sourceProfiles, TreeMap<Integer,CopasEvent> destOverview)
		{
		for(int id:destOverview.keySet())
			{
			CopasEvent profSource=sourceProfiles.get(id);
			CopasEvent profDest=destOverview.get(id);
			profDest.levels=profSource.levels;
			profDest.wormLength=profSource.wormLength;
			profDest.prop.putAll(profSource.prop);
			}
		}



	
	}
