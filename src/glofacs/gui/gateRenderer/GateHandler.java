package glofacs.gui.gateRenderer;

import glofacs.gates.Gate;
import glofacs.gates.GateRect;
import glofacs.gates.GateRoot;

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
