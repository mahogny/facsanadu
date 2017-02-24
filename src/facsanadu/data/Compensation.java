package facsanadu.data;

import java.util.ArrayList;

import facsanadu.gui.FacsanaduProject;

/**
 * Compensation matrix
 * 
 * @author Johan Henriksson
 *
 */
public class Compensation
	{
	double[][] matrix=new double[0][0];
	public ArrayList<String> cnames=new ArrayList<String>();
	
	public Compensation()
		{
		}
	//How to deal with virtual channels?
	//TODO
	
	public int getSize()
		{
		return matrix.length;
		}
	
	public void updateMatrix(FacsanaduProject p)
		{
		//Collect which channels exist
		//Might actually be able to ensure each dataset have the same channels afterwards! (change getnumchan in dataset then)
		ArrayList<ChannelInfo> chaninfo=p.getChannelInfo();
		ArrayList<String> newCname=new ArrayList<String>();
		for(ChannelInfo ci:chaninfo)
			newCname.add(ci.formatName());
				
		//If channels have changed then overhaul the matrix
		if(!cnames.equals(newCname))
			{
			//Make a new diagonal matrix
			int n=newCname.size();
			double[][] newmat=new double[n][n];
			for(int i=0;i<n;i++)
				newmat[i][i]=1;
			
			//Copy old values whenever possible
			for(int i=0;i<n;i++)
				for(int j=0;j<n;j++)
					{
					int oldi=cnames.indexOf(newCname.get(i));
					int oldj=cnames.indexOf(newCname.get(j));
					if(oldi>=0 && oldj>=0)
						newmat[i][j] = matrix[oldi][oldj];
					}
			cnames=newCname;
			matrix=newmat;
			
			System.out.println("Updating compensation matrix");
			}
		}
	

	public void apply(FacsanaduProject p)
		{
		//Update transformed signal
		for(Dataset ds:p.datasets)
			apply(ds);
		}
	
	public void apply(Dataset ds)
		{
		//If different datasets have different channels, could deal with it here
		int n=matrix.length; 

		//Multiply each event with the unmixing matrix
		ArrayList<double[]> out=new ArrayList<double[]>(ds.eventsFloat.size());
		for(int oi=0;oi<ds.eventsFloat.size();oi++)
			{
			double[] from=ds.eventsFloat.get(oi);
			double[] to=multiply(matrix, from);//
			out.add(to);
			}
		ds.eventsFloatCompensated=out;
		ds.numCompensated=n;
		}
	
	private static double[] multiply(double[][] m, double[] v)
		{
		double[] to=new double[v.length];
		//Assumes a square matrix. TODO: might not be square any more!
		for(int i=0;i<v.length;i++)
			{
			double sum=0;
			for(int j=0;j<v.length;j++)
				sum += m[i][j]*v[j];
			to[i]=sum;
			}
		return to;
		}

	public double get(int to, int from)
		{
		return matrix[to][from];
		}

	public void set(int to, int from, double v)
		{
		matrix[to][from]=v;
		}

	public double[][] getMatrix()
		{
		return matrix;
		}

	/**
	 * Set the matrix. No safety checks
	 */
	public void setMatrix(double[][] m)
		{
		matrix=m;
		}
	
	
	}
