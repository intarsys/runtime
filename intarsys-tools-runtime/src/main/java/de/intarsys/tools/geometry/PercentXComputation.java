package de.intarsys.tools.geometry;

import java.awt.geom.Point2D;

public class PercentXComputation extends ValueComputation {

	public static final PercentXComputation VALUE = new PercentXComputation();

	@Override
	public double getValue(PointSpec spec, Point2D size) {
		return spec.getX() * size.getX() / 100;
	}

}
