package facsanadu.gui;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import com.trolltech.qt.core.QCoreApplication;
import com.trolltech.qt.core.QUrl;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QCloseEvent;
import com.trolltech.qt.gui.QFileDialog;
import com.trolltech.qt.gui.QFileDialog.AcceptMode;
import com.trolltech.qt.gui.QFileDialog.FileMode;
import com.trolltech.qt.gui.QDesktopServices;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QInputDialog;
import com.trolltech.qt.gui.QMainWindow;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QMenuBar;
import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QDropEvent;
import com.trolltech.qt.gui.QTabWidget;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;

import facsanadu.data.Dataset;
import facsanadu.data.ExportFcsToCSV;
import facsanadu.data.ProfChannel;
import facsanadu.gates.Gate;
import facsanadu.gates.measure.GateMeasure;
import facsanadu.gui.events.EventCompensationChanged;
import facsanadu.gui.events.EventDatasetsChanged;
import facsanadu.gui.events.EventGatesChanged;
import facsanadu.gui.events.EventGatesMoved;
import facsanadu.gui.events.EventViewsChanged;
import facsanadu.gui.events.FacsanaduEvent;
import facsanadu.gui.lengthprofile.ProfilePane;
import facsanadu.gui.panes.CompensationPane;
import facsanadu.gui.panes.DatasetInfoPane;
import facsanadu.gui.panes.GateStatsPane;
import facsanadu.gui.panes.ViewsPane;
import facsanadu.gui.qt.QTutil;
import facsanadu.gui.resource.ImgResource;
import facsanadu.gui.view.GraphExporter;
import facsanadu.gui.view.ViewSettings;
import facsanadu.gui.view.tool.EventSetViewTool;
import facsanadu.io.FacsanaduXML;

/**
 * 
 * The main window
 * 
 * @author Johan Henriksson
 *
 */
