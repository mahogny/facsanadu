package facsanadu.gates;

/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class IntArray
	{
	private int arr[]=new int[100];
	private int len=0;
	
	public IntArray()
		{
		}
	
	public IntArray(int capacity)
		{
		arr=new int[capacity];
		}
	
	public void add(int value)
		{
		if(len==arr.length)
			{
			int newarr[]=new int[arr.length*2];
			System.arraycopy(arr, 0, newarr, 0, arr.length);
			arr=newarr;
			}
		arr[len]=value;
		len++;
		}

	public int size()
		{
		return len;
		}

	public int get(int i)
		{
		return arr[i];
		}

	
	/**
	 * Add a value without checking if there is enough space. Caller has to ensure this
	 */
	public void addUnchecked(int value)
		{
		arr[len]=value;
		len++;
		}
	
	

	}
