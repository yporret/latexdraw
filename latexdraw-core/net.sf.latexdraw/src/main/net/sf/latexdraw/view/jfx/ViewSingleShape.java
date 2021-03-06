/*
 * This file is part of LaTeXDraw<br>
 * Copyright (c) 2005-2015 Arnaud BLOUIN<br>
 * <br>
 * LaTeXDraw is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.<br>
 * <br>
 * LaTeXDraw is distributed without any warranty; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.<br>
 */
package net.sf.latexdraw.view.jfx;

import java.awt.geom.Point2D;
import java.util.Optional;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import net.sf.latexdraw.models.MathUtils;
import net.sf.latexdraw.models.ShapeFactory;
import net.sf.latexdraw.models.interfaces.shape.Color;
import net.sf.latexdraw.models.interfaces.shape.FillingStyle;
import net.sf.latexdraw.models.interfaces.shape.ILine;
import net.sf.latexdraw.models.interfaces.shape.IPoint;
import net.sf.latexdraw.models.interfaces.shape.ISingleShape;
import net.sf.latexdraw.models.interfaces.shape.LineStyle;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * The base class of a JFX single shape view.
 * @param <S> The type of the model.
 * @param <T> The type of the JFX shape used to draw the view.
 */
public abstract class ViewSingleShape<S extends ISingleShape, T extends Shape> extends ViewShape<S> {
	protected final @NonNull T border = createJFXShape();
	protected final @Nullable T dblBorder;

	private final ChangeListener<?> strokesUpdateCall = (obj, oldVal, newVal) -> updateStrokes();
	private final ChangeListener<?> fillUpdateCall = (obs, oldVal, newVal) -> border.setFill(getFillingPaint(model.getFillingStyle()));


	/**
	 * Creates the view.
	 * @param sh The model.
	 */
	ViewSingleShape(final @NonNull S sh) {
		super(sh);

		if(model.isDbleBorderable()) {
			dblBorder = createJFXShape();
			dblBorder.setFill(null);
			dblBorder.layoutXProperty().bind(border.layoutXProperty());
			dblBorder.layoutYProperty().bind(border.layoutYProperty());
			model.dbleBordProperty().addListener((ChangeListener<? super Boolean>) strokesUpdateCall);
			model.dbleBordSepProperty().addListener((ChangeListener<? super Number>) strokesUpdateCall);
			model.dbleBordColProperty().addListener((ChangeListener<? super Color>) strokesUpdateCall);
		} else {
			dblBorder = null;
		}

		getChildren().add(border);
		getChildren().add(dblBorder);

		border.setStrokeLineJoin(StrokeLineJoin.MITER);

		if(model.isThicknessable()) {
			model.thicknessProperty().addListener((ChangeListener<? super Number>) strokesUpdateCall);
		}

		if(model.isLineStylable()) {
			model.linestyleProperty().addListener((ChangeListener<? super LineStyle>) strokesUpdateCall);
			model.dashSepBlackProperty().addListener((ChangeListener<? super Number>) strokesUpdateCall);
			model.dashSepWhiteProperty().addListener((ChangeListener<? super Number>) strokesUpdateCall);
			model.dotSepProperty().addListener((ChangeListener<? super Number>) strokesUpdateCall);
		}

		if(model.isFillable()) {
			model.fillingProperty().addListener((ChangeListener<? super FillingStyle>) fillUpdateCall);
			model.gradColStartProperty().addListener((ChangeListener<? super Color>) fillUpdateCall);
			model.gradColEndProperty().addListener((ChangeListener<? super Color>) fillUpdateCall);
			model.gradMidPtProperty().addListener((ChangeListener<? super Number>) fillUpdateCall);
			model.gradAngleProperty().addListener((ChangeListener<? super Number>) fillUpdateCall);
			model.fillingColProperty().addListener((ChangeListener<? super Color>) fillUpdateCall);
			border.setFill(getFillingPaint(model.getFillingStyle()));
		}

		border.strokeProperty().bind(Bindings.createObjectBinding(() -> model.getLineColour().toJFX(), model.lineColourProperty()));

		bindBorderMovable();
		updateStrokes();
	}

