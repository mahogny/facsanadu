package facsanadu.transformations;

/**
 * Transformation: Log
 * 
 * @author Johan Henriksson
 *
 */
public class TransformationLog extends Transformation
	{
	public void transform(double[] v)
		{
		v[channel]=Math.log(v[channel]);
		}
	public void invert(double[] v)
		{
		}
	
	@Override
	public boolean equals(Object obj)
		{
		if(obj instanceof TransformationLog)
			{
			TransformationLog o=(TransformationLog)obj;
			return channel==o.channel;
			}
		else
			return false;
		}

	public double transform(double x, int index)
		{
		if(index==channel)
			return Math.log(x);
		else
			return x;
		}
	@Override
	public double invert(double x, int index)
		{
		if(index==channel)
			return Math.exp(x);
		else
			return x;
		}

	}
