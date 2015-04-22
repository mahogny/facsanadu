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
	public HashMap<Gate, IntArray> acceptedFromGate=new HashMap<Gate, IntArray>();
	public HashMap<GateMeasure, Double> gatecalc=new HashMap<GateMeasure, Double>();
	private IntArray gateForObs=new IntArray();
	
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
		boolean approximate=true;
		
		
		this.gating=gating;
		Gate gRoot=gating.getRootGate();
		int n=ds.getNumObservations();
		
		int inc=1;
		if(approximate && n>10000)
			inc=n/10000;
		
		//Initial reverse map
		gateForObs=new IntArray(n);
		
		//Recursively do gating
		IntArray res=new IntArray(n);
		for(int i=0;i<n;i+=inc)
			classifyobs(gRoot, ds, res, i);
		acceptedFromGate.put(gRoot, res);
		for(GateMeasure calc:gRoot.getMeasures())
			gatecalc.put(calc,calc.calc(ds, gRoot, this));		
		for(Gate child:gRoot.children)
			dogate(gRoot,child, ds);
		}
	
	/**
	 * Do gating for a gate with a parent
	 */
	private void dogate(Gate parent, Gate g, Dataset ds)
		{
		IntArray prevres=acceptedFromGate.get(parent);
		IntArray res=new IntArray(prevres.size());
		for(int i=0;i<prevres.size();i++)
			{
			int id=prevres.get(i);
			classifyobs(g, ds, res, id);
			}
		acceptedFromGate.put(g, res);
		for(GateMeasure calc:g.getMeasures())
			gatecalc.put(calc,calc.calc(ds, g, this));
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

	}
