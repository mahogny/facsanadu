package facsanadu.gui;


import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import com.trolltech.qt.core.QCoreApplication;
import com.trolltech.qt.core.QUrl;
import com.trolltech.qt.core.Qt.ScrollBarPolicy;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QFileDialog;
import com.trolltech.qt.gui.QFileDialog.AcceptMode;
import com.trolltech.qt.gui.QFileDialog.FileMode;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QMainWindow;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QMenuBar;
import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QScrollArea;
import com.trolltech.qt.gui.QSizePolicy.Policy;
import com.trolltech.qt.gui.QDropEvent;
import com.trolltech.qt.gui.QTabWidget;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;

import facsanadu.data.Dataset;
import facsanadu.gates.Gate;
import facsanadu.gui.events.EventDatasetsChanged;
import facsanadu.gui.events.EventGatesChanged;
import facsanadu.gui.events.EventGatesMoved;
import facsanadu.gui.events.EventViewsChanged;
import facsanadu.gui.events.FacsanaduEvent;
import facsanadu.gui.lengthprofile.ProfilePane;
import facsanadu.gui.qt.QTutil;
import facsanadu.gui.resource.ImgResource;
import facsanadu.gui.view.GateViewsPane;
import facsanadu.gui.view.GraphExporter;
import facsanadu.gui.view.ViewSettings;
import facsanadu.io.FacsanaduXML;

/**
 * 
 * Main window for this software
 * 
 * @author Johan Henriksson
 *
 */
public class MainWindow extends QMainWindow
	{
	public FacsanaduProject project=new FacsanaduProject();

	public File lastDirectory=new File(".");

	private QTabWidget tabwidget=new QTabWidget();
	private QMenuBar menubar=new QMenuBar();
	private GatesListWidget gatesw=new GatesListWidget(this);
	private ViewsListWidget viewsw=new ViewsListWidget(this);
	private DatasetListWidget datasetsw=new DatasetListWidget(this);
	
	private GateViewsPane paneViews;
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
		
		paneViews=new GateViewsPane(this);
		paneStats=new GateStatsPane(this);
		paneProfile=new ProfilePane(this);
		
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

		


		QVBoxLayout layLeft=new QVBoxLayout();
		layLeft.addLayout(datasetsw);
		layLeft.addLayout(viewsw);
		layLeft.addLayout(gatesw);

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
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		//loadFile(new File("/ztuff/ztufffromvenus/ztuff/customer/jin/rpt-5/rp5-larva-PMT530-day8-2010-09-11.txt"));
		

		QScrollArea scrollArea=new QScrollArea();
		scrollArea.setWidgetResizable(true);
		scrollArea.setWidget(paneViews);
		
		scrollArea.setSizePolicy(Policy.Expanding, Policy.Expanding);
		scrollArea.setVerticalScrollBarPolicy(ScrollBarPolicy.ScrollBarAlwaysOn);
		
		tabwidget.addTab(scrollArea, tr("Graphs"));
		tabwidget.addTab(paneStats, tr("Statistics"));
		tabwidget.addTab(paneProfile, tr("Length profiles"));

		QHBoxLayout lay=new QHBoxLayout();
		lay.addLayout(layLeft);
		lay.addWidget(tabwidget);
		
		QWidget cent=new QWidget();
		cent.setLayout(lay);
		setCentralWidget(cent);

		
		updateall();
		setAcceptDrops(true);
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
	
	private void updateall()
		{
		boolean wasUpdating=isUpdating;
		isUpdating=true;
		viewsw.updateViewsList();
		gatesw.updateGatesList();
		datasetsw.updateDatasetList();
		isUpdating=wasUpdating;
		dothelayout();
		}
	

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
	
	/**
	 * Load one file
	 * @throws IOException 
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
		project.performGating(getSelectedDatasets());
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
			dothelayout();
			}
		else if(event instanceof EventViewsChanged)
			{
			viewsw.updateViewsList(); //just added. problem?
			dothelayout();
			}
		else if(event instanceof EventGatesMoved)
			{
			dothelayout();
			}
		else if(event instanceof EventDatasetsChanged)
			{
			datasetsw.updateDatasetList();
			}
		else
			throw new RuntimeException("!!!");
		}

	
	@Override
	protected void resizeEvent(QResizeEvent e)
		{
		super.resizeEvent(e);
		dothelayout();
		}
	
	public void dothelayout()
		{
		if(!isUpdating)
			{
			dogating();
			paneViews.updateViews();
			paneStats.updateStats();
			paneProfile.updateViews();
			}
		}
	
	protected void dragEnterEvent(com.trolltech.qt.gui.QDragEnterEvent event) 
		{
	   if(event.mimeData().hasFormat("text/uri-list"))
       event.acceptProposedAction();
		}

	@Override
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


	public void addGate(Gate g)
		{
		gatesw.addGate(g);
		}
	
	}
