package quickfacs.gui.view.gate;

import quickfacs.gates.Gate;
import quickfacs.gates.GatePolygon;
import quickfacs.gui.view.ViewSettings;
import quickfacs.gui.view.ViewTransform;

import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPolygonF;

/**
 * 
 * Renderer for polygon gates
 * 
 * @author Johan Henriksson
 *
 */
public class GateRendererPoly implements GateRenderer
	{
	public void render(Gate gate, QPainter p, ViewTransform w, ViewSettings viewsettings)
		{
		GatePolygon cg=(GatePolygon)gate;
		
		//Figure out which dimension is what
		double thex[]=new double[cg.getNumPoints()];
		double they[]=new double[cg.getNumPoints()];
		
		if(viewsettings.indexX==cg.indexX)
			for(int i=0;i<cg.getNumPoints();i++)
				thex[i]=cg.arrX.get(i);
		if(viewsettings.indexY==cg.indexX)
			for(int i=0;i<cg.getNumPoints();i++)
				they[i]=cg.arrX.get(i);
		
		if(viewsettings.indexX==cg.indexY)
			for(int i=0;i<cg.getNumPoints();i++)
				thex[i]=cg.arrY.get(i);
		if(viewsettings.indexY==cg.indexY)
			for(int i=0;i<cg.getNumPoints();i++)
				they[i]=cg.arrY.get(i);

		QPolygonF poly=new QPolygonF();
		for(int i=0;i<cg.getNumPoints();i++)
			poly.add(w.mapFacsToScreenX(thex[i]), w.mapFacsToScreenY(they[i]));

		p.drawPolygon(poly);
		p.drawText(poly.first(), gate.name);
		}

	}
