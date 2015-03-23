package glofacs.gui;

/**
 * 
 * Information about one channel
 * 
 * @author Johan Henriksson
 *
 */
public class ChannelInfo
	{
	public String name="";
	public String shortName="";
	
	public String formatName()
		{
		if(shortName!=null)
			{
			if(name!=null)
				return name+" - "+shortName;
			else
				return shortName;
			}
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
	}