public class MainWindow extends QMainWindow
	{
	public FacsanaduProject project=new FacsanaduProject();

	private Collection<Dataset> selDatasetsCache=new LinkedList<Dataset>();
	public GateCalcThread calcthread=new GateCalcThread(){
		public FacsanaduProject getProject()
			{
			return project;
			}
		public void callbackDoneCalc(Dataset dataset)
			{
			 QApplication.invokeLater(new Runnable()
         {
         public void run()
           {
           updateall();
           //System.out.println("Thread called");
           }
         });
			}
		public Collection<Dataset> getCurrentDatasets()
			{
			synchronized (selDatasetsCache)
				{
				ArrayList<Dataset> d=new ArrayList<Dataset>(selDatasetsCache);
				return d;
				}
			}

	};

	public File lastDirectory=new File(".");

	private QTabWidget tabwidget=new QTabWidget();
	private QMenuBar menubar=new QMenuBar();
	private GatesListWidget gatesw=new GatesListWidget(this);
	private ProfileChannelWidget pc=new ProfileChannelWidget(this);
	private ViewsListWidget viewsw=new ViewsListWidget(this);
	private DatasetListWidget datasetsw=new DatasetListWidget(this);
	private CompensationPane paneCompensation=new CompensationPane(this);
	private DatasetInfoPane paneMetadata=new DatasetInfoPane(this);
	
	private ViewsPane paneViews;
	private GateStatsPane paneStats;
	private ProfilePane paneProfile;
	
	private File currentProjectFile=null;
	
	private boolean isUpdating=false;

	
	

	/**
	 * Constructor
	 */
	public MainWindow()
		{
		setMenuBar(menubar);

		ImgResource.setWindowIcon(this);
		
		paneViews=new ViewsPane(this);
		paneStats=new GateStatsPane(this);
		paneProfile=new ProfilePane(this);
		pc.paneProfile=paneProfile;
		
		QMenu mFile=menubar.addMenu(tr("File"));
		mFile.addAction(tr("New project"), this, "actionNewProject()");
		mFile.addAction(tr("Open project"), this, "actionOpenProject()");
		mFile.addAction(tr("Save project"), this, "actionSaveProject()");
		mFile.addAction(tr("Save project as"), this, "actionSaveProjectAs()");
		mFile.addSeparator();
		mFile.addAction(tr("Exit"), this, "close()");

		
		QMenu mExport=menubar.addMenu(tr("Export"));
		mExport.addAction(tr("Graphs"), this, "actionExportGraphs()");
		mExport.addAction(tr("Statistics"), this, "actionExportStatistics()");
		mExport.addAction(tr("Dataset as CSV"), this, "actionExportCSV()");

		QMenu mSettings=menubar.addMenu(tr("Settings"));
		mSettings.addAction(tr("Set number of CPU cores"), this, "actionSetNumCores()");
		menubar.addSeparator();
		
		QMenu mHelp=menubar.addMenu(tr("Help"));
		mHelp.addAction(tr("About"), this, "actionAbout()");
		mHelp.addAction(tr("Website"), this, "actionWebsite()");

		datasetsw.selectionChanged.connect(this,"actionDsChanged()");
		
		QVBoxLayout layLeft=new QVBoxLayout();
		layLeft.addLayout(datasetsw);
		layLeft.addLayout(viewsw);
		layLeft.addLayout(gatesw);
		layLeft.addLayout(pc);

		viewsw.actionNewView();

		
		/// Load all files from directory
		try
			{
			File getfrom=new File("/home/mahogny/javaproj/quickfacs/test4");
			if(getfrom.exists())
				for(File path:getfrom.listFiles())
					if(path.getName().endsWith(".fcs") || path.getName().endsWith(".dat"))
						loadFile(path);
			}
		catch (IOException e)
			{
			e.printStackTrace();
			}
		//loadFile(new File("/ztuff/ztufffromvenus/ztuff/customer/jin/rpt-5/rp5-larva-PMT530-day8-2010-09-11.txt"));
		

		
		tabwidget.addTab(paneViews, tr("Graphs"));
		tabwidget.addTab(paneStats, tr("Statistics"));
		tabwidget.addTab(paneProfile, tr("Length profiles"));
		tabwidget.addTab(paneCompensation, tr("Compensation"));
		tabwidget.addTab(paneMetadata, tr("Dataset info"));

		QHBoxLayout lay=new QHBoxLayout();
		lay.addLayout(layLeft);
		lay.addWidget(tabwidget);
		
		QWidget cent=new QWidget();
		cent.setLayout(lay);
		setCentralWidget(cent);

		
		updateall();
		setAcceptDrops(true);
		adjustSize();
		resize(1000, size().height());
		show();
		}
	
	
	/**
	 * Action: New project
	 */
	public void actionNewProject()
		{
		project=new FacsanaduProject();
		updateall();
		}
	
	/**
	 * Update all widgets
	 */
	private void updateall()
		{
		boolean wasUpdating=isUpdating;
		isUpdating=true;
		viewsw.updateViewsList();
		gatesw.updateGatesList();
		datasetsw.updateDatasetList();
		pc.updateChannelList();
		paneCompensation.updateForm();
		dogating();
		isUpdating=wasUpdating;
		dothelayout();
		}
	
	/**
	 * Open a project
	 */
	public void actionOpenProject()
		{
		QFileDialog dia=new QFileDialog();
		dia.setFileMode(FileMode.ExistingFile);
		dia.setDirectory(lastDirectory.getAbsolutePath());
		dia.setNameFilter(tr("Project files")+" (*.facsanadu)");
		if(dia.exec()!=0)
			{
			File f=new File(dia.selectedFiles().get(0));
			lastDirectory=f.getParentFile();
			try
				{
				project=FacsanaduXML.importXML(f);
				currentProjectFile=f;
				}
			catch (IOException e)
				{
				QTutil.showNotice(this, e.getMessage());
				e.printStackTrace();
				}
			updateall();
			}		
		}
	
	
	/**
	 * Action: Save project
	 */
	public void actionSaveProject()
		{
		if(currentProjectFile==null)
			actionSaveProjectAs();
		if(currentProjectFile!=null)
			try
				{
				FacsanaduXML.exportToFile(project, currentProjectFile);
				}
			catch (IOException e)
				{
				QTutil.showNotice(this, e.getMessage());
				e.printStackTrace();
				}
		}

	
	/**
	 * Action: Save as... file
	 */
	public void actionSaveProjectAs()
		{
		QFileDialog dia=new QFileDialog();
		dia.setFileMode(FileMode.AnyFile);
		dia.setAcceptMode(AcceptMode.AcceptSave);
		dia.setDirectory(lastDirectory.getAbsolutePath());
		dia.setDefaultSuffix("facsanadu");
		dia.setNameFilter(tr("Project files")+" (*.facsanadu)");
		if(dia.exec()!=0)
			{
			File f=new File(dia.selectedFiles().get(0));
			lastDirectory=f.getParentFile();
			currentProjectFile=f;
			actionSaveProject();
			}
		}

	
		
	


	
	
	/**
	 * Action: Export graphs
	 */
	public void actionExportGraphs()
		{
		LinkedList<Dataset> listds=getSelectedDatasets();
		LinkedList<ViewSettings> listviews=getSelectedViews();
		
		GraphExportWindow w=new GraphExportWindow();
		w.exec();
		if(w.wasOk)
			{
			QFileDialog dia=new QFileDialog();
			dia.setFileMode(FileMode.AnyFile);
			dia.setNameFilter(tr("Image files)")+" (*.png)");
			dia.setAcceptMode(AcceptMode.AcceptSave);
			dia.setDefaultSuffix("png");

			if(dia.exec()!=0)
				{
				try
					{
					File f=new File(dia.selectedFiles().get(0));
					GraphExporter.render(f, project, listds, listviews, w.splitByDataset(), w.splitByView(), w.getWidth(), w.getHeight());
					}
				catch (RuntimeException e)
					{
					QTutil.showNotice(this, e.getMessage());
					e.printStackTrace();
					}
				}		
			}
		}
	
	/**
	 * Export everything to CSV
	 */
	public void actionExportStatistics()
		{
		paneStats.actionExportCSV();
		}
	
	public void actionExportCSV()
		{
		try
			{
			LinkedList<Dataset> dsList=datasetsw.getSelectedDatasets();

			if(dsList.isEmpty())
				{
				QTutil.printError(this, tr("No datasets selected"));
				}
			else if(dsList.size()==1)
				{
				QFileDialog dia=new QFileDialog();
				dia.setFileMode(FileMode.AnyFile);
				dia.setNameFilter(tr("CSV files (*.csv)"));
				dia.setAcceptMode(AcceptMode.AcceptSave);
				dia.setDefaultSuffix("csv");

				if(dia.exec()!=0)
					{
					try
						{
						ExportFcsToCSV.save(dsList.get(0), new File(dia.selectedFiles().get(0)));
						/*
						PrintWriter fw=new PrintWriter();
						fw.println(tableStats.allToCSV());
						fw.close();
						*/
						}
					catch (IOException e)
						{
						QTutil.showNotice(this, e.getMessage());
						e.printStackTrace();
						}
					}		
				}
			else
				{
				QFileDialog dia=new QFileDialog();
				dia.setFileMode(FileMode.DirectoryOnly);
				//dia.setNameFilter(tr("CSV files (*.csv)"));
				dia.setAcceptMode(AcceptMode.AcceptSave);
				//dia.setDefaultSuffix("csv");

				if(dia.exec()!=0)
					{
					try
						{
						for(Dataset oneDataset:dsList)
							{
							File parent=new File(dia.selectedFiles().get(0));
							ExportFcsToCSV.save(oneDataset, new File(parent, oneDataset.getName()+".csv"));
							}
						}
					catch (IOException e)
						{
						QTutil.showNotice(this, e.getMessage());
						e.printStackTrace();
						}
					}		
				}
			}
		catch (Exception e)
			{
			QTutil.printError(this, tr("Failed to save file: ")+e.getMessage());
			e.printStackTrace();
			}
		}
	
	/**
	 * Load one file
	 */
	public void loadFile(File path) throws IOException
		{
		project.addDataset(path);
		handleEvent(new EventDatasetsChanged());
		}

	
	/**
	 * Get selected views
	 */
	public LinkedList<ViewSettings> getSelectedViews()
		{
		return viewsw.getSelectedViews();
		}

	
	
	/**
	 * Get selected datasets
	 */
	public LinkedList<Dataset> getSelectedDatasets()
		{
		return datasetsw.getSelectedDatasets();
		}
	public void actionDsChanged()
		{
		//Update list of selected datasets, in a thread neutral list
		synchronized (selDatasetsCache)
			{
			selDatasetsCache.clear();
			selDatasetsCache.addAll(datasetsw.getSelectedDatasets());
			}
		//Better to send a signal here instead?
		paneViews.invalidateCache();
		dogating();
		dothelayout();
		paneMetadata.updateForm();
		}
	
	/**
	 * Get selected gates
	 */
	public LinkedList<Gate> getSelectedGates()
		{
		return gatesw.getSelectedGates();
		}


	/**
	 * Update gating results
	 */
	public void dogating()
		{
		//For speed, only do selected ones
		//project.performGating(getSelectedDatasets());
		calcthread.wakeup();
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
	public void handleEvent(FacsanaduEvent event)
		{
		if(event instanceof EventGatesChanged)
			{
			gatesw.updateGatesList();
			paneViews.invalidateCache();
			dogating();
			dothelayout();
			}
		else if(event instanceof EventViewsChanged)
			{
			viewsw.updateViewsList(); //just added. problem?
			paneViews.invalidateCache();
			dogating();
			dothelayout();
			}
		else if(event instanceof EventCompensationChanged)
			{
			project.updateCompensation();
			dogating();
			dothelayout();
			}
		else if(event instanceof EventGatesMoved)
			{
			dothelayout();
			}
		else if(event instanceof EventDatasetsChanged)
			{
			datasetsw.updateDatasetList();
			paneViews.invalidateCache();
			dogating();
			paneMetadata.updateForm();
			}
		else if(event instanceof EventSetViewTool)
			{
			paneViews.setTool(((EventSetViewTool) event).choice);
			}
		else
			throw new RuntimeException("!!!");
		}


	/**
	 * Event: Widget resized
	 */
	protected void resizeEvent(QResizeEvent e)
		{
		super.resizeEvent(e);
		dothelayout();
		}
	
	public void dothelayout()
		{
		if(!isUpdating)
			{
			paneViews.updateViews();
			paneStats.updateStats();
			paneProfile.updateViews();
			QApplication.processEvents();
			//or flush?
			}
		}

	
	
	/**
	 * Event: User drags something onto widget
	 */
	protected void dragEnterEvent(com.trolltech.qt.gui.QDragEnterEvent event) 
		{
	   if(event.mimeData().hasFormat("text/uri-list"))
       event.acceptProposedAction();
		}

	/**
	 * Event: User drops MIME onto widget
	 */
	protected void dropEvent(QDropEvent event)
		{
		try
			{
			for(QUrl url:event.mimeData().urls())
				{
				File f=new File(url.path());
				lastDirectory=f.getParentFile();
				loadFile(f);
				}
			}
		catch (IOException e)
			{
			e.printStackTrace();
			}
		}

	/**
	 * Add a gate with a suggested parent
	 */
	public void addGate(Gate suggestParent, Gate g)
		{
		gatesw.addGate(suggestParent, g);
		}

	/**
	 * Get currently selected measures
	 */
	public LinkedList<GateMeasure> getSelectedMeasures()
		{
		return gatesw.getSelectedMeasures();
		}
	
	/**
	 * Show About-information
	 */
	public void actionAbout()
		{
		new DialogAbout().exec();
		}
	
	/**
	 * Open up website
	 */
	public void actionWebsite()
		{
		QDesktopServices.openUrl(new QUrl("http://www.facsanadu.org"));
		}
	
	/**
	 * Set number of CPU cores
	 */
	public void actionSetNumCores()
		{
		int th=QInputDialog.getInt(this, QtProgramInfo.programName, tr("Number of cores: "), calcthread.getNumCores());
		if(th>=1 && th<=128)
			calcthread.setNumCores(th);
		}

	public void recalcProfChan(ProfChannel chChanged)
		{
		// TODO Auto-generated method stub
		project.recalcProfChan(chChanged);
		dothelayout();
		//handleEvent(new EventViewsChanged()); //maybe too light
		}

	/**
	 * Ensure proper exit
	 */
	@Override
	protected void closeEvent(QCloseEvent arg)
		{
		super.closeEvent(arg);
		System.exit(0);
		}
	
	}
