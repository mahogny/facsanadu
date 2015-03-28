package facsanadu.gui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import facsanadu.data.Dataset;
import facsanadu.gates.GateSet;
import facsanadu.gates.GatingResult;
import facsanadu.gui.view.ViewSettings;
import facsanadu.io.CopasIO;
import facsanadu.io.FCSFile;

/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class QuickfacsProject
	{
	public GateSet gateset=new GateSet();
	public LinkedList<Dataset> datasets=new LinkedList<Dataset>();
	public LinkedList<ViewSettings> views=new LinkedList<ViewSettings>();

	public HashMap<Dataset, GatingResult> gatingResult=new HashMap<Dataset, GatingResult>();
	
	/**
	 * Get gating result for dataset
	 */
	public GatingResult getGatingResult(Dataset segment)
		{
		if(gatingResult.get(segment)==null)
			return new GatingResult();
		else
			return gatingResult.get(segment);
		}

	
	/**
	 * Update gating results
	 */
	public void dogating(LinkedList<Dataset> listDatasets)
		{
		gatingResult.clear();
		for(Dataset ds:listDatasets)
			{
			GatingResult gr=new GatingResult();
			gr.perform(gateset, ds);
			gatingResult.put(ds, gr);
			}
		
		}


	public void addDataset(File path) throws IOException
		{
		if(path.getName().endsWith(".fcs"))
			{
			//Assume it is an FCS file
			datasets.add(FCSFile.load(path));
			}
		else
			{
			//Assume COPAS file
			datasets.add(CopasIO.readAll(path));
			}
		}


	public int getNumChannels()
		{
		if(datasets.size()==0)
			return 0;
		else
			return datasets.get(0).getChannelInfo().size();
		}


	}
