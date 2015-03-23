package glofacs.gui.gateRenderer;

import glofacs.gates.Gate;

/**
 * 
 * @author Johan Henriksson
 *
 */
public class GateHandler
	{

	public static GateRendererRect getGateRenderer(Gate g)
		{
		return new GateRendererRect();
		}
	}
