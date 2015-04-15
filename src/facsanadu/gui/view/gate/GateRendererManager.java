package facsanadu.gui.view.gate;

import facsanadu.gates.Gate;
import facsanadu.gates.GatePolygon;
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
		else if(g instanceof GatePolygon)
			return new GateRendererPoly();
		else
			throw new RuntimeException("no renderer");
		}
	}
