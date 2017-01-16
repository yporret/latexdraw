package test.gui;

import com.google.inject.AbstractModule;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import net.sf.latexdraw.instruments.Hand;
import net.sf.latexdraw.instruments.Pencil;
import net.sf.latexdraw.instruments.ShapeBorderCustomiser;
import net.sf.latexdraw.models.ShapeFactory;
import net.sf.latexdraw.models.interfaces.shape.IRectangle;
import net.sf.latexdraw.view.jfx.Canvas;
import net.sf.latexdraw.view.jfx.PageView;
import net.sf.latexdraw.view.jfx.ViewRectangle;
import net.sf.latexdraw.view.latex.DviPsColors;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestUndoRedo extends TestLatexdrawGUI {
	Pencil pencil;
	Hand hand;
	Canvas canvas;
	IRectangle addedRec;

	final GUIVoidCommand addRec = () -> Platform.runLater(() -> {
		addedRec = ShapeFactory.INST.createRectangle(ShapeFactory.INST.createPoint(-Canvas.ORIGIN.getX(), -Canvas.ORIGIN.getY()), 100, 100);
		addedRec.setFilled(true);
		addedRec.setFillingCol(DviPsColors.APRICOT);
		canvas.getDrawing().addShape(addedRec);
	});
	
	final GUIVoidCommand addRec2 = () -> Platform.runLater(() -> {
		IRectangle rec = ShapeFactory.INST.createRectangle(ShapeFactory.INST.createPoint(-Canvas.ORIGIN.getX()+300, -Canvas.ORIGIN.getY()+300), 100, 100);
		rec.setFilled(true);
		rec.setFillingCol(DviPsColors.APRICOT);
		canvas.getDrawing().addShape(rec);
	});

	final GUIVoidCommand clickOnAddedRec = () -> rightClickOn(new Point2D(50, 50));
	
	final GUIVoidCommand ctrlClickOnAddedRec2 = () -> press(KeyCode.CONTROL).rightClickOn(new Point2D(330, 350)).release(KeyCode.CONTROL);

	final GUIVoidCommand shiftClickOnAddedRec = () -> press(KeyCode.SHIFT).rightClickOn(new Point2D(55, 55)).release(KeyCode.SHIFT);

	@Override
	public String getFXMLPathFromLatexdraw() {
		return "/fxml/Canvas.fxml";
	}

	@Override
	protected AbstractModule createModule() {
		return new ShapePropModule() {
			@Override
			protected void configure() {
				super.configure();
				this.pencil = mock(Pencil.class);
				bind(ShapeBorderCustomiser.class).asEagerSingleton();
				bind(Hand.class).asEagerSingleton();
				bind(Canvas.class).asEagerSingleton();
				bind(Pencil.class).toInstance(this.pencil);
			}
		};
	}

	@Override
	@Before
	public void setUp() {
		super.setUp();
		pencil = (Pencil)guiceFactory.call(Pencil.class);
		hand = (Hand)guiceFactory.call(Hand.class);

		hand.setActivated(true);
		when(pencil.isActivated()).thenReturn(false);

		canvas = lookup("#canvas").query();
	}

	Group getPane() {
		return (Group)canvas.getChildren().get(1);
	}

	
	
	@Test
	public void test() {
		sleep(2000);
		assertTrue(true);
	}
}
