package glofacs.gui.channel;

import glofacs.gates.Gate;
import glofacs.gates.GateSet;
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
	
	
	public void render(ChannelWidget w, int width, int height)
		{
		//Clear image
		img=new QImage(new QSize(width, height), Format.Format_RGB32);
		img.fill(0xFFFFFF);
		QPainter pm=new QPainter(img);
	
		ArrayList<ChannelInfo> chans=segment.getChannelInfo();
		int numobs=segment.getNumObservations();
		
		pm.setPen(QColor.fromRgb(0,0,255));
		for(int i=0;i<numobs;i++)
			{
			double chanA=segment.eventsFloat.get(i)[viewsettings.indexX];
			double chanB=segment.eventsFloat.get(i)[viewsettings.indexY];
			int x=(int)(viewsettings.scaleX*chanA);
			int y=img.height()-((int)(viewsettings.scaleY*chanB))-1;
			pm.drawPoint(x, y);			
			}
		
		//Draw labels
		QFontMetrics fm=pm.fontMetrics();
		String labelX=chans.get(viewsettings.indexX).formatName();
		String labelY=chans.get(viewsettings.indexY).formatName();
		
		pm.setPen(QColor.fromRgb(0,0,0));
		pm.drawText((width-fm.boundingRect(labelX).width())/2, img.height()-30, labelX);
		pm.save();
		pm.translate(30, (height+fm.boundingRect(labelY).width())/2);
		pm.rotate(-90);
		pm.drawText(0, 0, labelY);
		pm.restore();
		
		//Draw all gates
		pm.setPen(QColor.fromRgb(255,0,0));
		for(Gate g:gs.mapIdGate.values())
			{
			GateRenderer rend=GateHandler.getGateRenderer(g);
			rend.render(g, pm, w, viewsettings);
			}
		
		
		pm.end();
		}
	
	
	public void renderGate()
		{
		
//		viewsettings.fromGateID
		// mw
		}
	
	

	}