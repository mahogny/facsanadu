package quickfacs.gui.view.gate;

import quickfacs.gates.Gate;
import quickfacs.gates.GatePolygon;
import quickfacs.gates.GateRect;
import quickfacs.gates.GateRoot;

/**
 * 
 * @author Johan Henriksson
 *
 */
public class GateHandler
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
