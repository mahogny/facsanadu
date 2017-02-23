package facsanadu.gates;

import java.util.ArrayList;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.Qt.FillRule;
import com.trolltech.qt.gui.QPolygonF;

/**
 * 
 * Polygon gate
 * 
 * @author Johan Henriksson
 *
 */
public class GatePolygon extends Gate
	{
	public int indexX, indexY;

	public ArrayList<Double> arrX=new ArrayList<Double>();
	public ArrayList<Double> arrY=new ArrayList<Double>();

	private QPolygonF poly;
	
	public void updateInternal()
		{
		poly=new QPolygonF();
		for(int i=0;i<getNumPoints();i++)
			poly.add(arrX.get(i), arrY.get(i));
		setUpdated();
		}
	

	public boolean classify(double[] obs)
		{
		if(poly!=null)
			return poly.containsPoint(new QPointF(obs[indexX], obs[indexY]),FillRule.WindingFill);
		else
			return false;
		}

	public void addPoint(double x, double y)
		{
		arrX.add(x);
		arrY.add(y);
		}

	public int getNumPoints()
		{
		return arrX.size();
		}

	public void setPoint(int i, double x, double y)
		{
		arrX.set(i, x);
		arrY.set(i, y);
		}

	
	private static boolean almostEqual(double x, double y)
		{
		return Math.abs(x-y)<0.001; //good value??
		}
	
	public void removeRedundantPoints()
		{
		for(int i=1;i<getNumPoints();)
			if(almostEqual(arrX.get(i),arrX.get(i-1)) && almostEqual(arrY.get(i),arrY.get(i-1)))
				{
				arrX.remove(i);
				arrY.remove(i);
				}
			else
				i++;
		}
	
	}
