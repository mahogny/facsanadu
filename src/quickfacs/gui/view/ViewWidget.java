package quickfacs.gui.view;

import java.util.ArrayList;
import java.util.LinkedList;

import quickfacs.data.ChannelInfo;
import quickfacs.data.Dataset;
import quickfacs.gates.Gate;
import quickfacs.gates.GateRect;
import quickfacs.gates.GatingResult;
import quickfacs.gui.MainWindow;
import quickfacs.gui.QuickfacsProject;
import quickfacs.gui.events.EventGatesChanged;
import quickfacs.gui.events.EventViewsChanged;

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
 * Widget showing one view
 * 
 * @author Johan Henriksson
 *
 */
public class ViewWidget extends QWidget
	{
	private Dataset dataset;
	private ViewRenderer r=new ViewRenderer();
	private MainWindow mw;

	private LinkedList<CallbackSetChannel> setchans=new LinkedList<ViewWidget.CallbackSetChannel>();
	private LinkedList<CallbackSetGate> setgate=new LinkedList<CallbackSetGate>();
	private Gate isDrawing=null;

	
	ViewTransform trans=new ViewTransform();

	public ViewWidget(MainWindow mw)
		{
		this.mw=mw;
		setSizePolicy(Policy.Expanding, Policy.Expanding);
		}
	
	public void setDataset(Dataset ds)
		{
		this.dataset=ds;
		r.setDataset(ds, mw.project);
		}
	
	public void render()
		{
		repaint();
		}
	
	@Override
	protected void paintEvent(QPaintEvent pe)
		{
		QuickfacsProject project=mw.project;
		GatingResult gr=project.gatingResult.get(dataset);
		
		trans.height=contentsRect().height();
		trans.width=contentsRect().width();
		trans.viewsettings=r.viewsettings;
		
		
		QPainter pm=new QPainter(this);
		r.render(gr, trans);
		pm.drawImage(0, 0, r.img);
		pm.end();
		}
	

	
	@Override
	protected void mousePressEvent(QMouseEvent event)
		{
		super.mousePressEvent(event);
		if(event.button()==MouseButton.LeftButton)
			{
			QPointF p = trans.mapScreenToFacs(event.posF()); 
			
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
			
			QPointF p = trans.mapScreenToFacs(event.posF()); 
			
			grect.x2=p.x();
			grect.y2=p.y();
			grect.updateInternal();
			render();
			}
		}


	
	@Override
	protected void contextMenuEvent(QContextMenuEvent ev)
		{
		QuickfacsProject proj=mw.project;
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
						
			ArrayList<ChannelInfo> chans=dataset.getChannelInfo();
			setchans.clear();
			for(int i=0;i<chans.size();i++)
				{
				ChannelInfo ci=chans.get(i);
				CallbackSetChannel set=new CallbackSetChannel();
				set.chanid=i;
				set.forx=lastwasx;
				menu.addAction(ci.formatName(), set, "actionSet()");
				setchans.add(set);
				}
			
			
			menu.exec(ev.globalPos());
			}
		else
			{
			QMenu menu=new QMenu();

			QMenu mSetSource=menu.addMenu(tr("Set source population"));

			setgate.clear();
			for(Gate g:proj.gateset.getGates())
				{
				CallbackSetGate sg=new CallbackSetGate();
				sg.g=g;
				setgate.add(sg);
				mSetSource.addAction(g.name, sg, "actionSet()");
				}
			
			menu.exec(ev.globalPos());
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
/*
	@Override
	protected void resizeEvent(QResizeEvent arg__1)
		{
		super.resizeEvent(arg__1);
		repaint(); //obvious?
		}*/


	/**
	 * Callback: Set channel
	 */
	public class CallbackSetChannel
		{
		public boolean forx;
		int chanid;
		public void actionSet()
			{
			if(forx)
				r.viewsettings.indexX=chanid;
			else
				r.viewsettings.indexY=chanid;
			mw.handleEvent(new EventViewsChanged());
			}
		}

	
	/**
	 * Callback: set displayed gate
	 */
	public class CallbackSetGate
		{
		Gate g;
		public void actionSet()
			{
			r.viewsettings.fromGate=g;
			mw.handleEvent(new EventViewsChanged());
			}
		}

	}