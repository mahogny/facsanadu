package facsanadu.gui.view.tool;

import com.trolltech.qt.gui.QMouseEvent;

import facsanadu.gui.view.ViewWidget;

/**
 * 
 * Tool to do nothing except selecting
 * 
 * @author Johan Henriksson
 *
 */
public class ViewToolDrawSelect implements ViewTool
	{
	ViewWidget w;
	public ViewToolDrawSelect(ViewWidget w)
		{
		this.w=w;
		}
	
	/**
	 * Mouse button released
	 */
	public void mouseReleaseEvent(QMouseEvent ev)
		{
		}

	/**
	 * Mouse moved
	 */
	public void mouseMoveEvent(QMouseEvent event)
		{
		}

	
	/**
	 * Mouse button pressed
	 */
	public void mousePressEvent(QMouseEvent event)
		{
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
		return true;
		}

	}
