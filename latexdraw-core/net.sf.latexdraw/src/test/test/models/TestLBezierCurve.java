package test.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import net.sf.latexdraw.models.ShapeFactory;
import net.sf.latexdraw.models.interfaces.shape.IBezierCurve;
import net.sf.latexdraw.models.interfaces.shape.ICircle;
import net.sf.latexdraw.models.interfaces.shape.IControlPointShape;
import net.sf.latexdraw.models.interfaces.shape.IModifiablePointsShape;
import net.sf.latexdraw.models.interfaces.shape.IRectangle;
import net.sf.latexdraw.models.interfaces.shape.IShape;

import org.junit.Before;
import org.junit.Test;

import test.HelperTest;
import test.models.interfaces.TestIBezierCurve;

public class TestLBezierCurve extends TestIBezierCurve<IBezierCurve> {
	@Before
	public void setUp() {
		shape = ShapeFactory.INST.createBezierCurve();
		shape2 = ShapeFactory.INST.createBezierCurve();
	}

	@Override
	@Test
	public void testIsTypeOf() {
		assertFalse(shape.isTypeOf(null));
		assertFalse(shape.isTypeOf(IRectangle.class));
		assertFalse(shape.isTypeOf(ICircle.class));
		assertTrue(shape.isTypeOf(IShape.class));
		assertTrue(shape.isTypeOf(IModifiablePointsShape.class));
		assertTrue(shape.isTypeOf(IControlPointShape.class));
		assertTrue(shape.isTypeOf(IBezierCurve.class));
		assertTrue(shape.isTypeOf(shape.getClass()));
	}

	@Test
	public void testConstructors() {
		IBezierCurve curve = ShapeFactory.INST.createBezierCurve();

		assertNotNull(curve);
		assertEquals(0, curve.getPoints().size());
		assertEquals(0, curve.getFirstCtrlPts().size());
		assertEquals(0, curve.getSecondCtrlPts().size());
		assertEquals(2, curve.getNbArrows());
	}

	@Test
	public void testConstructors2() {
		IBezierCurve curve = ShapeFactory.INST.createBezierCurve(ShapeFactory.INST.createPoint(100, 200), ShapeFactory.INST.createPoint(300, 400));

		assertNotNull(curve);
		assertEquals(2, curve.getPoints().size());
		HelperTest.assertEqualsDouble(100., curve.getPoints().get(0).getX());
		HelperTest.assertEqualsDouble(200., curve.getPoints().get(0).getY());
		HelperTest.assertEqualsDouble(300., curve.getPoints().get(1).getX());
		HelperTest.assertEqualsDouble(400., curve.getPoints().get(1).getY());
		assertEquals(2, curve.getFirstCtrlPts().size());
		assertEquals(2, curve.getSecondCtrlPts().size());
		assertEquals(2, curve.getNbArrows());
	}
}
