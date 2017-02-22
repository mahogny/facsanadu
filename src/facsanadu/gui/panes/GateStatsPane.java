package facsanadu.gui.panes;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.LinkedList;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QCheckBox;
import com.trolltech.qt.gui.QFileDialog;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QTableWidgetItem;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.gui.QFileDialog.AcceptMode;
import com.trolltech.qt.gui.QFileDialog.FileMode;
import com.trolltech.qt.gui.QHeaderView.ResizeMode;

import facsanadu.data.Dataset;
import facsanadu.gates.Gate;
import facsanadu.gates.GatingResult;
import facsanadu.gates.IntArray;
import facsanadu.gates.measure.GateMeasure;
import facsanadu.gui.MainWindow;
import facsanadu.gui.qt.QTableWidgetWithCSVcopy;
import facsanadu.gui.qt.QTutil;

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
	private QPushButton bCopyCSV=new QPushButton(tr("Export to clipboard"));
	
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
		LinkedList<GateMeasure> listCalc=mw.getSelectedMeasures();
		LinkedList<Dataset> listDatasets=mw.getSelectedDatasets();

		int perGateNumStat=0;
		if(cbShowParent.isChecked())
			perGateNumStat++;
		if(cbShowTotal.isChecked())
			perGateNumStat++;
		tableStats.setColumnCount(listGates.size()*perGateNumStat + listCalc.size() + 1);
		LinkedList<String> header=new LinkedList<String>();
		header.add(tr("Dataset"));
		for(Gate g:listGates)
			{
			if(cbShowParent.isChecked())
				header.add(g.name+ " (parent)");
			if(cbShowTotal.isChecked())
				header.add(g.name+" (total)");
			}
		for(GateMeasure calc:listCalc)
			{
			header.add(calc.gate.name+"/"+calc.getDesc(mw.project));
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

				IntArray arr=gr.getAcceptedFromGate(gate);
				if(arr==null)
					arr=new IntArray(); 

				
				//Compute percent of parent
				Gate gparent=gate.parent;
				if(gparent==null)
					gparent=gr.getRootGate();
				IntArray arrParent=gr.getAcceptedFromGate(gparent);
				double percParent;
				if(arrParent!=null)
					percParent=arr.size()/(double)arrParent.size();
				else
					percParent=-1; //Cannot be calculated yes
				
				//Compute percent of total
				double percTotal=arr.size()/(double)gr.getTotalCount(); 
				
				if(cbShowParent.isChecked())
					{
					QTableWidgetItem it=QTutil.createReadOnlyItem(formatPerc(percParent));
					it.setData(Qt.ItemDataRole.UserRole, percParent);
					tableStats.setItem(row, curcol, it);
					curcol++;
					}
				if(cbShowTotal.isChecked())
					{
					QTableWidgetItem it=QTutil.createReadOnlyItem(formatPerc(percTotal));
					it.setData(Qt.ItemDataRole.UserRole, percTotal);
					tableStats.setItem(row, curcol, it);
					curcol++;
					}
				}

			for(int i=0;i<listCalc.size();i++)
				{
				GateMeasure calc=listCalc.get(i);
				
				Double v=gr.getCalcResult(calc);
				QTableWidgetItem it=QTutil.createReadOnlyItem(v!=null ? ""+v : tr("N/A"));
				it.setData(Qt.ItemDataRole.UserRole, v);
				tableStats.setItem(row, curcol, it);
				curcol++;
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


	/**
	 * Export everything to CSV
	 */
	public void actionExportCSV()
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
				PrintWriter fw=new PrintWriter(new File(dia.selectedFiles().get(0)));
				fw.println(tableStats.allToCSV());
				fw.close();
				}
			catch (IOException e)
				{
				QTutil.showNotice(this, e.getMessage());
				e.printStackTrace();
				}
			}		
		}
	}
