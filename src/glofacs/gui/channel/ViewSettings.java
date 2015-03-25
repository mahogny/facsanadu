package glofacs.gui.channel;

import glofacs.gates.Gate;
import glofacs.io.Dataset;

public class ViewSettings
	{
	public Gate fromGate;
	
	public int indexX=7;
	public int indexY=6;
	
	//FCS space * scale = screen space
	public double scaleX=1;
	public double scaleY=1;
	
	
	public void autoscale(Dataset segment)
		{
		double maxx=getMaxForChannel(segment,indexX);
		double maxy=getMaxForChannel(segment,indexY);
		scaleX=1.0/maxx;
		scaleY=1.0/maxy;
		
//		System.out.println("autoscale -- "+maxx+"   "+maxy);
		} 


	public static double getMaxForChannel(Dataset segment, int chanid)
		{
		//faster to check all channels in parallel
		double max=-Double.MAX_VALUE;
		for(int i=0;i<segment.eventsFloat.size();i++)
			max=Math.max(max,segment.eventsFloat.get(i)[chanid]);
		return max;
		}
	}
