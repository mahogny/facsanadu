package facsanadu.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QCheckBox;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QTableWidget;
import com.trolltech.qt.gui.QTableWidgetItem;
import com.trolltech.qt.gui.QAbstractItemView.SelectionBehavior;
import com.trolltech.qt.gui.QHeaderView.ResizeMode;

import facsanadu.data.ChannelInfo;
import facsanadu.gates.measure.GateMeasure;
import facsanadu.gates.measure.GateMeasureMean;
import facsanadu.gui.qt.QTutil;

/**
 * 
 * Dialog to add measurements
 * 
 * @author Johan Henriksson
 *
 */
public class AddMeasureDialog extends QDialog
	{
	private QTableWidget tableChannels=new QTableWidget();
	private QCheckBox cbMean=new QCheckBox(tr("Mean"));
	private QCheckBox cbMedian=new QCheckBox(tr("Median"));
	private QCheckBox cbSD=new QCheckBox(tr("Standard deviation"));
	private QPushButton bOk=new QPushButton(tr("OK"));
	private QPushButton bCancel=new QPushButton(tr("Cancel"));
	
	private FacsanaduProject proj;
	
	public AddMeasureDialog(FacsanaduProject proj)
		{
		this.proj=proj;
		
		tableChannels.setColumnCount(1);
		tableChannels.verticalHeader().hide();
		tableChannels.setHorizontalHeaderLabels(Arrays.asList(tr("Channel")));
		tableChannels.setSelectionBehavior(SelectionBehavior.SelectRows);
		tableChannels.horizontalHeader().setResizeMode(ResizeMode.ResizeToContents);
		tableChannels.horizontalHeader().setStretchLastSection(true);		

		bOk.clicked.connect(this,"actionOK()");
		bCancel.clicked.connect(this,"close()");
		
		updatechanlist();
		setLayout(QTutil.layoutVertical(tableChannels, cbMean, cbMedian, cbSD, QTutil.layoutHorizontal(bOk,bCancel)));
		
		setMinimumSize(200, 400);
		}

	private boolean wasOK=false;
	public void actionOK()
		{
		wasOK=true;
		close();
		}
	
	public void updatechanlist()
		{
		int row=0;
		ArrayList<ChannelInfo> chans=proj.getChannelInfo();
		tableChannels.setRowCount(chans.size());
		for(int i=0;i<chans.size();i++)
			{
			ChannelInfo ci=chans.get(i);
			QTableWidgetItem it=QTutil.createReadOnlyItem(ci.formatName());
			it.setData(Qt.ItemDataRole.UserRole, ci);
			tableChannels.setItem(row, 0, it);
			row++;
			}
		}
	
	/**
	 * Get selected channels
	 */
	public LinkedList<ChannelInfo> getSelectedChannels()
		{
		LinkedList<ChannelInfo> selviews=new LinkedList<ChannelInfo>();
		for(QModelIndex in:tableChannels.selectionModel().selectedRows())
			selviews.add((ChannelInfo)tableChannels.item(in.row(),0).data(Qt.ItemDataRole.UserRole));
		return selviews;
		}
	
	public Collection<GateMeasure> getMeasures()
		{
		LinkedList<GateMeasure> list=new LinkedList<GateMeasure>();
		if(wasOK)
			{
			ArrayList<ChannelInfo> chans=proj.getChannelInfo();
			for(ChannelInfo info:getSelectedChannels())
				{
				int i=chans.indexOf(info);
				if(cbMean.isChecked())
					{
					GateMeasureMean c=new GateMeasureMean();
					c.channelIndex=i;
					list.add(c);
					}
				}
			}
		return list;
		}

	
	}
