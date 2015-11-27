package facsanadu.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import facsanadu.data.Dataset;
import facsanadu.gates.Gate;
import facsanadu.gates.GatePolygon;
import facsanadu.gates.GateRect;
import facsanadu.gui.FacsanaduProject;
import facsanadu.gui.view.ViewSettings;


/**
 * 
 * I/O for native format
 * 
 * @author Johan Henriksson
 *
 */
public class FacsanaduXML
	{
	
	/**
	 * Export a project
	 */
	public static void exportToFile(FacsanaduProject proj,File f) throws IOException
		{
		Element e=exportXML(proj, f.getParentFile());
		XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
    xmlOutputter.output(e, new FileOutputStream(f));
		}
	
	
	
	private static Element exportXML(FacsanaduProject proj, File root) throws IOException
		{
		Element etot=new Element("facsanadu");
		
		//Store dataset references
		for(Dataset ds:proj.datasets)
			{
			String relpath=getRelativePath(root.getAbsolutePath(), ds.source.getAbsolutePath());
			
			Element eSeq=new Element("dataset");
			eSeq.setAttribute("path",relpath);
			etot.addContent(eSeq);
			}
		
		//Store gates
		Element egate=new Element("gateset");
		storeGate(proj.gateset.getRootGate(), egate);
		etot.addContent(egate);
		
		//Store views
		for(ViewSettings vs:proj.views)
			{
			Element eview=new Element("view");
			eview.setAttribute("indexX",""+vs.indexX);
			eview.setAttribute("indexY",""+vs.indexY);
			eview.setAttribute("gate",""+vs.gate.name);

			eview.setAttribute("scaleX",""+vs.scaleX);
			eview.setAttribute("scaleY",""+vs.scaleY);
			
			etot.addContent(eview);
			}
		
		return etot;
		}
	

	/**
	 * Store gates recursively
	 */
	private static void storeGate(Gate parent, Element e) throws IOException
		{
		for(Gate g:parent.children)
			{
			Element ge=new Element("gate");
			ge.setAttribute("name",g.name);
			ge.setAttribute("colr",""+g.color.r);
			ge.setAttribute("colg",""+g.color.g);
			ge.setAttribute("colb",""+g.color.b);
			String type=null;
			if(g instanceof GateRect)
				{
				GateRect gr=(GateRect)g;
				type="rect";
				
				ge.setAttribute("ix",""+gr.indexX);
				ge.setAttribute("iy",""+gr.indexY);

				ge.setAttribute("x1",""+gr.x1);
				ge.setAttribute("x2",""+gr.x2);
				ge.setAttribute("y1",""+gr.y1);
				ge.setAttribute("y2",""+gr.y2);
				}
			else if(g instanceof GatePolygon)
				{
				GatePolygon gr=(GatePolygon)g;
				type="poly";
				
				ge.setAttribute("ix",""+gr.indexX);
				ge.setAttribute("iy",""+gr.indexY);

				for(int i=0;i<gr.getNumPoints();i++)
					{
					Element epoint=new Element("point");
					epoint.setAttribute("x",""+gr.arrX.get(i));
					epoint.setAttribute("y",""+gr.arrY.get(i));
					ge.addContent(epoint);
					}
				}

			if(type==null)
				throw new IOException("gate cannot be stored "+g);
			ge.setAttribute("type",type);
			e.addContent(ge);
			
			
			storeGate(g, ge);
			}
		}

	
	/**
	 * Load gates recursively
	 */
	private static void loadGate(Gate parent, Element eParent) throws IOException
		{
		try
			{
			for(Element one:eParent.getChildren())
				{
				if(one.getName().equals("gate"))
					{
					Gate g=null;
					String type=one.getAttributeValue("type");
					if(type.equals("rect"))
						{
						GateRect gr=new GateRect();
						g=gr;
						gr.indexX=one.getAttribute("ix").getIntValue();
						gr.indexY=one.getAttribute("iy").getIntValue();
						gr.x1=one.getAttribute("x1").getDoubleValue();
						gr.x2=one.getAttribute("x2").getDoubleValue();
						gr.y1=one.getAttribute("y1").getDoubleValue();
						gr.y2=one.getAttribute("y2").getDoubleValue();
						}
					else if(type.equals("poly"))
						{
						GatePolygon gr=new GatePolygon();
						g=gr;
						gr.indexX=one.getAttribute("ix").getIntValue();
						gr.indexY=one.getAttribute("iy").getIntValue();
						for(Element epoint:one.getChildren())
							if(epoint.getName().equals("point"))
								gr.addPoint(
										epoint.getAttribute("x").getDoubleValue(),
										epoint.getAttribute("y").getDoubleValue());
						}
					if(g==null)
						throw new IOException("Unknown gate type "+type);
					g.name=one.getAttributeValue("name");
					g.color.r=one.getAttribute("colr").getIntValue();
					g.color.g=one.getAttribute("colg").getIntValue();
					g.color.b=one.getAttribute("colb").getIntValue();
					parent.attachChild(g);
					g.updateInternal();
					
					loadGate(g, one);
					}
				}
			}
		catch (DataConversionException e1)
			{
			e1.printStackTrace();
			throw new IOException("read error");
			}
		}
	
	
	
	/**
	 * Import from native format
	 */
	public static void importXML(FacsanaduProject proj, Element etot, File root) throws IOException
		{
		try
			{
			for(Element one:etot.getChildren())
				{
				if(one.getName().equals("gateset"))
					{
					loadGate(proj.gateset.getRootGate(), one);
					}
				else if(one.getName().equals("view"))
					{
					ViewSettings vs=new ViewSettings();
					vs.gate=proj.gateset.getGate(one.getAttributeValue("gate"));
					vs.indexX=one.getAttribute("indexX").getIntValue();
					vs.indexY=one.getAttribute("indexY").getIntValue();
					if(one.getAttribute("scaleX")!=null)
						{
						vs.scaleX=one.getAttribute("scaleX").getDoubleValue();
						vs.scaleY=one.getAttribute("scaleY").getDoubleValue();
						}
					
					proj.views.add(vs);
					}
				else if(one.getName().equals("dataset"))
					{
					String n=one.getAttributeValue("path");
					File f;
					if(n.startsWith("./") || n.startsWith("../"))
						f=new File(root.getAbsolutePath(), n);
					else
						f=new File(n);
					proj.addDataset(f);
					}
				}
			}
		catch (DataConversionException e)
			{
			e.printStackTrace();
			throw new IOException("read error");
			}
		}
	

	/**
	 * Import from XML
	 */
	public static FacsanaduProject importXML(File f) throws IOException
		{
		FacsanaduProject proj=new FacsanaduProject();
		try
			{
			FileInputStream is=new FileInputStream(f);
			SAXBuilder sax = new SAXBuilder();
			Document doc = sax.build(is);
			importXML(proj, doc.getRootElement(), f.getParentFile());
			is.close();
			}
		catch (Exception e)
			{
			e.printStackTrace();
			throw new IOException(e.getMessage());
			}
		return proj;
		}


	/**
	 * Construct relative path. 
	 * From http://stackoverflow.com/questions/204784/how-to-construct-a-relative-path-in-java-from-two-absolute-paths-or-urls
	 */
	public static String getRelativePath (String baseDir, String targetPath) 
		{
		String[] base = baseDir.replace('\\', '/').split("\\/");
		targetPath = targetPath.replace('\\', '/');
		String[] target = targetPath.split("\\/");

		// Count common elements and their length.
		int commonCount = 0, commonLength = 0, maxCount = Math.min(target.length, base.length);
		while (commonCount < maxCount) 
			{
			String targetElement = target[commonCount];
			if (!targetElement.equals(base[commonCount])) 
				break;
			commonCount++;
			commonLength += targetElement.length() + 1; // Directory name length plus slash.
			}
		if (commonCount == 0)
			return targetPath; // No common path element.

		int targetLength = targetPath.length();
		int dirsUp = base.length - commonCount;
		StringBuilder relative = new StringBuilder(dirsUp * 3 + targetLength - commonLength + 1);
		for (int i = 0; i < dirsUp; i++)
			relative.append("../");
		if (commonLength < targetLength) 
			relative.append(targetPath.substring(commonLength));
		return "./"+relative.toString();
		}
	
	}
