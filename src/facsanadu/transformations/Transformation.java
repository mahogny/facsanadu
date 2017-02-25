package facsanadu.transformations;


/**
 * Transformation
 * 
 * @author Johan Henriksson
 *
 */
public abstract class Transformation
	{
	public int channel=-1;
	public abstract void transform(double[] v);
	public abstract void invert(double[] v);

	public abstract double transform(double x, int index);
	public abstract double invert(double x, int index);
	}
