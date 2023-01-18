package facsanadu.gui.qt;


import java.io.File;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import io.qt.core.QByteArray;
import io.qt.core.QCoreApplication;
import io.qt.core.QDate;
import io.qt.core.QObject;
import io.qt.core.QRegularExpression;
import io.qt.core.QTime;
import io.qt.core.Qt.ItemFlag;
import io.qt.core.Qt.ItemFlags;
import io.qt.core.Qt.KeyboardModifier;
import io.qt.core.QDir;
import io.qt.core.QDir.Filter;
import io.qt.core.QDir.Filters;
import io.qt.core.QMetaObject;
import io.qt.widgets.QApplication;
import io.qt.widgets.QFileDialog;
import io.qt.widgets.QFileDialog.AcceptMode;
import io.qt.widgets.QFileDialog.DialogLabel;
import io.qt.widgets.QFileDialog.FileMode;
import io.qt.widgets.QFileDialog.Options;
import io.qt.widgets.QGroupBox;
import io.qt.widgets.QHBoxLayout;
import io.qt.gui.QImageReader;
import io.qt.widgets.QLabel;
import io.qt.widgets.QLayout;
import io.qt.widgets.QMessageBox;
import io.qt.widgets.QTableWidgetItem;
import io.qt.widgets.QMessageBox.StandardButton;
import io.qt.gui.QMouseEvent;
import io.qt.gui.QRegularExpressionValidator;
import io.qt.widgets.QTableView;
import io.qt.widgets.QVBoxLayout;
import io.qt.gui.QValidator;
import io.qt.widgets.QWidget;
import io.qt.widgets.QSizePolicy.Policy;

import facsanadu.gui.QtProgramInfo;

/**
 * QT utility functions
 * 
 * @author Johan Henriksson
 *
 */
