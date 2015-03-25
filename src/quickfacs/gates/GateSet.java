package quickfacs.gates;

import java.util.Collection;
import java.util.HashSet;
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
	private Gate rootgate=new GateRoot();
	
	
	public GateSet()
		{
		rootgate.name="root";
		}
	
	public Gate getRootGate()
		{
		return rootgate;
		}

	public void addNewGate(Gate g)
		{
		rootgate.attachChild(g);
		}

	public Collection<Gate> getGates()
		{
		LinkedList<Gate> list=new LinkedList<Gate>();
		getGatesRecursively(rootgate,list);
		return list;
		}

	private void getGatesRecursively(Gate parent, LinkedList<Gate> list)
		{
		list.add(parent);
		for(Gate g:parent.children)
			getGatesRecursively(g, list);
		}

	/**
	 * Get a free name for a gate
	 */
	public String getFreeName()
		{
		HashSet<String> prevnames=new HashSet<String>();
		for(Gate otherGate:getGates())
			prevnames.add(otherGate.name);
		
		int i=0;
		while(prevnames.contains("gate "+i))
			i++;
		return "gate "+i;
		}

	public Gate getGate(String name)
		{
		return getGate(name, getRootGate());
		}

	private static Gate getGate(String name, Gate parent)
		{
		if(parent.name.equals(name))
			return parent;
		for(Gate g:parent.children)
			{
			Gate got=getGate(name, g);
			if(got!=null)
				return got;
			}
		return null;
		}
	
	}
