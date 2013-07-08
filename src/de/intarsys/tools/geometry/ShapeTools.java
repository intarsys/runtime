package de.intarsys.tools.geometry;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

public class ShapeTools {

	static public Area createArea(Shape shape, boolean copy) {
		if (shape == null) {
			return null;
		}
		if (shape instanceof TransformedShape) {
			shape = ((TransformedShape) shape).getShape();
		}
		if (shape instanceof Area) {
			if (copy) {
				return (Area) ((Area) shape).clone();
			} else {
				return (Area) shape;
			}
		}
		return new Area(shape);
	}

	/**
	 * Returns the shapes fill style winding rule.
	 * 
	 * @return an integer representing the shapes winding rule.
	 */
	static public int getWindingRule(Shape shape) {
		if (shape instanceof IShapeWrapper) {
			return getWindingRule(((IShapeWrapper) shape).getBaseShape());
		}
		if (shape instanceof GeneralPath) {
			return ((GeneralPath) shape).getWindingRule();
		}
		return PathIterator.WIND_NON_ZERO;
	}
}
