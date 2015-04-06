package facsanadu.gui.view;

import java.util.ArrayList;
import java.util.LinkedList;

import com.trolltech.qt.core.QPoint;
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
		
		QPainter pm=new QPainter(this);
		pm.setBrush(new QBrush(QColor.white));
		pm.drawRect(-5,-5,10000,10000);
		ViewRenderer.render(viewsettings, dataset, gr, trans, pm);
		pm.end();
		}
	

	
	@Override
	protected void mousePressEvent(QMouseEvent event)
		{
		super.mousePressEvent(event);
		if(event.button()==MouseButton.LeftButton)
			{
			if(mousePosInBoundary(event.pos()))
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
	protected void mouseDoubleClickEvent(QMouseEvent arg__1)
		{
		super.mouseDoubleClickEvent(arg__1);
		tool.mouseDoubleClickEvent(arg__1);
		}
	
	@Override
	protected void mouseReleaseEvent(QMouseEvent ev)
		{
		super.mouseReleaseEvent(ev);
		tool.mouseReleaseEvent(ev);
		mw.handleEvent(new EventGatesMoved());
		}

	
	@Override
	protected void mouseMoveEvent(QMouseEvent event)
		{
		super.mouseMoveEvent(event);
		tool.mouseMoveEvent(event);
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