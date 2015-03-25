package glofacs.gui;

import glofacs.gates.Gate;
import glofacs.gui.channel.ChannelWidget;
import glofacs.gui.channel.ViewSettings;
import glofacs.io.FCSFile;

import java.util.ArrayList;
import java.util.LinkedList;

import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.gui.QSizePolicy.Policy;

/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class GateViewsPane extends QWidget
	{
	private QGridLayout layViews=new QGridLayout();
	private MainWindow mw;

	private LinkedList<QLabel> headerHorizontal=new LinkedList<QLabel>();
	private LinkedList<QVLabel> headerVertical=new LinkedList<QVLabel>();
	private ArrayList<ArrayList<ChannelWidget>> prevChanWidget=new ArrayList<ArrayList<ChannelWidget>>();
	private boolean orderDataset=false;


	public GateViewsPane(MainWindow mw)
		{
		this.mw=mw;
		setLayout(layViews);
		}


	
	
	/**
	 * Update the layout of everything
	 */
	public void updateViews()
		{
		LinkedList<FCSFile.DataSegment> selds=mw.getSelectedDatasets();
		LinkedList<ViewSettings> selviews=mw.getSelectedViews();
		
		//autoscale all views to the same size
		if(!selds.isEmpty())
			for(ViewSettings vs:selviews)
				vs.autoscale(selds.get(0));

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
			prevChanWidget.add(new ArrayList<ChannelWidget>());
		while(prevChanWidget.size()>numrow)
			{
			int row=prevChanWidget.size()-1;
			ArrayList<ChannelWidget> onerow=prevChanWidget.get(row);
			for(;onerow.size()>0;)
				{
				int col=onerow.size()-1;
				ChannelWidget lab=onerow.get(col);
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
			ArrayList<ChannelWidget> onerow=prevChanWidget.get(row);
			for(;onerow.size()<numcol;)
				{
				int col=onerow.size();
				ChannelWidget lab=new ChannelWidget(mw);
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
				ChannelWidget lab=onerow.get(col);
				lab.setVisible(false);
				layViews.removeWidget(lab);
				onerow.remove(col);
				}
			}
		
		int indexA=0;
		for(FCSFile.DataSegment ds:selds)
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
						
				ChannelWidget lab=prevChanWidget.get(posRow).get(posCol);
				lab.setSettings(vs);

				//First selection: do FSC-A  vs  SSC-A
				lab.setDataset(ds);
				//layViews.addWidget(lab, posRow+1, posCol+1);
				}
			indexA++;
			}

		//Update headers
		for(int i=0;i<headerHorizontal.size();i++)
			headerHorizontal.get(i).setText(mw.datasets.get(i).source.getName());
		for(int i=0;i<headerVertical.size();i++)
			{
			Gate g=selviews.get(i).fromGate;
			headerVertical.get(i).setText(g.name);
			}
		
		//Get the size of one. rescale. then rerender all
		for(ArrayList<ChannelWidget> row:prevChanWidget)
			for(ChannelWidget w:row)
				w.render();
		}

	}
