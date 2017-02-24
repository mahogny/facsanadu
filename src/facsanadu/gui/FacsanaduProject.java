package facsanadu.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import facsanadu.data.ChannelInfo;
import facsanadu.data.Compensation;
import facsanadu.data.Dataset;
import facsanadu.data.ProfChannel;
import facsanadu.gates.GateSet;
import facsanadu.gates.GatingResult;
import facsanadu.gui.view.ViewSettings;
import facsanadu.io.CopasIO;
import facsanadu.io.FCSFile;

/**
 * 
 * A project bundle of all data
 * 
 * @author Johan Henriksson
 *
 */
public class FacsanaduProject
	{
	public GateSet gateset=new GateSet();
	public LinkedList<Dataset> datasets=new LinkedList<Dataset>();
	public LinkedList<ViewSettings> views=new LinkedList<ViewSettings>();
	public LinkedList<ProfChannel> profchan=new LinkedList<ProfChannel>();

	public HashMap<Dataset, GatingResult> gatingResult=new HashMap<Dataset, GatingResult>();

	public Compensation compensation=new Compensation();
	
	/**
	 * Get gating result for dataset
	 */
	public GatingResult getGatingResult(Dataset ds)
		{
		return getCreateGatingResult(ds); //Why a separate method here??
		/*
		if(gatingResult.get(ds)==null)
			return new GatingResult();
		else
			return gatingResult.get(ds);
			*/
		}

	/**
	 * Get an existing or create a new gating result for a particular dataset. Each dataset should have one
	 */
	public GatingResult getCreateGatingResult(Dataset ds)
		{
		synchronized (gatingResult)
			{
			if(gatingResult.get(ds)==null)
				{
				GatingResult gr=new GatingResult(gateset);
				gatingResult.put(ds, gr);
				return gr;
				}
			return gatingResult.get(ds);
			}
		}
	
	/**
	 * Update gating results
	 */
	public void performGating(LinkedList<Dataset> listDatasets)
		{
		gatingResult.clear();
		for(Dataset ds:listDatasets)
			{
			GatingResult gr=getCreateGatingResult(ds);//new GatingResult();
			gr.perform(gateset, ds);
			gatingResult.put(ds, gr);
			}
		}


	public void addDataset(File path) throws IOException
		{
		if(FCSFile.isFCSfile(path))
			{
			//Assume it is an FCS file
			addDataset(FCSFile.load(path));
			}
		else if(CopasIO.isCopasFile(path))
			{
			//Assume COPAS file
			addDataset(CopasIO.readAll(path));
			}
		else
			throw new IOException("Cannot recognize file");
		}

	public void updateCompensation()
		{
		compensation.updateMatrix(this);
		compensation.apply(this);
		}
	
	
	public void addDataset(Dataset ds)
		{
		ds.computeProfChannel(this, null);
		datasets.add(ds);
		//recalcProfChan();
		updateCompensation();
		//What about gating?
		}

	public int getNumChannels()
		{
		if(datasets.size()==0)
			return 0;
		else
			return datasets.get(0).getChannelInfo().size();
		}


	public ArrayList<ChannelInfo> getChannelInfo()
		{
		ArrayList<ChannelInfo> names=new ArrayList<ChannelInfo>();
		if(!datasets.isEmpty())
			{
			Dataset ds=datasets.get(0);
			names=ds.getChannelInfo();
			}
		return names;
		}


	public void recalcProfChan()
		{
		recalcProfChan(null);
		}
	public void recalcProfChan(ProfChannel chChanged)
		{
		for(Dataset ds:datasets)
			ds.computeProfChannel(this, chChanged);
		}


	}
