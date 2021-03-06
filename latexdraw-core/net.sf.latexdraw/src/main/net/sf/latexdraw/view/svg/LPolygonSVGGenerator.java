package net.sf.latexdraw.view.svg;

import java.text.ParseException;
import net.sf.latexdraw.badaboom.BadaboomCollector;
import net.sf.latexdraw.models.ShapeFactory;
import net.sf.latexdraw.models.interfaces.shape.IPoint;
import net.sf.latexdraw.models.interfaces.shape.IPolygon;
import net.sf.latexdraw.parsers.svg.SVGAttributes;
import net.sf.latexdraw.parsers.svg.SVGDocument;
import net.sf.latexdraw.parsers.svg.SVGElement;
import net.sf.latexdraw.parsers.svg.SVGGElement;
import net.sf.latexdraw.parsers.svg.SVGPathElement;
import net.sf.latexdraw.parsers.svg.SVGPolygonElement;
import net.sf.latexdraw.util.LNamespace;
import net.sf.latexdraw.view.pst.PSTricksConstants;

/**
 * Defines a SVG generator for a polygon.<br>
 *<br>
 * This file is part of LaTeXDraw.<br>
 * Copyright (c) 2005-2015 Arnaud BLOUIN<br>
 *<br>
 *  LaTeXDraw is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.<br>
 *<br>
 *  LaTeXDraw is distributed without any warranty; without even the
 *  implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 *  PURPOSE. See the GNU General Public License for more details.<br>
 *<br>
 * 11/11/07<br>
 * @author Arnaud BLOUIN
 * @version 3.0
 */
class LPolygonSVGGenerator extends LModifiablePointsGenerator<IPolygon> {
	/**
	 * Creates a generator for IPolygon.
	 * @param polygon The source polygon used to generate the SVG element.
	 */
	protected LPolygonSVGGenerator(final IPolygon polygon) {
		super(polygon);
	}


	/**
	 * Creates a latexdraw shape from the given SVG element.
	 * @param elt The source SVG element.
	 * @throws IllegalArgumentException If the given SVG element is not valid.
	 * @since 3.0
	 */
	protected LPolygonSVGGenerator(final SVGPathElement elt) {
		super(ShapeFactory.INST.createPolygon());

		if(elt==null)
			throw new IllegalArgumentException();

		initModifiablePointsShape(elt);
	}



	/**
	 * Creates a polygon from an SVG polygon element.
	 * @param elt The source element.
	 * @since 2.0.0
	 */
	protected LPolygonSVGGenerator(final SVGPolygonElement elt) {
		this(ShapeFactory.INST.createPolygon());

		setSVGModifiablePointsParameters(elt);
		applyTransformations(elt);
	}


	/**
	 * Creates a polygon from a latexdraw-SVG element.
	 * @param elt The source element.
	 * @since 2.0.0
	 */
	protected LPolygonSVGGenerator(final SVGGElement elt, final boolean withTransformation) {
		this(ShapeFactory.INST.createPolygon());

		final SVGElement elt2 = getLaTeXDrawElement(elt, null);

		if(elt==null || !(elt2 instanceof SVGPolygonElement))
			throw new IllegalArgumentException();

		final SVGPolygonElement main = (SVGPolygonElement)elt2;
		setSVGLatexdrawParameters(elt);
		setSVGModifiablePointsParameters(main);
		setSVGShadowParameters(getLaTeXDrawElement(elt, LNamespace.XML_TYPE_SHADOW));
		setSVGDbleBordersParameters(getLaTeXDrawElement(elt, LNamespace.XML_TYPE_DBLE_BORDERS));

		if(withTransformation)
			applyTransformations(elt);
	}



	@Override
	public SVGElement toSVG(final SVGDocument doc) {
		if(doc==null)
			throw new IllegalArgumentException();

		final SVGElement root = new SVGGElement(doc);
		SVGPolygonElement elt;
		final StringBuilder pointsBuilder = new StringBuilder();

        root.setAttribute(LNamespace.LATEXDRAW_NAMESPACE+':'+LNamespace.XML_TYPE, LNamespace.XML_TYPE_POLYGON);
        root.setAttribute(SVGAttributes.SVG_ID, getSVGID());

        for(final IPoint pt : shape.getPoints())
     	   pointsBuilder.append(pt.getX()).append(',').append(pt.getY()).append(' ');

        final String points = pointsBuilder.toString();

        if(shape.hasShadow()) {
        	final SVGPolygonElement shad = new SVGPolygonElement(doc);
        	try { shad.setPoints(points); }catch(final ParseException ex) { BadaboomCollector.INSTANCE.add(ex); }
        	setSVGShadowAttributes(shad, true);
        	root.appendChild(shad);
        }

        if(shape.hasShadow() && !shape.getLineStyle().getLatexToken().equals(PSTricksConstants.LINE_NONE_STYLE)) {
        	// The background of the borders must be filled is there is a shadow.
        	elt = new SVGPolygonElement(doc);
        	try { elt.setPoints(points); }catch(final ParseException ex) { BadaboomCollector.INSTANCE.add(ex); }
        	setSVGBorderBackground(elt, root);
        }

        elt = new SVGPolygonElement(doc);
        try { elt.setPoints(points); }catch(final ParseException ex) { BadaboomCollector.INSTANCE.add(ex); }
        root.appendChild(elt);
        setSVGAttributes(doc, elt, true);
        elt.setAttribute(LNamespace.LATEXDRAW_NAMESPACE +':'+ LNamespace.XML_ROTATION, String.valueOf(shape.getRotationAngle()));

        if(shape.hasDbleBord()) {
        	final SVGPolygonElement dblBord = new SVGPolygonElement(doc);
        	try { dblBord.setPoints(points); }catch(final ParseException ex) { BadaboomCollector.INSTANCE.add(ex); }
        	setSVGDoubleBordersAttributes(dblBord);
        	root.appendChild(dblBord);
        }

        return root;
	}
}
