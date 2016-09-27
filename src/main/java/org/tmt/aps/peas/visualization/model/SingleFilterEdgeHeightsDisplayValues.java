package org.tmt.aps.peas.visualization.model;

import org.tmt.aps.peas.instrument.model.Filter;

public interface SingleFilterEdgeHeightsDisplayValues extends EdgeHeightsDisplayValues {

	public Filter getFilter();
	
	public void setFilter(Filter filter);
}
