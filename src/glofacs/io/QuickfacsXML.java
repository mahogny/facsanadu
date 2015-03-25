package glofacs.io;

import glofacs.gates.Gate;
import glofacs.gates.GateRect;
import glofacs.gui.QuickfacsProject;
import glofacs.gui.channel.ViewSettings;

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
	public static void export(QuickfacsProject proj,File f) throws IOException
		{
		Element e=export(proj);
		XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
    xmlOutputter.output(e, new FileOutputStream(f));
		}
	
	
	
	public static Element export(QuickfacsProject proj) throws IOException
		{
		Element etot=new Element("glofacs");
		
		
		
		//Store dataset references
		for(Dataset ds:proj.datasets)
			{
			Element eSeq=new Element("dataset");
			eSeq.setAttribute("path",ds.source.getAbsolutePath());
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
	public static void importXML(QuickfacsProject mw, Element etot, File basepath) throws IOException
		{
		try
			{
			for(Element one:etot.getChildren())
				{
				if(one.getName().equals("gateset"))
					{
					loadGate(mw.gateset.getRootGate(), one);
					}
				else if(one.getName().equals("view"))
					{
					ViewSettings vs=new ViewSettings();
					vs.fromGate=mw.gateset.getGate(one.getAttributeValue("gate"));
					vs.indexX=one.getAttribute("indexX").getIntValue();
					vs.indexY=one.getAttribute("indexY").getIntValue();
					mw.views.add(vs);
					}
				else if(one.getName().equals("dataset"))
					{
					String n=one.getAttributeValue("path");
					Dataset ds=new FCSFile(new File(n)).data.get(0);
					mw.datasets.add(ds);
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
	
	}
