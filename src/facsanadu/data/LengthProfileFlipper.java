package facsanadu.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LengthProfileFlipper
	{
	/**
	 * Flip profiles to align them. Algorithm as in Dupuy et al
	 */
	public static void run(Dataset ds)
		{
		//First sort by size
		ArrayList<LengthProfileData> prof=new ArrayList<LengthProfileData>(ds.lengthprofsData);
		Collections.sort(prof,new Comparator<LengthProfileData>()
			{
			@Override
			public int compare(LengthProfileData arg0, LengthProfileData arg1)
				{
				if(arg0.getLength()<arg1.getLength())
					return 1;
				else if(arg0.getLength()>arg1.getLength())
					return -1;
				else
					return 0;
				}
			});
//		for(LengthProfileData d:prof)
	//		System.out.println(d.getLength());

		
		ArrayList<LengthProfileData> out=new ArrayList<LengthProfileData>(prof.size());
		int curremove=0;
		if(!prof.isEmpty())
			{
			//Pick the initial profile
			LengthProfileData fst=prof.get(curremove);
			out.add(fst);
			curremove++;

			//For all other profiles
			for(;curremove<prof.size();curremove++)
				{
				LengthProfileData p=prof.get(curremove);
				double[][] pFlipped=p.getFlipped();
				
				//Compare to the last 20 profiles by pearson
				double v1=0, v2=0;
				for(int i=Math.max(0, out.size()-20); i<out.size();i++)
					{
					LengthProfileData comp=prof.get(curremove);
					v1+=pearson(comp.data, p.data);
					v2+=pearson(comp.data, pFlipped);
					}
				//Flip if it makes sense
				if(v2>v1)
					p.flip(pFlipped);
				out.add(p);
				}
			
			
			
			//Normalize what is left. Waited until here to have enough profiles to get a stable estimate (even if slower)
			int totest=Math.min(out.size(), 11);
			int onleft=0;
			for(int i=0;i<totest;i++)
				{
				double[] a=out.get(i).data[0];
				double sumxp=0;
				double sumx=0;
				for(int j=0;j<a.length;j++)
					{
					sumxp+=a[j]*j;
					sumx+=a[j];
					}
				double cog=sumxp/sumx;
				if(cog<a.length/2.0)
					onleft++;
				}
			if(onleft<totest/2.0)
				{
				for(LengthProfileData p:out)
					p.flip(p.getFlipped());
				}
			}
		}
	

	
	
	
	public static double pearson(double[][] a, double[][] b)
		{
		//this should maybe be a user choice
		double sum=0;
		for(int i=0;i<a.length;i++)
			sum+=pearson(a[i],b[i]);
		return sum;
		}

	/**
	 * Compare two profiles alone. assumes a is the long vector
	 */
	public static double pearson(double[] a, double[] b)
		{
		double suma=0, sumb=0, sumaa=0, sumbb=0, sumab=0;
		double n=b.length;
		for(int i=0;i<n;i++)
			{
			int scalei=i*a.length/b.length;
			double va=a[scalei];
			double vb=b[i];
			suma+=va;
			sumb+=vb;
			sumaa+=va*va;
			sumbb+=vb*vb;
			sumab+=va*vb;
			}
		return (sumab-suma*sumb/n)/Math.sqrt((sumaa-suma*suma/n)*(sumbb-sumb*sumb/n));
		}

	}
