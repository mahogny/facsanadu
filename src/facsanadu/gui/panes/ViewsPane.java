package facsanadu.gui.panes;

import com.trolltech.qt.core.Qt.ScrollBarPolicy;
import com.trolltech.qt.gui.QCheckBox;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QScrollArea;
import com.trolltech.qt.gui.QSpinBox;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.gui.QSizePolicy.Policy;

import facsanadu.gui.MainWindow;

/**
 * 
 * Pane showing all the views
 * 
 * @author Johan Henriksson
 *
 */
public class ViewsPane extends QWidget
	{
	private QSpinBox spMaxEvents=new QSpinBox();
	private QCheckBox cbMaxEvents=new QCheckBox(tr("Show max events:"));

	private ViewsMatrix matrix;
	
	public ViewsPane(MainWindow mw)
		{
		matrix=new ViewsMatrix(mw);
	
		spMaxEvents.setMinimum(100);
		spMaxEvents.setMaximum(10000000);
		spMaxEvents.setValue(100000);
		cbMaxEvents.setChecked(true);
		
		QHBoxLayout laytop=new QHBoxLayout();
		laytop.addStretch();
		laytop.addWidget(cbMaxEvents);
		laytop.addWidget(spMaxEvents);
		laytop.setMargin(0);
		laytop.setSpacing(2);		
		
		QScrollArea scrollArea=new QScrollArea();
		scrollArea.setWidgetResizable(true);
		scrollArea.setWidget(matrix);
		scrollArea.setSizePolicy(Policy.Expanding, Policy.Expanding);
		scrollArea.setVerticalScrollBarPolicy(ScrollBarPolicy.ScrollBarAlwaysOn);

		QVBoxLayout lay=new QVBoxLayout();
		lay.addLayout(laytop);
		lay.addWidget(scrollArea);
		lay.setMargin(0);
		lay.setSpacing(2);
		setLayout(lay);
		
		spMaxEvents.valueChanged.connect(this,"valuesupdated()");
		cbMaxEvents.stateChanged.connect(this,"valuesupdated()");
		valuesupdated();
		}

	public void updateViews()
		{
		matrix.updateViews();
		}

	public void valuesupdated()
		{
		int maxevents=Integer.MAX_VALUE;
		if(cbMaxEvents.isChecked())
			maxevents=spMaxEvents.value();
		matrix.setMaxEvents(maxevents);
		}

	}
