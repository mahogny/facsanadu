package facsanadu.gui;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QFileDialog;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QTableWidget;
import com.trolltech.qt.gui.QTableWidgetItem;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QAbstractItemView.SelectionBehavior;
import com.trolltech.qt.gui.QFileDialog.FileMode;
import com.trolltech.qt.gui.QHeaderView.ResizeMode;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QSizePolicy.Policy;

import facsanadu.data.Dataset;
import facsanadu.gui.events.EventDatasetsChanged;
import facsanadu.gui.events.FacsanaduEvent;
import facsanadu.gui.qt.QTutil;
import facsanadu.gui.resource.ImgResource;

/**
 * 
 * List of all datasets
 * 
 * @author Johan Henriksson
 *
 */
public class DatasetListWidget extends QVBoxLayout
	{
	private MainWindow mw;
	
	private boolean isUpdating=false;

	private QTableWidget tableDatasets=new QTableWidget();

	public QSignalEmitter.Signal0 selectionChanged=new QSignalEmitter.Signal0();
	
	public DatasetListWidget(MainWindow mw)
		{
		this.mw=mw;
		setMargin(0);
		
		tableDatasets.setColumnCount(1);
		tableDatasets.verticalHeader().hide();
		tableDatasets.setHorizontalHeaderLabels(Arrays.asList(tr("Dataset")));
		tableDatasets.setSelectionBehavior(SelectionBehavior.SelectRows);
		tableDatasets.horizontalHeader().setResizeMode(ResizeMode.ResizeToContents);
		tableDatasets.horizontalHeader().setStretchLastSection(true);		
		tableDatasets.selectionModel().selectionChanged.connect(this,"dothelayout()");


		QPushButton bAddDataset=new QPushButton(tr("Add dataset"));
		QPushButton bSelectAllDataset=new QPushButton(tr("Select all"));
		QPushButton bRemoveDataset=new QPushButton(new QIcon(ImgResource.delete),"");
		
		QPushButton bMoveUp=new QPushButton(new QIcon(ImgResource.moveUp),"");
		QPushButton bMoveDown=new QPushButton(new QIcon(ImgResource.moveDown),"");
		
		bAddDataset.clicked.connect(this,"actionAddDatasets()");
		bRemoveDataset.clicked.connect(this,"actionRemoveDataset()");
		bSelectAllDataset.clicked.connect(this,"actionSelectAllDataset()");
		bMoveUp.clicked.connect(this,"actionMoveUp()");
		bMoveDown.clicked.connect(this,"actionMoveDown()");

		addWidget(tableDatasets);
		addLayout(QTutil.layoutHorizontal(bMoveUp, bMoveDown, bAddDataset, bSelectAllDataset, bRemoveDataset));

		tableDatasets.setSizePolicy(Policy.Minimum, Policy.Expanding);
		}

	public void dothelayout()
		{
		selectionChanged.emit();
		}
		
	/**
	 * Update list with datasets
	 */
	void updateDatasetList()
		{
		LinkedList<Dataset> prevsel=getSelectedDatasets();
		
		FacsanaduProject project=mw.project;
		boolean wasUpdating=isUpdating;
		isUpdating=false;
		tableDatasets.setRowCount(project.datasets.size());
		int row=0;
		for(Dataset ds:project.datasets)
			{
			QTableWidgetItem it=QTutil.createReadOnlyItem(ds.source.getName());
			it.setData(Qt.ItemDataRole.UserRole, ds);
			tableDatasets.setItem(row, 0, it);
			if(prevsel.contains(ds))
				it.setSelected(true);
			else
				it.setSelected(false);
			row++;
			}
		isUpdating=wasUpdating;
		}

	public void actionSelectAllDataset()
		{
		tableDatasets.selectAll();
		}

	
	/**
	 * Action: Add/import datasets
	 */
	public void actionAddDatasets()
		{
		QFileDialog dia=new QFileDialog();
		dia.setFileMode(FileMode.ExistingFiles);
		dia.setDirectory(mw.lastDirectory.getAbsolutePath());
		dia.setNameFilter(tr(tr("FCS files")+" (*.fcs *.txt *.lmd)"));
		if(dia.exec()!=0)
			{
			try
				{
				for(String sf:dia.selectedFiles())
					{
					File f=new File(sf);
					mw.lastDirectory=f.getParentFile();
					mw.loadFile(f);
					}
				}
			catch (IOException e)
				{
				QTutil.showNotice(mw, e.getMessage());
				e.printStackTrace();
				}
			}		
		updateDatasetList();
		}



	
	/**
	 * Action: Remove selected datasets
	 */
	public void actionRemoveDataset()
		{
		FacsanaduProject project=mw.project;
		project.datasets.removeAll(getSelectedDatasets());
		updateDatasetList();
		emitEvent(new EventDatasetsChanged());
		}

	private void emitEvent(FacsanaduEvent event)
		{
		mw.handleEvent(event);
		}


	public void actionMoveUp()
		{
		LinkedList<Dataset> list=getSelectedDatasets();
		for(Dataset ds:list)
			{
			FacsanaduProject project=mw.project;
			int i=project.datasets.indexOf(ds);
			if(i==0)
				break; //Don't attempt
			project.datasets.remove(ds);
			project.datasets.add(i-1, ds);
			}
		emitEvent(new EventDatasetsChanged());
		}
	
	
	public void actionMoveDown()
		{
		LinkedList<Dataset> list=getSelectedDatasets();
		for(int j=list.size()-1;j>=0;j--)
			{
			Dataset ds=list.get(j);
			FacsanaduProject project=mw.project;
			int i=project.datasets.indexOf(ds);
			if(i==project.datasets.size()-1)
				break; //Don't attempt
			project.datasets.remove(ds);
			project.datasets.add(i+1, ds);
			}
		emitEvent(new EventDatasetsChanged());
		}


	/**
	 * Get selected datasets
	 */
	public LinkedList<Dataset> getSelectedDatasets()
		{
		LinkedList<Dataset> selviews=new LinkedList<Dataset>();
		for(QModelIndex in:tableDatasets.selectionModel().selectedRows())
			selviews.add((Dataset)tableDatasets.item(in.row(),0).data(Qt.ItemDataRole.UserRole));
		return selviews;
		}

	}
