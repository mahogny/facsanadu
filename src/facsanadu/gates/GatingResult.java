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
	
	GateSet gating=new GateSet();

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
		dogateRec(gRoot, ds);
		}

	/**
	 * Calculate for one gate
	 */
	public void doOneGate(Gate g, Dataset ds, boolean approximate)
		{
		approximate=false;
		int n;
		if(g.parent==null) //This is the root
			{
			n=ds.getNumObservations();
			gateForObs=new IntArray(n); //Should never need to be resized
			}
		else
			{
			IntArray arr=acceptedFromGate.get(g.parent);
			if(arr==null)
				throw new RuntimeException("Parent gate not calculated "+g);
			n=arr.size();
			}
		int inc=1;
		if(approximate && n>10000)
			inc=n/10000;
		IntArray res=new IntArray(n);
		for(int i=0;i<n;i+=inc)
			classifyobs(g, ds, res, i);
		setAcceptedFromGate(g, res);
		lastUpdateGate.put(g, g.lastModified);
		
		System.out.println("Calculated gate "+g+" for ds "+ds);
		
		//TODO separate these out
		for(GateMeasure calc:g.getMeasures())
			gatecalc.put(calc,calc.calc(ds, g, this));		
		}
	
	
	
	/**
	 * Set accepted result from a gate
	 */
	public void setAcceptedFromGate(Gate g, IntArray res)
		{
		//may need to synchronize!
		synchronized (acceptedFromGate)
			{
			acceptedFromGate.put(g, res);
			lastUpdateGate.put(g, g.lastModified);
			}
		}
	
	/**
	 * Do gating for a gate with a parent
	 */
	private void dogateRec(Gate g, Dataset ds)
		{
		doOneGate(g, ds, true);
		for(Gate child:g.children)
			dogateRec(child, ds);
		}
	
	private void classifyobs(Gate g, Dataset segment, IntArray res, int id)
		{
		if(g.classify(segment.eventsFloat.get(id)))
			{
			res.addUnchecked(id);
			gateForObs.setUnchecked(id,g.getIntID());
			}
		}
	
	public ArrayList<Gate> getIdGates()
		{
		return gating.getIdGates();
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
