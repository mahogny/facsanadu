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
		QPointF p=new QPointF(
				(pos.x()-graphOffsetXY)/getTotalScaleX(),
				(h -pos.y())/getTotalScaleY()
				);
		return p;
		}

	/**
	 * Map FCS value to screen space
	 */
	public QPointF mapFcsToScreen(QPointF pos)
		{
		int h=internalHeight-1;
		QPointF p=new QPointF(
				pos.x()*getTotalScaleX()+graphOffsetXY,
				h - pos.y()*getTotalScaleY()
				);
		return p;
		}

	public int mapFcsToScreenX(double x)
		{
		return mapGeneralToScreenX(viewsettings.scaleX*x);
		}

	public int mapFcsToScreenY(double y)
		{
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

	public double scaleScreenToFCSx(double dx)
		{
		return dx/getTotalScaleX();
		}
	public double scaleScreenToFCSy(double dy)
		{
		return dy/getTotalScaleY();
		}


	
	}
