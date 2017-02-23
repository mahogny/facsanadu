package facsanadu.gui.view.gate;

import java.util.Collection;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.gui.QPainter;

import facsanadu.gates.Gate;
import facsanadu.gates.GateRect;
import facsanadu.gui.MainWindow;
import facsanadu.gui.events.EventGatesMoved;
import facsanadu.gui.view.ViewSettings;
import facsanadu.gui.view.ViewTransform;

/**
 * 
 * Renderer for rectangular gates
 * 
 * @author Johan Henriksson
 *
 */
public class GateRendererRect implements GateRenderer
	{
	public void render(final Gate gate, QPainter p, ViewTransform w, final ViewSettings viewsettings, Collection<GateHandle> handles)
		{
		final GateRect cg=(GateRect)gate;
		if(viewsettings.coversXandY(cg.indexX, cg.indexY))
			{
			//Figure out which dimension is what
			final double x[]=new double[]{0,0};
			final double y[]=new double[]{0,0};
			if(viewsettings.indexX==cg.indexX)
				{
				x[0]=cg.x1;
				x[1]=cg.x2;
				}
			if(viewsettings.indexY==cg.indexX)
				{
				y[0]=cg.x1;
				y[1]=cg.x2;
				}
			
			if(viewsettings.indexX==cg.indexY)
				{
				x[0]=cg.y1;
				x[1]=cg.y2;
				}
			if(viewsettings.indexY==cg.indexY)
				{
				y[0]=cg.y1;
				y[1]=cg.y2;
				}

			final QPointF p1=w.mapFcsToScreen(new QPointF(x[0],y[0]));
			final QPointF p2=w.mapFcsToScreen(new QPointF(x[1],y[1]));
			
			p.drawRect(new QRectF(p1,p2));
			p.drawText(p1, gate.name);

			
			//Upper left handle
			handles.add(new GateHandle()
				{
				public void move2(MainWindow w, double dx, double dy)
					{
					if(viewsettings.indexX==cg.indexX)
						cg.x1=dx;
					else if(viewsettings.indexY==cg.indexX)
						cg.y1=dx;
					
					if(viewsettings.indexX==cg.indexY)
						cg.x1=dy;
					else if(viewsettings.indexY==cg.indexY)
						cg.y1=dy;
					
					gate.updateInternal();
					w.handleEvent(new EventGatesMoved());
					}

				public double getX()
					{
					return p1.x();//x[0];
					}

				public double getY()
					{
					return p1.y();//y[0];
					}
				});

			//Lower right
			handles.add(new GateHandle()
				{
				public void move2(MainWindow w, double dx, double dy)
					{
					if(viewsettings.indexX==cg.indexX)
						cg.x2=dx;
					else if(viewsettings.indexY==cg.indexX)
						cg.y2=dx;
					
					if(viewsettings.indexX==cg.indexY)
						cg.x2=dy;
					else if(viewsettings.indexY==cg.indexY)
						cg.y2=dy;
					
					gate.updateInternal();
					w.handleEvent(new EventGatesMoved());
					}

				public double getX()
					{
					return p2.x();
					}

				public double getY()
					{
					return p2.y();
					}
				});

			}
		}

	}
