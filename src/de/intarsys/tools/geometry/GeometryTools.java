/*
 * Copyright (c) 2007, intarsys consulting GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.tools.geometry;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Tool class for common geometry tasks.
 */
public class GeometryTools {

	static public Point2D deltaTransformPoint(AffineTransform transform,
			float x, float y) {
		Point2D pt = new Point2D.Float(x, y);
		transform.deltaTransform(pt, pt);
		return pt;
	}

	static public void deltaTransformPoint(AffineTransform transform,
			float[] pts) {
		double[] dpts = new double[2];
		dpts[0] = pts[0];
		dpts[1] = pts[1];
		transform.deltaTransform(dpts, 0, dpts, 0, 1);
		pts[0] = (float) dpts[0];
		pts[1] = (float) dpts[1];
	}

	static public void deltaTransformPoint(AffineTransform transform, Point2D pt) {
		transform.deltaTransform(pt, pt);
	}

	/**
	 * Transform a rectangle in device space to user space. The rectangle is
	 * modified. The coordinates of the result are normalized (lower left corner
	 * has smallest coordinate values).
	 * 
	 * @param transform
	 *            The transformation from user space to device space.
	 * 
	 * @param rect
	 *            The rectangle whose coordinates are transformed, the rectangle
	 *            is modified.
	 */
	static public void inverseTransformRect(AffineTransform transform,
			Rectangle2D rect) {
		double[] pts = new double[4];
		pts[0] = rect.getX();
		pts[2] = rect.getMaxX();
		pts[1] = rect.getY();
		pts[3] = rect.getMaxY();
		try {
			transform.inverseTransform(pts, 0, pts, 0, pts.length >> 1);
		} catch (NoninvertibleTransformException e) {
			// ?
		}
		rect.setRect(pts[0], pts[1], pts[2] - pts[0], pts[3] - pts[1]);
		normalizeRect(rect);
	}

	static public void normalizeRect(float[] pts) {
		float temp;
		if (pts[0] > pts[2]) {
			temp = pts[0];
			pts[0] = pts[2];
			pts[2] = temp;
		}
		if (pts[1] > pts[3]) {
			temp = pts[1];
			pts[1] = pts[3];
			pts[3] = temp;
		}
	}

	/**
	 * Normalize the rectangle. The x and y value of the rectangle are updated
	 * to have the smallest coordinates (lower left corner).
	 * 
	 * @param rect
	 *            The rectangle whose coordinates are normalized, the rectangle
	 *            is modified.
	 */
	static public void normalizeRect(Rectangle2D rect) {
		double llx = rect.getMinX();
		double lly = rect.getMinY();
		double width = rect.getWidth();
		double height = rect.getHeight();
		if (width < 0) {
			llx = llx + width;
			width = -width;
		}
		if (height < 0) {
			lly = lly + height;
			height = -height;
		}
		rect.setRect(llx, lly, width, height);
	}

	static public float[] toFloatArray(Rectangle2D rect) {
		float[] pts = new float[4];
		pts[0] = (float) rect.getX();
		pts[1] = (float) rect.getY();
		pts[2] = (float) (pts[0] + rect.getWidth());
		pts[3] = (float) (pts[1] + rect.getHeight());
		return pts;
	}

	static public Point2D transformPoint(AffineTransform transform, float x,
			float y) {
		Point2D pt = new Point2D.Float(x, y);
		transform.transform(pt, pt);
		return pt;
	}

	static public void transformPoint(AffineTransform transform, float[] pts) {
		transform.transform(pts, 0, pts, 0, 1);
	}

	static public void transformPoint(AffineTransform transform, Point2D pt) {
		transform.transform(pt, pt);
	}

	static public void transformRect(AffineTransform transform, float[] pts) {
		transform.transform(pts, 0, pts, 0, 2);
		GeometryTools.normalizeRect(pts);
	}

	/**
	 * Transform a rectangle, the rectangle is modified. The coordinates of the
	 * result are normalized (lower left corner has smallest coordinate values).
	 * 
	 * @param transform
	 *            The transformation from user space to device space.
	 * 
	 * @param rect
	 *            The rectangle whose coordinates are transformed, the rectangle
	 *            is modified.
	 */
	static public void transformRect(AffineTransform transform, Rectangle2D rect) {
		double[] pts = new double[4];
		pts[0] = rect.getMinX();
		pts[2] = rect.getMaxX();
		pts[1] = rect.getMinY();
		pts[3] = rect.getMaxY();
		transform.transform(pts, 0, pts, 0, pts.length >> 1);
		rect.setRect(pts[0], pts[1], pts[2] - pts[0], pts[3] - pts[1]);
		GeometryTools.normalizeRect(rect);
	}

	/**
	 * Tool class cannot be instantiated.
	 */
	private GeometryTools() {
	}
}
