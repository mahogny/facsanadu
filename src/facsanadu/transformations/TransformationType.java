package facsanadu.transformations;

/**
 * Transformation types
 * 
 * @author Johan Henriksson
 *
 */
public enum TransformationType
	{
	LINEAR, LOG;

	public static TransformationType of(Transformation t)
		{
		if(t instanceof TransformationLog)
			return LOG;
		else
			throw new RuntimeException("Unknow type "+t.getClass());
		}

	public static Transformation create(TransformationType type)
		{
		if(type==LOG)
			return new TransformationLog();
		else
			throw new RuntimeException("No such transform");
		}
	
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
