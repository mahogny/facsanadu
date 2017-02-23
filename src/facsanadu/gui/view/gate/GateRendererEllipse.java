package facsanadu.gui.view.gate;

import java.util.Collection;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPolygonF;

import facsanadu.gates.Gate;
import facsanadu.gates.GateEllipse;
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
public class GateRendererEllipse implements GateRenderer
	{
	public void render(final Gate gate, QPainter p, ViewTransform w, final ViewSettings viewsettings, Collection<GateHandle> handles)
		{
		final GateEllipse cg=(GateEllipse)gate;
		if(viewsettings.coversXandY(cg.indexX, cg.indexY))
			{
			//Figure out which dimension is what
			final double x[]=new double[]{0,0};
			final double y[]=new double[]{0,0};
			if(viewsettings.indexX==cg.indexX)
				{
				x[0]=cg.x;
				x[1]=cg.rx;
				}
			if(viewsettings.indexY==cg.indexX)
				{
				y[0]=cg.x;
				y[1]=cg.rx;
				}
			
			if(viewsettings.indexX==cg.indexY)
				{
				x[0]=cg.y;
				x[1]=cg.ry;
				}
			if(viewsettings.indexY==cg.indexY)
				{
				y[0]=cg.y;
				y[1]=cg.ry;
				}


			final QPointF pHm=w.mapFcsToScreen(new QPointF(x[0],     y[0]));
			final QPointF pHx=w.mapFcsToScreen(new QPointF(x[0]+x[1],y[0]));
			final QPointF pHy=w.mapFcsToScreen(new QPointF(x[0],     y[0]+y[1]));

			
			final QPointF p1=w.mapFcsToScreen(new QPointF(x[0]-x[1],y[0]-y[1]));
			final QPointF p2=w.mapFcsToScreen(new QPointF(x[0]+x[1],y[0]+y[1]));
			if(!viewsettings.transformation.isEmpty())
				{
				QPolygonF poly=new QPolygonF();
				int ns=64;
				for(int i=0;i<ns;i++)
					{
					double alpha=2*Math.PI*i/ns;
					QPointF pv=w.mapFcsToScreen(new QPointF(x[0]+Math.cos(alpha)*x[1],y[0]+Math.sin(alpha)*y[1]));
					poly.add(pv);
					}
				
				p.drawPolygon(poly);
				}
			else
				{
				p.drawEllipse(new QRectF(p1,p2));
				}
			p.drawText(p1, gate.name);

			//Mid handle
			handles.add(new GateHandle()
				{
				public void move2(MainWindow w, double dx, double dy)
					{
					if(viewsettings.indexX==cg.indexX)
						cg.x=dx;
					else if(viewsettings.indexY==cg.indexX)
						cg.y=dx;
					
					if(viewsettings.indexX==cg.indexY)
						cg.x=dy;
					else if(viewsettings.indexY==cg.indexY)
						cg.y=dy;
					
					gate.updateInternal();
					w.handleEvent(new EventGatesMoved());
					}

				public double getX()
					{
					return pHm.x();
					}

				public double getY()
					{
					return pHm.y();
					}
				});

			//Right
			handles.add(new GateHandle()
				{
				public void move2(MainWindow w, double dx, double dy)
					{
					if(viewsettings.indexX==cg.indexX)
						cg.rx=Math.abs(dx-cg.x);
					else if(viewsettings.indexY==cg.indexX)
						cg.ry=Math.abs(dx-cg.x);
					gate.updateInternal();
					w.handleEvent(new EventGatesMoved());
					}

				public double getX()
					{
					return pHx.x();
					}
				public double getY()
					{
					return pHx.y();
					}
				});
			

			//Bottom
			handles.add(new GateHandle()
				{
				public void move2(MainWindow w, double dx, double dy)
					{
					if(viewsettings.indexX==cg.indexY)
						cg.rx=Math.abs(dy-cg.x);
					else if(viewsettings.indexY==cg.indexY)
						cg.ry=Math.abs(dy-cg.y);
					
					gate.updateInternal();
					w.handleEvent(new EventGatesMoved());
					}

				public double getX()
					{
					return pHy.x();
					}
				public double getY()
					{
					return pHy.y();
					}
				});

			
			}
		}

	}
