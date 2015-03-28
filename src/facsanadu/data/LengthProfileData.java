package facsanadu.data;

public class LengthProfileData
	{
	public double[][] data;

	public int getLength()
		{
		if(data.length>0)
			return data[0].length;
		else
			return 0;
		}
	}
