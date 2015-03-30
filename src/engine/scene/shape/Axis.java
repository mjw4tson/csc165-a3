package engine.scene.shape;

import graphicslib3D.Point3D;

import java.awt.Color;

import sage.renderer.IRenderer;
import sage.scene.Leaf;
import sage.scene.shape.Line;

/**
 * Defines a 3D axis.
 *
 * @author ktajeran
 *
 */
public class Axis extends Leaf {
	Color	xColor;
	Color	yColor;
	Color	zColor;
	double	xBound;
	double	yBound;
	double	zBound;

	/**
	 * Constructs a 3D Axis with color and bounds specified.
	 *
	 * @param xB
	 *            - The X axis length.
	 * @param yB
	 *            - The Y axis length.
	 * @param zB
	 *            - The Z axis length.
	 * @param x
	 *            - The color of the X axis.
	 * @param y
	 *            - The color of the Y axis.
	 * @param z
	 *            - The color of the Z axis.
	 */
	public Axis( double length, Color x, Color y, Color z ) {
		xBound = length;
		yBound = length;
		zBound = length;
		xColor = x;
		yColor = y;
		zColor = z;
	}

	/*
	 * Defines the logic for drawing the 3D axis.
	 */
	@Override
	public void draw( IRenderer renderer ) {
		Point3D origin = new Point3D(0, 0, 0);
		Point3D xEnd = new Point3D(xBound, 0, 0);
		Point3D yEnd = new Point3D(0, yBound, 0);
		Point3D zEnd = new Point3D(0, 0, zBound);
		Line xAxis = new Line(origin, xEnd, xColor, 3);
		Line yAxis = new Line(origin, yEnd, yColor, 3);
		Line zAxis = new Line(origin, zEnd, zColor, 3);
		renderer.draw(xAxis);
		renderer.draw(yAxis);
		renderer.draw(zAxis);
	}

	@Override
	public void updateLocalBound() {
		// TODO Auto-generated method stub

	}

}
