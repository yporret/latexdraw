/*
 * This file is part of LaTeXDraw.
 * Copyright (c) 2005-2016 Arnaud BLOUIN
 * LaTeXDraw is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * LaTeXDraw is distributed without any warranty; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 */
package net.sf.latexdraw.instruments;

import com.google.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import net.sf.latexdraw.actions.shape.DeleteShapes;
import net.sf.latexdraw.actions.shape.SelectShapes;
import net.sf.latexdraw.models.interfaces.shape.IShape;
import org.malai.action.ActionsRegistry;
import org.malai.javafx.instrument.JfxInteractor;
import org.malai.javafx.interaction.JfxInteraction;
import org.malai.javafx.interaction.library.ButtonPressed;
import org.malai.javafx.interaction.library.KeyPressure;


/**
 * This instrument deletes the selected shapes.
 * @author Arnaud BLOUIN
 */
public class ShapeDeleter extends CanvasInstrument implements Initializable {
	/** The button used to remove the selected shapes. */
	@FXML protected Button deleteB;

	@Inject protected Hand hand;


	/**
	 * Creates the instrument.
	 */
	public ShapeDeleter() {
		super();
	}

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		setActivated(false);

		((ObservableList<IShape>)canvas.getDrawing().getSelection().getShapes()).addListener(
			(ListChangeListener.Change<? extends IShape> evt) -> setActivated(hand.isActivated() && !evt.getList().isEmpty()));
	}


	@Override
	public void setActivated(final boolean activ) {
		super.setActivated(activ);
		updateWidgets(false);
	}

	/**
	 * Updates the widgets of this instrument.
	 * @param hideWidgets True: the widgets are hidden on deactivation.
	 * @since 3.0
	 */
	protected void updateWidgets(final boolean hideWidgets) {
		deleteB.setVisible(activated || !hideWidgets);
		deleteB.setDisable(!activated);
	}

	@Override
	protected void initialiseInteractors() throws InstantiationException, IllegalAccessException {
		addInteractor(new DeleteShapesInteractor<>(this, ButtonPressed.class, deleteB));
		addInteractor(new DeleteShapesInteractor<KeyPressure>(this, KeyPressure.class, canvas) {
			@Override
			public boolean isConditionRespected() {
				return interaction.getKeyCode().isPresent() && interaction.getKeyCode().get() == KeyCode.DELETE && super.isConditionRespected();
			}
		});
	}


	/** This abstract link maps an interaction to an action that delete shapes. */
	private static class DeleteShapesInteractor<I extends JfxInteraction> extends JfxInteractor<DeleteShapes, I, ShapeDeleter> {
		DeleteShapesInteractor(final ShapeDeleter ins, final Class<I> clazzInteraction, Node widget) throws InstantiationException, IllegalAccessException {
			super(ins, false, DeleteShapes.class, clazzInteraction, widget);
		}

		@Override
		public void initAction() {
			final SelectShapes selection = ActionsRegistry.INSTANCE.getAction(SelectShapes.class);
			selection.getShapes().forEach(sh -> action.addShape(sh));
			action.setDrawing(selection.getDrawing().get());
		}

		@Override
		public boolean isConditionRespected() {
			final SelectShapes selection = ActionsRegistry.INSTANCE.getAction(SelectShapes.class);
			return selection != null && !selection.getShapes().isEmpty();
		}
	}
}

