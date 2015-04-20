package facsanadu.gui.view.tool;

import facsanadu.gui.events.FacsanaduEvent;

/**
 * 
 * 
 * @author Johan Henriksson
 *
 */
public class EventSetViewTool implements FacsanaduEvent
	{
	public ViewToolChoice choice;
	
	public EventSetViewTool(ViewToolChoice choice)
		{
		this.choice=choice;
		}

	}
