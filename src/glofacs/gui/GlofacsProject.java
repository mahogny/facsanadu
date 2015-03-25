package glofacs.gui;

import glofacs.gates.GateSet;
import glofacs.gates.GatingResult;
import glofacs.gui.channel.ViewSettings;
import glofacs.io.Dataset;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class GlofacsProject
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


	}
