package glofacs.gui.channel;

import glofacs.gates.Gate;
import glofacs.gates.GateSet;
import glofacs.gates.GatingResult;
import glofacs.gates.IntArray;
import glofacs.gui.ChannelInfo;
import glofacs.gui.gateRenderer.GateHandler;
import glofacs.gui.gateRenderer.GateRenderer;
import glofacs.io.FCSFile;

import java.util.ArrayList;

import com.trolltech.qt.core.QSize;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QFontMetrics;
import com.trolltech.qt.gui.QImage;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QImage.Format;

/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class ChannelRenderer
	{
	public QImage img;
	public ViewSettings viewsettings=new ViewSettings();
	private FCSFile.DataSegment segment;
	GateSet gs;
	
	public void setSegment(FCSFile.DataSegment segment, GateSet gs)
		{
		this.segment=segment;
		this.gs=gs;
		}
	
	public void autoscale()
		{
		viewsettings.autoscale(segment, img.width(), img.height());
		}
	
	
	public void render(GatingResult gr, ChannelWidget w, int width, int height)
		{
		//Clear image
		img=new QImage(new QSize(width, height), Format.Format_RGB32);
		img.fill(0xFFFFFF);
		QPainter pm=new QPainter(img);
	
		ArrayList<ChannelInfo> chans=segment.getChannelInfo();
	//	int numobs=segment.getNumObservations();
		
		pm.setPen(QColor.fromRgb(0,0,255));
		
		IntArray accepted=gr.acceptedFromGate.get(viewsettings.fromGate);
		if(accepted!=null)
			for(int i=0;i<accepted.size();i++)
				{
				int ind=accepted.get(i);
				double chanX=segment.eventsFloat.get(ind)[viewsettings.indexX];
				double chanY=segment.eventsFloat.get(ind)[viewsettings.indexY];
				
				int x=w.mapFacsToScreenX(chanX);
				int y=w.mapFacsToScreenY(chanY);
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
		pm.drawText((width-fm.boundingRect(labelX).width())/2, img.height()-labelOffset, labelX);
		pm.save();
		pm.translate(labelOffset, (height+fm.boundingRect(labelY).width())/2);
		pm.rotate(-90);
		pm.drawText(0, 0, labelY);
		pm.restore();
		
		//Draw all gates
		drawgatesRecursive(pm, w, viewsettings.fromGate);
		
		pm.end();
		}
	

	private void drawgatesRecursive(QPainter pm, ChannelWidget w, Gate parent)
		{
		for(Gate g:parent.children)
			{
			pm.setPen(QColor.fromRgb(255,0,0));
			GateRenderer rend=GateHandler.getGateRenderer(g);
			rend.render(g, pm, w, viewsettings);
			drawgatesRecursive(pm, w, g);
			}
		
		}
	
	

	}