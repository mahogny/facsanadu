package glofacs.gui.gateRenderer;

import glofacs.gates.Gate;
import glofacs.gui.channel.ChannelWidget;
import glofacs.gui.channel.ViewSettings;

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
