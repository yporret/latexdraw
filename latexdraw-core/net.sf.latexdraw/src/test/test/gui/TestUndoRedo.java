package test.gui;

import com.google.inject.AbstractModule;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.*;
import net.sf.latexdraw.instruments.EditionChoice;
import net.sf.latexdraw.instruments.Hand;
import net.sf.latexdraw.instruments.Pencil;
import net.sf.latexdraw.instruments.ShapeBorderCustomiser;
import net.sf.latexdraw.models.ShapeFactory;
import net.sf.latexdraw.models.interfaces.shape.IGroup;
import net.sf.latexdraw.models.interfaces.shape.IRectangle;
import net.sf.latexdraw.view.jfx.Canvas;
import net.sf.latexdraw.view.jfx.PageView;
import net.sf.latexdraw.view.jfx.ViewRectangle;
import net.sf.latexdraw.view.latex.DviPsColors;
import test.gui.robot.FxRobotDnD;

import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.stream.IntStream;

import javafx.scene.Node;

public class TestUndoRedo extends TestLatexdrawGUI {
	Pencil pencil;
	Hand hand;
	Canvas canvas;
	IRectangle addedRec;

	
	@Override
	public String getFXMLPathFromLatexdraw() {
		return "/fxml/UITest.fxml";
	}

	@Override
	protected AbstractModule createModule() {
		return new ShapePropModule() {
			@Override
			protected void configure() {
				super.configure();
				//this.pencil = mock(Pencil.class);
				bind(ShapeBorderCustomiser.class).asEagerSingleton();
				bind(Hand.class).asEagerSingleton();
				bind(Canvas.class).asEagerSingleton();
				//bind(Pencil.class).toInstance(this.pencil);
				bind(Pencil.class).asEagerSingleton();
			}
		};
	}

	@Override
	@Before
	public void setUp() {
		super.setUp();
		IGroup groupParams = ShapeFactory.INST.createGroup();
		groupParams.addShape(ShapeFactory.INST.createRectangle());
		pencil = (Pencil)guiceFactory.call(Pencil.class);
		hand = (Hand)guiceFactory.call(Hand.class);

		hand.setActivated(false);
		pencil.setActivated(true);
		//when(pencil.isActivated()).thenReturn(true);
		pencil.setCurrentChoice(EditionChoice.RECT);
		//when(pencil.getCurrentChoice()).thenReturn(EditionChoice.RECT);
		//when(pencil.getGroupParams()).thenReturn(groupParams);

		//canvas = find("#canvas");
		canvas = lookup("#canvas").query();
	}

	
	public class Robot extends FxRobot implements FxRobotDnD {
	}
	
	//cf : http://torgen-engineering.blogspot.fr/2015/12/gui-testing-how-to-compare-javafx-gui.html
	private double computeSnapshotSimilarity(final Image image1, final Image image2) {
		final int width = (int) image1.getWidth();
		final int height = (int) image1.getHeight();
		final PixelReader reader1 = image1.getPixelReader();
		final PixelReader reader2 = image2.getPixelReader();

		final double nbNonSimilarPixels = IntStream.range(0, width).parallel().
			mapToLong(i -> IntStream.range(0, height).parallel().filter(j -> reader1.getArgb(i, j) != reader2.getArgb(i, j)).count()).sum();

		return 100d - nbNonSimilarPixels / (width * height) * 100d;
	}
	
	
	@Test
	public void test() {
		
		FxRobotDnD robot = new Robot();
		
		Point2D ori = point("#canvas").query();
		Point2D dest = point("#canvas").atOffset(100,100).query();
		//clickOn("#recB");
		MouseButton button = MouseButton.PRIMARY;
		robot.dndFromPos(ori, dest, button);

		ori = point("#canvas").atOffset(-100,-100).query();
		dest = point("#canvas").atOffset(-150,100).query();
		robot.dndFromPos(ori, dest, button);
		//SnapshotParameters params = new SnapshotParameters();
		WritableImage image2Rec = new WritableImage((int)canvas.getWidth(),(int)canvas.getHeight());
		//image2Rec = canvas.snapshot(null, image2Rec);
		Platform.runLater(() -> canvas.snapshot(new SnapshotParameters(), image2Rec));
		WaitForAsyncUtils.waitForFxEvents();
		assertNotNull(image2Rec);
		
		ori = point("#canvas").atOffset(100,-100).query();
		dest = point("#canvas").atOffset(150,150).query();
		robot.dndFromPos(ori, dest, button);
		//sleep(500);
		
		clickOn("#undoB");
		WritableImage undoImage = new WritableImage((int)canvas.getWidth(),(int)canvas.getHeight());
		Platform.runLater(() -> canvas.snapshot(new SnapshotParameters(), undoImage));
		WaitForAsyncUtils.waitForFxEvents();
		assertEquals("The result of the Undo differ", 100d, computeSnapshotSimilarity(undoImage, image2Rec), 0.0);

		
		clickOn("#undoB");
		//sleep(100);
		
		
		clickOn("#redoB");
		WritableImage redoImage = new WritableImage((int)canvas.getWidth(),(int)canvas.getHeight());
		Platform.runLater(() -> canvas.snapshot(new SnapshotParameters(), redoImage));
		WaitForAsyncUtils.waitForFxEvents();
		assertEquals("The result of the Redo differ", 100d, computeSnapshotSimilarity(redoImage, image2Rec), 0.0);
		
	}
}
