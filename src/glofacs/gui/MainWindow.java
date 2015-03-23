package glofacs.gui;


import glofacs.gates.Gate;
import glofacs.gates.GateSet;
import glofacs.gui.panel.ChannelWidget;
import glofacs.gui.panel.EventGatesChanged;
import glofacs.gui.panel.ViewSettings;
import glofacs.io.FCSFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import com.trolltech.qt.core.QCoreApplication;
import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.core.Qt.ItemFlag;
import com.trolltech.qt.core.Qt.ItemFlags;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QMainWindow;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QMenuBar;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QSizePolicy.Policy;
import com.trolltech.qt.gui.QTableWidget;
import com.trolltech.qt.gui.QTableWidgetItem;
import com.trolltech.qt.gui.QTreeWidget;
import com.trolltech.qt.gui.QTreeWidgetItem;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.gui.QAbstractItemView.SelectionBehavior;
import com.trolltech.qt.gui.QHeaderView.ResizeMode;

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
	
	private QTableWidget tableDatasets=new QTableWidget();
	private QTableWidget tableViews=new QTableWidget();	
	private QTreeWidget treeGates=new QTreeWidget();

	QGridLayout layViews=new QGridLayout();
	LinkedList<ChannelWidget> prevChanWidget=new LinkedList<ChannelWidget>();
			
	
	QMenuBar menubar=new QMenuBar();
	
	/**
	 * Create a read-only list item
	 */
	private QTableWidgetItem createReadOnlyItem(String s)
		{
		QTableWidgetItem it=new QTableWidgetItem(s);
		it.setFlags(new ItemFlags(ItemFlag.ItemIsSelectable, ItemFlag.ItemIsEnabled));
		return it;
		}

	
	public MainWindow()
		{
		
		setMenuBar(menubar);

		QMenu mFile=menubar.addMenu(tr("File"));
		mFile.addAction(tr("Open"));
		mFile.addAction(tr("Exit"));
		
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
			{
			try
				{
				if(FCSFile.isFCSfile(path))
					{
					loadFile(path);
					}
				}
			catch (FileNotFoundException e)
				{
				e.printStackTrace();
				}
			}
		
//		File path=new File("/home/mahogny/javaproj/glofacs/test/Specimen_001_test4 J3 pbabe gfppuro a_D03_009.fcs");
//		loadFile(path);
		
		updateViewsList();
		updateDatasetList();
		updateGatesList();
		dothelayout();

		QHBoxLayout lay=new QHBoxLayout();
		lay.addLayout(layLeft);
		lay.addLayout(layViews);
		
		QWidget cent=new QWidget();
		cent.setLayout(lay);
		setCentralWidget(cent);
		
		
		setMinimumWidth(200);
		setMinimumHeight(200);
		show();
		}
	
	public void addGate(Gate g)
		{
		int id=gateset.addNewGate(g);
		g.name=""+id;
		updateGatesList();
		}
	
	
	public void actionNewView()
		{
		ViewSettings vs=new ViewSettings();
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

	
	
	private LinkedList<ViewSettings> getSelectedViews()
		{
		LinkedList<ViewSettings> selviews=new LinkedList<ViewSettings>();
		for(QModelIndex in:tableViews.selectionModel().selectedRows())
			selviews.add((ViewSettings)tableViews.item(in.row(),0).data(Qt.ItemDataRole.UserRole));
		return selviews;
		}

	private LinkedList<FCSFile.DataSegment> getSelectedDatasets()
		{
		LinkedList<FCSFile.DataSegment> selviews=new LinkedList<FCSFile.DataSegment>();
		for(QModelIndex in:tableDatasets.selectionModel().selectedRows())
			selviews.add((FCSFile.DataSegment)tableDatasets.item(in.row(),0).data(Qt.ItemDataRole.UserRole));
		return selviews;
		}

	
	/**
	 * Update the layout of everything
	 */
	public void dothelayout()
		{
		for(ChannelWidget lab:prevChanWidget)
			{
			layViews.removeWidget(lab);
			lab.setVisible(false);
			}
		prevChanWidget.clear();

		LinkedList<FCSFile.DataSegment> selds=getSelectedDatasets();
		LinkedList<ViewSettings> selviews=getSelectedViews();
		
		//autoscale all views to the same size
		if(!selds.isEmpty())
			for(ViewSettings vs:selviews)
				vs.autoscale(selds.get(0), 300, 300);
		
		int currow=0;
		for(FCSFile.DataSegment ds:selds)
			{
			int curcol=0;
			for(ViewSettings vs:selviews)
				{
				ChannelWidget lab=new ChannelWidget(this);
				prevChanWidget.add(lab);
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
		for(ChannelWidget w:prevChanWidget)
			w.render();
		
		
		}
	
	
	private void updateViewsList()
		{
		tableViews.clear(); //clears title?
		
		tableViews.setColumnCount(1);
		tableViews.setHorizontalHeaderLabels(Arrays.asList(tr("View")));

		tableViews.setRowCount(views.size());
		int row=0;
		for(ViewSettings vs:views)
			{
			QTableWidgetItem it=createReadOnlyItem(vs.name);
			it.setData(Qt.ItemDataRole.UserRole, vs);
			tableViews.setItem(row, 0, it);
			row++;
			}
		}
	
	private void updateDatasetList()
		{
		tableDatasets.setRowCount(datasets.size());
		int row=0;
		for(FCSFile.DataSegment ds:datasets)
			{
			QTableWidgetItem it=createReadOnlyItem(ds.source.getName());
			it.setData(Qt.ItemDataRole.UserRole, ds);
			tableDatasets.setItem(row, 0, it);
			row++;
			}
		
		}
	
	private void updateGatesList()
		{
		treeGates.clear();
		System.out.println("here "+gateset.mapIdGate.values());
		for(Gate g:gateset.mapIdGate.values())
			{			
			QTreeWidgetItem item=new QTreeWidgetItem(treeGates);  //if root level?
			item.setText(0, g.name);
			}
		}
	
	/**
	 * Entry point
	 */
	public static void main(String[] args)
		{
		QApplication.initialize(QtProgramInfo.programName, args);
		QCoreApplication.setApplicationName(QtProgramInfo.programName);
		/*MainWindow w=*/new MainWindow();
		QTutil.execStaticQApplication();		
		}


	/**
	 * Event bus
	 */
	public void handleEvent(EventGatesChanged eventGatesChanged)
		{
		updateGatesList();
		}

	
	@Override
	protected void resizeEvent(QResizeEvent arg__1)
		{
		super.resizeEvent(arg__1);
		
		dothelayout();
		}
	}
