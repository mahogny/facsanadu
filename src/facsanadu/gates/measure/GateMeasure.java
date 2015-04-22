package facsanadu.gates.measure;

import facsanadu.data.Dataset;
import facsanadu.gates.Gate;
import facsanadu.gates.GatingResult;
import facsanadu.gui.FacsanaduProject;

/**
 * 
 * Calculation for a gate
 * 
 * @author Johan Henriksson
 *
 */
public abstract class GateMeasure
	{
	public Gate gate;
	
	public abstract double calc(Dataset ds, Gate g, GatingResult res);
	public abstract String getDesc(FacsanaduProject proj);
	
	
	public void detachFromGate()
		{
		gate.removeMeasure(this);
		}
	}