public class QTutil
	{

	/**
	 * Return a widget with a label to the left of it
	 */
	public static QLayout withLabel(String s, QWidget w)
		{
		QHBoxLayout layout=new QHBoxLayout();
		QLabel l=new QLabel(s);
		l.setSizePolicy(Policy.Fixed, Policy.Fixed);
		layout.addWidget(l);
		layout.addWidget(w);
		layout.setSpacing(0);
		layout.setContentsMargins(0,0,0,0);

		return layout;
		}

	
	/**
	 * Return a widget with a label to the right of it
	 */
	public static QLayout withLabel(String s, QWidget w, String sRight)
		{
		QHBoxLayout layout=new QHBoxLayout();
		if(s!=null)
			{
			QLabel lLeft=new QLabel(s);
			lLeft.setSizePolicy(Policy.Minimum, Policy.Minimum);
			layout.addWidget(lLeft);
			}
		layout.addWidget(w);
		if(sRight!=null)
			{
			QLabel lRight=new QLabel(sRight);
			lRight.setSizePolicy(Policy.Minimum, Policy.Minimum);
			layout.addWidget(lRight);
			}
		return layout;
		}


	/**
	 * Place widget within a titled frame
	 */
	public static QWidget withinTitledFrame(String title, QWidget w)
		{
		QVBoxLayout layout=new QVBoxLayout();
		layout.addWidget(w);
		layout.setContentsMargins(0,0,0,0);
		return QTutil.withinTitledFrame(title,layout);
		}

	/**
	 * Place layout within a titled frame
	 */
	public static QWidget withinTitledFrame(String title, QLayout layout)
		{
		QGroupBox b=new QGroupBox(title);
		b.setLayout(layout);
		return b;
		}

	/**
	 * Convert QT date to Java date
	 */
	public static Date convertToJavaDate(QDate qd, QTime qt)
		{
		Calendar c=Calendar.getInstance();
		c.clear();
		c.set(qd.year(), qd.month()-1, qd.day());    // check day!

		if(qt!=null)
			{
			c.set(Calendar.HOUR, qt.hour());
			c.set(Calendar.MINUTE, qt.minute());
			c.set(Calendar.SECOND, qt.second());
			c.set(Calendar.MILLISECOND, 0);
			}
		return c.getTime();
		}
	
	public static QDate convertToQDate(Calendar calendar)
		{
		return new QDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
		}
	
	

/*
	public static QFileDialog.Filter buildFileDialogSupportedFormatsFilter(String fileType,	Collection<String> formatsList)
		{
		String formats="";
		for(String arr:formatsList)
			{
			if(!formats.equals(""))
				formats+=" ";
			formats+="*."+arr;
			}
// TODO: QFileDialog.Filter missing. FileMode?
		return new QFileDialog.Filter(fileType+" ("+formats+")");
		}*/


	/**
	 * Request an open-file dialog
	 * 
	 * @param parent  Qt widget parent
	 * @param title   What to ask the user
	 * @param filter  Filter for the files
	 * @return        The file, or null if none opened
	 */
	/*
	public static File openFileDialog(QWidget parent, String title,	String filter)
		{
    String fileName = QFileDialog.getOpenFileName(parent, title, lastQtDir, filter).result;
    if(!fileName.equals(""))
    	{
    	File f=new File(fileName);
    	lastQtDir=f.getParentFile().getAbsolutePath();
    	return f;
    	}
    else
    	return null;
		}*/

	/**
	 * Open multiple files dialog. Never returns null
	 */
	/*
	public static Collection<File> openFilesDialog(QWidget parent, String title,	String filter)
		{
    List<String> fileName = QFileDialog.getOpenFileNames(parent, title, lastQtDir, filter).result;
    if(!fileName.isEmpty())
    	{
    	List<File> fs=new LinkedList<File>();
    	for(String f:fileName)
    		{
    		File tf=new File(f);
    		fs.add(tf);
      	lastQtDir=tf.getParentFile().getAbsolutePath();
    		}
    	return fs;
    	}
    else
    	return new LinkedList<File>();
		}
		*/

	/**
	 * Request a save-file dialog
	 * 
	 * @param parent       Qt widget parent
	 * @param title        What to ask the user
	 * @param suggestName  Suggested name of file, or null
	 * @param filter       Filter for the files
	 * @return             The file, or null if none opened
	 */
	/*
	public static File saveFileDialog(QWidget parent, String title,	String suggestName, String defaultSuffix, QDir.Filter filter)
		{
		QFileDialog dia=new QFileDialog(parent, title, lastQtDir);
		dia.setFilter(filter);
		if(defaultSuffix!=null)
			dia.setDefaultSuffix(defaultSuffix);  
		if(suggestName!=null)
			dia.selectFile(suggestName);
		dia.setAcceptMode(AcceptMode.AcceptSave);
		if(dia.exec()==0)
			return null;
		if(dia.selectedFiles().isEmpty())
			return null;
		
		String fileName = dia.selectedFiles().iterator().next();
    if(!fileName.equals(""))
    	{
    	File f=new File(fileName);
    	lastQtDir=f.getParentFile().getAbsolutePath();
    	return f;
    	}
    else
    	return null;
		}

	public static File saveFileDialog(QWidget parent, String title,	Filter filter)
		{
		return saveFileDialog(parent, title, null, null, filter);
		}
		*/
		
	/**
	 * Last directory where a file was opened from
	 */
	private static String lastQtDir="";

	/**
	 * Open dialog for selecting an existing directory
	 * @param parent  QT widget parent
	 * @param title   Title to show
	 * @return        Directory if selected, otherwise null
	 */
	public static File openExistingDirectoryDialog(QWidget parent, String title, String acceptText)
		{
		QFileDialog dia=new QFileDialog(parent, title, lastQtDir);
		if(acceptText!=null)
			dia.setLabelText(DialogLabel.Accept, acceptText);
		if(dia.exec()!=0)
//    String fileName = QFileDialog.getExistingDirectory(parent, title, lastQtDir);
//    if(!fileName.equals(""))
    	{
//    	File f=new File(fileName);
    	File f=new File(dia.selectedFiles().iterator().next());
    	lastQtDir=f.getAbsolutePath();
    	return f;
    	}
    else
    	return null;
		}

/* no longer used
	public static Filter getAllFilesFilter()
		{
		return new QFileDialog.Filter(QCoreApplication.translate("labstory","Files")+" (*.*)");
		}*/


	public static List<String> getSupportedImageFormats()
		{
		LinkedList<String> formats=new LinkedList<String>();
		for(QByteArray arr:QImageReader.supportedImageFormats())
			formats.add(arr.toString());
		return formats;
		}


	/**
	 * Executes the QApplication::exec() method, which has different names in different versions of QTJambi, using reflection
	 * 
	 * @return The value from QApplication::exec()
	 */
	public static int execStaticQApplication()
		{
		//New name
		try
			{
			Method method=QApplication.class.getDeclaredMethod("execStatic");
			return (Integer)method.invoke(null);
			}
		catch (Exception e){}
		
		//Old name
		try
			{
			Method method=QCoreApplication.class.getDeclaredMethod("exec");
			return (Integer)method.invoke(null);
			}
		catch (Exception e){}
		
		throw new RuntimeException("Could not find any static method in QApplication to perform exec()"); 
		}


	public static String formatDateTime(long t)
		{
		DateFormat df=new SimpleDateFormat("yyyy-MM-dd kk:mm");
		return df.format(t);
		}


	public static QLayout layoutHorizontal(QWidget... widgets)
		{
		QHBoxLayout layout=new QHBoxLayout();
		for(QWidget w:widgets)
			layout.addWidget(w);
		return layout;
		}

	public static QLayout layoutVertical(Object... widgets)
		{
		QVBoxLayout layout=new QVBoxLayout();
		for(Object w:widgets)
			{
			if(w instanceof QWidget)
				layout.addWidget((QWidget)w);
			else if(w instanceof QLayout)
				layout.addLayout((QLayout)w);
			else
				throw new RuntimeException("Neither widget nor layout");
			}
		return layout;
		}
	

	public static void showNotice(final QWidget parent, final String text)
		{
		QMessageBox.information(parent, QtProgramInfo.programName, text);
		}

	
	public static void printError(final QWidget parent, final String text)
		{
		QMetaObject.invokeMethod(()->QMessageBox.critical(parent, QtProgramInfo.programName, text));
		}
	
	/*
	public static void printError(final QWidget parent, final String text, final Throwable e)
		{
		QApplication.invokeAndWait(new Runnable()
			{
			public void run()
				{
				String etext=e.getMessage();
				if(e instanceof PermissionDeniedException)
					etext=QCoreApplication.translate("qtutil","Permission denied");
				else if(e instanceof ServerErrorException)
					etext=QCoreApplication.translate("qtutil","Internal server error");
				else
					e.printStackTrace();
				QMessageBox.critical(parent, QtProgramInfo.programName, text+": "+etext);
				}
			});
		}*/


	public static boolean checkIsSigned(boolean isSigned, QWidget parent)
		{
		if(isSigned)
			{
			QMessageBox.critical(parent, QtProgramInfo.programName, QCoreApplication.translate(
					"qtutil","The object has been signed and cannot be modified"));
			return true;
			}
		else
			return false;
		}
	

	/**
	 * Fit number of rows
	 */
	public static void setProperHeightOfTable(QTableView listTables)
		{
		int nNumRows = listTables.model().rowCount();
		int nRowHeight = listTables.rowHeight(0);
		int nTableHeight = (nNumRows * nRowHeight) + listTables.horizontalHeader().height() + 2*listTables.frameWidth();
		if(nTableHeight>300)
			nTableHeight=300;		
		listTables.setMinimumHeight(nTableHeight);
		listTables.setMaximumHeight(nTableHeight);
		}


	public static QValidator getLabstoryIdvalidator(QObject parent)
		{
		//Do NOT use the constructor(regexp), windows qt bug!
		QRegularExpressionValidator validator=new QRegularExpressionValidator(parent);
		validator.setRegularExpression(new QRegularExpression("[abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890]*"));
		return validator;
		}
	
	
	public static boolean showOkCancel(String title)
		{
		QMessageBox msgBox=new QMessageBox();
		msgBox.setText(title);
		msgBox.setStandardButtons(StandardButton.Ok, StandardButton.Cancel);
		msgBox.setDefaultButton(StandardButton.Ok);
		int ret = msgBox.exec();
		return ret==StandardButton.Ok.value();
		}
	
	public static boolean showYesNo(String title)
		{
		QMessageBox msgBox=new QMessageBox();
		msgBox.setText(title);
		msgBox.setStandardButtons(StandardButton.Yes, StandardButton.No);
		msgBox.setDefaultButton(StandardButton.Yes);
		int ret = msgBox.exec();
		return ret==StandardButton.Yes.value();
		}


	public static boolean addingKey(QMouseEvent event)
		{
		return event.modifiers().isSet(KeyboardModifier.ShiftModifier) || event.modifiers().isSet(KeyboardModifier.ControlModifier);
		}

	/**
	 * Create a read-only list item
	 */
	public static QTableWidgetItem createReadOnlyItem(String s)
		{
		QTableWidgetItem it=new QTableWidgetItem(s);
		it.setFlags(new ItemFlags(ItemFlag.ItemIsSelectable, ItemFlag.ItemIsEnabled));
		return it;
		}

	}
