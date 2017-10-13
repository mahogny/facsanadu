package facsanadu.gui.panes;


import java.util.LinkedList;

import com.trolltech.qt.core.Qt.ItemFlag;
import com.trolltech.qt.core.Qt.ItemFlags;
import com.trolltech.qt.gui.QTableWidgetItem;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.gui.QHeaderView.ResizeMode;

import facsanadu.data.Compensation;
import facsanadu.gui.MainWindow;
import facsanadu.gui.events.EventCompensationChanged;
import facsanadu.gui.qt.QTableWidgetWithCSVcopy;

/**
 * 
 * Pane showing compensation
 * 
 * @author Johan Henriksson
 *
 */
public class CompensationPane extends QWidget
	{
	private QTableWidgetWithCSVcopy tableMatrix=new QTableWidgetWithCSVcopy();
	private MainWindow mw;
	private boolean updating=false;
		
	public CompensationPane(MainWindow mw)
		{
		this.mw=mw;		
		
		QVBoxLayout lay=new QVBoxLayout();
		lay.addWidget(tableMatrix);
		//lay.addStretch();
		lay.setMargin(0);
		setLayout(lay);
		updateForm();
		}
	
	
	public void updateForm()
		{
		updating=true;
		tableMatrix.clear();
		
		tableMatrix.horizontalHeader().setResizeMode(ResizeMode.ResizeToContents);
		tableMatrix.horizontalHeader().setStretchLastSection(true);		

		Compensation comp=mw.project.compensation;
		
		LinkedList<String> header=new LinkedList<String>();
		LinkedList<String> headerFrom=new LinkedList<String>();
		for(String s:comp.cnames)
			{
			header.add(s);
			headerFrom.add(tr("To: ")+s);
			}
		
		//ROW is TO. COL is FROM
		
		tableMatrix.setColumnCount(comp.getSize());
		tableMatrix.setRowCount(comp.getSize());
		tableMatrix.setHorizontalHeaderLabels(header);
		tableMatrix.setVerticalHeaderLabels(headerFrom);
		
		for(int row=0;row<comp.getSize();row++)
			for(int col=0;col<comp.getSize();col++)
				{
				QTableWidgetItem it=new QTableWidgetItem(""+comp.get(row,col));
				it.setFlags(new ItemFlags(ItemFlag.ItemIsSelectable, ItemFlag.ItemIsEnabled, ItemFlag.ItemIsEditable));
				tableMatrix.setItem(row, col, it);
				}
		tableMatrix.itemChanged.connect(this,"dataChanged(QTableWidgetItem)");
		updating=false;
		}
	
	
	public void dataChanged(QTableWidgetItem it)
		{
		if(!updating)
			{
			Compensation comp=mw.project.compensation;
			comp.set(it.row(),it.column(),Double.parseDouble(it.text()));
			mw.handleEvent(new EventCompensationChanged());
			}
		}
	}
