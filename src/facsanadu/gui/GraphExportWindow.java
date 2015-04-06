package facsanadu.gui;

import com.trolltech.qt.gui.QButtonGroup;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QLayout;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QRadioButton;
import com.trolltech.qt.gui.QSpinBox;
import facsanadu.gui.qt.QTutil;

/**
 * 
 * Dialog for exporting graphs
 * 
 * @author Johan Henriksson
 *
 */
public class GraphExportWindow extends QDialog
	{
	private QSpinBox spWidth=new QSpinBox();
	private QSpinBox spHeight=new QSpinBox();
	
	private QPushButton bOk=new QPushButton(tr("OK"));
	private QPushButton bCancel=new QPushButton(tr("Cancel"));

	private QRadioButton bAsOne=new QRadioButton(tr("All in one"));
	private QRadioButton bByDataset=new QRadioButton(tr("Split by dataset"));
	private QRadioButton bByView=new QRadioButton(tr("Split by view"));
	private QRadioButton bAllSeparate=new QRadioButton(tr("All graphs individually"));

	public boolean wasOk=false;

	
	public GraphExportWindow()
		{
		spWidth.setMinimum(100);
		spWidth.setMaximum(10000);
		spWidth.setValue(500);

		spHeight.setMinimum(100);
		spHeight.setMaximum(10000);
		spHeight.setValue(400);
		
		QButtonGroup bg=new QButtonGroup();
		bg.addButton(bAsOne);
		bg.addButton(bByDataset);
		bg.addButton(bByView);
		bg.addButton(bAllSeparate);
		bAsOne.setChecked(true);
		
		QLayout lay=QTutil.layoutVertical(
				QTutil.withLabel(tr("Width:"), spWidth),
				QTutil.withLabel(tr("Height:"), spHeight),
				bAsOne,
				bByDataset,
				bByView,
				bAllSeparate,
				QTutil.layoutHorizontal(bOk,bCancel)
				);
		setLayout(lay);
		
		bOk.clicked.connect(this,"actionOK()");
		bCancel.clicked.connect(this,"actionCancel()");
		}
	
	
	public boolean splitByDataset()
		{
		return bByView.isChecked() || bAllSeparate.isChecked();
		}
	public boolean splitByView()
		{
		return bByDataset.isChecked() || bAllSeparate.isChecked();
		}
	
	public int getWidth()
		{
		return spWidth.value();
		}
	public int getHeight()
		{
		return spHeight.value();
		}
	
	public void actionOK()
		{
		if(storeAnnot())
			{
			wasOk=true;
			close();
			}
		}

	public boolean storeAnnot()
		{
		return true;
		}
	
	public void actionCancel()
		{
		close();
		}

	
	}
