package facsanadu.gui;

import java.util.Arrays;
import java.util.LinkedList;

import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QTableWidget;
import com.trolltech.qt.gui.QTableWidgetItem;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QAbstractItemView.SelectionBehavior;
import com.trolltech.qt.gui.QHeaderView.ResizeMode;
import com.trolltech.qt.gui.QItemSelectionModel.SelectionFlag;
import com.trolltech.qt.gui.QSizePolicy.Policy;

import facsanadu.data.ProfChannel;
import facsanadu.gui.lengthprofile.ProfilePane;
import facsanadu.gui.qt.QTutil;
import facsanadu.gui.resource.ImgResource;

/**
 * 
 * List of all profile channels
 * 
 * @author Johan Henriksson
 *
 */
public class ProfileChannelWidget extends QVBoxLayout
	{
	private MainWindow mw;
	private boolean isUpdating=false;
	private QTableWidget tableDatasets=new QTableWidget();
	public ProfilePane paneProfile;
	
	public ProfileChannelWidget(MainWindow mw)
		{
		this.mw=mw;
		setMargin(0);
		
		tableDatasets.setColumnCount(1);
		tableDatasets.verticalHeader().hide();
		tableDatasets.setHorizontalHeaderLabels(Arrays.asList(tr("Prof.Channel")));
		tableDatasets.setSelectionBehavior(SelectionBehavior.SelectRows);
		tableDatasets.horizontalHeader().setResizeMode(ResizeMode.ResizeToContents);
		tableDatasets.horizontalHeader().setStretchLastSection(true);		
		tableDatasets.selectionModel().selectionChanged.connect(this,"dothelayout()");

		QPushButton bAddChannel=new QPushButton(tr("Create prof.channel"));
		QPushButton bRemoveDataset=new QPushButton(new QIcon(ImgResource.delete),"");
		
		bAddChannel.clicked.connect(this,"actionAddChannel()");
		bRemoveDataset.clicked.connect(this,"actionRemoveChannel()");
		tableDatasets.selectionModel().selectionChanged.connect(this,"actionSelectionChanged()");
		
		addWidget(tableDatasets);
		addLayout(QTutil.layoutHorizontal(bAddChannel, bRemoveDataset));

		tableDatasets.setSizePolicy(Policy.Minimum, Policy.Expanding);
		}

	public void dothelayout()
		{
		mw.dothelayout();
		}
	
	
	/**
	 * Update list with channels
	 */
	void updateChannelList()
		{
		FacsanaduProject project=mw.project;
		boolean wasUpdating=isUpdating;
		isUpdating=false;
		tableDatasets.setRowCount(project.profchan.size());
		int row=0;
		for(ProfChannel chan:project.profchan)
			{
			QTableWidgetItem it=QTutil.createReadOnlyItem(chan.getName());   //Maybe separate columns here
			it.setData(Qt.ItemDataRole.UserRole, chan);
			tableDatasets.setItem(row, 0, it);
			row++;
			}
		isUpdating=wasUpdating;
		}

	public void actionSelectAllDataset()
		{
		tableDatasets.selectAll();
		}

	
	/**
	 * Action: Create channel
	 */
	public void actionAddChannel()
		{
		FacsanaduProject project=mw.project;
		for(int chid:paneProfile.getSelChans())
			{
			ProfChannel ch=new ProfChannel();
			ch.channel=chid;
			project.profchan.add(ch);
			
			updateChannelList();
			tableDatasets.selectionModel().select(tableDatasets.model().index(tableDatasets.rowCount()-1, 0), SelectionFlag.ClearAndSelect);
			mw.recalcProfChan(null);
			break; //Only create one?
			}
		}



	
	/**
	 * Action: Remove selected channels
	 */
	public void actionRemoveChannel()
		{
		FacsanaduProject project=mw.project;
		project.profchan.removeAll(getSelectedChannels());
		updateChannelList();
		mw.recalcProfChan(null);
		}

	/*
	private void emitEvent(FacsanaduEvent event)
		{
		mw.handleEvent(event);
		}*/




	/**
	 * Get selected channels
	 */
	public LinkedList<ProfChannel> getSelectedChannels()
		{
		LinkedList<ProfChannel> selviews=new LinkedList<ProfChannel>();
		for(QModelIndex in:tableDatasets.selectionModel().selectedRows())
			selviews.add((ProfChannel)tableDatasets.item(in.row(),0).data(Qt.ItemDataRole.UserRole));
		return selviews;
		}

	public void actionSelectionChanged()
		{
		ProfChannel ch=null;
		for(ProfChannel c:getSelectedChannels())
			ch=c;
		paneProfile.setCurChan(ch);
		}
	
	}
