package facsanadu.gui.view.tool;

import io.qt.core.QPointF;
import io.qt.core.Qt.MouseButton;
import io.qt.gui.QMouseEvent;

import facsanadu.gates.Gate;
import facsanadu.gates.GateEllipse;
import facsanadu.gui.events.EventGatesChanged;
import facsanadu.gui.events.EventGatesMoved;
import facsanadu.gui.events.FacsanaduEvent;
import facsanadu.gui.view.ViewWidget;

/**
 * 
 * Tool to draw ellipse gates
 * 
 * @author Johan Henriksson
 *
 */
public class ViewToolDrawEllipse implements ViewTool
	{
	private Gate isDrawing=null;

	ViewWidget w;
	public ViewToolDrawEllipse(ViewWidget w)
		{
		this.w=w;
		}
	
	/**
	 * Mouse button released
	 */
	public void mouseReleaseEvent(QMouseEvent ev)
		{
		isDrawing=null;
		emitEvent(new EventGatesChanged());
		w.sendEvent(new EventSetViewTool(ViewToolChoice.SELECT));
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
			GateEllipse grect=(GateEllipse)isDrawing;
			
			QPointF p = w.trans.mapScreenToFcs(event.position());
			
			grect.rx=p.x()-grect.x;
			grect.ry=p.y()-grect.y;
			grect.updateInternal();
			w.sendEvent(new EventGatesMoved());
			}
		}

	
	/**
	 * Mouse button pressed
	 */
	public void mousePressEvent(QMouseEvent event)
		{
		if(event.button()==MouseButton.LeftButton && !w.viewsettings.isHistogram())
			{
			QPointF p = w.trans.mapScreenToFcs(event.position());
			
			GateEllipse grect=new GateEllipse();
			grect.indexX=w.getIndexX();
			grect.indexY=w.getIndexY();
			grect.x=p.x();
			grect.y=p.y();
			grect.updateInternal();
			isDrawing=grect;

			w.addGate(grect);
			grect.setUniqueColor();
			emitEvent(new EventGatesMoved());
			}
		
		}

	/**
	 * Mouse button double-clicked
	 */
	public void mouseDoubleClickEvent(QMouseEvent event)
		{
		}
	
	
	public boolean allowHandle()
		{
		return isDrawing==null;
		}

	}
