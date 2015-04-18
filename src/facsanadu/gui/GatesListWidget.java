package facsanadu.gui;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QInputDialog;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QTreeWidget;
import com.trolltech.qt.gui.QTreeWidgetItem;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QAbstractItemView.SelectionBehavior;
import com.trolltech.qt.gui.QAbstractItemView.SelectionMode;
import com.trolltech.qt.gui.QLineEdit.EchoMode;
import com.trolltech.qt.gui.QSizePolicy.Policy;

import facsanadu.gates.Gate;
import facsanadu.gui.events.EventGatesChanged;
import facsanadu.gui.events.EventViewsChanged;
import facsanadu.gui.events.FacsanaduEvent;
import facsanadu.gui.qt.QTutil;
import facsanadu.gui.view.ViewSettings;


/**
 * 
 * List of all gates
 * 
 * @author Johan Henriksson
 *
 */
public class GatesListWidget extends QVBoxLayout
	{
	private boolean isUpdating=false;

	private QTreeWidget treeGates=new QTreeWidget();
	private MainWindow mw;
	
	public GatesListWidget(MainWindow mw)
		{
		this.mw=mw;
		setMargin(0);
		
		treeGates.setHeaderLabels(Arrays.asList(tr("Gate")));
		treeGates.setSelectionBehavior(SelectionBehavior.SelectRows);
		treeGates.setSelectionMode(SelectionMode.MultiSelection);
		treeGates.selectionModel().selectionChanged.connect(this,"dothelayout()");

		treeGates.setSizePolicy(Policy.Minimum, Policy.Expanding);

		QPushButton bSelectAllGates=new QPushButton(tr("Select all"));
		QPushButton bRenameGate=new QPushButton(tr("Rename gate"));
		QPushButton bRemoveGate=new QPushButton(tr("Remove gate"));

		bRenameGate.clicked.connect(this,"actionRenameGate()");
		bRemoveGate.clicked.connect(this,"actionRemoveGates()");
		bSelectAllGates.clicked.connect(this,"actionSelectAllGates()");

		addWidget(treeGates);
		addLayout(QTutil.layoutHorizontal(bSelectAllGates, bRenameGate, bRemoveGate));
		}
	
	
	public void dothelayout()
		{
		mw.dothelayout();
		}
	
	public void actionSelectAllGates()
		{
		treeGates.selectAll();
		}

	/**
	 * Get the currently selected gate or null
	 */
	public Gate getCurrentGate()
		{
		QTreeWidgetItem it=treeGates.currentItem();
		if(it!=null)
			return (Gate)it.data(0,Qt.ItemDataRole.UserRole);
		else
			return null;
		}
	
	/**
	 * Get selected gates
	 */
	public LinkedList<Gate> getSelectedGates()
		{
		LinkedList<Gate> selviews=new LinkedList<Gate>();
		for(QTreeWidgetItem it:treeGates.selectedItems())
			selviews.add((Gate)it.data(0,Qt.ItemDataRole.UserRole));
		return selviews;
		}

	
	/**
	 * Update list with gates
	 */
	public void updateGatesList()
		{
		boolean wasUpdating=isUpdating;
		LinkedList<Gate> selgates=getSelectedGates();
		isUpdating=false;
		treeGates.clear();
		updateGatesListRecursive(null, mw.project.gateset.getRootGate(), selgates);
		treeGates.expandAll();
		isUpdating=wasUpdating;
		}
	private void updateGatesListRecursive(QTreeWidgetItem parentItem, Gate g, LinkedList<Gate> selgates)
		{
		QTreeWidgetItem item;
		if(parentItem==null)
			item=new QTreeWidgetItem(treeGates);
		else
			item=new QTreeWidgetItem(parentItem);
		item.setData(0,Qt.ItemDataRole.UserRole, g);
		item.setText(0, g.name);
		if(selgates.contains(g))
			item.setSelected(true);
		for(Gate child:g.children)
			updateGatesListRecursive(item, child, selgates);
		}

	/**
	 * Rename current gate
	 */
	public void actionRenameGate()
		{
		FacsanaduProject project=mw.project;
		Gate g=getCurrentGate();
		if(g!=null && g!=project.gateset.getRootGate())
			{
			String newname=QInputDialog.getText(mw, tr("Rename gate"), tr("New name:"), EchoMode.Normal, g.name);
			if(!newname.equals("") && (newname.equals(g.name) || !project.gateset.getGateNames().contains(newname)))
				{
				g.name=newname;
				emitEvent(new EventGatesChanged());
				}
			else
				QTutil.showNotice(mw, tr("Invalid name"));
			}
		
		}


	private void emitEvent(FacsanaduEvent event)
		{
		mw.handleEvent(event);
		}

	
	/**
	 * Action: Remove selected gates
	 */
	public void actionRemoveGates()
		{
		FacsanaduProject project=mw.project;
		Collection<Gate> gates=getSelectedGates();
		gates.remove(project.gateset.getRootGate());
		//Should include gates recursively!

		for(Gate g:gates)
			g.detachParent();

		boolean changedViews=false;
		for(ViewSettings vs:new LinkedList<ViewSettings>(project.views))
			if(gates.contains(vs.gate))  //TODO or any gate below!!
				{
				project.views.remove(vs);
				changedViews=true;
				}
		emitEvent(new EventGatesChanged());
		if(changedViews)
			emitEvent(new EventViewsChanged());
//		updateGatesList();
//		mw.updateViewsList();  //use a signal instead
//		dothelayout();
		}
	
	/**
	 * Add a new gate
	 */
	public void addGate(Gate g)
		{
		FacsanaduProject project=mw.project;
		g.name=project.gateset.getFreeName();
		Gate parent=getCurrentGate();
		if(parent==null)
			parent=project.gateset.getRootGate();
		parent.attachChild(g);
		
		updateGatesList();
		}


	}
