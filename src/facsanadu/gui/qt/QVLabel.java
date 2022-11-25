package facsanadu.gui.qt;

import io.qt.core.QPoint;
import io.qt.gui.QColor;
import io.qt.gui.QFont;
import io.qt.gui.QFontMetrics;
import io.qt.gui.QPaintEvent;
import io.qt.gui.QPainter;
import io.qt.widgets.QWidget;

/**
 * 
 * Vertical label
 * 
 * @author Johan Henriksson
 *
 */
public class QVLabel extends QWidget
	{
	private String text="abc";

	public QVLabel(QWidget parent)
		{
		super(parent);
		setMinimumWidth(15);
		}
	
	public void setText(String text)
		{
		this.text=text;
		update();
		}
	
	
	@Override
	protected void paintEvent(QPaintEvent e)
		{
		super.paintEvent(e);
		
		QPainter painter=new QPainter(this);
		QFontMetrics fm=new QFontMetrics(painter.font());
		QFont font=painter.font();
		font.setBold(true);
		painter.setFont(font);
    painter.setPen(QColor.black);
    painter.rotate(-90);
    painter.drawText(new QPoint(-(height()-fm.width(text))/2,11), text);
		}

	
	}
