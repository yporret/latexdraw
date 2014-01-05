package test.glib.models.interfaces;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import net.sf.latexdraw.glib.models.ShapeFactory;
import net.sf.latexdraw.glib.models.interfaces.IEllipse;
import net.sf.latexdraw.glib.models.interfaces.ILine;
import net.sf.latexdraw.glib.models.interfaces.IPoint;

import org.junit.Test;

import test.HelperTest;

public abstract class TestIEllipse<T extends IEllipse> extends TestIRectangularShape<T> {
	@Test
	public void testGetIntersectionHoriz1() {
		ILine line = ShapeFactory.factory().createLine(ShapeFactory.factory().createPoint(-10,0), ShapeFactory.factory().createPoint(10,0));
		shape.setPosition(-1,1);
		shape.setWidth(2);
		shape.setHeight(2);
		IPoint[] pts = shape.getIntersection(line);

		assertEquals(2, pts.length);
		if(pts[0].getX()<pts[1].getX()) {
			HelperTest.assertEqualsDouble(-1., pts[0].getX());
			HelperTest.assertEqualsDouble(0., pts[0].getY());
			HelperTest.assertEqualsDouble(1., pts[1].getX());
			HelperTest.assertEqualsDouble(0., pts[1].getY());
		}else {
			HelperTest.assertEqualsDouble(1., pts[0].getX());
			HelperTest.assertEqualsDouble(0., pts[0].getY());
			HelperTest.assertEqualsDouble(-1., pts[1].getX());
			HelperTest.assertEqualsDouble(0., pts[1].getY());
		}
	}


	@Test
	public void testGetIntersectionHoriz2() {
		ILine line = ShapeFactory.factory().createLine(ShapeFactory.factory().createPoint(-10,1), ShapeFactory.factory().createPoint(10,1));
		shape.setPosition(-1,1);
		shape.setWidth(2);
		shape.setHeight(2);
		IPoint[] pts = shape.getIntersection(line);

		assertNotNull(pts);
		HelperTest.assertEqualsDouble(1, pts.length);
		HelperTest.assertEqualsDouble(0., pts[0].getX());
		HelperTest.assertEqualsDouble(1., pts[0].getY());
	}


	@Test
	public void testGetIntersectionHoriz3() {
		ILine line = ShapeFactory.factory().createLine(ShapeFactory.factory().createPoint(-10,-1), ShapeFactory.factory().createPoint(10,-1));
		shape.setPosition(-1,1);
		shape.setWidth(2);
		shape.setHeight(2);
		IPoint[] pts = shape.getIntersection(line);

		assertNotNull(pts);
		HelperTest.assertEqualsDouble(1, pts.length);
		HelperTest.assertEqualsDouble(-0., pts[0].getX());
		HelperTest.assertEqualsDouble(-1., pts[0].getY());
	}



	@Test
	public void testGetIntersectionVert1() {
		ILine line = ShapeFactory.factory().createLine(ShapeFactory.factory().createPoint(0,10), ShapeFactory.factory().createPoint(0,-10));
		shape.setPosition(-1,1);
		shape.setWidth(2);
		shape.setHeight(2);
		IPoint[] pts = shape.getIntersection(line);

		assertNotNull(pts);
		assertEquals(2, pts.length);
		if(pts[0].getY()<pts[1].getY()) {
			HelperTest.assertEqualsDouble(0., pts[0].getX());
			HelperTest.assertEqualsDouble(-1., pts[0].getY());
			HelperTest.assertEqualsDouble(0., pts[1].getX());
			HelperTest.assertEqualsDouble(1., pts[1].getY());
		}else {
			HelperTest.assertEqualsDouble(0., pts[0].getX());
			HelperTest.assertEqualsDouble(1., pts[0].getY());
			HelperTest.assertEqualsDouble(0., pts[1].getX());
			HelperTest.assertEqualsDouble(-1., pts[1].getY());
		}
	}


	@Test
	public void testGetIntersectionVert2() {
		ILine line = ShapeFactory.factory().createLine(ShapeFactory.factory().createPoint(1,-10), ShapeFactory.factory().createPoint(1,10));
		shape.setPosition(-1,1);
		shape.setWidth(2);
		shape.setHeight(2);
		IPoint[] pts = shape.getIntersection(line);

		assertNotNull(pts);
		assertEquals(1, pts.length);
		HelperTest.assertEqualsDouble(1., pts[0].getX());
		HelperTest.assertEqualsDouble(0., pts[0].getY());
	}


	@Test
	public void testGetIntersectionVert3() {
		ILine line = ShapeFactory.factory().createLine(ShapeFactory.factory().createPoint(-1,-10), ShapeFactory.factory().createPoint(-1,10));
		shape.setPosition(-1,1);
		shape.setWidth(2);
		shape.setHeight(2);
		IPoint[] pts = shape.getIntersection(line);

		assertNotNull(pts);
		assertEquals(1, pts.length);
		HelperTest.assertEqualsDouble(-1., pts[0].getX());
		HelperTest.assertEqualsDouble(0., pts[0].getY());
	}


	@Test
	public void testGetA() {
		shape.setPosition(10, 5);
		shape.setWidth(20);
		shape.setHeight(15);

		HelperTest.assertEqualsDouble(10., shape.getA());

		shape.setWidth(10);
		shape.setHeight(15);

		HelperTest.assertEqualsDouble(7.5, shape.getA());
	}



	@Test
	public void testGetB() {
		shape.setPosition(10, 5);
		shape.setWidth(20);
		shape.setHeight(15);

		HelperTest.assertEqualsDouble(7.5, shape.getB());

		shape.setWidth(10);
		shape.setHeight(15);

		HelperTest.assertEqualsDouble(5., shape.getB());
	}
}
