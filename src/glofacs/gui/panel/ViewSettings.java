package glofacs.gui.panel;

import glofacs.io.FCSFile;
import glofacs.io.FCSFile.DataSegment;

public class ViewSettings
	{
	public String name="";
	
	public int fromGateID;
	
	public int indexX=7;
	public int indexY=6;
	
	//FCS space * scale = screen space
	public double scaleX=1;
	public double scaleY=1;
	
	public void autoscale(DataSegment segment, int width, int height)
		{
		double maxx=getMaxForChannel(segment,indexX);
		double maxy=getMaxForChannel(segment,indexY);
		scaleX=width/maxx;
		scaleY=height/maxy;
		
//		System.out.println("autoscale -- "+maxx+"   "+maxy);
		} 


	public static double getMaxForChannel(FCSFile.DataSegment segment, int chanid)
		{
		//faster to check all channels in parallel
		double max=-Double.MAX_VALUE;
		for(int i=0;i<segment.eventsFloat.size();i++)
			{
			max=Math.max(max,segment.eventsFloat.get(i)[chanid]);
			}
		return max;
		}
	}
