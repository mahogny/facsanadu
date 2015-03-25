package quickfacs.io;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import quickfacs.data.Dataset;

/**
 * 
 * 3.1
 * http://www.isac-net.org/index.php?option=com_content&task=view&id=828&Itemid=150
 * http://www.ncbi.nlm.nih.gov/pmc/articles/PMC2892967/
 * 
 * 3.0
 * http://www.isac-net.org/index.php?option=com_content&task=view&id=101&Itemid=150
 * http://murphylab.web.cmu.edu/publications/64-seamer1997.pdf
 *
 * 2.0
 * http://www.ncbi.nlm.nih.gov/pubmed/2340769
 *
 * Gating-ML
 * http://flowcyt.sourceforge.net/gating/
 *
 * The Union Bio COPAS LMD-files are in fact FCS-files
 * 
 * @author Johan Henriksson
 *
 */
public class FCSFile
	{
	public ArrayList<FCSFileDataset> data=new ArrayList<FCSFileDataset>(); 
	
	static String readString(DataInputStream r, int count) throws IOException
		{		
		byte[] buf=new byte[count];
		r.readFully(buf);
		
		InputStream is=new ByteArrayInputStream(buf);
		BufferedReader r2=new BufferedReader(new InputStreamReader(is));
		//r2.read(cbuf)
		//new String()
		return r2.readLine();
		}
	
	static long readOffset(DataInputStream r) throws IOException
		{
		String s=readString(r,8);
		int i=0;
		while(s.charAt(i)==' ')
			i++;
		s=s.substring(i);
		return Long.parseLong(s);
		}
	
	static int readA(DataInputStream r, int count) throws IOException
		{
		String s=readString(r,count);
		int i=0;
		while(s.charAt(i)==' ')
			i++;
		s=s.substring(i);
		return Integer.parseInt(s);
		}
	
	static boolean isPnX(String tok, String x)
		{
		if(tok.startsWith("$P") && tok.endsWith(x)) 
			{
			try
				{
				Integer.parseInt(tok.substring(2,tok.length()-1));
				return true;
				}
			catch (NumberFormatException e)
				{
				System.out.println(e);
				}
			}
		return false;
		}

	
	
	
	static long parseDataValue(String val)
		{
		if(val.endsWith("I"))
			val=val.substring(0,val.length()-1); //This came up in a FacStar dataset. not part of standard?
		int i=0;
		while(val.charAt(i)==' ')
			i++;
		val=val.substring(i);
		return Long.parseLong(val.trim());
		}
	
	
	
	
	
	
	/*
	private static int readInt(DataInputStream r, boolean invert) throws IOException
		{
		int val=r.readInt();
		if(invert)
			{
			//TODO
			}
		return val;
		}
	
	private static int readUnsignedShort(DataInputStream r, boolean invert) throws IOException
		{
		int val=r.readUnsignedShort();
		if(invert)
			{
			//TODO
			}
		return val;
		}*/
	
	
	
	public FCSFile(File f) throws IOException
		{
		Long nextData=(long)0;
		while(nextData!=null)
			{
			FCSFileDataset dataSegment=new FCSFileDataset();
			dataSegment.source=f;
			data.add(dataSegment);
			nextData=dataSegment.parse(f, 0);
			}
		}

	
	
	public FCSFile()
		{
		}

	public static boolean isFCSfile(File f) throws FileNotFoundException
		{
		try
			{
			//FileInputStream fi=new FileInputStream(f);
			DataInputStream rHeader=new DataInputStream(new FileInputStream(f));
			String fcsversion=readString(rHeader, 10);
			return fcsversion.startsWith("FCS");
			}
		catch (FileNotFoundException e)
			{
			throw e;
			}
		catch (IOException e)
			{
			return false;
			}
		}
	
	
	
	public static Dataset load(File f) throws IOException
		{
		FCSFile fcs=new FCSFile(f);
		
		return fcs.data.get(0).getDataset();
		}
	
	
	}
