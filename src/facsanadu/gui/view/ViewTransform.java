package facsanadu.gui.view;

import com.trolltech.qt.core.QPointF;

public class ViewTransform
	{

	public int graphOffsetXY=30;
	
	//note: previously used height() and width()
	public ViewSettings viewsettings;
	public int height;
	public int width;

	public double getTotalScaleX()
		{
		return viewsettings.scaleX*width;
		}
	public double getTotalScaleY()
		{
		return viewsettings.scaleY*height;
		}

	public QPointF mapScreenToFacs(QPointF pos)
		{
		int h=height-graphOffsetXY-1;
		QPointF p=new QPointF(
				(pos.x()-graphOffsetXY)/getTotalScaleX(),
				(h -pos.y())/getTotalScaleY()
				);
		return p;
		}

	public QPointF mapFacsToScreen(QPointF pos)
		{
		int h=height-graphOffsetXY-1;
		QPointF p=new QPointF(
				pos.x()*getTotalScaleX()+graphOffsetXY,
				h - pos.y()*getTotalScaleY()
				);
		return p;
		}

	public int mapFacsToScreenX(double x)
		{
		return graphOffsetXY+(int)(getTotalScaleX()*x);
		}

	public int mapFacsToScreenY(double y)
		{
		int h=height-graphOffsetXY-1;
		return h-((int)(getTotalScaleY()*y));
		}

	}
