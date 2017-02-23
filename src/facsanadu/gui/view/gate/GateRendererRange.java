package facsanadu.gui.view.gate;

import java.util.Collection;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.gui.QPainter;

import facsanadu.gates.Gate;
import facsanadu.gates.GateRange;
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
public class GateRendererRange implements GateRenderer
	{
	public void render(final Gate gate, QPainter p, ViewTransform w, final ViewSettings viewsettings, Collection<GateHandle> handles)
		{
		final GateRange cg=(GateRange)gate;
		if(viewsettings.coversX(cg.index))
			{
			//Figure out which dimension is what
			final double x[]=new double[]{0,0};
			x[0]=cg.x1;
			x[1]=cg.x2;
			
			final double[] pH;
			if(viewsettings.indexX==cg.index)
				{
				int e=10000;
				final QPointF p1a=w.mapFcsToScreen(new QPointF(x[0],0));
				final QPointF p1b=w.mapFcsToScreen(new QPointF(x[0],e));
				p1a.setY(0);
				p1b.setY(10000);
				p.drawLine(p1a,p1b);
				
				final QPointF p2a=w.mapFcsToScreen(new QPointF(x[1],0));
				final QPointF p2b=w.mapFcsToScreen(new QPointF(x[1],e));
				p2a.setY(0);
				p2b.setY(10000);
				p.drawLine(p2a,p2b);
				
				p1a.setY(120);
				p.drawText(p1a, gate.name);

				pH=new double[]{p1a.x(),p2a.x()};
				}
			else
				{
				int e=10000;
				final QPointF p1a=w.mapFcsToScreen(new QPointF(0, x[0]));
				final QPointF p1b=w.mapFcsToScreen(new QPointF(e, x[0]));
				p1a.setX(0);
				p1b.setX(10000);
				p.drawLine(p1a,p1b);
				
				final QPointF p2a=w.mapFcsToScreen(new QPointF(0, x[1]));
				final QPointF p2b=w.mapFcsToScreen(new QPointF(e, x[1]));
				p2a.setX(0);
				p2b.setX(10000);
				p.drawLine(p2a,p2b);
				
				p1a.setX(100);
				p.drawText(p1a, gate.name);

				pH=new double[]{p1a.y(),p2a.y()};
				}
		
			//Handle 1 & 2
			for(int hi=0;hi<2;hi++)
				{
				final int fhi=hi;
				handles.add(new GateHandle()
					{
					public void move2(MainWindow w, double dx, double dy)
						{
						double x[]=new double[]{cg.x1,cg.x2};
						if(viewsettings.indexX==cg.index)
							x[fhi]=dx;
						else if(viewsettings.indexY==cg.index)
							x[fhi]=dy;
						cg.x1=x[0];
						cg.x2=x[1];

						gate.updateInternal();
						w.handleEvent(new EventGatesMoved());
						}

					public double getX()
						{
						if(viewsettings.indexX==cg.index)
							return pH[fhi];
						else
							return 100;
						}

					public double getY()
						{
						if(viewsettings.indexX==cg.index)
							return 100; //Half way
						else
							return pH[fhi];
						}
					});
				}

			
			
			


			}
		}

	}
