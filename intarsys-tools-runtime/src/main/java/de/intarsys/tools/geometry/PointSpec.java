package de.intarsys.tools.geometry;

import java.awt.geom.Point2D;

import de.intarsys.tools.string.StringTools;

/**
 * The specification of a {@link Point2D}.
 * 
 * The concrete values for the coordinates can be derived at runtime from contextual information.
 * 
 */
public class PointSpec {

	public static PointSpec absolute(double x, double y) {
		PointSpec spec = new PointSpec();
		spec.setComputeX(IdentityXComputation.VALUE);
		spec.setX(x);
		spec.setComputeY(IdentityYComputation.VALUE);
		spec.setY(y);
		return spec;
	}

	public static PointSpec parse(String definition) {
		if (StringTools.isEmpty(definition)) {
			return null;
		}
		PointSpec spec = new PointSpec();
		String[] split = definition.split("[xX@*]");
		if (split.length != 2) {
			throw new IllegalArgumentException("point definition requires 2 elements");
		}
		ValueComputation computation;
		String value;
		//
		value = split[0].trim();
		computation = IdentityXComputation.VALUE;
		if (value.endsWith("%")) {
			computation = PercentXComputation.VALUE;
			value = value.substring(0, value.length() - 1).trim();
		}
		spec.setComputeX(computation);
		spec.setX(Double.parseDouble(value));
		//
		value = split[1].trim();
		computation = IdentityYComputation.VALUE;
		if (value.endsWith("%")) {
			computation = PercentYComputation.VALUE;
			value = value.substring(0, value.length() - 1).trim();
		}
		spec.setComputeY(computation);
		spec.setY(Double.parseDouble(value));
		return spec;
	}

	public static PointSpec percent(double x, double y) {
		PointSpec spec = new PointSpec();
		spec.setComputeX(PercentXComputation.VALUE);
		spec.setX(x);
		spec.setComputeY(PercentYComputation.VALUE);
		spec.setY(y);
		return spec;
	}

	private double x;

	private double y;

	private ValueComputation computeX;

	private ValueComputation computeY;

	protected PointSpec() {
	}

	public PointSpec(double x, double y) {
		this.x = x;
		this.y = y;
		this.computeX = IdentityXComputation.VALUE;
		this.computeY = IdentityYComputation.VALUE;
	}

	protected ValueComputation getComputeX() {
		return computeX;
	}

	protected ValueComputation getComputeY() {
		return computeY;
	}

	public Point2D getPoint(Point2D size) {
		return new Point2D.Double(getX(size), getY(size));
	}

	protected double getX() {
		return x;
	}

	protected double getX(Point2D size) {
		return getComputeX().getValue(this, size);
	}

	protected double getY() {
		return y;
	}

	protected double getY(Point2D size) {
		return getComputeY().getValue(this, size);
	}

	protected void setComputeX(ValueComputation computeX) {
		this.computeX = computeX;
	}

	protected void setComputeY(ValueComputation computeY) {
		this.computeY = computeY;
	}

	protected void setX(double x) {
		this.x = x;
	}

	protected void setY(double y) {
		this.y = y;
	}

}
