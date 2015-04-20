package facsanadu.gui.panes;

import com.trolltech.qt.core.Qt.ScrollBarPolicy;
import com.trolltech.qt.gui.QButtonGroup;
import com.trolltech.qt.gui.QCheckBox;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QScrollArea;
import com.trolltech.qt.gui.QSpinBox;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.gui.QSizePolicy.Policy;

import facsanadu.gui.MainWindow;
import facsanadu.gui.resource.ImgResource;
import facsanadu.gui.view.tool.ViewToolChoice;

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
	private QButtonGroup bgroup=new QButtonGroup(this);

	private QPushButton bGateSelect=new QPushButton(new QIcon(ImgResource.gateSelect),"");
	private QPushButton bGatePoly=new QPushButton(new QIcon(ImgResource.gatePolygon),"");
	private QPushButton bGateRect=new QPushButton(new QIcon(ImgResource.gateRect),"");

	public ViewsPane(MainWindow mw)
		{
		matrix=new ViewsMatrix(mw);
		
		bgroup.addButton(bGateSelect);
		bgroup.addButton(bGateRect);
		bgroup.addButton(bGatePoly);
		
		bGatePoly.setCheckable(true);
		bGateRect.setCheckable(true);
		bGateSelect.setCheckable(true);
		bGateSelect.setChecked(true);
		
		bGatePoly.toggled.connect(this,"actionSetTool()");
		bGateRect.toggled.connect(this,"actionSetTool()");
		bGateSelect.toggled.connect(this,"actionSetTool()");
		
		spMaxEvents.setMinimum(100);
		spMaxEvents.setMaximum(10000000);
		spMaxEvents.setValue(100000);
		cbMaxEvents.setChecked(true);
		
		QHBoxLayout laytop=new QHBoxLayout();
		laytop.addWidget(bGateSelect);
		laytop.addWidget(bGatePoly);
		laytop.addWidget(bGateRect);
		laytop.addStretch();
		laytop.addWidget(cbMaxEvents);
		laytop.addWidget(spMaxEvents);
		laytop.setMargin(2);
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

	public void actionSetTool()
		{
		if(bgroup.checkedButton()==bGatePoly)
			setTool(ViewToolChoice.POLY);
		else if(bgroup.checkedButton()==bGateSelect)
			setTool(ViewToolChoice.SELECT);
		else if(bgroup.checkedButton()==bGateRect)
			setTool(ViewToolChoice.RECT);
		}

	public void setTool(ViewToolChoice t)
		{
		if(t==ViewToolChoice.SELECT)
			bGateSelect.setChecked(true);
		else if(t==ViewToolChoice.POLY)
			bGatePoly.setChecked(true);
		else if(t==ViewToolChoice.RECT)
			bGateRect.setChecked(true);
		matrix.setTool(t);
		}
	}
