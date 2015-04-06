package facsanadu.gui.view;

import com.trolltech.qt.core.QPointF;

/**
 * Transformation of coordinates view <-> world
 * 
 * @author Johan Henriksson
 *
 */
public class ViewTransform
	{
	public int graphOffsetXY=30;
	
	public ViewSettings viewsettings;
	private int internalHeight;
	private int internalWidth;

	public void setTotalHeight(int h)
		{
		internalHeight=h-graphOffsetXY;
		}
	public void setTotalWidth(int w)
		{
		internalWidth=w-graphOffsetXY;
		}
	
	public int getTotalHeight()
		{
		return graphOffsetXY+internalHeight;
		}
	public int getTotalWidth()
		{
		return graphOffsetXY+internalWidth;
		}
	
	public double getTotalScaleX()
		{
		return viewsettings.scaleX*internalWidth; 
		}
	public double getTotalScaleY()
		{
		return viewsettings.scaleY*internalHeight; 
		}

	public QPointF mapScreenToFacs(QPointF pos)
		{
		int h=internalHeight-1;
		QPointF p=new QPointF(
				(pos.x()-graphOffsetXY)/getTotalScaleX(),
				(h -pos.y())/getTotalScaleY()
				);
		return p;
		}

	public QPointF mapFacsToScreen(QPointF pos)
		{
		int h=internalHeight-1;
		QPointF p=new QPointF(
				pos.x()*getTotalScaleX()+graphOffsetXY,
				h - pos.y()*getTotalScaleY()
				);
		return p;
		}

	public int mapFacsToScreenX(double x)
		{
		return mapGeneralToScreenX(viewsettings.scaleX*x);
		//return graphOffsetXY+(int)(getTotalScaleX()*x);
		}

	public int mapFacsToScreenY(double y)
		{
		return mapGeneralToScreenY(viewsettings.scaleY*y);
//		int h=height-graphOffsetXY-1;
//		return h-((int)(getTotalScaleY()*y));
		}

	
	public int mapGeneralToScreenX(double x)
		{
		return graphOffsetXY+(int)(x*internalWidth);
		}

	public int mapGeneralToScreenY(double y)
		{
		int h=internalHeight-1;
		return h-((int)(y*h));
		}

	
	}
