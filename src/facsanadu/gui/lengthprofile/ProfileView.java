package facsanadu.gui.lengthprofile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.trolltech.qt.core.QRect;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QPaintEvent;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPainterPath;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QWidget;

import facsanadu.data.Dataset;
import facsanadu.data.LengthProfileData;

/**
 * 
 * View showing one length profile
 * 
 */
public class ProfileView extends QWidget
	{
	private Dataset ds;
	private ArrayList<Integer> eventid=new ArrayList<Integer>();
	
	public boolean normalizeLength;
	public ArrayList<Boolean> showchan=new ArrayList<Boolean>();
	public ArrayList<Double> scale=new ArrayList<Double>();
	
	@Override
	protected void paintEvent(QPaintEvent e)
		{
		super.paintEvent(e);
		QPainter pm=new QPainter(this);
		pm.fillRect(new QRect(0, 0,width(),height()), new QBrush(QColor.fromRgb(255,255,255)));
		
		ArrayList<QColor> colors=new ArrayList<QColor>();
		colors.add(QColor.fromRgb(0,0,0));
		colors.add(QColor.fromRgb(255,0,0));
		colors.add(QColor.fromRgb(0,255,0));
		colors.add(QColor.fromRgb(0,0,255));
		
		int h=height();
		for(int eventid:this.eventid)
			if(ds!=null && eventid>=0 && eventid<ds.getNumObservations())
				{
				for(int profid=0;profid<ds.lengthprofsInfo.size();profid++)
					if(showchan.get(profid))
						{
						//LengthProfile prof=ds.lengthprofsInfo.get(profid);
						LengthProfileData data=ds.lengthprofsData.get(eventid);
						double scaleY=scale.get(profid)*h;
						
						QPen pen=new QPen(colors.get(profid));
						//pen.setWidth(2);
						pm.setPen(pen);
						
						int len=data.getLength();
						QPainterPath path=new QPainterPath();
						for(int i=0;i<len;i++)
							{
							double x;
							if(normalizeLength)
								x=i*width()/data.getLength();
							else
								x=i*width()/1050; //what is the maximum length in the set? could be stored in the info
							
							double y=h-1-data.data[profid][i]*scaleY;
							if(i==0)
								path.moveTo(x,y);
							else
								path.lineTo(x,y);
							}
		
						pm.drawPath(path);
						}
				}
		
		pm.end();
		}

	public void setevent(Dataset ds, int i)
		{
		setevent(ds, Arrays.asList(i));
		}

	public void setevent(Dataset ds, List<Integer> ids)
		{
		this.ds=ds;
		eventid.clear();
		eventid.addAll(ids);
		repaint();
		}

	}
