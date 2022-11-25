package facsanadu.gui.colors;

import io.qt.core.Qt.WindowType;
import io.qt.widgets.QGridLayout;
import io.qt.gui.QIcon;
import io.qt.widgets.QPushButton;
import io.qt.widgets.QWidget;

import facsanadu.gates.GateColor;

/**
 * 
 * Color-swatch popup
 * 
 * @author Johan Henriksson
 *
 */
public class QColorComboPopup extends QWidget
	{
	private QColorCombo popupParent;

	public QColorComboPopup(QColorCombo parent, ColorSet colorset)
		{
		popupParent = parent;
		
    move( popupParent.mapToGlobal( popupParent.rect().bottomLeft() ) );
    setWindowFlags(WindowType.Popup, WindowType.FramelessWindowHint);
    
    QGridLayout lay=new QGridLayout();
    int curcol=0;
    int currow=0;
    for(int i=0;i<colorset.size();i++)
    	{
    	if(curcol==7)
    		{
    		currow++;
    		curcol=0;
    		}
    	
    	final GateColor col=colorset.get(i);
    	QPushButton b=new QPushButton(new QIcon(QColorCombo.makeColPM(col, 20)),""){
	    	@SuppressWarnings("unused")
				public void actionClick(){
	    		popupParent.setCurrentColor(col);
	    		QColorComboPopup.this.close();
	    	}
    	};
    	b.setStyleSheet("border: none;");
    	b.clicked.connect(b,"actionClick()");
    	
      lay.addWidget(b,currow, curcol);
    	curcol++;
    	}
    setLayout(lay);

    adjustSize();
    show();
		}
	
	}
