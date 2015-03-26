package quickfacs.gui.view;

import java.util.ArrayList;

import quickfacs.data.ChannelInfo;
import quickfacs.data.Dataset;
import quickfacs.gates.Gate;
import quickfacs.gates.GatingResult;
import quickfacs.gates.IntArray;
import quickfacs.gui.QuickfacsProject;
import quickfacs.gui.view.gate.GateHandler;
import quickfacs.gui.view.gate.GateRenderer;

import com.trolltech.qt.core.QSize;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QFontMetrics;
import com.trolltech.qt.gui.QImage;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QImage.Format;

/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class ViewRenderer
	{
	public QImage img;
	public ViewSettings viewsettings=new ViewSettings();
	private Dataset segment;
	QuickfacsProject proj;
	
	public void setDataset(Dataset segment, QuickfacsProject proj)
		{
		this.segment=segment;
		this.proj=proj;
		}
	
	/*
	public void autoscale()
		{
		viewsettings.autoscale(segment);
		}*/
	
	
	public void render(GatingResult gr, ViewTransform trans)
		{
		//Clear image
		img=new QImage(new QSize(trans.width, trans.height), Format.Format_RGB32);
		img.fill(0xFFFFFF);
		QPainter pm=new QPainter(img);
	
		ArrayList<ChannelInfo> chans=segment.getChannelInfo();

		QPen pen=new QPen(QColor.fromRgb(0,0,255));
		pen.setWidth(2);
		pm.setPen(pen);
		
		IntArray accepted=gr.acceptedFromGate.get(viewsettings.fromGate);
		if(accepted!=null)
			for(int i=0;i<accepted.size();i++)
				{
				int ind=accepted.get(i);
				double chanX=segment.eventsFloat.get(ind)[viewsettings.indexX];
				double chanY=segment.eventsFloat.get(ind)[viewsettings.indexY];
				
				int x=trans.mapFacsToScreenX(chanX);
				int y=trans.mapFacsToScreenY(chanY);
				pm.drawPoint(x, y);			
				}
		else
			System.out.println("gating not done yet");
		
		int labelOffset=15;
		
		//Draw labels
		QFontMetrics fm=pm.fontMetrics();
		String labelX=chans.get(viewsettings.indexX).formatName();
		String labelY=chans.get(viewsettings.indexY).formatName();
		
		pm.setPen(QColor.fromRgb(0,0,0));
		pm.drawText((trans.width-fm.boundingRect(labelX).width())/2, img.height()-labelOffset, labelX);
		pm.save();
		pm.translate(labelOffset, (trans.height+fm.boundingRect(labelY).width())/2);
		pm.rotate(-90);
		pm.drawText(0, 0, labelY);
		pm.restore();
		
		//Draw separating lines
		pm.setPen(QColor.fromRgb(0,0,0));
		int off2=5;
		pm.drawLine(
				trans.graphOffsetXY,off2, 
				trans.graphOffsetXY, trans.height-trans.graphOffsetXY);
		pm.drawLine(
				trans.graphOffsetXY, trans.height-trans.graphOffsetXY, 
				trans.width-off2, trans.height-trans.graphOffsetXY);
		
		
		//Draw all gates
		drawgatesRecursive(pm, trans, viewsettings.fromGate);
		
		pm.end();
		}
	

	private void drawgatesRecursive(QPainter pm, ViewTransform trans, Gate parent)
		{
		for(Gate g:parent.children)
			{
			pm.setPen(QColor.fromRgb(255,0,0));
			GateRenderer rend=GateHandler.getGateRenderer(g);
			rend.render(g, pm, trans, viewsettings);
			drawgatesRecursive(pm, trans, g);
			}
		
		}
	
	

	}