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
	//How far the graph is pushed from the boundary (where labels are placed)
	public int graphOffsetXY=30;
	
	public ViewSettings viewsettings;
	private int internalHeight;
	private int internalWidth;

	/**
	 * Set the total view height
	 */
	public void setTotalHeight(int h)
		{
		internalHeight=h-graphOffsetXY;
		}
	
	/**
	 * Set the total view width
	 */
	public void setTotalWidth(int w)
		{
		internalWidth=w-graphOffsetXY;
		}

	/**
	 * Get the total height of the view
	 */
	public int getTotalHeight()
		{
		return graphOffsetXY+internalHeight;
		}
	
	/**
	 * Set the total width of the view
	 */
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

	/**
	 * Map screen space to FCS value
	 */
	public QPointF mapScreenToFcs(QPointF pos)
		{
		int h=internalHeight-1;
		double x=(pos.x()-graphOffsetXY)/getTotalScaleX();
		double y=(h -pos.y())/getTotalScaleY();
		x=viewsettings.transformation.invert(x, viewsettings.indexX);
		y=viewsettings.transformation.invert(y, viewsettings.indexY);
		QPointF p=new QPointF(x,y);
		return p;
		}

	/**
	 * Map FCS value to screen space
	 */
	public QPointF mapFcsToScreen(QPointF pos)
		{
//		int h=internalHeight-1;
		QPointF p=new QPointF(
				mapFcsToScreenX(pos.x()),//  pos.x()*getTotalScaleX()+graphOffsetXY,
				mapFcsToScreenY(pos.y())//h - pos.y()*getTotalScaleY()
				);
		return p;
		}

	public int mapFcsToScreenX(double x)
		{
		x=viewsettings.transformation.perform(x, viewsettings.indexX);
		return mapGeneralToScreenX(viewsettings.scaleX*x);
		}

	public int mapFcsToScreenY(double y)
		{
		y=viewsettings.transformation.perform(y, viewsettings.indexY);
		return mapGeneralToScreenY(viewsettings.scaleY*y);
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

	/*
	public double scaleScreenToFCSx(double dx)
		{
		return dx/getTotalScaleX();
		}
	public double scaleScreenToFCSy(double dy)
		{
		return dy/getTotalScaleY();
		}
*/

	
	}
