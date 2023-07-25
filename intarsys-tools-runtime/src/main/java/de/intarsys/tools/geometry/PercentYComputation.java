package de.intarsys.tools.geometry;

import java.awt.geom.Point2D;

public class PercentYComputation extends ValueComputation {

	public static final PercentYComputation VALUE = new PercentYComputation();

	@Override
	public double getValue(PointSpec spec, Point2D size) {
		return spec.getY() * size.getY() / 100;
	}

}
