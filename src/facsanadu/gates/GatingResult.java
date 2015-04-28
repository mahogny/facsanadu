package facsanadu.gates;

import java.util.ArrayList;
import java.util.HashMap;

import facsanadu.data.Dataset;
import facsanadu.gates.measure.GateMeasure;

/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class GatingResult
	{
	private HashMap<Gate, IntArray> acceptedFromGate=new HashMap<Gate, IntArray>();
	private HashMap<GateMeasure, Double> gatecalc=new HashMap<GateMeasure, Double>();
	private IntArray gateForObs=new IntArray();
	
	private HashMap<Gate, Long> lastUpdateGate=new HashMap<Gate, Long>();
	
	GateSet gating;

	public int getGateIntIDForObs(int obs)
		{
		return gateForObs.get(obs);
		}
	
	/**
	 * Perform gating for all gates
	 */
	public void perform(GateSet gating, Dataset ds)
		{
		this.gating=gating;
		Gate gRoot=gating.getRootGate();
		int n=ds.getNumObservations();
				
		//Initial reverse map
		dogate(gRoot, ds, n);
		
		//Recursively do gating
		for(Gate child:gRoot.children)
			dogate(gRoot,child, ds);
		}

	/**
	 * Calculate for one gate
	 */
	private void dogate(Gate g, Dataset ds, int n)
		{
		boolean approximate=true;
		int inc=1;
		if(approximate && n>10000)
			inc=n/10000;
		IntArray res=new IntArray(n);
		for(int i=0;i<n;i+=inc)
			classifyobs(g, ds, res, i);
		setAcceptedFromGate(g, res);
		for(GateMeasure calc:g.getMeasures())
			gatecalc.put(calc,calc.calc(ds, g, this));		
		}
	
	/**
	 * Set accepted result from a gate
	 */
	public void setAcceptedFromGate(Gate g, IntArray res)
		{
		acceptedFromGate.put(g, res);
		lastUpdateGate.put(g, g.lastModified);
		}
	
	/**
	 * Do gating for a gate with a parent
	 */
	private void dogate(Gate parent, Gate g, Dataset ds)
		{
		IntArray prevres=acceptedFromGate.get(parent);
		dogate(g, ds, prevres.size());
		for(Gate child:g.children)
			dogate(g, child, ds);
		}
	
	private void classifyobs(Gate g, Dataset segment, IntArray res, int id)
		{
		if(g.classify(segment.eventsFloat.get(id)))
			{
			res.addUnchecked(id);
			gateForObs.set(id,g.getIntID());
			}
		}
	
	public ArrayList<Gate> getIdGates()
		{
		ArrayList<Gate> list=new ArrayList<Gate>();
		getIdGates(getRootGate(), list);
		return list;
		}
	private void getIdGates(Gate g, ArrayList<Gate> list)
		{
		while(list.size()<=g.getIntID())
			list.add(null);
		list.set(g.getIntID(), g);
		for(Gate child:g.children)
			getIdGates(child, list);
		}
	

	public int getTotalCount()
		{
		return acceptedFromGate.get(gating.getRootGate()).size();
		}

	public Gate getRootGate()
		{
		return gating.getRootGate();
		}

	public Double getCalcResult(GateMeasure calc)
		{
		return gatecalc.get(calc);
		}

	public boolean gateNeedsUpdate(Gate g)
		{
		Long lastupd=lastUpdateGate.get(g);
		if(lastupd==null)
			lastupd=0L;
		return g.lastModified>lastupd;
		}

	public IntArray getAcceptedFromGate(Gate g)
		{
		return acceptedFromGate.get(g);
		}

	}
