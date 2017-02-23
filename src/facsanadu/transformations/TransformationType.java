package facsanadu.transformations;

/**
 * Transformation types
 * 
 * @author Johan Henriksson
 *
 */
public enum TransformationType
	{
	LINEAR, EXPONENTIAL, LOG;
	
	/**
	 * Create a transformation given type
	 */
	/*
	public static Transformation getTransform(TransformationType t)
		{
		if(t==LINEAR)
			return new TransformationLinear();
		else
			throw new RuntimeException("No such transform");
		}
		*/
	}
