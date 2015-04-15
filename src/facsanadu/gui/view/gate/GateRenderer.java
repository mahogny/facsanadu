package facsanadu.gui.view.gate;

import java.util.Collection;

import com.trolltech.qt.gui.QPainter;

import facsanadu.gates.Gate;
import facsanadu.gui.view.ViewSettings;
import facsanadu.gui.view.ViewTransform;

/**
 * 
 * Renderer of one type of gates
 * 
 * @author Johan Henriksson
 *
 */
public interface GateRenderer
	{
	public void render(Gate gate, QPainter p, ViewTransform w, ViewSettings viewsettings, Collection<GateHandle> handles);
	}
