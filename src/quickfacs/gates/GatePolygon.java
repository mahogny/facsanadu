package quickfacs.gates;

import java.util.ArrayList;

import com.trolltech.qt.core.QPointF;
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
		for(int i=0;i<arrX.size();i++)
			poly.add(arrX.get(i), arrY.get(i));
		}
	
	public boolean classify(double[] obs)
		{
		return poly.contains(new QPointF(obs[indexX], obs[indexY]));
		}

	public void addPoint(double x, double y)
		{
		arrX.add(x);
		arrY.add(y);
		}
	
	}
