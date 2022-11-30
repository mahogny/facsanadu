package facsanadu.gui.colors;

import io.qt.gui.QColor;
import io.qt.gui.QIcon;
import io.qt.gui.QPainter;
import io.qt.gui.QPixmap;
import io.qt.widgets.QPushButton;
import io.qt.widgets.QSizePolicy.Policy;
import io.qt.widgets.QComboBox;

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

	public final Signal0 selectionChanged=new Signal0();
	
	

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
  	currentIndexChanged(c);
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
