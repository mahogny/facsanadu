package quickfacs.gui.view.gate;

import quickfacs.gates.Gate;
import quickfacs.gui.view.ViewSettings;
import quickfacs.gui.view.ViewTransform;
import com.trolltech.qt.gui.QPainter;

/**
 * 
 * Renderer of one type of gates
 * 
 * @author Johan Henriksson
 *
 */
public interface GateRenderer
	{
	public void render(Gate gate, QPainter p, ViewTransform w, ViewSettings viewsettings);
	}
