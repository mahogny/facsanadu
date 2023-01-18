package facsanadu.gui.view.tool;

import io.qt.core.QPointF;
import io.qt.core.Qt.MouseButton;
import io.qt.gui.QMouseEvent;

import facsanadu.gates.Gate;
import facsanadu.gates.GateRange;
import facsanadu.gui.events.EventGatesChanged;
import facsanadu.gui.events.EventGatesMoved;
import facsanadu.gui.events.FacsanaduEvent;
import facsanadu.gui.view.ViewWidget;

/**
 * 
 * Tool to draw range gates
 * 
 * @author Johan Henriksson
 *
 */
public class ViewToolDrawRange implements ViewTool
	{
	private Gate isDrawing=null;

	ViewWidget w;
	public ViewToolDrawRange(ViewWidget w)
		{
		this.w=w;
		}
	
	/**
	 * Mouse button released
	 */
	public void mouseReleaseEvent(QMouseEvent ev)
		{
		isDrawing=null;
		w.sendEvent(new EventSetViewTool(ViewToolChoice.SELECT));
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
			GateRange grect=(GateRange)isDrawing;
			
			QPointF p = w.trans.mapScreenToFcs(event.position());
			
			grect.x2=p.x();
			grect.updateInternal();
			w.sendEvent(new EventGatesMoved());
			}
		}

	
	/**
	 * Mouse button pressed
	 */
	public void mousePressEvent(QMouseEvent event)
		{
		if(event.button()==MouseButton.LeftButton)
			{
			QPointF p = w.trans.mapScreenToFcs(event.position());
			
			GateRange grect=new GateRange();
			grect.index=w.getIndexX();
			grect.x1=grect.x2=p.x();
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
