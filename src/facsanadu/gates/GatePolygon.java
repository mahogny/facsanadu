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
		}
	
	public boolean classify(double[] obs)
		{
		return poly.containsPoint(new QPointF(obs[indexX], obs[indexY]),FillRule.WindingFill);
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

	public void removeRedundantPoints()
		{
		for(int i=1;i<getNumPoints();)
			if(arrX.get(i)==arrX.get(i-1) && arrY.get(i)==arrY.get(i-1))
				{
				arrX.remove(i);
				arrY.remove(i);
				}
			else
				i++;
		}
	
	}
