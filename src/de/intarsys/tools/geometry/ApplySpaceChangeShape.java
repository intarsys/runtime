package de.intarsys.tools.geometry;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.NoninvertibleTransformException;

/**
 * The transformation is defined by two matrices, each defining a coordinate
 * system. The source transformation is the space the shape is defined in, the
 * target transformation is the space for the resulting shape.
 */
public class ApplySpaceChangeShape extends TransformedShape {

	/**
	 * Create a new {@link Shape} whose coordinates are specified in the space
	 * defined by "transform".
	 * 
	 * @param shape
	 *            The wrapped base shape
	 * @param transform
	 *            The coordinate space where "shape" is defined.
	 * 
	 * @return The new {@link TransformedShape}
	 */
	public static TransformedShape create(Shape shape, AffineTransform transform) {
		if (shape instanceof ApplySpaceChangeShape) {
			ApplySpaceChangeShape ascShape;

			ascShape = (ApplySpaceChangeShape) shape;
			if (ascShape.getTargetTransform().equals(transform)) {
				return create(ascShape.getBaseShape(), ascShape
						.getSourceTransform(), transform);
			}
		}
		return create(shape, (AffineTransform) transform.clone(), transform);
	}

	public static TransformedShape create(Shape shape,
			AffineTransform sourceTransform, AffineTransform targetTransform) {
		return new ApplySpaceChangeShape(shape, sourceTransform,
				targetTransform);
	}

	/**
	 * Create a new {@link Shape} that behaves like the base shape with its
	 * coordinates relative to the new space defined by "newTransform".
	 * <p>
	 * You can use this to "view" on an already existing shape from another
	 * coordinate system.
	 * 
	 * @param shape
	 *            The base shape
	 * @param targetTransform
	 *            The new coordinate space
	 * 
	 * @return The transformed shape, showing "shape" from the space defined by
	 *         "newTransform"
	 */
	public static TransformedShape setTransform(Shape shape,
			AffineTransform targetTransform) {
		if (shape instanceof ApplySpaceChangeShape) {
			ApplySpaceChangeShape ts = (ApplySpaceChangeShape) shape;
			return new ApplySpaceChangeShape(ts.getBaseShape(), ts
					.getSourceTransform(), targetTransform);
		}
		return new ApplySpaceChangeShape(shape, IDENTITY, targetTransform);
	}

	final private AffineTransform sourceTransform;

	final private AffineTransform targetTransform;

	protected ApplySpaceChangeShape(ApplySpaceChangeShape shape) {
		super(shape);
		this.sourceTransform = shape.sourceTransform;
		this.targetTransform = (AffineTransform) shape.targetTransform.clone();
	}

	protected ApplySpaceChangeShape(Shape shape,
			AffineTransform sourceTransform, AffineTransform targetTransform) {
		super(shape);
		this.sourceTransform = sourceTransform;
		this.targetTransform = targetTransform;
	}

	@Override
	protected Shape apply() {
		if (targetTransform.equals(sourceTransform)) {
			return getBaseShape();
		}
		if (getBaseShape() instanceof Area) {
			return new Area(getTransform().createTransformedShape(
					getBaseShape()));
		} else {
			return getTransform().createTransformedShape(getBaseShape());
		}
	}

	/**
	 * Returns an exact copy of this <code>Area</code> object.
	 * 
	 * @return Created clone object
	 */
	@Override
	public Object clone() {
		return new ApplySpaceChangeShape(this);
	}

	public AffineTransform getSourceTransform() {
		return sourceTransform;
	}

	public AffineTransform getTargetTransform() {
		return targetTransform;
	}

	@Override
	public AffineTransform getTransform() {
		try {
			if (targetTransform.equals(sourceTransform)) {
				return new AffineTransform();
			}
			AffineTransform tempTransform = targetTransform.createInverse();
			tempTransform.concatenate(sourceTransform);
			return tempTransform;
		} catch (NoninvertibleTransformException e) {
			// this happens indeed if some nerd scales down presentation
			// excessive..
			return new AffineTransform(0, 0, 0, 0, 0, 0);
		}
	}
}
