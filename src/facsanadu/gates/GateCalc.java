package facsanadu.gates;

import facsanadu.data.Dataset;

/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public interface GateCalc
	{
	public double calc(Dataset ds, Gate g, GatingResult res);


	}
