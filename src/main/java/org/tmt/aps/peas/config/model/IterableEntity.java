package org.tmt.aps.peas.config.model;

/**
 * Interface for an entities that can change values over procedure iterations (for example, Filter, ReferenceBeam)
 * There is also a {@link IndexIteratableEntity} which iterates over the loop index. 
 * @author smichaels
 *
 */
public interface IterableEntity {
	String getClassName();
	String getKeyFieldName();
	String getLabelFieldName();
	String getLabel();
}
