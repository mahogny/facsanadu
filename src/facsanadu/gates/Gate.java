package facsanadu.gates;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import facsanadu.gates.measure.GateMeasure;
import facsanadu.gui.colors.ColorSet;
import facsanadu.transformations.TransformationStack;

/**
 * 
 * One gate
 * 
 * @author Johan Henriksson
 *
 */
public abstract class Gate
	{
	//In principle, should here have a hierarchy of transformations. A list of them?
	public TransformationStack transformations=new TransformationStack();
	
	
	//If we ignore and/or, the hierarchy will be much easier!
	public Gate parent;
	public LinkedList<Gate> children=new LinkedList<Gate>();
	public String name="";
	public GateColor color=new GateColor();
	
	LinkedList<GateMeasure> calculations=new LinkedList<GateMeasure>();
	
	public abstract boolean classify(double[] obs);
	public abstract void updateInternal();

	private static int idgen=0;
	private int intid=++idgen;
	
	public long lastModified=System.currentTimeMillis();
	
	public int getIntID()
		{
		return intid;
		}
	
	public void attachChild(Gate g)
		{
		children.add(g);
		g.parent=this;
		}
	
	public void attachMeasure(GateMeasure calc)
		{
		calc.gate=this;
		calculations.add(calc);
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
	
	public Collection<GateMeasure> getMeasures()
		{
		return calculations;
		}
	
	public void removeMeasure(GateMeasure calc)
		{
		calculations.remove(calc);
		calc.gate=null;
		}
	
	public void setUpdated()
		{
		//For now, set the entire list as updated. otherwise color is not handled properly
		long t=System.currentTimeMillis();
		Gate pc=this;
		while(pc.parent!=null)
			pc=pc.parent;
		pc.setUpdatedRecursive(t);
		}
	private void setUpdatedRecursive(long t)
		{
		lastModified=t;
		for(Gate g:children)
			g.setUpdatedRecursive(t);
		}
	
	
	public void setUniqueColor()
		{
		color=new GateColor(255, 255, -255); //Never used
		HashSet<GateColor> colset=new HashSet<GateColor>();
		getRootGate().getColorsRecursive(colset);
		color=new ColorSet().getUnusedColor(colset);
		}
	private void getColorsRecursive(HashSet<GateColor> colset)
		{
		colset.add(color);
		for(Gate g:children)
			g.getColorsRecursive(colset);
		}
	
	
	public Gate getRootGate()
		{
		Gate g=this;
		while(!(g instanceof GateRoot))
			g=g.parent;
		return g;
		}

	}