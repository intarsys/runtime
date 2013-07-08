package de.intarsys.tools.geometry;

import java.awt.Shape;
import java.awt.geom.AffineTransform;

/**
 * The transformation is defined explicitly.
 * 
 */
public class ApplyTransformationShape extends TransformedShape {

	/**
	 * Create a new {@link Shape} created from <code>shape</code> by applying
	 * <code>transform</code>
	 * 
	 * @param shape
	 *            The wrapped base shape
	 * @param transform
	 *            The coordinate space where "shape" is defined.
	 * 
	 * @return The new {@link TransformedShape}
	 */
	public static TransformedShape create(Shape shape, AffineTransform transform) {
		return new ApplyTransformationShape(shape, transform);
	}

	final private AffineTransform transform;

	protected ApplyTransformationShape(ApplyTransformationShape shape) {
		super(shape);
		this.transform = shape.transform;
	}

	protected ApplyTransformationShape(Shape shape, AffineTransform transform) {
		super(shape);
		this.transform = transform;
	}

	@Override
	protected Shape apply() {
		return getTransform().createTransformedShape(getBaseShape());
	}

	/**
	 * Returns an exact copy of this <code>Area</code> object.
	 * 
	 * @return Created clone object
	 */
	@Override
	public Object clone() {
		return new ApplyTransformationShape(this);
	}

	@Override
	public AffineTransform getTransform() {
		return transform;
	}
}
