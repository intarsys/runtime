package de.intarsys.tools.geometry;

import java.awt.geom.Point2D;

/**
 * Compute the concrete value for a coordinate from the context information.
 */
public abstract class ValueComputation {

	public abstract double getValue(PointSpec spec, Point2D size);

}
