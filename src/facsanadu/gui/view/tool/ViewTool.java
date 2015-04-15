package facsanadu.gui.view.tool;

import com.trolltech.qt.gui.QMouseEvent;

/**
 * 
 * Tool for the view widget
 * 
 * @author Johan Henriksson
 *
 */
public interface ViewTool
	{
	public void mousePressEvent(QMouseEvent event);
	public void mouseReleaseEvent(QMouseEvent ev);
	public void mouseMoveEvent(QMouseEvent event);
	public void mouseDoubleClickEvent(QMouseEvent event);
	public boolean allowHandle();
	}
