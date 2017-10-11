package facsanadu.gui.colors;

import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPixmap;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QSizePolicy.Policy;

import facsanadu.gates.GateColor;

/**
 * 
 * Combo box: List of colors
 * 
 * @author Johan Henriksson
 *
 */
public class QColorCombo extends QPushButton
	{
	private ColorSet colorset=ColorSet.colorset;
	GateColor currentColor=colorset.get(0);
	
	int size=12;
	
	public QSignalEmitter.Signal0 currentIndexChanged=new QSignalEmitter.Signal0();
	
	

	public QColorCombo()
		{
		setSizePolicy(Policy.Minimum, Policy.Minimum);
		updateColorIcon();
		//fillColorCombo();
		
		clicked.connect(this,"actionClick()");
		}
	
	private void updateColorIcon()
		{
		setIcon(new QIcon(makeColPM(currentColor, size)));
		}
	

  public static QPixmap makeColPM(GateColor col, int size)
	  {
	  QPixmap pm=new QPixmap(size, size);
	  pm.fill(new QColor(0,0,0,0));   
	  QPainter p=new QPainter(pm);
	  p.setBrush(new QColor(col.r,col.g,col.b));
	  p.drawEllipse(1,1,size-2,size-2);
	  p.end();
	  return pm;
	  }
  
  
  public void setCurrentColor(GateColor c)
  	{
  	currentColor=c;
  	updateColorIcon();
  	currentIndexChanged.emit();
  	}
  
  public GateColor getCurrentColor()
  	{
  	return currentColor;
  	}


	public void actionClick()
		{
		new QColorComboPopup(this, colorset);
		}
	}
