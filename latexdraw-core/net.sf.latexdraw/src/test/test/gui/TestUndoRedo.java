package test.gui;

import com.google.inject.AbstractModule;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.*;
import javafx.embed.swing.SwingFXUtils;
import net.sf.latexdraw.LaTeXDraw;
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

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import javafx.scene.Node;

public class TestUndoRedo extends TestLatexdrawGUI {
	Pencil pencil;
	Hand hand;
	Canvas canvas;
	IRectangle addedRec;
	Image image2Rec;

	public class Robot extends FxRobot implements FxRobotDnD {
	}
	
	//cf : http://torgen-engineering.blogspot.fr/2015/12/gui-testing-how-to-compare-javafx-gui.html
	private double computeSnapshotSimilarity(final Image image1) {
		String file = LaTeXDraw.class.getResource("/snapshot/testUndoredo.png").getFile();
		try {
			Image  image2 = new Image(new File(file).toURI().toURL().toExternalForm());
			final int width = (int) image1.getWidth();
			final int height = (int) image1.getHeight();
			final PixelReader reader1 = image1.getPixelReader();
			final PixelReader reader2 = image2.getPixelReader();

			final double nbNonSimilarPixels = IntStream.range(0, width).parallel().
				mapToLong(i -> IntStream.range(0, height).parallel().filter(j -> reader1.getArgb(i, j) != reader2.getArgb(i, j)).count()).sum();

			return 100d - nbNonSimilarPixels / (width * height) * 100d;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return 0.0;
	}
	
	private WritableImage createSnapshot(){
		WritableImage image = new WritableImage((int)canvas.getWidth(),(int)canvas.getHeight());
		Platform.runLater(() -> canvas.snapshot(new SnapshotParameters(), image));
		WaitForAsyncUtils.waitForFxEvents();
		return image;
	}

	Group getPane() {
		return (Group)canvas.getChildren().get(1);
	}
	
	@Override
	public String getFXMLPathFromLatexdraw() {
		return "/fxml/TestUndoRedo.fxml";
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
		pencil = (Pencil)guiceFactory.call(Pencil.class);
		hand = (Hand)guiceFactory.call(Hand.class);

		hand.setActivated(false);
		pencil.setActivated(true);
		//when(pencil.isActivated()).thenReturn(true);
		pencil.setCurrentChoice(EditionChoice.RECT);
		//when(pencil.getCurrentChoice()).thenReturn(EditionChoice.RECT);
		
		canvas = lookup("#canvas").query();
		
		
		FxRobotDnD robot = new Robot();
		
		Point2D ori = point("#canvas").query();
		Point2D dest = point("#canvas").atOffset(100,100).query();
		MouseButton button = MouseButton.PRIMARY;
		robot.dndFromPos(ori, dest, button);

		ori = point("#canvas").atOffset(-100,-100).query();
		dest = point("#canvas").atOffset(-150,100).query();
		robot.dndFromPos(ori, dest, button);
		/*image2Rec = createSnapshot();

	    try {
			ImageIO.write(SwingFXUtils.fromFXImage(image2Rec, null), "png", new File("src/resources/test/snapshot/testUndoredo.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		ori = point("#canvas").atOffset(100,-100).query();
		dest = point("#canvas").atOffset(150,150).query();
		robot.dndFromPos(ori, dest, button);
	}

	
	@Test
	public void DemoGuiTesting() {
		clickOn("#undoB");
		WritableImage undoImage = createSnapshot();
		assertEquals("The result of the Undo differ", 100d, computeSnapshotSimilarity(undoImage), 0.0);

		
		clickOn("#undoB");
		
		
		clickOn("#redoB");
		WritableImage redoImage = createSnapshot();
		assertEquals("The result of the Redo differ", 100d, computeSnapshotSimilarity(redoImage), 0.0);
		
	}

	@Test
	public void testUndoModel(){
		Group group = getPane();
		int prev = group.getChildren().size();
		clickOn("#undoB");
		int now = group.getChildren().size();
		assertEquals(prev-1, now);
	}
	@Test
	public void testRedoModel(){
		Group group = getPane();
		int beforeUndo = group.getChildren().size();
		
		clickOn("#undoB");
		int afterUndo = group.getChildren().size();
		
		clickOn("#redoB");
		int now = group.getChildren().size();
		assertEquals(beforeUndo, now);
		assertEquals(afterUndo+1, now);
		assertTrue(group.getChildren().get(now-1) instanceof ViewRectangle);
		
	}

	@Test
	public void testUndoSnapshot(){
		clickOn("#undoB");
		WritableImage undoImage = createSnapshot();
		assertEquals("The result of the Undo differ", 100d, computeSnapshotSimilarity(undoImage), 0.0);
	}
	@Test
	public void testRedoSnapshot(){
		clickOn("#undoB");
		clickOn("#undoB");
		
		
		clickOn("#redoB");
		WritableImage redoImage = createSnapshot();
		assertEquals("The result of the Redo differ", 100d, computeSnapshotSimilarity(redoImage), 0.0);
		
	}
}
