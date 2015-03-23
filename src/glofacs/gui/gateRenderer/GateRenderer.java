package glofacs.gui.gateRenderer;

import glofacs.gates.Gate;
import glofacs.gui.panel.ChannelWidget;
import glofacs.gui.panel.ViewSettings;

import com.trolltech.qt.gui.QPainter;

/**
 * 
 * @author Johan Henriksson
 *
 */
public interface GateRenderer
	{

	public void render(Gate gate, QPainter p, ChannelWidget w, ViewSettings viewsettings);

	}
