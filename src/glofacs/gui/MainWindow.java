package glofacs.gui;


import glofacs.gates.Gate;
import glofacs.gates.GateSet;
import glofacs.gates.GatingResult;
import glofacs.gui.channel.ChannelWidget;
import glofacs.gui.channel.EventGatesChanged;
import glofacs.gui.channel.ViewSettings;
import glofacs.gui.gatestats.GateStatsPane;
import glofacs.io.FCSFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import com.trolltech.qt.core.QCoreApplication;
import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QAbstractItemView.SelectionBehavior;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QFileDialog;
import com.trolltech.qt.gui.QFileDialog.FileMode;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QHeaderView.ResizeMode;
import com.trolltech.qt.gui.QMainWindow;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QMenuBar;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QSizePolicy.Policy;
import com.trolltech.qt.gui.QTabWidget;
import com.trolltech.qt.gui.QTableWidget;
import com.trolltech.qt.gui.QTableWidgetItem;
import com.trolltech.qt.gui.QTreeWidget;
import com.trolltech.qt.gui.QTreeWidgetItem;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;

/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class MainWindow extends QMainWindow
	{
	public GateSet gateset=new GateSet();
	public LinkedList<FCSFile.DataSegment> datasets=new LinkedList<FCSFile.DataSegment>();
	public LinkedList<ViewSettings> views=new LinkedList<ViewSettings>();
	
	public HashMap<FCSFile.DataSegment, GatingResult> gatingResult=new HashMap<FCSFile.DataSegment, GatingResult>();
			
	private QTableWidget tableDatasets=new QTableWidget();
	private QTableWidget tableViews=new QTableWidget();	
	private QTreeWidget treeGates=new QTreeWidget();
	private GateStatsPane paneStats;
	private QTabWidget tabwidget=new QTabWidget();
	private QMenuBar menubar=new QMenuBar();
	private QGridLayout layViews=new QGridLayout();
	

	private File lastDirectory=new File(".");

	
	/**
	 * Get gating result for dataset
	 */
	public GatingResult getGatingResult(FCSFile.DataSegment segment)
		{
		if(gatingResult.get(segment)==null)
			return new GatingResult();
		else
			return gatingResult.get(segment);
		}
	
	

	/**
	 * Constructor
	 */
	public MainWindow()
		{
		
		setMenuBar(menubar);

		QMenu mFile=menubar.addMenu(tr("File"));
		//mFile.addAction(tr("New project"));
		mFile.addAction(tr("Add datasets"), this, "actionAddDatasets()");
		mFile.addSeparator();
		mFile.addAction(tr("Exit"), this, "close()");
		
		tableDatasets.setColumnCount(1);
		tableDatasets.verticalHeader().hide();
		tableDatasets.setHorizontalHeaderLabels(Arrays.asList(tr("Dataset")));
		tableDatasets.setSelectionBehavior(SelectionBehavior.SelectRows);
		tableDatasets.horizontalHeader().setResizeMode(ResizeMode.ResizeToContents);
		tableDatasets.horizontalHeader().setStretchLastSection(true);		
		tableDatasets.selectionModel().selectionChanged.connect(this,"dothelayout()");

		tableViews.verticalHeader().hide();
		tableViews.setSelectionBehavior(SelectionBehavior.SelectRows);
		tableViews.horizontalHeader().setResizeMode(ResizeMode.ResizeToContents);
		tableViews.horizontalHeader().setStretchLastSection(true);		
		tableViews.selectionModel().selectionChanged.connect(this,"dothelayout()");
	
		treeGates.setHeaderLabels(Arrays.asList(tr("Gate")));
		treeGates.setSelectionBehavior(SelectionBehavior.SelectRows);
		treeGates.selectionModel().selectionChanged.connect(this,"dothelayout()");

		

		QPushButton bNewView=new QPushButton(tr("New view"));
		bNewView.clicked.connect(this,"actionNewView()");


		QVBoxLayout layLeft=new QVBoxLayout();
		layLeft.addWidget(tableDatasets);
		layLeft.addWidget(tableViews);
		layLeft.addWidget(bNewView);
		layLeft.addWidget(treeGates);

		actionNewView();

		treeGates.setSizePolicy(Policy.Minimum, Policy.Expanding);
		tableDatasets.setSizePolicy(Policy.Minimum, Policy.Expanding);
		tableViews.setSizePolicy(Policy.Minimum, Policy.Expanding);
		
		/// Load all files from directory
		for(File path:new File("/home/mahogny/javaproj/glofacs/test/").listFiles())
			loadFile(path);
		
//		File path=new File("/home/mahogny/javaproj/glofacs/test/Specimen_001_test4 J3 pbabe gfppuro a_D03_009.fcs");
//		loadFile(path);

		QWidget wGraphs=new QWidget();
		wGraphs.setLayout(layViews);
		
		paneStats=new GateStatsPane(this);
		
		tabwidget.addTab(wGraphs, tr("Graphs"));
		tabwidget.addTab(paneStats, tr("Statistics"));

		QHBoxLayout lay=new QHBoxLayout();
		lay.addLayout(layLeft);
		lay.addWidget(tabwidget);
		
		QWidget cent=new QWidget();
		cent.setLayout(lay);
		setCentralWidget(cent);
		

		updateViewsList();
		updateDatasetList();
		updateGatesList();
		dothelayout();

		setMinimumWidth(500);
		setMinimumHeight(200);
		show();
		}
	
	
	
	/**
	 * Action: Add/import datasets
	 */
	public void actionAddDatasets()
		{
		QFileDialog dia=new QFileDialog();
		dia.setFileMode(FileMode.ExistingFiles);
		dia.setDirectory(lastDirectory.getAbsolutePath());
		dia.setNameFilter(tr("FACS files (*.fcs)"));
		if(dia.exec()!=0)
			{
			try
				{
				for(String sf:dia.selectedFiles())
					{
					File f=new File(sf);
					lastDirectory=f.getParentFile();
					if(FCSFile.isFCSfile(f))
						loadFile(f);
					else
						QTutil.showNotice(this, "Not a FACS file: "+f);
					}
				}
			catch (IOException e)
				{
				QTutil.showNotice(this, e.getMessage());
				e.printStackTrace();
				}
			}		
		}
	
	
	public void addGate(Gate g)
		{
		int id=gateset.addNewGate(g);
		g.name=""+id;
		updateGatesList();
		}
	

	/**
	 * Action: Create a new view
	 */
	public void actionNewView()
		{
		ViewSettings vs=new ViewSettings();
		vs.fromGate=gateset.getRootGate();
		vs.indexX=7;
		vs.indexY=6;
		vs.name=""+Math.random();
		
		
		//autoscale here the first time?
		
		views.add(vs);
		updateViewsList();
		}
	
	/**
	 * Load one file
	 */
	public void loadFile(File path)
		{
		try
			{
			FCSFile f=new FCSFile(path);
			//f=new FCSFile(new File("/home/mahogny/javaproj/glofacs/test/Specimen_001_C10_C10_008.fcs"));
			
			FCSFile.DataSegment segment=f.data.get(0);

			segment.source=path;
			datasets.add(segment);
			updateDatasetList();
			}
		catch (IOException e)
			{
			e.printStackTrace();
			}
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
	 * Get selected datasets
	 */
	public LinkedList<FCSFile.DataSegment> getSelectedDatasets()
		{
		LinkedList<FCSFile.DataSegment> selviews=new LinkedList<FCSFile.DataSegment>();
		for(QModelIndex in:tableDatasets.selectionModel().selectedRows())
			selviews.add((FCSFile.DataSegment)tableDatasets.item(in.row(),0).data(Qt.ItemDataRole.UserRole));
		return selviews;
		}

	/**
	 * Get selected gates
	 */
	public LinkedList<Gate> getSelectedGates()
		{
		LinkedList<Gate> selviews=new LinkedList<Gate>();
		for(QTreeWidgetItem it:treeGates.selectedItems())
			selviews.add((Gate)it.data(0,Qt.ItemDataRole.UserRole));
		return selviews;
		}

	
	/**
	 * Update gating results
	 */
	public void dogating()
		{
		//For speed, only do selected ones
		LinkedList<FCSFile.DataSegment> listDatasets=getSelectedDatasets();

		gatingResult.clear();
		for(FCSFile.DataSegment ds:listDatasets)
			{
			GatingResult gr=new GatingResult();
			gr.perform(gateset, ds);
			gatingResult.put(ds, gr);
			}
		
		}


	private ArrayList<ArrayList<ChannelWidget>> prevChanWidget=new ArrayList<ArrayList<ChannelWidget>>();
			
	/**
	 * Update the layout of everything
	 */
	public void dothelayout()
		{
		dogating();

		LinkedList<FCSFile.DataSegment> selds=getSelectedDatasets();
		LinkedList<ViewSettings> selviews=getSelectedViews();
		
		//autoscale all views to the same size
		if(!selds.isEmpty())
			for(ViewSettings vs:selviews)
				vs.autoscale(selds.get(0), 300, 300);

		int numrow=selds.size();
		int numcol=selviews.size();
		
	
		//Adjust number of rows
		while(prevChanWidget.size()<numrow)
			prevChanWidget.add(new ArrayList<ChannelWidget>());
		while(prevChanWidget.size()>numrow)
			{
			int row=prevChanWidget.size()-1;
			ArrayList<ChannelWidget> onerow=prevChanWidget.get(row);
			for(;onerow.size()>=0;)
				{
				int col=onerow.size()-1;
				ChannelWidget lab=onerow.get(col);
				lab.setVisible(false);
				layViews.removeWidget(lab);
				onerow.remove(col);
				}
			prevChanWidget.remove(row);
			}
		
		//Fix number of columns
		for(int row=0;row<prevChanWidget.size();row++)
			{
			//Add columns
			ArrayList<ChannelWidget> onerow=prevChanWidget.get(row);
			for(;onerow.size()<numcol;)
				{
				int col=onerow.size();
				ChannelWidget lab=new ChannelWidget(this);
				onerow.add(lab);
				layViews.addWidget(lab, row, col);
				}
			//Remove columns
			for(;onerow.size()>numcol;)
				{
				int col=onerow.size()-1;
				ChannelWidget lab=onerow.get(col);
				lab.setVisible(false);
				layViews.removeWidget(lab);
				onerow.remove(col);
				}
			}
		
		int currow=0;
		for(FCSFile.DataSegment ds:selds)
			{
			int curcol=0;
			for(ViewSettings vs:selviews)
				{
				ChannelWidget lab=prevChanWidget.get(currow).get(curcol);//new ChannelWidget(this);
				//prevChanWidget.add(lab);
				lab.setSettings(vs);
				//lab.setChannels(0,4);

				//First selection: do FSC-A  vs  SSC-A
				lab.setDataset(ds);
				//lab.render();
				layViews.addWidget(lab, currow, curcol);
				curcol++;
				}
			currow++;
			}
		
		//Get the size of one. rescale. then rerender all
		for(ArrayList<ChannelWidget> row:prevChanWidget)
			for(ChannelWidget w:row)
				w.render();
		
		paneStats.updatestats();
		}
	
	
	/**
	 * Update list with views
	 */
	private void updateViewsList()
		{
		tableViews.clear(); //clears title?
		
		tableViews.setColumnCount(1);
		tableViews.setHorizontalHeaderLabels(Arrays.asList(tr("View")));

		tableViews.setRowCount(views.size());
		int row=0;
		for(ViewSettings vs:views)
			{
			QTableWidgetItem it=QTutil.createReadOnlyItem(vs.name);
			it.setData(Qt.ItemDataRole.UserRole, vs);
			tableViews.setItem(row, 0, it);
			row++;
			}
		}
	
	/**
	 * Update list with datasets
	 */
	private void updateDatasetList()
		{
		tableDatasets.setRowCount(datasets.size());
		int row=0;
		for(FCSFile.DataSegment ds:datasets)
			{
			QTableWidgetItem it=QTutil.createReadOnlyItem(ds.source.getName());
			it.setData(Qt.ItemDataRole.UserRole, ds);
			tableDatasets.setItem(row, 0, it);
			row++;
			}
		}

	/**
	 * Update list with gates
	 */
	private void updateGatesList()
		{
		treeGates.clear();
		updateGatesListRecursive(null, gateset.getRootGate());
		treeGates.expandAll();
		}
	private void updateGatesListRecursive(QTreeWidgetItem parentItem, Gate g)
		{
		QTreeWidgetItem item;
		if(parentItem==null)
			item=new QTreeWidgetItem(treeGates);
		else
			item=new QTreeWidgetItem(parentItem);
		item.setData(0,Qt.ItemDataRole.UserRole, g);
		item.setText(0, g.name);
		for(Gate child:g.children)
			updateGatesListRecursive(item, child);
		}

	
	/**
	 * Entry point
	 */
	public static void main(String[] args)
		{
		QApplication.initialize(QtProgramInfo.programName, args);
		QCoreApplication.setApplicationName(QtProgramInfo.programName);
		new MainWindow();
		QTutil.execStaticQApplication();		
		}


	/**
	 * Event bus
	 */
	public void handleEvent(EventGatesChanged event)
		{
		dothelayout();
		}

	
	@Override
	protected void resizeEvent(QResizeEvent arg__1)
		{
		super.resizeEvent(arg__1);
		dothelayout();
		}
	}
