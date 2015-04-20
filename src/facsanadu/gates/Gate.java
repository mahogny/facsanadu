package facsanadu.gates;

import java.util.LinkedList;

/**
 * 
 * One gate
 * 
 * @author Johan Henriksson
 *
 */
public abstract class Gate
	{
	//If we ignore and/or, the hierarchy will be much easier!
	public Gate parent;
	public LinkedList<Gate> children=new LinkedList<Gate>();
	public String name="";
	public GateColor color=new GateColor();
	
	public LinkedList<GateCalc> calculations=new LinkedList<GateCalc>();
	
	public abstract boolean classify(double[] obs);
	public abstract void updateInternal();

	private static int idgen=0;
	private int intid=++idgen;
	
	public int getIntID()
		{
		return intid;
		}
	
	public void attachChild(Gate g)
		{
		children.add(g);
		g.parent=this;
		}
	
	public void setParent(Gate parent)
		{
		this.parent=parent;
		parent.children.add(this);
		}

	public void detachParent()
		{
		parent.children.remove(this);
		parent=null;
		}
	}