	protected abstract @NonNull T createJFXShape();

	private Paint getFillingPaint(final FillingStyle style) {
		switch(style) {
			case NONE:
				if(model.hasShadow() && model.shadowFillsShape()) return model.getFillingCol().toJFX();
				return null;
			case PLAIN: return model.getFillingCol().toJFX();
			case GRAD: return computeGradient();
			case CLINES_PLAIN:
			case HLINES_PLAIN:
			case VLINES_PLAIN:
			case CLINES:
			case VLINES:
			case HLINES: return null;
			default: return null;
		}
	}

	private LinearGradient computeGradient() {
		final IPoint tl = model.getTopLeftPoint();
		final IPoint br = model.getBottomRightPoint();
		IPoint pt1 = ShapeFactory.INST.createPoint((tl.getX() + br.getX()) / 2d, tl.getY());
		IPoint pt2 = ShapeFactory.INST.createPoint((tl.getX() + br.getX()) / 2d, br.getY());
		double angle = model.getGradAngle() % (2d * Math.PI);
		double gradMidPt = model.getGradMidPt();

		if(angle < 0d) angle = 2d * Math.PI + angle;

		if(angle >= Math.PI) {
			gradMidPt = 1d - gradMidPt;
			angle -= Math.PI;
		}

		if(MathUtils.INST.equalsDouble(angle, 0d)) {
			if(gradMidPt < 0.5) pt1.setY(pt2.getY() - Point2D.distance(pt2.getX(), pt2.getY(), (tl.getX() + br.getX()) / 2d, br.getY()));

			pt2.setY(tl.getY() + (br.getY() - tl.getY()) * gradMidPt);
		}else {
			if(MathUtils.INST.equalsDouble(angle % (Math.PI / 2d), 0d)) {
				pt1 = ShapeFactory.INST.createPoint(tl.getX(), (tl.getY() + br.getY()) / 2d);
				pt2 = ShapeFactory.INST.createPoint(br.getX(), (tl.getY() + br.getY()) / 2d);

				if(gradMidPt < 0.5)
					pt1.setX(pt2.getX() - Point2D.distance(pt2.getX(), pt2.getY(), br.getX(), (tl.getY() + br.getY()) / 2d));

				pt2.setX(tl.getX() + (br.getX() - tl.getX()) * gradMidPt);
			}else {
				final IPoint cg = model.getGravityCentre();
				final ILine l2;
				final ILine l;

				pt1 = pt1.rotatePoint(cg, -angle);
				pt2 = pt2.rotatePoint(cg, -angle);
				l = ShapeFactory.INST.createLine(pt1, pt2);

				if(angle >= 0d && angle < Math.PI / 2d) l2 = l.getPerpendicularLine(tl);
				else l2 = l.getPerpendicularLine(ShapeFactory.INST.createPoint(tl.getX(), br.getY()));

				pt1 = l.getIntersection(l2);
				final double distance = Point2D.distance(cg.getX(), cg.getY(), pt1.getX(), pt1.getY());
				l.setX1(pt1.getX());
				l.setY1(pt1.getY());
				final IPoint[] pts = l.findPoints(pt1, 2d * distance * gradMidPt);
				pt2 = pts[0];

				if(gradMidPt < 0.5) pt1 = pt1.rotatePoint(model.getGravityCentre(), Math.PI);
			}
		}

		return new LinearGradient(pt1.getX(), pt1.getY(), pt2.getX(), pt2.getY(), false, CycleMethod.NO_CYCLE,
			new Stop(0d, model.getGradColStart().toJFX()), new Stop(1d, model.getGradColEnd().toJFX()));
	}

	private void bindBorderMovable() {
		if(model.isBordersMovable()) {
			border.strokeTypeProperty().bind(Bindings.createObjectBinding(() -> {
				switch(model.getBordersPosition()) {
					case INTO: return StrokeType.INSIDE;
					case MID: return StrokeType.CENTERED;
					case OUT: return StrokeType.OUTSIDE;
					default: return StrokeType.INSIDE;
				}
			}, model.borderPosProperty()));

			if(dblBorder != null) {
				dblBorder.strokeTypeProperty().bind(border.strokeTypeProperty());
			}
		}
	}

