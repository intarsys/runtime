/*
 * Copyright (c) 2008, intarsys consulting GmbH
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

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * A shape that can be transformed with lazy access to the transformed shape.
 * <p>
 * baseShape -> transform -> shape
 * 
 */
abstract public class TransformedShape implements Shape, IShapeWrapper,
		Cloneable {

	protected static final AffineTransform IDENTITY = new AffineTransform();

	final private Shape baseShape;

	private Shape resultShape;

	protected TransformedShape(Shape shape) {
		this.baseShape = shape;
		this.resultShape = null;
	}

	protected TransformedShape(TransformedShape shape) {
		this.baseShape = shape.baseShape;
		this.resultShape = shape.resultShape;
	}

	abstract protected Shape apply();

	@Override
	abstract public Object clone();

	public boolean contains(double x, double y) {
		return getShape().contains(x, y);
	}

	public boolean contains(double x, double y, double w, double h) {
		return getShape().contains(x, y, w, h);
	}

	public boolean contains(Point2D p) {
		return getShape().contains(p);
	}

	public boolean contains(Rectangle2D r) {
		return getShape().contains(r);
	}

	public Shape getBaseShape() {
		return baseShape;
	}

	public java.awt.Rectangle getBounds() {
		return getShape().getBounds();
	}

	public Rectangle2D getBounds2D() {
		return getShape().getBounds2D();
	}

	public PathIterator getPathIterator(AffineTransform at) {
		return getShape().getPathIterator(at);
	}

	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return getShape().getPathIterator(at, flatness);
	}

	public Shape getShape() {
		if (resultShape == null) {
			resultShape = apply();
		}
		return resultShape;
	}

	abstract public AffineTransform getTransform();

	public boolean intersects(double x, double y, double w, double h) {
		return getShape().intersects(x, y, w, h);
	}

	public boolean intersects(Rectangle2D r) {
		return getShape().intersects(r);
	}

	public void invalidate() {
		resultShape = null;
	}
}