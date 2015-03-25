package glofacs.gui;

import java.text.NumberFormat;
import java.util.LinkedList;

import glofacs.gates.Gate;
import glofacs.gates.GatingResult;
import glofacs.gates.IntArray;
import glofacs.io.Dataset;

import com.trolltech.qt.gui.QTableWidget;
import com.trolltech.qt.gui.QTableWidgetItem;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.gui.QAbstractItemView.SelectionBehavior;
import com.trolltech.qt.gui.QHeaderView.ResizeMode;

/**
 * 
 * Pane showing gating statistics
 * 
 * @author Johan Henriksson
 *
 */
public class GateStatsPane extends QWidget
	{
	private QTableWidget tableStats=new QTableWidget();
	private MainWindow mw;
	
	boolean showOfParent=true;
	
	public GateStatsPane(MainWindow mw)
		{
		this.mw=mw;		
		
		QVBoxLayout lay=new QVBoxLayout();
		lay.addWidget(tableStats);
		setLayout(lay);
		}
	
	
	public void updateStats()
		{
		tableStats.clear();
		
		
		tableStats.verticalHeader().hide();
		tableStats.setSelectionBehavior(SelectionBehavior.SelectRows);
		tableStats.horizontalHeader().setResizeMode(ResizeMode.ResizeToContents);
		tableStats.horizontalHeader().setStretchLastSection(true);		


		LinkedList<Gate> listGates=mw.getSelectedGates();
		LinkedList<Dataset> listDatasets=mw.getSelectedDatasets();

		
		tableStats.setColumnCount(listGates.size()+1);
		LinkedList<String> header=new LinkedList<String>();
		header.add(tr("Dataset"));
		for(Gate g:listGates)
			header.add(g.name); //TODO perc total etc
		
		
		tableStats.setHorizontalHeaderLabels(header);
		tableStats.setRowCount(listDatasets.size());
		for(int row=0;row<listDatasets.size();row++)
			{
			Dataset dataset=listDatasets.get(row);
			GatingResult gr=mw.project.getGatingResult(dataset);

			int totalcount=gr.getTotalCount();

			for(int col=0;col<listGates.size();col++)
				{
				Gate gate=listGates.get(col);

				IntArray arr=gr.acceptedFromGate.get(gate);
				if(arr==null)
					arr=new IntArray(); 

				
				
				double percParent=1;
				if(gate.parent!=null)
					{
					IntArray parent=gr.acceptedFromGate.get(gate.parent);
					if(parent==null)
						parent=new IntArray(); //saving from problems
					percParent=arr.size()/(double)parent.size();
					}
				
				//one more reason to have a root gate!
				double percTotal=arr.size()/(double)totalcount; 
				
				QTableWidgetItem it;
				if(showOfParent)
					it=QTutil.createReadOnlyItem(formatPerc(percParent));
				else
					it=QTutil.createReadOnlyItem(formatPerc(percTotal));
				tableStats.setItem(row, col+1, it);
				}

			QTableWidgetItem it=QTutil.createReadOnlyItem(dataset.source.getName());
			tableStats.setItem(row, 0, it);
			}
		
		}

	private static String formatPerc(double d)
		{
		NumberFormat nf=NumberFormat.getInstance();
		if(d>5)
			nf.setMaximumFractionDigits(1);
		else
			nf.setMaximumFractionDigits(3);
		return nf.format(d*100)+"%";
		}
	
	}