	protected double getDbleBorderGap() {
		if(!model.isDbleBorderable()) {
			return 0d;
		}

		switch(model.getBordersPosition()) {
			case MID: return 0d;
			case INTO: return model.getThickness();
			case OUT: return -model.getThickness();
		}

		return 0d;
	}

	private void updateStrokes() {
		if(model.isThicknessable()) {
			border.setStrokeWidth(model.getFullThickness());
		}

		if(dblBorder != null) {
			dblBorder.setDisable(!model.hasDbleBord());
			if(model.hasDbleBord()) {
				dblBorder.setStroke(model.getDbleBordCol().toJFX());
				dblBorder.setStrokeWidth(model.getDbleBordSep());
			}
		}

		if(model.isLineStylable()) {
			switch(model.getLineStyle()) {
				case DASHED:
					border.setStrokeLineCap(StrokeLineCap.BUTT);
					border.getStrokeDashArray().clear();
					border.getStrokeDashArray().addAll(model.getDashSepBlack(), model.getDashSepWhite());
					break;
				case DOTTED:// FIXME problem when dotted line + INTO/OUT border position.
					border.setStrokeLineCap(StrokeLineCap.ROUND);
					border.getStrokeDashArray().clear();
					border.getStrokeDashArray().addAll(0d, model.getDotSep() + model.getFullThickness());
					break;
				case SOLID:
					border.setStrokeLineCap(StrokeLineCap.SQUARE);
					border.getStrokeDashArray().clear();
					break;
			}
		}
	}

	public T getBorder() {
		return border;
	}

	public Optional<T> getDbleBorder() {
		return Optional.ofNullable(dblBorder);
	}

	@Override
	public void flush() {
		super.flush();
		if(model.isThicknessable()) {
			model.thicknessProperty().removeListener((ChangeListener<? super Number>) strokesUpdateCall);
		}

		if(model.isLineStylable()) {
			model.linestyleProperty().removeListener((ChangeListener<? super LineStyle>) strokesUpdateCall);
			model.dashSepBlackProperty().removeListener((ChangeListener<? super Number>) strokesUpdateCall);
			model.dashSepWhiteProperty().removeListener((ChangeListener<? super Number>) strokesUpdateCall);
			model.dotSepProperty().removeListener((ChangeListener<? super Number>) strokesUpdateCall);
		}

		if(dblBorder!=null) {
			dblBorder.layoutXProperty().unbind();
			dblBorder.layoutYProperty().unbind();
			model.dbleBordProperty().removeListener((ChangeListener<? super Boolean>) strokesUpdateCall);
			model.dbleBordSepProperty().removeListener((ChangeListener<? super Number>) strokesUpdateCall);
			model.dbleBordColProperty().removeListener((ChangeListener<? super Color>) strokesUpdateCall);
			dblBorder.strokeTypeProperty().unbind();
		}

		if(model.isFillable()) {
			model.fillingProperty().removeListener((ChangeListener<? super FillingStyle>) fillUpdateCall);
			model.gradColStartProperty().removeListener((ChangeListener<? super Color>) fillUpdateCall);
			model.gradColEndProperty().removeListener((ChangeListener<? super Color>) fillUpdateCall);
			model.gradMidPtProperty().removeListener((ChangeListener<? super Number>) fillUpdateCall);
			model.gradAngleProperty().removeListener((ChangeListener<? super Number>) fillUpdateCall);
			model.gradColEndProperty().removeListener((ChangeListener<? super Color>) fillUpdateCall);
			model.fillingColProperty().removeListener((ChangeListener<? super Color>) fillUpdateCall);
		}

		border.strokeProperty().unbind();

		if(model.isBordersMovable()) {
			border.strokeTypeProperty().unbind();
		}
	}
}
