package facsanadu.gui.view.tool;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.Qt.MouseButton;
import com.trolltech.qt.gui.QMouseEvent;

import facsanadu.gates.GatePolygon;
import facsanadu.gui.events.EventGatesChanged;
import facsanadu.gui.events.EventGatesMoved;
import facsanadu.gui.events.FacsanaduEvent;
import facsanadu.gui.view.ViewWidget;

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

	
	/**
	 * Mouse button pressed
	 */
	public void mousePressEvent(QMouseEvent event)
		{
		if(event.button()==MouseButton.LeftButton && !w.viewsettings.isHistogram())
			{
			QPointF p = w.trans.mapScreenToFcs(event.posF()); 
			
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
				w.addGate(g);
				g.setUniqueColor();
				}
			g.addPoint(p.x(), p.y());
			if(justcreated)
				g.addPoint(p.x(), p.y());
			g.updateInternal();
			
//			if(justcreated)
	//			w.sendEvent(new EventGatesChanged());				
		//	else
				w.sendEvent(new EventGatesMoved());
			}
		
		}

	/**
	 * Mouse button released
	 */
	public void mouseReleaseEvent(QMouseEvent ev)
		{
		emitEvent(new EventGatesChanged());
		}

	public void emitEvent(FacsanaduEvent e)
		{
		w.mainWindow.handleEvent(e);
		}

	/**
	 * Mouse moved
	 */
	public void mouseMoveEvent(QMouseEvent event)
		{
		if(isDrawing!=null)
			{
			GatePolygon g=isDrawing;
			
			QPointF p = w.trans.mapScreenToFcs(event.posF()); 

			g.setPoint(g.getNumPoints()-1, p.x(), p.y());
			g.updateInternal();
			emitEvent(new EventGatesMoved());
			}
		}


	/**
	 * Mouse button double-clicked
	 */
	public void mouseDoubleClickEvent(QMouseEvent event)
		{
		if(isDrawing!=null)
			{
			isDrawing.removeRedundantPoints();
			isDrawing=null;
			w.sendEvent(new EventSetViewTool(ViewToolChoice.SELECT));
			emitEvent(new EventGatesChanged());
			}
		}


	@Override
	public boolean allowHandle()
		{
		return isDrawing==null;
		}

	
	
	
	}
