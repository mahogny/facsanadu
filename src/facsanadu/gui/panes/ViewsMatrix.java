package facsanadu.gui.panes;

import java.util.ArrayList;
import java.util.LinkedList;

import com.trolltech.qt.core.Qt.AlignmentFlag;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.gui.QSizePolicy.Policy;

import facsanadu.data.Dataset;
import facsanadu.gates.Gate;
import facsanadu.gui.MainWindow;
import facsanadu.gui.qt.QVLabel;
import facsanadu.gui.view.ViewSettings;
import facsanadu.gui.view.ViewWidget;
import facsanadu.gui.view.tool.ViewToolChoice;

/**
 * 
 * Pane showing a grid of views X datasets
 * 
 * @author Johan Henriksson
 *
 */
public class ViewsMatrix extends QWidget
	{
	private QGridLayout layViews=new QGridLayout();
	private MainWindow mw;

	private LinkedList<QLabel> headerHorizontal=new LinkedList<QLabel>();
	private LinkedList<QVLabel> headerVertical=new LinkedList<QVLabel>();
	private ArrayList<ArrayList<ViewWidget>> prevChanWidget=new ArrayList<ArrayList<ViewWidget>>();
	private boolean orderDataset=false;
	private int maxevents;

	public ViewToolChoice currentTool=ViewToolChoice.SELECT;
	
	public ViewsMatrix(MainWindow mw)
		{
		this.mw=mw;

		layViews.setMargin(2);
		layViews.setSpacing(2);
		setLayout(layViews);
		setStyleSheet("QWidget {background: white;}");
		}


	
	
	/**
	 * Update the layout of everything
	 */
	public void updateViews()
		{
		//FacsanaduProject project=mw.project;
		LinkedList<Dataset> selds=mw.getSelectedDatasets();
		LinkedList<ViewSettings> selviews=mw.getSelectedViews();
		
		//Autoscale all views to the same size
		ViewSettings.autoscale(selds, selviews);

		int numrow=selds.size();
		int numcol=selviews.size();
		if(orderDataset)
			{
			numrow=selds.size();
			numcol=selviews.size();
			}
		else
			{
			numcol=selds.size();
			numrow=selviews.size();
			}

		//Adjust horizontal header size
		//Add columns
		for(;headerHorizontal.size()<numcol;)
			{
			int i=headerHorizontal.size();
			QLabel lab=new QLabel(this);
			lab.setAlignment(AlignmentFlag.AlignCenter);
			QFont font=new QFont();
			font.setBold(true);
			lab.setFont(font);
			headerHorizontal.add(lab);
			layViews.addWidget(lab, 0, i+1);
			}
		//Remove columns
		for(;headerHorizontal.size()>numcol;)
			{
			int i=headerHorizontal.size()-1;
			QLabel lab=headerHorizontal.get(i);
			lab.setVisible(false);
			layViews.removeWidget(lab);
			headerHorizontal.remove(i);
			}


		//Adjust vertical header size
		//Add rows
		for(;headerVertical.size()<numrow;)
			{
			int i=headerVertical.size();
			QVLabel lab=new QVLabel(this);
			headerVertical.add(lab);
			layViews.addWidget(lab, i+1, 0);
			}
		//Remove rows
		for(;headerVertical.size()>numrow;)
			{
			int col=headerVertical.size()-1;
			QVLabel lab=headerVertical.get(col);
			lab.setVisible(false);
			layViews.removeWidget(lab);
			headerVertical.remove(col);
			}
		
		//Adjust number of view rows
		while(prevChanWidget.size()<numrow)
			prevChanWidget.add(new ArrayList<ViewWidget>());
		while(prevChanWidget.size()>numrow)
			{
			int row=prevChanWidget.size()-1;
			ArrayList<ViewWidget> onerow=prevChanWidget.get(row);
			for(;onerow.size()>0;)
				{
				int col=onerow.size()-1;
				ViewWidget lab=onerow.get(col);
				lab.setVisible(false);
				layViews.removeWidget(lab);
				onerow.remove(col);
				}
			prevChanWidget.remove(row);
			}
		
		//Fix number of columns
		for(int row=0;row<prevChanWidget.size();row++)
			{
			//Add columns
			ArrayList<ViewWidget> onerow=prevChanWidget.get(row);
			for(;onerow.size()<numcol;)
				{
				int col=onerow.size();
				ViewWidget lab=new ViewWidget(mw);
				lab.setTool(currentTool);
				lab.setSizePolicy(Policy.Expanding, Policy.Expanding);
				lab.setMinimumHeight(200);
				lab.setMinimumWidth(200);
				onerow.add(lab);
				layViews.addWidget(lab, row+1, col+1);
				}
			//Remove columns
			for(;onerow.size()>numcol;)
				{
				int col=onerow.size()-1;
				ViewWidget lab=onerow.get(col);
				lab.setVisible(false);
				layViews.removeWidget(lab);
				onerow.remove(col);
				}
			}
		
		int indexA=0;
		for(Dataset ds:selds)
			{
			for(int indexB=0;indexB<selviews.size();indexB++)
				{
				ViewSettings vs=selviews.get(indexB);
				int posRow,posCol;
				if(orderDataset)
					{
					posRow=indexA;
					posCol=indexB;
					}
				else
					{
					posCol=indexA;
					posRow=indexB;
					}
						
				ViewWidget lab=prevChanWidget.get(posRow).get(posCol);
				lab.setSettings(vs);
				lab.setDataset(ds);
				}
			indexA++;
			}

		//Update headers
		for(int i=0;i<selds.size();i++)
			{
			String name=selds.get(i).getName();
			if(orderDataset)
				headerVertical.get(i).setText(name);
			else
				headerHorizontal.get(i).setText(name);
			}
		for(int i=0;i<selviews.size();i++)
			{
			Gate g=selviews.get(i).gate;
			if(orderDataset)
				headerHorizontal.get(i).setText(g.name);
			else
				headerVertical.get(i).setText(g.name);
			}
		
		//Get the size of one. rescale. then rerender all
		for(ArrayList<ViewWidget> row:prevChanWidget)
			for(ViewWidget w:row)
				{
				w.maxevents=maxevents;
				w.render();
				}
		}



	public void setMaxEvents(int maxevents)
		{
		this.maxevents=maxevents;
		updateViews();
		}

	
	public void setTool(ViewToolChoice choice)
		{
		currentTool=choice;
		for(ArrayList<ViewWidget> row:prevChanWidget)
			for(ViewWidget w:row)
				w.setTool(currentTool);
		}




	public void invalidateCache()
		{
		for(ArrayList<ViewWidget> list:prevChanWidget)
			for(ViewWidget w:list)
				w.invalidateCache();
		}
	}
