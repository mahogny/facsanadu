package facsanadu.gui.view.tool;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.Qt.MouseButton;
import com.trolltech.qt.gui.QMouseEvent;

import facsanadu.gates.Gate;
import facsanadu.gates.GateRect;
import facsanadu.gui.events.EventGatesChanged;
import facsanadu.gui.events.EventGatesMoved;
import facsanadu.gui.events.FacsanaduEvent;
import facsanadu.gui.view.ViewWidget;

/**
 * 
 * Tool to draw rectangle gates
 * 
 * @author Johan Henriksson
 *
 */
public class ViewToolDrawRect implements ViewTool
	{
	private Gate isDrawing=null;

	ViewWidget w;
	public ViewToolDrawRect(ViewWidget w)
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
			GateRect grect=(GateRect)isDrawing;
			
			QPointF p = w.trans.mapScreenToFcs(event.posF()); 
			
			grect.x2=p.x();
			grect.y2=p.y();
			grect.updateInternal();
			emitEvent(new EventGatesMoved());
			}
		}

	
	/**
	 * Mouse button pressed
	 */
	public void mousePressEvent(QMouseEvent event)
		{
		if(event.button()==MouseButton.LeftButton && !w.viewsettings.isHistogram())
			{
			QPointF p = w.trans.mapScreenToFcs(event.posF()); 
			
			GateRect grect=new GateRect();
			grect.indexX=w.getIndexX();
			grect.indexY=w.getIndexY();
			grect.x1=grect.x2=p.x();
			grect.y1=grect.y2=p.y();
			grect.updateInternal();
			isDrawing=grect;

			w.addGate(grect);
			grect.setUniqueColor();
			w.sendEvent(new EventGatesMoved());
			}
		
		}

	/**
	 * Mouse button double-clicked
	 */
	public void mouseDoubleClickEvent(QMouseEvent event)
		{
		}
	
	
	@Override
	public boolean allowHandle()
		{
		return isDrawing==null;
		}

	}
