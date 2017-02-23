package facsanadu.gui.view.gate;

import java.util.Collection;

import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPolygonF;

import facsanadu.gates.Gate;
import facsanadu.gates.GatePolygon;
import facsanadu.gui.MainWindow;
import facsanadu.gui.events.EventGatesMoved;
import facsanadu.gui.view.ViewSettings;
import facsanadu.gui.view.ViewTransform;

/**
 * 
 * Renderer for polygon gates
 * 
 * @author Johan Henriksson
 *
 */
public class GateRendererPoly implements GateRenderer
	{
	public void render(final Gate gate, QPainter p, final ViewTransform w, final ViewSettings viewsettings, Collection<GateHandle> handles)
		{
		final GatePolygon cg=(GatePolygon)gate;
		if(viewsettings.coversXandY(cg.indexX, cg.indexY))
			{
			//Figure out which dimension is what
			double thex[]=new double[cg.getNumPoints()];
			double they[]=new double[cg.getNumPoints()];
			
			if(viewsettings.indexX==cg.indexX)
				for(int i=0;i<cg.getNumPoints();i++)
					thex[i]=cg.arrX.get(i);
			if(viewsettings.indexY==cg.indexX)
				for(int i=0;i<cg.getNumPoints();i++)
					they[i]=cg.arrX.get(i);
			
			if(viewsettings.indexX==cg.indexY)
				for(int i=0;i<cg.getNumPoints();i++)
					thex[i]=cg.arrY.get(i);
			if(viewsettings.indexY==cg.indexY)
				for(int i=0;i<cg.getNumPoints();i++)
					they[i]=cg.arrY.get(i);

			QPolygonF poly=new QPolygonF();
			for(int i=0;i<cg.getNumPoints();i++)
				{
				final int x=w.mapFcsToScreenX(thex[i]);
				final int y=w.mapFcsToScreenY(they[i]);
				final int ii=i;
				poly.add(x, y);

				if(!viewsettings.transformation.isEmpty())
					{
					//If rendering in log space then break down the line into segments
					int nseg=15;
					for(int j=1;j<nseg;j++)
						{
						double pa=j/(double)nseg;
						double pb=1-pa;
						
						int i2=(i+1)%cg.getNumPoints();
						
						double vx=pb*thex[i] + pa*thex[i2];
						double vy=pb*they[i] + pa*they[i2];
										
						int x2=w.mapFcsToScreenX(vx);
						int y2=w.mapFcsToScreenY(vy);
						poly.add(x2, y2);
						}
					}
				GateHandle handle=new GateHandle()
					{
					public void move2(MainWindow w, double dx, double dy)
						{
						if(viewsettings.indexX==cg.indexX)
							cg.arrX.set(ii, dx);
						else if(viewsettings.indexY==cg.indexX)
							cg.arrY.set(ii, dx);
						
						if(viewsettings.indexX==cg.indexY)
							cg.arrX.set(ii, dy);
						else if(viewsettings.indexY==cg.indexY)
							cg.arrY.set(ii, dy);
						
						gate.updateInternal();
						w.handleEvent(new EventGatesMoved());
						}

					public double getX()
						{
						return x;
						}

					public double getY()
						{
						return y;
						}
					};
				handles.add(handle);
				}

			p.drawPolygon(poly);
			p.drawText(poly.first(), gate.name);
			}
		}

	}
