package facsanadu.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import facsanadu.data.Compensation;
import facsanadu.data.Dataset;
import facsanadu.data.LengthProfileData;
import facsanadu.data.ProfChannel;
import facsanadu.gates.Gate;
import facsanadu.gates.GateEllipse;
import facsanadu.gates.GatePolygon;
import facsanadu.gates.GateRange;
import facsanadu.gates.GateRect;
import facsanadu.gui.FacsanaduProject;
import facsanadu.gui.view.ViewSettings;
import facsanadu.transformations.Transformation;
import facsanadu.transformations.TransformationStack;
import facsanadu.transformations.TransformationType;


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

			//Attach information about profiles being flipped, if profile data is included
			if(ds.getNumObservations()>0 && ds.lengthprofsData.size()>0 && ds.lengthprofsData.get(0).getLength()>0)
				{
				StringBuilder sb=new StringBuilder();
				for(LengthProfileData d:ds.lengthprofsData)
					sb.append(d.isFlipped ? "F" : "0");
				eSeq.setAttribute("pflip",sb.toString());
				}
			
			etot.addContent(eSeq);
			}
		
		
		//Store gates
		Element egate=new Element("gateset");
		storeGate(proj.gateset.getRootGate(), egate);
		etot.addContent(egate);

		//Store prof channels
		for(ProfChannel pc:proj.profchan)
			{
			Element epc=new Element("profchan");
			epc.setAttribute("chan",""+pc.channel);
			epc.setAttribute("from",""+pc.from);
			epc.setAttribute("to",""+pc.to);
			epc.setAttribute("normalize",""+pc.forNormalized);
			etot.addContent(epc);
			}
		
		//Store views
		for(ViewSettings vs:proj.views)
			{
			Element eview=new Element("view");
			eview.setAttribute("indexX",""+vs.indexX);
			eview.setAttribute("indexY",""+vs.indexY);
			eview.setAttribute("gate",""+vs.gate.name);

			eview.setAttribute("scaleX",""+vs.scaleX);
			eview.setAttribute("scaleY",""+vs.scaleY);

			eview.setAttribute("hbins",""+vs.numHistBins);

			Element eTrans=storeTransform(vs.transformation);
			eview.addContent(eTrans);

			etot.addContent(eview);
			}
		
		//Store compensation. Should do after all the datasets are stored
		etot.addContent(storeCompensation(proj));
		
		
		return etot;
		}

	
	private static Element storeTransform(TransformationStack trans)
		{
		Element etrans=new Element("transformation");
		for(Transformation t:trans.list)
			{
			Element e=new Element("t");
			e.setAttribute("type",""+TransformationType.of(t));
			e.setAttribute("channel",""+t.channel);
			etrans.addContent(e);
			}
		return etrans;
		}
	
	
	private static TransformationStack loadTransform(Element etot)
		{
		TransformationStack trans=new TransformationStack();
				
		for(Element one:etot.getChildren())
			{
			if(one.getName().equals("t"))
				{
				String type=one.getAttributeValue("type");
				Transformation t=TransformationType.create(TransformationType.valueOf(type));
				t.channel=Integer.parseInt(one.getAttributeValue("channel"));
				trans.list.add(t);
				}
			}
		return trans;
		}
	
	

	private static Element storeCompensation(FacsanaduProject proj)
		{
		Element ecompensation=new Element("compensation");
		Compensation comp=proj.compensation;

		for(String s:comp.cnames)
			{
			Element e=new Element("cname");
			e.setAttribute("n",s);
			ecompensation.addContent(e);
			}
		
		StringBuilder sb=new StringBuilder();
		double[][] m=comp.getMatrix();
		for(int i=0;i<m.length;i++)
			{
			double[] arr=m[i];
			for(double v:arr)
				sb.append(""+v+" ");
			}
		Element v=new Element("matrix");
		v.setText(sb.toString());
		ecompensation.addContent(v);
		
		return ecompensation;
		}


	private static Compensation loadCompensation(Element etot)
		{
		Compensation comp=new Compensation();

		for(Element one:etot.getChildren())
			{
			if(one.getName().equals("cname"))
				comp.cnames.add(one.getAttributeValue("n"));
			else
				if(one.getName().equals("matrix"))
					{
					int n=comp.cnames.size();
					StringTokenizer stok=new StringTokenizer(one.getText(), " ");
					double[][] m=new double[n][n];
					for(int i=0;i<n;i++)
						for(int j=0;j<n;j++)
							m[i][j]=Double.parseDouble(stok.nextToken());
					comp.setMatrix(m);
					}
			}
		return comp;
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
			else if(g instanceof GateRect)
				{
				GateRange gr=(GateRange)g;
				type="range";
				
				ge.setAttribute("i",""+gr.index);

				ge.setAttribute("x1",""+gr.x1);
				ge.setAttribute("x2",""+gr.x2);
				}
			else if(g instanceof GateEllipse)
				{
				GateEllipse gr=(GateEllipse)g;
				type="ellipse";
				
				ge.setAttribute("ix",""+gr.indexX);
				ge.setAttribute("iy",""+gr.indexY);

				ge.setAttribute("x",""+gr.x);
				ge.setAttribute("rx",""+gr.rx);
				ge.setAttribute("y",""+gr.y);
				ge.setAttribute("ry",""+gr.ry);
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
					else if(type.equals("range"))
						{
						GateRange gr=new GateRange();
						g=gr;
						gr.index=one.getAttribute("i").getIntValue();
						gr.x1=one.getAttribute("x1").getDoubleValue();
						gr.x2=one.getAttribute("x2").getDoubleValue();
						}
					else if(type.equals("ellipse"))
						{
						GateEllipse gr=new GateEllipse();
						g=gr;
						gr.indexX=one.getAttribute("ix").getIntValue();
						gr.indexY=one.getAttribute("iy").getIntValue();
						gr.x=one.getAttribute("x").getDoubleValue();
						gr.y=one.getAttribute("y").getDoubleValue();
						gr.rx=one.getAttribute("rx").getDoubleValue();
						gr.ry=one.getAttribute("ry").getDoubleValue();
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

					if(one.getAttribute("hbins")!=null)
						vs.numHistBins=one.getAttribute("hbins").getIntValue();

					Element eTrans=one.getChild("transformation");
					if(eTrans!=null)
						vs.transformation=loadTransform(eTrans);

					proj.views.add(vs);
					}
				else if(one.getName().equals("profchan"))
					{
					ProfChannel pc=new ProfChannel();
					pc.channel=one.getAttribute("chan").getIntValue();
					pc.from=one.getAttribute("from").getIntValue();
					pc.to=one.getAttribute("to").getIntValue();
					pc.forNormalized=one.getAttribute("normalize").getBooleanValue();
					proj.profchan.add(pc);
					}
				else if(one.getName().equals("compensation"))
					{
					proj.compensation=loadCompensation(one);
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
		
		//Make certain compensation is up to date
		proj.updateCompensation();
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
