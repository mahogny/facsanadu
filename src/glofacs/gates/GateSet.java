package glofacs.gates;

import java.util.HashMap;
import java.util.LinkedList;

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
	
	//an artificial root with id=0 would be handy
	
	public LinkedList<Gate> getRootGates()
		{
		LinkedList<Gate> list=new LinkedList<Gate>();
		for(Gate g:mapIdGate.values())
			if(g.parent==null)
				list.add(g);
		return list;
		}

	public int addNewGate(Gate g)
		{
		int id=0;
		for(;mapIdGate.containsKey(id);id++);
		mapIdGate.put(id, g);
		return id;
		}

	}
