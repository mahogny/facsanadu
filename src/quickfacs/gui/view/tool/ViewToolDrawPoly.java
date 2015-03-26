package quickfacs.gui.view.tool;

import quickfacs.gates.GatePolygon;
import quickfacs.gui.events.EventGatesChanged;
import quickfacs.gui.events.EventGatesMoved;
import quickfacs.gui.view.ViewWidget;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.Qt.MouseButton;
import com.trolltech.qt.gui.QMouseEvent;

/**
 * 
 * Tool to draw polygon gates
 * 
 * @author Johan Henriksson
 *
 */
public class ViewToolDrawPoly implements ViewTool
	{
	private GatePolygon isDrawing=null;

	ViewWidget w;
	public ViewToolDrawPoly(ViewWidget w)
		{
		this.w=w;
		}

	
	public void mousePressEvent(QMouseEvent event)
		{
		if(event.button()==MouseButton.LeftButton)
			{
			QPointF p = w.trans.mapScreenToFacs(event.posF()); 
			
			boolean justcreated=false;
			GatePolygon g;
			if(isDrawing!=null)
				g=isDrawing;
			else
				{
				justcreated=true;
				g=new GatePolygon();
				g.indexX=w.getIndexX();
				g.indexY=w.getIndexY();
				isDrawing=g;
				w.mw.addGate(g);
				}
			g.addPoint(p.x(), p.y());
			if(justcreated)
				g.addPoint(p.x(), p.y());
			g.updateInternal();
			
			if(justcreated)
				w.sendEvent(new EventGatesChanged());				
			else
				w.sendEvent(new EventGatesMoved());
			}
		
		}

	public void mouseReleaseEvent(QMouseEvent ev)
		{
//		isDrawing=null;
//		w.mw.handleEvent(new EventGatesMoved());
		}

	public void mouseMoveEvent(QMouseEvent event)
		{
		if(isDrawing!=null)
			{
			GatePolygon g=isDrawing;
			
			QPointF p = w.trans.mapScreenToFacs(event.posF()); 

			g.setPoint(g.getNumPoints()-1, p.x(), p.y());
			g.updateInternal();
			w.sendEvent(new EventGatesMoved());
			}
		}


	@Override
	public void mouseDoubleClickEvent(QMouseEvent event)
		{
		if(isDrawing!=null)
			{
			isDrawing.removeRedundantPoints();
			isDrawing=null;
			}
		}

	
	
	
	}
