package facsanadu.gui.view;

import java.util.ArrayList;
import java.util.LinkedList;

import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.Qt.MouseButton;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QMouseEvent;
import com.trolltech.qt.gui.QPaintEvent;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.gui.QSizePolicy.Policy;

import facsanadu.data.ChannelInfo;
import facsanadu.data.Dataset;
import facsanadu.gates.Gate;
import facsanadu.gates.GatingResult;
import facsanadu.gui.MainWindow;
import facsanadu.gui.FacsanaduProject;
import facsanadu.gui.events.EventGatesMoved;
import facsanadu.gui.events.EventViewsChanged;
import facsanadu.gui.events.QuickfacsEvent;
import facsanadu.gui.view.gate.GateHandle;
import facsanadu.gui.view.tool.ViewTool;
import facsanadu.gui.view.tool.ViewToolDrawPoly;

/**
 * 
 * Widget showing one view
 * 
 * @author Johan Henriksson
 *
 */
public class ViewWidget extends QWidget
	{
	private Dataset dataset;
	public MainWindow mw;

	private LinkedList<Callback> setchans=new LinkedList<Callback>();
	private ViewTool tool=new ViewToolDrawPoly(this);

	public ViewTransform trans=new ViewTransform();
	public ViewSettings viewsettings=new ViewSettings();

	LinkedList<GateHandle> handles=new LinkedList<GateHandle>();

	public ViewWidget(MainWindow mw)
		{
		this.mw=mw;
		setMouseTracking(true);
		setSizePolicy(Policy.Expanding, Policy.Expanding);
		}

	
	public void setDataset(Dataset ds)
		{
		this.dataset=ds;
		}
	
	public void render()
		{
		repaint();
		}
	
	@Override
	protected void paintEvent(QPaintEvent pe)
		{
		super.paintEvent(pe);
		FacsanaduProject project=mw.project;
		GatingResult gr=project.gatingResult.get(dataset);
		
		trans.setTotalHeight(contentsRect().height());
		trans.setTotalWidth(contentsRect().width());
		trans.viewsettings=viewsettings;
		
		handles=new LinkedList<GateHandle>();
		QPainter pm=new QPainter(this);
		pm.setBrush(new QBrush(QColor.white));
		pm.drawRect(-5,-5,10000,10000);
		ViewRenderer.render(viewsettings, dataset, gr, trans, pm, handles, 10000); //how many? TODO
				
		//Now render handles?
		for(GateHandle h:handles)
			{
			pm.setBrush(new QBrush(QColor.transparent));
			pm.setPen(QColor.red);

			int size=2;
			pm.drawRect(new QRectF(h.getX()-size, h.getY()-size,2*size,2*size));
			}
		
		
		pm.end();
		}
	

	public GateHandle getClosestHandle(QPointF pos, double cutoff)
		{
		if(!tool.allowHandle())
			return null;
		GateHandle ch=null;
		double cd=Double.MAX_VALUE;
		for(GateHandle h:handles)
			{
			double dx=pos.x()-h.getX();
			double dy=pos.y()-h.getY();
			double d2=dx*dx + dy*dy;
			if(d2<cd)
				{
				cd=d2;
				ch=h;
				}
			}
		if(cd<cutoff*cutoff)
			return ch;
		else
			return null;
		}
	
	GateHandle curhandle=null;
	
	QPointF pointLast=new QPointF();
	
	@Override
	protected void mousePressEvent(QMouseEvent event)
		{
		pointLast=event.posF();
		super.mousePressEvent(event);
		if(event.button()==MouseButton.LeftButton)
			{
			curhandle=null;
			GateHandle handle=getClosestHandle(event.posF(), 10);
			if(handle!=null)
				{
				//Move a handle
				curhandle=handle;
				}
			else if(mousePosInBoundary(event.pos()))
				{
				setchans.clear();
				FacsanaduProject proj=mw.project;
				int invy=height()-event.pos().y();
				QMenu menu=new QMenu();
				
				//Menu to set axis, and histogram
				QMenu menu2=menu.addMenu(tr("Set axis"));
				QMenu menuHist=menu.addMenu(tr("Set histogram"));
				boolean lastwasx=true;
				if(event.pos().x()>invy)
					lastwasx=true;
				else
					lastwasx=false;
							
				ArrayList<ChannelInfo> chans=dataset.getChannelInfo();
				for(int i=0;i<chans.size();i++)
					{
					ChannelInfo ci=chans.get(i);
					if(!ci.isProfile)
						{
						CallbackSetChannel set=new CallbackSetChannel();
						set.chanid=i;
						set.forx=lastwasx;
						menu2.addAction(ci.formatName(), set, "actionSet()");
						setchans.add(set);
						
						CallbackSetHistogram sethist=new CallbackSetHistogram();
						set.chanid=i;
						menuHist.addAction(ci.formatName(), sethist, "actionSet()");
						setchans.add(sethist);
						}
					}
				

				//Menu to set source population
				QMenu mSetSource=menu.addMenu(tr("Set source population"));
				for(Gate g:proj.gateset.getGates())
					{
					CallbackSetGate sg=new CallbackSetGate();
					sg.g=g;
					setchans.add(sg);
					mSetSource.addAction(g.name, sg, "actionSet()");
					}
				menu.exec(event.globalPos());
				}
			else
				{
				tool.mousePressEvent(event);
				}
			}
		
		}
	
	@Override
	protected void mouseDoubleClickEvent(QMouseEvent e)
		{
		super.mouseDoubleClickEvent(e);
		tool.mouseDoubleClickEvent(e);
		}
	
	@Override
	protected void mouseReleaseEvent(QMouseEvent ev)
		{
		super.mouseReleaseEvent(ev);
		tool.mouseReleaseEvent(ev);
		mw.handleEvent(new EventGatesMoved());
		curhandle=null;
		}

	
	@Override
	protected void mouseMoveEvent(QMouseEvent event)
		{
		super.mouseMoveEvent(event);
		if(curhandle!=null)
			{
			double dx=event.posF().x() - pointLast.x();
			double dy=event.posF().y() - pointLast.y();

			dx=trans.scaleScreenToFCSx(dx);
			dy=trans.scaleScreenToFCSy(dy);
			curhandle.move(mw, dx, -dy);
			}
		else
			tool.mouseMoveEvent(event);
		pointLast=event.posF();
		}


	
	private boolean mousePosInBoundary(QPoint pos)
		{
		int invy=height()-pos.y();
		return pos.x()<trans.graphOffsetXY || invy<trans.graphOffsetXY;
		}
	

	
	
	public void setChannels(int indexX, int indexY)
		{
		viewsettings.indexX=indexX;
		viewsettings.indexY=indexY;
		}

	public void setSettings(ViewSettings vs)
		{
		viewsettings=vs;
		}

	public interface Callback
		{
		public void actionSet();
		}

	/**
	 * Callback: Set channel
	 */
	public class CallbackSetChannel implements Callback
		{
		public boolean forx;
		int chanid;
		public void actionSet()
			{
			if(forx)
				viewsettings.indexX=chanid;
			else
				viewsettings.indexY=chanid;
			mw.handleEvent(new EventViewsChanged());
			}
		}

	/**
	 * Callback: Set histogram
	 */
	public class CallbackSetHistogram implements Callback
		{
		int chanid;
		public void actionSet()
			{
			viewsettings.indexX=viewsettings.indexY=chanid;
			mw.handleEvent(new EventViewsChanged());
			}
		}

	
	/**
	 * Callback: set displayed gate
	 */
	public class CallbackSetGate implements Callback
		{
		Gate g;
		public void actionSet()
			{
			viewsettings.gate=g;
			mw.handleEvent(new EventViewsChanged());
			}
		}


	public int getIndexX()
		{
		return viewsettings.indexX;
		}
	public int getIndexY()
		{
		return viewsettings.indexY;
		}

	public void sendEvent(QuickfacsEvent event)
		{
		mw.handleEvent(event);
		}

	}