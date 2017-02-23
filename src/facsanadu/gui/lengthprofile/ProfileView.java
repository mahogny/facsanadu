package facsanadu.gui.lengthprofile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRect;
import com.trolltech.qt.core.Qt.MouseButton;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QMouseEvent;
import com.trolltech.qt.gui.QPaintEvent;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPainterPath;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QWidget;

import facsanadu.data.Dataset;
import facsanadu.data.LengthProfileData;
import facsanadu.data.ProfChannel;
import facsanadu.gui.MainWindow;

/**
 * 
 * View showing one length profile
 * 
 */
public class ProfileView extends QWidget
	{
	private Dataset ds;
	private ArrayList<Integer> eventid=new ArrayList<Integer>();
	
	public boolean normalizeLength;
	public ArrayList<Boolean> showchan=new ArrayList<Boolean>();
	public ArrayList<Double> scale=new ArrayList<Double>();
	
	public ProfChannel curchannel=null;
	private MainWindow mw;
	
	public ProfileView(MainWindow mw)
		{
		this.mw=mw;
		setMouseTracking(true);
		}
	
	@Override
	protected void paintEvent(QPaintEvent e)
		{
		super.paintEvent(e);
		QPainter pm=new QPainter(this);
		pm.fillRect(new QRect(0, 0,width(),height()), new QBrush(QColor.fromRgb(255,255,255)));
		
		ArrayList<QColor> colors=new ArrayList<QColor>();
		colors.add(QColor.fromRgb(0,0,0));
		colors.add(QColor.fromRgb(255,0,0));
		colors.add(QColor.fromRgb(0,255,0));
		colors.add(QColor.fromRgb(0,0,255));
		
		int h=height();
		for(int eventid:this.eventid)
			if(ds!=null && eventid>=0 && eventid<ds.getNumObservations())
				{
				for(int profid=0;profid<ds.lengthprofsInfo.size();profid++)
					if(showchan.get(profid))
						{
						//LengthProfile prof=ds.lengthprofsInfo.get(profid);
						LengthProfileData data=ds.lengthprofsData.get(eventid);
						double scaleY=scale.get(profid)*h;
						
						QPen pen=new QPen(colors.get(profid));
						pm.setPen(pen);
						
						int len=data.getLength();
						QPainterPath path=new QPainterPath();
						for(int i=0;i<len;i++)
							{
							double x;
							if(normalizeLength)
								x=i*width()/data.getLength();
							else
								x=i*width()/1050; //what is the maximum length in the set? could be stored in the info
							
							double y=h-1-data.data[profid][i]*scaleY;
							if(i==0)
								path.moveTo(x,y);
							else
								path.lineTo(x,y);
							}
						pm.drawPath(path);
						}
				}
		
		//Draw profile channel
		if(curchannel!=null)
			{
			QPen pen=new QPen(colors.get(curchannel.channel));
			pen.setWidth(2);
			pm.setPen(pen);

			int x1=toViewX(curchannel.from);
			int x2=toViewX(curchannel.to);
			
			pm.drawLine(x1, 3, x1, height());
			pm.drawLine(x2, 3, x2, height());			
			pen.setWidth(0);
			pm.drawLine(x1, 3, x2, 3);
//			pm.drawLine(x1, height()-1, x2, height()-1);
			}
		
		pm.end();
		}

	private int toViewX(int x)
		{
		return x*width()/1050;
		}
	private int fromViewX(int x)
		{
		return x*1050/width();
		}
	/*
	private int scaleViewX(int dx)
		{
		return dx*width()/1050;
		}*/
	
	public void setevent(Dataset ds, int i)
		{
		setevent(ds, Arrays.asList(i));
		}

	public void setevent(Dataset ds, List<Integer> ids)
		{
		this.ds=ds;
		eventid.clear();
		eventid.addAll(ids);
		update();
		}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	QPointF pointLast=new QPointF();
	int moveBoundary=-1;
	
	@Override
	protected void mousePressEvent(QMouseEvent event)
		{
		pointLast=event.posF();
		super.mousePressEvent(event);
		if(event.button()==MouseButton.LeftButton)
			{
			moveBoundary=-1;
			if(curchannel!=null)
				{
				int[] x=new int[]{
						toViewX(curchannel.from),
						toViewX(curchannel.to)};
				int si=-1;
				int smallest=100000;
				for(int i=0;i<x.length;i++)
					{
					int dx=Math.abs(event.x()-x[i]);
					if(si==-1 || dx<smallest)
						{
						smallest=dx;
						si=i;
						}
					}
				if(smallest<30)
					{
					//Close enough
					moveBoundary=si;
					}
				}
			}
		
		}
	
	@Override
	protected void mouseDoubleClickEvent(QMouseEvent e)
		{
		super.mouseDoubleClickEvent(e);
		}
	
	@Override
	protected void mouseReleaseEvent(QMouseEvent ev)
		{
		super.mouseReleaseEvent(ev);
		if(curchannel!=null && moveBoundary!=-1)
			{
			//Recompute channels
			mw.recalcProfChan(curchannel);
			}
		moveBoundary=-1;
		//mw.handleEvent(new EventGatesMoved());   //TODO something like this!
		}

	private int clamp(int x, int from, int to)
		{
		if(x<from)
			return from;
		else if(x>to)
			return to;
		else
			return x;
		}
	
	@Override
	protected void mouseMoveEvent(QMouseEvent event)
		{
		super.mouseMoveEvent(event);
		
		if(moveBoundary!=-1 && curchannel!=null)
			{

			int newx=fromViewX(event.pos().x());
			if(moveBoundary==0)
				curchannel.from=clamp(newx,0,curchannel.to-1);
			else if(moveBoundary==1)
				curchannel.to=clamp(newx,curchannel.from+1,1050);
			update();
			}
		pointLast=event.posF();
		}

	
	
	
	}
