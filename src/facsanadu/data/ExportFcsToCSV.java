package facsanadu.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

import facsanadu.gates.Gate;
import facsanadu.gates.GateSet;
import facsanadu.gates.GatingResult;
import facsanadu.gates.IntArray;
import facsanadu.gui.FacsanaduProject;


/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class ExportFcsToCSV
	{

	/**
	 * Export the FCS data as a CSV file. This includes virtual channels, and gate membership status
	 * @param dataset
	 * @param file
	 * @throws IOException
	 */
	public static void save(Dataset dataset, FacsanaduProject proj, File file) throws IOException
		{
		PrintWriter pw=new PrintWriter(new FileWriter(file));

		//Ensure gating updated
		GateSet gs = proj.gateset;
		GatingResult gres = proj.gatingResult.get(dataset);
		gres.perform(gs, dataset);
		Collection<Gate> gates = gs.getGates();
		
		//Write header: channels
		for(int i=0;i<dataset.channelInfo.size();i++)
			{
			ChannelInfo ci=dataset.channelInfo.get(i);
			if(i!=0)
				pw.print("\t");
			pw.print(ci.formatName());
			}
		//Write header: gates
		for(Gate g:gates)
			{
			pw.print("\t");
			pw.print("g:"+g.name.replaceAll(" ", "_"));
			}
		pw.println();
		
		//Prepare gating results for quick lookup
		HashMap<Gate,HashSet<Integer>> gateHasEvent=new HashMap<Gate, HashSet<Integer>>();
		for(Gate g:gates)
			{
			IntArray arr=gres.getAcceptedFromGate(g);
			HashSet<Integer> hs=new HashSet<Integer>();
			for(int i=0;i<arr.size();i++)
				hs.add(arr.get(i));
			gateHasEvent.put(g, hs);
			}
		
		//Write events
		for(int eventID=0;eventID<dataset.eventsFloat.size();eventID++)
		//for(double event[]:dataset.eventsFloat)
			{
			double event[] = dataset.eventsFloat.get(eventID);   //TODO is this ok with virtual channels?
			
			//For each channel
			for(int i=0;i<event.length;i++)
				{
				if(i!=0)
					pw.print("\t");
				pw.print(event[i]);	
				}
			//For each gate
			for(Gate g:gates)
				{
				if(gateHasEvent.get(g).contains(eventID))
					pw.print("\t1");
				else
					pw.print("\t0");
				}
			pw.println();
			}

		pw.close();
		}

	
	
	
	}
