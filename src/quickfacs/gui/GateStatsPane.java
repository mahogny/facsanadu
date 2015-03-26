package quickfacs.gui;

import java.text.NumberFormat;
import java.util.LinkedList;

import quickfacs.data.Dataset;
import quickfacs.gates.Gate;
import quickfacs.gates.GatingResult;
import quickfacs.gates.IntArray;
import quickfacs.gui.qt.QTableWidgetWithCSVcopy;
import quickfacs.gui.qt.QTutil;

import com.trolltech.qt.gui.QCheckBox;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QTableWidgetItem;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;
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
	private QTableWidgetWithCSVcopy tableStats=new QTableWidgetWithCSVcopy();
	
	private MainWindow mw;
	
	private QCheckBox cbShowParent=new QCheckBox(tr("Show % of parent"));
	private QCheckBox cbShowTotal=new QCheckBox(tr("Show % of total"));
	private QPushButton bCopyCSV=new QPushButton(tr("Copy all"));
	
	public GateStatsPane(MainWindow mw)
		{
		this.mw=mw;		
		
		cbShowParent.setChecked(true);
		cbShowTotal.setChecked(false);
		
		cbShowParent.stateChanged.connect(this,"updateStats()");
		cbShowTotal.stateChanged.connect(this,"updateStats()");
		bCopyCSV.clicked.connect(this,"actionCopyToClipboard()");
		
		QVBoxLayout lay=new QVBoxLayout();
		lay.addWidget(tableStats);
		lay.addLayout(QTutil.layoutHorizontal(cbShowParent, cbShowTotal, bCopyCSV));
		lay.setMargin(0);
		setLayout(lay);
		}
	
	
	public void updateStats()
		{
		tableStats.clear();
		
		
		tableStats.verticalHeader().hide();
		tableStats.horizontalHeader().setResizeMode(ResizeMode.ResizeToContents);
		tableStats.horizontalHeader().setStretchLastSection(true);		


		LinkedList<Gate> listGates=mw.getSelectedGates();
		LinkedList<Dataset> listDatasets=mw.getSelectedDatasets();

		int perg=0;
		if(cbShowParent.isChecked())
			perg++;
		if(cbShowTotal.isChecked())
			perg++;
		tableStats.setColumnCount(listGates.size()*perg+1);
		LinkedList<String> header=new LinkedList<String>();
		header.add(tr("Dataset"));
		for(Gate g:listGates)
			{
			if(cbShowParent.isChecked())
				header.add(g.name+ " (parent)");
			if(cbShowTotal.isChecked())
				header.add(g.name+" (total)");
			}
		
		
		tableStats.setHorizontalHeaderLabels(header);
		tableStats.setRowCount(listDatasets.size());
		for(int row=0;row<listDatasets.size();row++)
			{
			Dataset dataset=listDatasets.get(row);
			GatingResult gr=mw.project.getGatingResult(dataset);

			int curcol=1;
			for(int igate=0;igate<listGates.size();igate++)
				{
				Gate gate=listGates.get(igate);

				IntArray arr=gr.acceptedFromGate.get(gate);
				if(arr==null)
					arr=new IntArray(); 

				
				//Compute percent of parent
				Gate gparent=gate.parent;
				if(gparent==null)
					gparent=gr.getRootGate();
				IntArray arrParent=gr.acceptedFromGate.get(gparent);
				double percParent=arr.size()/(double)arrParent.size();
				
				//Compute percent of total
				double percTotal=arr.size()/(double)gr.getTotalCount(); 
				
				if(cbShowParent.isChecked())
					{
					QTableWidgetItem it=QTutil.createReadOnlyItem(formatPerc(percParent));
					tableStats.setItem(row, curcol, it);
					curcol++;
					}
				if(cbShowTotal.isChecked())
					{
					QTableWidgetItem it=QTutil.createReadOnlyItem(formatPerc(percTotal));
					tableStats.setItem(row, curcol, it);
					curcol++;
					}
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

	
	/**
	 * Copy all of table to clipboard
	 */
	public void actionCopyToClipboard()
		{
		tableStats.copyAll();
		}
	}
