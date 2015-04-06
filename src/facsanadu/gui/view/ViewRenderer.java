package facsanadu.gui.view;

import java.util.ArrayList;

import com.trolltech.qt.core.QRect;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QFontMetrics;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPen;

import facsanadu.data.ChannelInfo;
import facsanadu.data.Dataset;
import facsanadu.gates.Gate;
import facsanadu.gates.GatingResult;
import facsanadu.gates.IntArray;
import facsanadu.gui.view.gate.GateHandler;
import facsanadu.gui.view.gate.GateRenderer;

/**
 * 
 * Renderer of one view
 * 
 * @author Johan Henriksson
 *
 */
public class ViewRenderer
	{
	private static int labelOffset=15;
	
	/**
	 * Render view to device
	 */
	public static void render(ViewSettings viewsettings, Dataset segment, GatingResult gr, ViewTransform trans, QPainter pm)
		{
		if(viewsettings.isHistogram())
			renderHistogram(viewsettings, segment, gr, trans, pm);
		else
			renderXY(viewsettings, segment, gr, trans, pm);
		}
	
	
	/**
	 * Render histogram
	 */
	private static void renderHistogram(ViewSettings viewsettings, Dataset segment, GatingResult gr, ViewTransform trans, QPainter pm)
		{
		ArrayList<ChannelInfo> chans=segment.getChannelInfo();
		//Headache - for scaling, here it would make more sense to scale by the output histograms rather than just datasize
		
		Histogram h=viewsettings.computeHistogram(segment, gr); //better if this was only once!
		
		pm.setPen(new QPen(QColor.fromRgb(0,0,0)));
		pm.setBrush(new QBrush(QColor.gray));
		
		double binw=1.0/(h.getNumBins()+1);
		for(int i=0;i<h.getNumBins();i++)
			{
			double frac=h.getFrac(i);
			int x1=trans.mapGeneralToScreenX(i*binw);
			int x2=trans.mapGeneralToScreenX((i+1)*binw);
			int y1=trans.mapGeneralToScreenY(0); 
			int y2=trans.mapGeneralToScreenY(frac*8); //here is the problem
			pm.drawRect(new QRect(x1,y1,x2-x1,y2-y1));
			}

		//Draw boundary
		String labelX=chans.get(viewsettings.indexX).formatName();
		String labelY="Fraction";
		drawLines(pm, trans, labelX, labelY);
		}

	/**
	 * Draw scatter plot
	 */
	private static void renderXY(ViewSettings viewsettings, Dataset segment, GatingResult gr, ViewTransform trans, QPainter pm)
		{
		ArrayList<ChannelInfo> chans=segment.getChannelInfo();

		QPen pen=new QPen(QColor.fromRgb(0,0,255));
		pen.setWidth(2);
		pm.setPen(pen);
		
		IntArray accepted=gr.acceptedFromGate.get(viewsettings.gate);
		if(accepted!=null)
			for(int i=0;i<accepted.size();i++)
				{
				int ind=accepted.get(i);
				double chanX=segment.getAsFloat(ind,viewsettings.indexX);
				double chanY=segment.getAsFloat(ind,viewsettings.indexY);
				
				int x=trans.mapFcsToScreenX(chanX);
				int y=trans.mapFcsToScreenY(chanY);
				pm.drawPoint(x, y);			
				}
		else
			System.out.println("gating not done yet");
		
		
		//Draw boundary
		String labelX=chans.get(viewsettings.indexX).formatName();
		String labelY=chans.get(viewsettings.indexY).formatName();
		drawLines(pm, trans, labelX, labelY);
		
		//Draw all gates
		drawgatesRecursive(pm, trans, viewsettings.gate, viewsettings);
		}

	
	
	/**
	 * Draw things surrounding graph
	 */
	private static void drawLines(QPainter pm, ViewTransform trans, String labelX, String labelY)
		{
		//Draw labels
		QFontMetrics fm=pm.fontMetrics();
		pm.setPen(QColor.fromRgb(0,0,0));
		pm.drawText(
				(trans.getTotalWidth()-fm.boundingRect(labelX).width())/2, 
				trans.getTotalHeight()-labelOffset, labelX);
		pm.save();
		pm.translate(labelOffset, (trans.getTotalHeight()+fm.boundingRect(labelY).width())/2);
		pm.rotate(-90);
		pm.drawText(0, 0, labelY);
		pm.restore();

		//Draw lines
		pm.setPen(QColor.fromRgb(0,0,0));
		int off2=5;
		pm.drawLine(
				trans.graphOffsetXY,off2, 
				trans.graphOffsetXY, trans.getTotalHeight()-trans.graphOffsetXY);
		pm.drawLine(
				trans.graphOffsetXY, trans.getTotalHeight()-trans.graphOffsetXY, 
				trans.getTotalWidth()-off2, trans.getTotalHeight()-trans.graphOffsetXY);
		}

	/**
	 * Draw all gates recursively
	 */
	private static void drawgatesRecursive(QPainter pm, ViewTransform trans, Gate parent, ViewSettings viewsettings)
		{
		for(Gate g:parent.children)
			{
			pm.setPen(QColor.fromRgb(255,0,0));
			GateRenderer rend=GateHandler.getGateRenderer(g);
			rend.render(g, pm, trans, viewsettings);
			drawgatesRecursive(pm, trans, g, viewsettings);
			}
		
		}
	
	

	}