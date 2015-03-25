package quickfacs.gui.gateRenderer;

import quickfacs.gates.Gate;
import quickfacs.gates.GateRect;
import quickfacs.gates.GateRoot;

/**
 * 
 * @author Johan Henriksson
 *
 */
public class GateHandler
	{

	public static GateRenderer getGateRenderer(Gate g)
		{
		if(g instanceof GateRect)
			return new GateRendererRect();
		else if(g instanceof GateRoot)
			return new GateRendererRoot();
		else
			throw new RuntimeException("no renderer");
		}
	}
