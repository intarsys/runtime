package de.intarsys.tools.geometry;

import java.awt.geom.Point2D;

public class IdentityXComputation extends ValueComputation {

	public static final IdentityXComputation VALUE = new IdentityXComputation();

	@Override
	public double getValue(PointSpec spec, Point2D size) {
		return spec.getX();
	}

}
