package glofacs.gui.channel;

import java.util.ArrayList;
import java.util.LinkedList;

import glofacs.data.ChannelInfo;
import glofacs.gates.Gate;
import glofacs.gates.GateRect;
import glofacs.gates.GatingResult;
import glofacs.gui.MainWindow;
import glofacs.io.FCSFile;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.Qt.MouseButton;
import com.trolltech.qt.gui.QContextMenuEvent;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QMouseEvent;
import com.trolltech.qt.gui.QPaintEvent;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.gui.QSizePolicy.Policy;

/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class ChannelWidget extends QWidget
	{
	private FCSFile.DataSegment segment;
	ChannelRenderer r=new ChannelRenderer();
	
	MainWindow mw;
	
	public ChannelWidget(MainWindow mw)
		{
		this.mw=mw;
		setSizePolicy(Policy.Expanding, Policy.Expanding);
		}
	
	public void setDataset(FCSFile.DataSegment segment)
		{
		this.segment=segment;
		r.setSegment(segment, mw.gateset);
		//r.autoscale();
		}
	
	public void render()
		{
		repaint();
		}
	
	@Override
	protected void paintEvent(QPaintEvent pe)
		{
		GatingResult gr=mw.gatingResult.get(segment);
		QPainter pm=new QPainter(this);
		r.render(gr, this, contentsRect().width(), contentsRect().height());
		pm.drawImage(0, 0, r.img);
		pm.end();
		}
	

	private Gate isDrawing=null;
	
	@Override
	protected void mousePressEvent(QMouseEvent event)
		{
		super.mousePressEvent(event);
		if(event.button()==MouseButton.LeftButton)
			{
			QPointF p = mapScreenToFacs(event.posF()); 
			
			GateRect grect=new GateRect();
			grect.indexX=r.viewsettings.indexX;
			grect.indexY=r.viewsettings.indexY;
			grect.x1=grect.x2=p.x();
			grect.y1=grect.y2=p.y();
			grect.updateInternal();
			isDrawing=grect;
			

			mw.addGate(grect);
			mw.handleEvent(new EventGatesChanged());
			render();
			}
		
		}
	@Override
	protected void mouseReleaseEvent(QMouseEvent ev)
		{
		super.mouseReleaseEvent(ev);
		isDrawing=null;
		render();
		}

	@Override
	protected void mouseMoveEvent(QMouseEvent event)
		{
		super.mouseMoveEvent(event);
		if(isDrawing!=null)
			{
			GateRect grect=(GateRect)isDrawing;
			
			QPointF p = mapScreenToFacs(event.posF()); 
			
			grect.x2=p.x();
			grect.y2=p.y();
			grect.updateInternal();
			render();
			}
		}

	public double getTotalScaleX()
		{
		return r.viewsettings.scaleX*width();
		}
	public double getTotalScaleY()
		{
		return r.viewsettings.scaleY*height();
		}

	public QPointF mapScreenToFacs(QPointF pos)
		{
		int h=height()-offsetXY-1;
		QPointF p=new QPointF(
				(pos.x()-offsetXY)/getTotalScaleX(),
				(h -pos.y())/getTotalScaleY()
				);
		return p;
		}

	public QPointF mapFacsToScreen(QPointF pos)
		{
		int h=height()-offsetXY-1;
		QPointF p=new QPointF(
				pos.x()*getTotalScaleX()+offsetXY,
				h - pos.y()*getTotalScaleY()
				);
		return p;
		}

	public static int offsetXY=30;
	public int mapFacsToScreenX(double x)
		{
		return offsetXY+(int)(getTotalScaleX()*x);
		}

	public int mapFacsToScreenY(double y)
		{
		int h=height()-offsetXY-1;
		return h-((int)(getTotalScaleY()*y));
		}

	
	@Override
	protected void contextMenuEvent(QContextMenuEvent ev)
		{
		super.contextMenuEvent(ev);
		int invy=height()-ev.pos().y();
		if(ev.pos().x()<50 || invy<50)
			{
			QMenu menu=new QMenu();
			boolean lastwasx=true;
			if(ev.pos().x()>invy)
				lastwasx=true;
			else
				lastwasx=false;
						
			ArrayList<ChannelInfo> chans=segment.getChannelInfo();
			setchans.clear();
			for(int i=0;i<chans.size();i++)
				{
				ChannelInfo ci=chans.get(i);
				SetChannel set=new SetChannel();
				set.chanid=i;
				set.forx=lastwasx;
				menu.addAction(ci.formatName(), set, "actionSetChannel()");
				setchans.add(set);
				}
			
			
			menu.exec(ev.globalPos());
			}
		else
			{
			QMenu menu=new QMenu();

			QMenu mSetSource=menu.addMenu(tr("Set source population"));
			
			setgate.clear();
			for(Gate g:mw.gateset.getGates())
				{
				SetGate sg=new SetGate();
				sg.g=g;
				setgate.add(sg);
				mSetSource.addAction(g.name, sg, "actionSet()");
				}
			
			menu.exec(ev.globalPos());
			}
		}

	
	
	private LinkedList<SetChannel> setchans=new LinkedList<ChannelWidget.SetChannel>();
	public class SetChannel
		{
		public boolean forx;
		int chanid;
		public void actionSetChannel()
			{
			if(forx)
				r.viewsettings.indexX=chanid;
			else
				r.viewsettings.indexY=chanid;
			r.autoscale(); 
			render();
			}
		}

	

	private LinkedList<SetGate> setgate=new LinkedList<SetGate>();
	public class SetGate
		{
		Gate g;
		public void actionSet()
			{
			r.viewsettings.fromGate=g;
			r.autoscale(); 
			render();
			}
		}
	
	public void setChannels(int indexX, int indexY)
		{
		r.viewsettings.indexX=indexX;
		r.viewsettings.indexY=indexY;
		}

	public void setSettings(ViewSettings vs)
		{
		r.viewsettings=vs;
		}

	@Override
	protected void resizeEvent(QResizeEvent arg__1)
		{
		super.resizeEvent(arg__1);
		repaint(); //obvious?
		}


	}