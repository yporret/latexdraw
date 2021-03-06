/*
  * This file is part of LaTeXDraw.
  * Copyright (c) 2005-2014 Arnaud BLOUIN
  * LaTeXDraw is free software; you can redistribute it and/or modify it under
  * the terms of the GNU General Public License as published by the Free Software
  * Foundation; either version 2 of the License, or (at your option) any later version.
  * LaTeXDraw is distributed without any warranty; without even the implied
  * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * General Public License for more details.
 */
package net.sf.latexdraw.actions.shape;

import java.util.Optional;
import net.sf.latexdraw.actions.Modifying;
import net.sf.latexdraw.actions.ShapeActionImpl;
import net.sf.latexdraw.models.MathUtils;
import net.sf.latexdraw.models.interfaces.shape.IPoint;
import net.sf.latexdraw.models.interfaces.shape.IShape;
import net.sf.latexdraw.util.LangTool;
import org.malai.undo.Undoable;

/**
 * This action increments to rotation angle of shapes.
 */
public class RotateShapes extends ShapeActionImpl<IShape> implements Undoable, Modifying {
	/** The rotation angle to apply. */
	double rotationAngle;

	/** The gravity centre used for the rotation. */
	Optional<IPoint> gc;

	/** The last increment performed on shapes. Used to execute several times the action. */
	double lastRotationAngle;


	public RotateShapes() {
		super();
		gc = Optional.empty();
	}

	@Override
	public boolean canDo() {
		return super.canDo() && gc.isPresent() && MathUtils.INST.isValidCoord(rotationAngle) && MathUtils.INST.isValidPt(gc.get());
	}

	@Override
	public boolean isRegisterable() {
		return true;
	}

	@Override
	public void doActionBody() {
		rotateShapes(rotationAngle - lastRotationAngle);
		lastRotationAngle = rotationAngle;
	}

	/**
	 * Rotates the shape.
	 * @param angleIncrement The increment to add to the rotation angle of the shape.
	 */
	private void rotateShapes(final double angleIncrement) {
		shape.ifPresent(sh -> gc.ifPresent(centre -> {
			sh.addToRotationAngle(centre, angleIncrement);
			sh.setModified(true);
		}));
	}

	@Override
	public void undo() {
		rotateShapes(-rotationAngle);
	}

	@Override
	public void redo() {
		rotateShapes(rotationAngle);
	}

	@Override
	public String getUndoName() {
		return LangTool.INSTANCE.getBundle().getString("Actions.31");
	}

	public void setRotationAngle(final double angle) {
		rotationAngle = angle;
	}

	public double getRotationAngle() {
		return rotationAngle;
	}

	public void setGravityCentre(final IPoint gcpt) {
		gc = Optional.ofNullable(gcpt);
	}

	public Optional<IPoint> getGc() {
		return gc;
	}
}
