package facsanadu.gates;

import java.util.HashSet;

/**
 * 
 * Color used for sequences
 * 
 * @author Johan Henriksson
 *
 */
public class GateColor
	{
	public int r,g,b;

	public GateColor()
		{
		
		}
	
	
	public GateColor(int r, int g, int b)
		{
		this.r=r;
		this.g=g;
		this.b=b;
		}

	public GateColor(GateColor col)
		{
		this.r=col.r;
		this.g=col.g;
		this.b=col.b;
		}


	@Override
	public String toString()
		{
		return "("+r+","+g+","+b+")";
		}


	public String getColorAsRGBstring()
		{
		return to2hex(r)+to2hex(g)+to2hex(b);
		}
	
	private String to2hex(double d)
		{
		int i=(int)(255*d);
		if(i>255)
			i=255;
		String s=Integer.toHexString(i);
		if(s.length()==1)
			return "0"+s;
		else
			return s;
		}
	
	
	public int getLightness()
		{
		int max=Math.max(Math.max(r,g),b);
		int min=Math.min(Math.min(r,g),b);
		return (max+min)/2;
		}

	@Override
	public boolean equals(Object obj)
		{
		if(obj instanceof GateColor)
			{
			GateColor o=(GateColor)obj;
			return r==o.r && g==o.g && b==o.b;
			}
		else
			return false;
		}


	@Override
	public int hashCode()
		{
		return r+g+b;
		}


	}
