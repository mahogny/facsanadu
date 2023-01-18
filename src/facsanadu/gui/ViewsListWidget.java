package facsanadu.gui;

import java.util.Arrays;
import java.util.LinkedList;

import io.qt.core.QModelIndex;
import io.qt.core.Qt;
import io.qt.core.QItemSelectionModel.SelectionFlag;
import io.qt.gui.QIcon;
import io.qt.widgets.QPushButton;
import io.qt.widgets.QTableWidget;
import io.qt.widgets.QTableWidgetItem;
import io.qt.widgets.QVBoxLayout;
import io.qt.widgets.QAbstractItemView.SelectionBehavior;
import io.qt.widgets.QHeaderView.ResizeMode;
import io.qt.widgets.QSizePolicy.Policy;

import facsanadu.data.Dataset;
import facsanadu.gui.events.EventViewsChanged;
import facsanadu.gui.qt.QTutil;
import facsanadu.gui.resource.ImgResource;
import facsanadu.gui.view.ViewSettings;

/**
 * 
 * List of all views
 * 
 * @author Johan Henriksson
 *
 */
public class ViewsListWidget extends QVBoxLayout
	{
	private boolean isUpdating=false;

	private QTableWidget tableViews=new QTableWidget();	

	MainWindow mw;
	public ViewsListWidget(MainWindow mw)
		{
		this.mw=mw;
		setContentsMargins(0,0,0,0);
		
		
		tableViews.verticalHeader().hide();
		tableViews.setSelectionBehavior(SelectionBehavior.SelectRows);
		tableViews.horizontalHeader().setSectionResizeMode(ResizeMode.ResizeToContents);
		tableViews.horizontalHeader().setStretchLastSection(true);		
		tableViews.selectionModel().selectionChanged.connect(this,"dothelayout()");
	
		QPushButton bSelectAllViews=new QPushButton(tr("Select all"));
		QPushButton bNewView=new QPushButton(tr("New view"));
		QPushButton bRemoveView=new QPushButton(new QIcon(ImgResource.delete),"");


		bNewView.clicked.connect(this,"actionNewView()");
		bRemoveView.clicked.connect(this,"actionRemoveView()");
		bSelectAllViews.clicked.connect(this,"actionSelectAllViews()");

		addWidget(tableViews);
		addLayout(QTutil.layoutHorizontal(bSelectAllViews, bNewView, bRemoveView));

		tableViews.setSizePolicy(Policy.Minimum, Policy.Expanding);

		}
	
	public void dothelayout()
		{
		mw.handleEvent(new EventViewsChanged()); //possible overkill?
		}
	
	public void actionSelectAllViews()
		{
		tableViews.selectAll();
		}

	/**
	 * Get selected views
	 */
	public LinkedList<ViewSettings> getSelectedViews()
		{
		LinkedList<ViewSettings> selviews=new LinkedList<ViewSettings>();
		for(QModelIndex in:tableViews.selectionModel().selectedRows())
			selviews.add((ViewSettings)tableViews.item(in.row(),0).data(Qt.ItemDataRole.UserRole));
		return selviews;
		}

	
	/**
	 * Update list with views
	 */
	void updateViewsList()
		{
		LinkedList<ViewSettings> selviews=getSelectedViews();
		
		FacsanaduProject project=mw.project;
		boolean wasUpdating=isUpdating;
		isUpdating=true;
		tableViews.clear(); //clears title?
		
		tableViews.setColumnCount(1);
		tableViews.setHorizontalHeaderLabels(Arrays.asList(tr("View")));

		tableViews.setRowCount(project.views.size());
		int row=0;
		for(ViewSettings vs:project.views)
			{
			String showname=vs.gate.name+": ";
			if(!project.datasets.isEmpty())
				{
				Dataset ds=project.datasets.get(0);
				if(vs.indexX==vs.indexY)
					showname+=ds.getChannelInfo().get(vs.indexX).getShortestName();
				else
					showname+=ds.getChannelInfo().get(vs.indexX).getShortestName()+" / "+ds.getChannelInfo().get(vs.indexY).getShortestName();
				}
			
			QTableWidgetItem it=QTutil.createReadOnlyItem(showname);
			it.setData(Qt.ItemDataRole.UserRole, vs);
			tableViews.setItem(row, 0, it);
			if(selviews.contains(vs))
				tableViews.selectionModel().select(tableViews.model().index(row, 0), SelectionFlag.Rows, SelectionFlag.Select);
			row++;
			}
		
		
		isUpdating=wasUpdating;
		}

	

	/**
	 * Action: Remove selected views
	 */
	public void actionRemoveView()
		{
		FacsanaduProject project=mw.project;
		project.views.removeAll(getSelectedViews());
		updateViewsList();
		}

	
	/**
	 * Action: Create a new view
	 */
	public void actionNewView()
		{
		FacsanaduProject project=mw.project;
		ViewSettings vs=new ViewSettings();
		vs.gate=project.gateset.getRootGate();
		vs.indexX=0;
		vs.indexY=1;                                                    
		if(project.getNumChannels()>vs.indexX)
			vs.indexX=0;
		if(project.getNumChannels()>vs.indexY)
			vs.indexY=0;
		
		//autoscale here the first time?
		
		project.views.add(vs);
		updateViewsList();
		}

	}
