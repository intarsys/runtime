package de.intarsys.tools.geometry;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import junit.framework.TestCase;

public class TestTransformedShape extends TestCase {

	public void testInit1() {
		Shape shape = new Rectangle2D.Float(0, 0, 1, 1);
		Shape resultShape;
		Rectangle2D bounds;
		//
		AffineTransform base = new AffineTransform();
		AffineTransform tx = AffineTransform.getScaleInstance(2, 2);
		TransformedShape ts = new ApplySpaceChangeShape(shape, base, tx);
		resultShape = ts.getShape();
		bounds = resultShape.getBounds2D();
		assertTrue(resultShape.contains(0.1, 0.1));
		assertTrue(resultShape.contains(0.4, 0.4));
		assertTrue(!resultShape.contains(0.6, 0.6));
		//
		AffineTransform tx2 = AffineTransform.getScaleInstance(10, 10);
		TransformedShape ts2 = ApplySpaceChangeShape.create(ts, tx2);
		resultShape = ts2.getShape();
		bounds = resultShape.getBounds2D();
		assertTrue(resultShape.contains(0.1, 0.1));
		assertTrue(resultShape.contains(0.4, 0.4));
		assertTrue(!resultShape.contains(0.6, 0.6));
	}

	public void testInit2() {
		Shape shape = new Rectangle2D.Float(0, 0, 1, 1);
		Shape resultShape;
		Rectangle2D bounds;
		//
		AffineTransform base = AffineTransform.getScaleInstance(2, 2);
		AffineTransform tx = AffineTransform.getScaleInstance(4, 4);
		TransformedShape ts = new ApplySpaceChangeShape(shape, base, tx);
		resultShape = ts.getShape();
		bounds = resultShape.getBounds2D();
		assertTrue(resultShape.contains(0.1, 0.1));
		assertTrue(resultShape.contains(0.4, 0.4));
		assertTrue(!resultShape.contains(0.6, 0.6));
		//
		AffineTransform tx2 = AffineTransform.getScaleInstance(10, 10);
		TransformedShape ts2 = ApplySpaceChangeShape.create(ts, tx2);
		resultShape = ts2.getShape();
		bounds = resultShape.getBounds2D();
		assertTrue(resultShape.contains(0.1, 0.1));
		assertTrue(resultShape.contains(0.4, 0.4));
		assertTrue(!resultShape.contains(0.6, 0.6));
	}

	public void testNested() {
		Shape shape = new Rectangle2D.Float(0, 0, 1, 1);
		assertTrue(shape.contains(0.1, 0.1));
		assertTrue(shape.contains(0.9, 0.9));
		AffineTransform txTranslate = AffineTransform.getTranslateInstance(10, 10);
		AffineTransform txScale = AffineTransform.getScaleInstance(10, 10);
		Shape transformedShape1 = ApplySpaceChangeShape.setTransform(shape, txTranslate);
	}

	public void testSimple() {
		Shape shape = new Rectangle2D.Float(0, 0, 1, 1);
		assertTrue(shape.contains(0.1, 0.1));
		assertTrue(shape.contains(0.9, 0.9));
		AffineTransform transform = AffineTransform.getTranslateInstance(10, 10);
		Shape transformedShape = ApplySpaceChangeShape.setTransform(shape, transform);
		assertTrue(transformedShape.contains(-9.9, -9.9));
		assertTrue(transformedShape.contains(-9.1, -9.1));
	}
}
