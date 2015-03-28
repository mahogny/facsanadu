package facsanadu.gui.qt;

import java.util.LinkedList;
import java.util.TreeSet;

import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QTableWidget;
import com.trolltech.qt.gui.QKeySequence.StandardKey;

/**
 * 
 * 
 * Much of code inspired from stack overflow
 * 
 * @author Johan Henriksson
 *
 */
public class QTableWidgetWithCSVcopy extends QTableWidget
	{
	protected void keyPressEvent(com.trolltech.qt.gui.QKeyEvent event) 
		{
		if(event.matches(StandardKey.Copy) )
			copy();
		else
			super.keyPressEvent(event);
		}

	public void copyAll()
		{
		TreeSet<Integer> whichcol=new TreeSet<Integer>();
		TreeSet<Integer> whichrow=new TreeSet<Integer>();
		for(int i=0;i<columnCount();i++)
			whichcol.add(i);
		for(int i=0;i<rowCount();i++)
			whichrow.add(i);
		copy(whichcol, whichrow);
		}
	
	public void copy(TreeSet<Integer> whichcol, TreeSet<Integer> whichrow)
		{
		StringBuilder sb=new StringBuilder();
	
		boolean fst=true;
		for(int i:whichcol)
			{
			if(!fst)
				sb.append("\t");
			fst=false;
			sb.append(horizontalHeaderItem(i).text());
			}
		sb.append("\n");

		for(int currow:whichrow)
			{
			fst=true;
			for(int curcol:whichcol)
				{
				if(!fst)
					sb.append("\t");
				fst=false;
				sb.append(item(currow,curcol).text());
				}
			sb.append("\n");
			}
		sb.append("\n");
		QApplication.clipboard().setText(sb.toString());
		}
	
	public void copy()
		{
		LinkedList<QModelIndex> indexes=new LinkedList<QModelIndex>(selectionModel().selectedIndexes());
		if(indexes.size()>0)
			{
			TreeSet<Integer> whichcol=new TreeSet<Integer>();
			TreeSet<Integer> whichrow=new TreeSet<Integer>();
			for(QModelIndex in:indexes)
				{
				whichcol.add(in.column());
				whichrow.add(in.row());
				}
			copy(whichcol, whichrow);
			}
		}


	}
