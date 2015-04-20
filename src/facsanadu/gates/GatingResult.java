package facsanadu.gates;

import java.util.ArrayList;
import java.util.HashMap;

import facsanadu.data.Dataset;

/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class GatingResult
	{
	public HashMap<Gate, IntArray> acceptedFromGate=new HashMap<Gate, IntArray>();

	private IntArray gateForObs=new IntArray();
	
	GateSet gating;

	public int getGateIntIDForObs(int obs)
		{
		return gateForObs.get(obs);
		}
	
	/**
	 * Perform gating for all gates
	 */
	public void perform(GateSet gating, Dataset segment)
		{
		boolean approximate=true;
		
		
		this.gating=gating;
		Gate g=gating.getRootGate();
		int n=segment.getNumObservations();
		
		int inc=1;
		if(approximate && n>10000)
			inc=n/10000;
		
		//Initial reverse map
		gateForObs=new IntArray(n);
		
		//Recursively do gating
		IntArray res=new IntArray(n);
		for(int i=0;i<n;i+=inc)
			classifyobs(g, segment, res, i);/*
			if(g.classify(segment.getAsFloat(i)))
				res.addUnchecked(i);*/
		acceptedFromGate.put(g, res);
		for(Gate child:g.children)
			dogate(g,child, segment);
		}
	
	/**
	 * Do gating for a gate with a parent
	 */
	private void dogate(Gate parent, Gate g, Dataset segment)
		{
		IntArray prevres=acceptedFromGate.get(parent);
		IntArray res=new IntArray(prevres.size());
		for(int i=0;i<prevres.size();i++)
			{
			int id=prevres.get(i);
			classifyobs(g, segment, res, id);/*
			if(g.classify(segment.eventsFloat.get(id)))
				{
				res.addUnchecked(id);
				gateForObs.set(id,g.getIntID());
				System.out.println(g.getIntID());
				}*/
			}
		acceptedFromGate.put(g, res);
		for(Gate child:g.children)
			dogate(g, child, segment);
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

	}
