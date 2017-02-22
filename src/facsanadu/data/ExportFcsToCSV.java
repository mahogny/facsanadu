package facsanadu.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class ExportFcsToCSV
	{

	public static void save(Dataset dataset, File file) throws IOException
		{
		PrintWriter pw=new PrintWriter(new FileWriter(file));
		
		//Write header
		for(int i=0;i<dataset.channelInfo.size();i++)
			{
			ChannelInfo ci=dataset.channelInfo.get(i);
			if(i!=0)
				pw.print("\t");
			pw.print(ci.formatName());
			}
		pw.println();
		
		//Write events
		for(double event[]:dataset.eventsFloat)
			{
			for(int i=0;i<event.length;i++)
				{
				if(i!=0)
					pw.print("\t");
				pw.print(event[i]);	
				}
			pw.println();
			}

		pw.close();
		}

	}
