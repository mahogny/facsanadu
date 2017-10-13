package facsanadu.gui.colors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;

import facsanadu.gates.GateColor;


/**
 * 
 * Set of colors
 * 
 * @author Johan Henriksson
 *
 */
public class ColorSet
	{
	public LinkedList<GateColor> colors=new LinkedList<GateColor>();
	
	public static ColorSet colorset=new ColorSet();
	
	static
		{
		try
			{
			colorset.parseStandardColors();
			}
		catch (IOException e)
			{
			e.printStackTrace();
			}
		}

	
	
	public void parseStandardColors() throws IOException
		{
		BufferedReader br=new BufferedReader(new InputStreamReader(ColorSet.class.getResourceAsStream("commoncolors.txt")));

		String line;
		while((line=br.readLine())!=null)
			{
			GateColor c=new GateColor();
			c.r=Integer.parseInt(line.substring(1,1+2),16);
			c.g=Integer.parseInt(line.substring(3,3+2),16);
			c.b=Integer.parseInt(line.substring(5,5+2),16);
			colors.add(c);
			}
		
		br.close();
		}



	public GateColor get(int curcol)
		{
		return colors.get(curcol);
		}



	public int size()
		{
		return colors.size();
		}



	public GateColor getRandomColor()
		{
		return new GateColor(colors.get((int)(Math.random()*colors.size())));
		}



	public GateColor getUnusedColor(HashSet<GateColor> colset)
		{
		for(GateColor c:colors)
			if(!colset.contains(c))
				return c;
		//Fallback - any random color
		return new GateColor(
				(int)(Math.random()*255), 
				(int)(Math.random()*255), 
				(int)(Math.random()*255));
		}
	
	}
