package quickfacs.io;

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

import quickfacs.data.Dataset;
import quickfacs.gates.Gate;
import quickfacs.gates.GateRect;
import quickfacs.gui.QuickfacsProject;
import quickfacs.gui.view.ViewSettings;


/**
 * 
 * I/O for native format
 * 
 * @author Johan Henriksson
 *
 */
public class QuickfacsXML
	{
	
	/**
	 * Export a project
	 */
	public static void exportToFile(QuickfacsProject proj,File f) throws IOException
		{
		Element e=exportXML(proj, f.getParentFile());
		XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
    xmlOutputter.output(e, new FileOutputStream(f));
		}
	
	
	
	private static Element exportXML(QuickfacsProject proj, File root) throws IOException
		{
		Element etot=new Element("quickfacs");
		
		
		
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
		
		//Store views
		for(ViewSettings vs:proj.views)
			{
			Element eview=new Element("view");
			eview.setAttribute("indexX",""+vs.indexX);
			eview.setAttribute("indexY",""+vs.indexY);
			eview.setAttribute("gate",""+vs.fromGate.name);
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

			if(type==null)
				throw new IOException("gate cannot be stored "+g);
			ge.setAttribute("type",type);
			
			storeGate(g, ge);
			}
		}

	
	/**
	 * Load gates recursively
	 */
	private static void loadGate(Gate parent, Element e) throws IOException
		{
		try
			{
			for(Element one:e.getChildren())
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
					
					if(g==null)
						throw new IOException("Unknown gate type "+type);
					g.name=one.getAttributeValue("name");
					parent.attachChild(g);
					
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
	public static void importXML(QuickfacsProject proj, Element etot, File root) throws IOException
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
					vs.fromGate=proj.gateset.getGate(one.getAttributeValue("gate"));
					vs.indexX=one.getAttribute("indexX").getIntValue();
					vs.indexY=one.getAttribute("indexY").getIntValue();
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
	public static QuickfacsProject importXML(File f) throws IOException
		{
		QuickfacsProject proj=new QuickfacsProject();
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
