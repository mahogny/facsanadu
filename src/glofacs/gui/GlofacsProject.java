package glofacs.gui;

import glofacs.gates.GateSet;
import glofacs.gates.GatingResult;
import glofacs.gui.channel.ViewSettings;
import glofacs.io.FCSFile;
import glofacs.io.FCSFile.DataSegment;

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
	public LinkedList<FCSFile.DataSegment> datasets=new LinkedList<FCSFile.DataSegment>();
	public LinkedList<ViewSettings> views=new LinkedList<ViewSettings>();

	public HashMap<FCSFile.DataSegment, GatingResult> gatingResult=new HashMap<FCSFile.DataSegment, GatingResult>();
	
	/**
	 * Get gating result for dataset
	 */
	public GatingResult getGatingResult(FCSFile.DataSegment segment)
		{
		if(gatingResult.get(segment)==null)
			return new GatingResult();
		else
			return gatingResult.get(segment);
		}

	
	/**
	 * Update gating results
	 */
	public void dogating(LinkedList<DataSegment> listDatasets)
		{
		gatingResult.clear();
		for(FCSFile.DataSegment ds:listDatasets)
			{
			GatingResult gr=new GatingResult();
			gr.perform(gateset, ds);
			gatingResult.put(ds, gr);
			}
		
		}


	}
