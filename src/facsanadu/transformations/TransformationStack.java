package facsanadu.transformations;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * A set of transformations
 * 
 * @author Johan Henriksson
 *
 */
public class TransformationStack
	{
	public ArrayList<Transformation> list=new ArrayList<Transformation>();
	 
	
	@Override
	public boolean equals(Object obj)
		{
		if(obj instanceof TransformationStack)
			{
			return ((TransformationStack)obj).list.equals(list);
			}
		else
			return false;
		}
	
	/**
	 * Transform a point. Returns a new point. Might be the same array (optimization)
	 */
	public double[] perform(double[] v)
		{
		if(list.isEmpty())
			return v;
		else
			{
			double[] n=new double[v.length];
			System.arraycopy(v, 0, n, 0, v.length);
			for(Transformation one:list)
				one.transform(n);
			return n;
			}
		}
	
	
	/**
	 * Invert a point. Returns a new point. Might be the same array (optimization)
	 */
	public double[] invert(double[] v)
		{
		if(list.isEmpty())
			return v;
		else
			{
			double[] n=new double[v.length];
			System.arraycopy(v, 0, n, 0, v.length);
			for(int i=list.size()-1;i>=0;i--)
				list.get(i).invert(n);
			return n;
			}
		}

	public void set(int index, Transformation trans)
		{
		//Remove any previous for index
		for(Transformation t:new LinkedList<Transformation>(list))
			if(t.channel==index)
				list.remove(t);

		//Add new one
		if(trans!=null)
			{
			trans.channel=index;
			list.add(trans);
			}
		}

	public double perform(double x, int indexX)
		{
		for(Transformation t:list)
			x=t.transform(x, indexX);
		return x;
		}

	public double invert(double x, int indexX)
		{
		for(int i=list.size()-1;i>=0;i--)
			x=list.get(i).invert(x, indexX);
		return x;
		}

	public boolean isEmpty()
		{
		return list.isEmpty();
		}

	/*
	public double transform(Dataset ds, int observationIndex, int index)
		{
		double x=ds.getAsFloat(observationIndex,index);
		for(Transformation t:list)
			x=t.transform(x, index);
		return x;
		}*/
	
	
	}
