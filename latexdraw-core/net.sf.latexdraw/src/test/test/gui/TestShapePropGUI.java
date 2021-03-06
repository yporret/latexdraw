package test.gui;

import javafx.application.Platform;
import net.sf.latexdraw.instruments.EditionChoice;
import net.sf.latexdraw.instruments.Hand;
import net.sf.latexdraw.instruments.Pencil;
import net.sf.latexdraw.instruments.ShapePropertyCustomiser;
import net.sf.latexdraw.models.ShapeFactory;
import net.sf.latexdraw.models.interfaces.shape.IDrawing;
import net.sf.latexdraw.models.interfaces.shape.IFreehand;
import net.sf.latexdraw.models.interfaces.shape.IShape;
import org.junit.Before;
import org.testfx.util.WaitForAsyncUtils;
import test.gui.robot.FxRobotColourPicker;
import test.gui.robot.FxRobotListSelection;
import test.gui.robot.FxRobotSpinner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public abstract class TestShapePropGUI<T extends ShapePropertyCustomiser> extends TestLatexdrawGUI implements FxRobotColourPicker, FxRobotListSelection, FxRobotSpinner {
	protected Pencil pencil;
	protected Hand hand;
	protected IDrawing drawing;
	protected T ins;

	protected final GUIVoidCommand pencilCreatesRec = () -> pencil.setCurrentChoice(EditionChoice.RECT);
	protected final GUIVoidCommand pencilCreatesBezier = () -> pencil.setCurrentChoice(EditionChoice.BEZIER_CURVE);
	protected final GUIVoidCommand pencilCreatesCircle = () -> pencil.setCurrentChoice(EditionChoice.CIRCLE);
	protected final GUIVoidCommand pencilCreatesText = () -> pencil.setCurrentChoice(EditionChoice.TEXT);
	protected final GUIVoidCommand pencilCreatesPic = () -> pencil.setCurrentChoice(EditionChoice.PICTURE);
	protected final GUIVoidCommand pencilCreatesArc = () -> pencil.setCurrentChoice(EditionChoice.CIRCLE_ARC);
	protected final GUIVoidCommand pencilCreatesAxes = () -> pencil.setCurrentChoice(EditionChoice.AXES);
	protected final GUIVoidCommand pencilCreatesDot = () -> pencil.setCurrentChoice(EditionChoice.DOT);
	protected final GUIVoidCommand pencilCreatesFreehand = () -> pencil.setCurrentChoice(EditionChoice.FREE_HAND);
	protected final GUIVoidCommand pencilCreatesGrid = () -> pencil.setCurrentChoice(EditionChoice.GRID);
	protected final GUIVoidCommand pencilCreatesPlot = () -> pencil.setCurrentChoice(EditionChoice.PLOT);

	protected final GUIVoidCommand updateIns = () -> {
		Platform.runLater(() -> ins.update());
		WaitForAsyncUtils.waitForFxEvents();
	};
	protected final GUIVoidCommand checkInsActivated = () -> assertTrue(ins.isActivated());
	protected final GUIVoidCommand checkInsDeactivated = () -> assertFalse(ins.isActivated());

	protected final GUIVoidCommand activatePencil = () -> {
		// when(pencil.isActivated()).thenReturn(true);
		pencil.setActivated(true);
		when(hand.isActivated()).thenReturn(false);
	};

	protected final GUIVoidCommand activateHand = () -> {
		when(pencil.isActivated()).thenReturn(false);
		// when(hand.isActivated()).thenReturn(true);
		hand.setActivated(true);
	};

	// protected final GUICommand pencilGroupParams = () -> {
	// IGroup g = ShapeFactory.INST.createGroup();
	// g.addShape(ShapeFactory.INST.createRectangle());
	// g.addShape(ShapeFactory.INST.createDot(ShapeFactory.INST.createPoint()));
	// g.addShape(ShapeFactory.INST.createGrid(ShapeFactory.INST.createPoint()));
	// g.addShape(ShapeFactory.INST.createAxes(ShapeFactory.INST.createPoint()));
	// g.addShape(ShapeFactory.INST.createText());
	// g.addShape(ShapeFactory.INST.createCircleArc());
	// g.addShape(ShapeFactory.INST.createPolyline());
	// g.addShape(ShapeFactory.INST.createBezierCurve());
	// g.addShape(ShapeFactory.INST.createFreeHand());
	// g.addShape(ShapeFactory.INST.createPlot(ShapeFactory.INST.createPoint(), 1, 10, "x", false));
	// when(pencil.getGroupParams()).thenReturn(g);
	// };

	protected final GUIVoidCommand selectionAddRec = () -> {
		IShape sh = ShapeFactory.INST.createRectangle();
		drawing.addShape(sh);
		drawing.getSelection().addShape(sh);
	};

	protected final GUIVoidCommand selectionAddArc = () -> {
		IShape sh = ShapeFactory.INST.createCircleArc();
		drawing.addShape(sh);
		drawing.getSelection().addShape(sh);
	};

	protected final GUIVoidCommand selectionAddDot = () -> {
		IShape sh = ShapeFactory.INST.createDot(ShapeFactory.INST.createPoint());
		drawing.addShape(sh);
		drawing.getSelection().addShape(sh);
	};

	protected final GUIVoidCommand selectionAddAxes = () -> {
		IShape sh = ShapeFactory.INST.createAxes(ShapeFactory.INST.createPoint());
		drawing.addShape(sh);
		drawing.getSelection().addShape(sh);
	};

	protected final GUIVoidCommand selectionAddBezier = () -> {
		IShape sh = ShapeFactory.INST.createBezierCurve(ShapeFactory.INST.createPoint(), ShapeFactory.INST.createPoint());
		drawing.addShape(sh);
		drawing.getSelection().addShape(sh);
	};

	protected final GUIVoidCommand selectionAddFreehand = () -> {
		IFreehand sh = ShapeFactory.INST.createFreeHand();
		sh.addPoint(ShapeFactory.INST.createPoint());
		sh.addPoint(ShapeFactory.INST.createPoint());
		drawing.addShape(sh);
		drawing.getSelection().addShape(sh);
	};

	protected final GUIVoidCommand selectionAddGrid = () -> {
		IShape sh = ShapeFactory.INST.createGrid(ShapeFactory.INST.createPoint());
		drawing.addShape(sh);
		drawing.getSelection().addShape(sh);
	};

	protected final GUIVoidCommand selectionAddText = () -> {
		IShape sh = ShapeFactory.INST.createText();
		drawing.addShape(sh);
		drawing.getSelection().addShape(sh);
	};

	protected final GUIVoidCommand selectionAddPlot = () -> {
		IShape sh = ShapeFactory.INST.createPlot(ShapeFactory.INST.createPoint(), 1, 10, "x", false);
		drawing.addShape(sh);
		drawing.getSelection().addShape(sh);
	};

	@Override
	@Before
	public void setUp() {
		super.setUp();
		pencil = (Pencil)guiceFactory.call(Pencil.class);
		hand = (Hand)guiceFactory.call(Hand.class);
		drawing = (IDrawing) guiceFactory.call(IDrawing.class);
	}
}
