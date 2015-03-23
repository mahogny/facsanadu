package glofacs.gates;

import glofacs.io.FCSFile;

import java.util.HashMap;

/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class GatingResult
	{

	public HashMap<Gate, IntArray> acceptedFromGate=new HashMap<Gate, IntArray>();
	
	GateSet gating;

	/**
	 * Perform gating for all gates
	 */
	public void perform(GateSet gating, FCSFile.DataSegment segment)
		{
		this.gating=gating;
		Gate g=gating.getRootGate();
		IntArray res=new IntArray(segment.getNumObservations());
		for(int i=0;i<segment.getNumObservations();i++)
			if(g.classify(segment.eventsFloat.get(i)))
				res.add(i);
		acceptedFromGate.put(g, res);
		for(Gate child:g.children)
			dogate(g,child, segment);
		}
	
	/**
	 * Do gating for a gate with a parent
	 */
	private void dogate(Gate parent, Gate g, FCSFile.DataSegment segment)
		{
		IntArray prevres=acceptedFromGate.get(parent);
		IntArray res=new IntArray(prevres.size());
		for(int i=0;i<prevres.size();i++)
			if(g.classify(segment.eventsFloat.get(prevres.get(i))))
				res.add(i);
		acceptedFromGate.put(g, res);
		for(Gate child:g.children)
			dogate(g, child, segment);
		}
	
	
	

	public int getTotalCount()
		{
		return acceptedFromGate.get(gating.getRootGate()).size();
		}

	}
