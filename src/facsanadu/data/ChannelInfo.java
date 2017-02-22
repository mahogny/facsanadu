package facsanadu.data;

/**
 * 
 * Information about one channel
 * 
 * @author Johan Henriksson
 *
 */
public class ChannelInfo
	{
	public String label;
	public String name;
	
	public String formatName()
		{
		if(name!=null)
			{
			if(label!=null)
				return label+" - "+name;
			else
				return name;
			}
		else
			return label;
		}

	public String getShortestName()
		{
		if(label!=null)
			return label;
		else
			return name;
		}
	
	/*
	public HashMap<Integer, Integer> numBitsForPar=new HashMap<Integer, Integer>();
	public HashMap<Integer, String> ampTypeForPar=new HashMap<Integer, String>();
	public HashMap<Integer, String> shortNameForPar=new HashMap<Integer, String>();
	public HashMap<Integer, String> nameForPar=new HashMap<Integer, String>();
	public HashMap<Integer, String> rangeForPar=new HashMap<Integer, String>();
	public HashMap<Integer, String> detectorVoltage=new HashMap<Integer, String>();
	public HashMap<Integer, String> amplifierGain=new HashMap<Integer, String>();
	*/
	
	
	
	public ProfChannel pc=null;
	}
