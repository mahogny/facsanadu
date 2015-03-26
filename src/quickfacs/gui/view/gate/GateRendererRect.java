package quickfacs.gui.view.gate;

import quickfacs.gates.Gate;
import quickfacs.gates.GateRect;
import quickfacs.gui.view.ViewSettings;
import quickfacs.gui.view.ViewTransform;
import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.gui.QPainter;

/**
 * 
 * Renderer for rectangular gates
 * 
 * @author Johan Henriksson
 *
 */
public class GateRendererRect implements GateRenderer
	{
	
	
	
	public void render(Gate gate, QPainter p, ViewTransform w, ViewSettings viewsettings)
		{
		GateRect cg=(GateRect)gate;
		
		//Figure out which dimension is what
		double x1=0,x2=0, y1=0, y2=0;
		if(viewsettings.indexX==cg.indexX)
			{
			x1=cg.x1;
			x2=cg.x2;
			}
		if(viewsettings.indexY==cg.indexX)
			{
			y1=cg.x1;
			y2=cg.x2;
			}
		
		if(viewsettings.indexX==cg.indexY)
			{
			x1=cg.y1;
			x2=cg.y2;
			}
		if(viewsettings.indexY==cg.indexY)
			{
			y1=cg.y1;
			y2=cg.y2;
			}

		QPointF p1=w.mapFacsToScreen(new QPointF(x1,y1));
		QPointF p2=w.mapFacsToScreen(new QPointF(x2,y2));
		
		p.drawRect(new QRectF(p1,p2));
		p.drawText(p1, gate.name);
		}

	}
