package glofacs.gates;

import java.util.Collection;
import java.util.HashMap;

/**
 * 
 * A set of gates
 * 
 * @author Johan Henriksson
 *
 */
public class GateSet
	{
	public HashMap<Integer, Gate> mapIdGate=new HashMap<Integer, Gate>();

	public GateSet()
		{
		Gate g=new GateRoot();
		g.name="root";
		mapIdGate.put(0, g);
		}
	
	//an artificial root with id=0 would be handy
	
	public Gate getRootGate()
		{
		return mapIdGate.get(0);
		}

	public int addNewGate(Gate g)
		{
		int id=1;
		for(;mapIdGate.containsKey(id);id++);
		mapIdGate.put(id, g);
		if(g.parent==null)
			g.setParent(getRootGate());
		return id;
		}

	public Collection<Gate> getGates()
		{
		return mapIdGate.values();
		}
	
	}
