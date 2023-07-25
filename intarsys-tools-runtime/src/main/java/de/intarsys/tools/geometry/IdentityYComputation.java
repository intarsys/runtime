package de.intarsys.tools.geometry;

import java.awt.geom.Point2D;

public class IdentityYComputation extends ValueComputation {

	public static final IdentityYComputation VALUE = new IdentityYComputation();

	@Override
	public double getValue(PointSpec spec, Point2D size) {
		return spec.getY();
	}

}
