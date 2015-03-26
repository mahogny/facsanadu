package quickfacs.gui.view.tool;

import com.trolltech.qt.gui.QMouseEvent;

public interface ViewTool
	{
	public void mousePressEvent(QMouseEvent event);
	public void mouseReleaseEvent(QMouseEvent ev);
	public void mouseMoveEvent(QMouseEvent event);
	public void mouseDoubleClickEvent(QMouseEvent event);
	}
