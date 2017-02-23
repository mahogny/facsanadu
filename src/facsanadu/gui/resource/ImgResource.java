package facsanadu.gui.resource;

import java.io.IOException;
import java.io.InputStream;

import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QPixmap;
import com.trolltech.qt.gui.QWidget;

/**
 * Common icons. By loading them once, memory is conserved
 * 
 * @author Johan Henriksson
 *
 */
public class ImgResource
	{
	/**
	 * Read a stream into a byte array
	 */
	public static byte[] readStreamIntoArray(InputStream is) throws IOException
		{
		if(is==null)
			throw new IOException("Inputstream is null");
		byte[] arr=LabnoteUtil.readStreamToArray(is);
		is.close();
		return arr;
		}
	
	/**
	 * Get an icon as a resource - this will work even if the icons are embedded into the jar file
	 */
	private static QPixmap getIcon(String name)
		{
		try 
			{
			QPixmap pm=new QPixmap();
			pm.loadFromData(readStreamIntoArray(ImgResource.class.getResourceAsStream(name)));
			return pm;
			} 
		catch (IOException e) 
			{
			System.err.println("Unable to read "+name+" "+e.getMessage());
			return null;
			}
		}
	

	public static QPixmap imgWindowIcon= getIcon("programIcon.png");

	public static QPixmap moveRight=getIcon("tango-go-next.png");
	public static QPixmap moveLeft=getIcon("tango-go-previous.png");

	public static QPixmap moveUp=getIcon("tango-go-up.png");
	public static QPixmap moveDown=getIcon("tango-go-down.png");

	public static QPixmap delete=getIcon("tango-trash.png");

	public static QPixmap gateEllipse=getIcon("fugue-shape-ellipse.png");
	public static QPixmap gateRect=getIcon("fugue-shape-rect.png");
	public static QPixmap gatePolygon=getIcon("fugue-shape-polygon.png");
	public static QPixmap gateRange=getIcon("shape-interval.png");
	public static QPixmap gateSelect=getIcon("drawSelect.png");
	
	
	public static void setWindowIcon(QWidget w)
		{
		w.setWindowIcon(new QIcon(imgWindowIcon));
		}

	public static QLabel label(QPixmap p)
		{
		QLabel lab=new QLabel();
		lab.setPixmap(p);
		return lab;
		}
	
	
	}
