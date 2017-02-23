package facsanadu.gui.view.gate;

import facsanadu.gates.Gate;
import facsanadu.gates.GateEllipse;
import facsanadu.gates.GatePolygon;
import facsanadu.gates.GateRange;
import facsanadu.gates.GateRect;
import facsanadu.gates.GateRoot;

/**
 * 
 * Manager of gate renderers
 * 
 * @author Johan Henriksson
 *
 */
public class GateRendererManager
	{
	/**
	 * Get a suitable renderer for the gate
	 */
	public static GateRenderer getGateRenderer(Gate g)
		{
		if(g instanceof GateRect)
			return new GateRendererRect();
		else if(g instanceof GateRoot)
			return new GateRendererRoot();
		else if(g instanceof GateRange)
			return new GateRendererRange();
		else if(g instanceof GatePolygon)
			return new GateRendererPoly();
		else if(g instanceof GateEllipse)
			return new GateRendererEllipse();
		else
			throw new RuntimeException("no renderer");
		}
	}
