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
package net.sf.latexdraw.actions;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.bmp.BMPImageWriteParam;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import net.sf.latexdraw.badaboom.BadaboomCollector;
import net.sf.latexdraw.ui.dialog.ExportDialog;
import net.sf.latexdraw.util.LangTool;
import net.sf.latexdraw.view.pst.PSTCodeGenerator;
import org.malai.action.ActionImpl;

/**
 * This action allows to export a drawing in different formats.
 */
public class Export extends ActionImpl {
	/** The format with which the drawing must be exported. */
	protected ExportFormat format;

	//	/** The canvas that contains views. */
	//	protected ICanvas canvas;

	/** Defines if the shapes have been successfully exported. */
	protected boolean exported;

	/** The dialogue chooser used to select the targeted file. */
	protected ExportDialog dialogueBox;

	/** The PST generator to use. */
	protected PSTCodeGenerator pstGen;


	/**
	 * Creates the action.
	 * @since 3.0
	 */
	public Export() {
		super();
		exported = false;
	}

	@Override
	public void flush() {
		super.flush();
		//		canvas 			= null;
		format = null;
		dialogueBox = null;
	}

	@Override
	public boolean isRegisterable() {
		return false;
	}

	@Override
	protected void doActionBody() {
		// Showing the dialog.
		final int response = dialogueBox.showSaveDialog(null);
		File f = dialogueBox.getSelectedFile();

		exported = true;

		// Analysing the result of the dialog.
		if(response != JFileChooser.APPROVE_OPTION || f == null) exported = false;
		else {
			if(!f.getName().toLowerCase().endsWith(format.getFileExtension().toLowerCase()))
				f = new File(f.getPath() + format.getFileExtension());

			if(f.exists()) {
				final int replace = JOptionPane.showConfirmDialog(null, LangTool.INSTANCE.getBundle().getString("LaTeXDrawFrame.173"), //$NON-NLS-1$
					LangTool.INSTANCE.getBundle().getString("Exporter.1"), JOptionPane.YES_NO_OPTION);

				if(replace == JOptionPane.NO_OPTION) exported = false; // The user doesn't want to replace the file
			}
		}

		if(exported) exported = export(f);
	}

	protected boolean export(final File file) {
		switch(format) {
			case BMP: return exportAsBMP(file);
			case EPS_LATEX: return exportAsEPS(file);
			case JPG: 		return exportAsJPG(file);
			case PDF: 		return exportAsPDF(file);
			case PDF_CROP: 	return exportAsPDF(file);
			case PNG: 		return exportAsPNG(file);
			case TEX: 		return exportAsPST(file);
		}
		return false;
	}


	@Override
	public boolean canDo() {
		return false;
		//		return canvas!=null && format!=null && dialogueBox!=null &&
		//				(format==ExportFormat.BMP || format==ExportFormat.JPG || format==ExportFormat.PNG || pstGen!=null);
	}


	@Override
	public boolean hadEffect() {
		return super.hadEffect() && exported;
	}


	/**
	 * Exports the drawing as a PNG picture.
	 * @param file The targeted location.
	 * @return true if the picture was well created.
	 */
	protected boolean exportAsPNG(final File file) {
		final BufferedImage rendImage = createRenderedImage();
		boolean success = false;

		try {
			ImageIO.write(rendImage, "png", file);  //$NON-NLS-1$
			success = true;
		}catch(final IOException e) { BadaboomCollector.INSTANCE.add(e); }
		rendImage.flush();
		return success;
	}


	/**
	 * Exports the drawing as a JPG picture.
	 * @param file The targeted location.
	 * @return true if the picture was well created.
	 */
	protected boolean exportAsJPG(final File file) {
		final BufferedImage rendImage = createRenderedImage();
		boolean success = false;

		try {
			final ImageWriteParam iwparam = new JPEGImageWriteParam(Locale.getDefault());
			final ImageWriter iw = ImageIO.getImageWritersByFormatName("jpg").next();//$NON-NLS-1$
			try(final ImageOutputStream ios = ImageIO.createImageOutputStream(file)) {
				iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				iwparam.setCompressionQuality(1f - dialogueBox.getCompressionRate() / 100f);
				iw.setOutput(ios);
				iw.write(null, new IIOImage(rendImage, null, null), iwparam);
				iw.dispose();
				success = true;
			}
		}catch(final IOException e) { BadaboomCollector.INSTANCE.add(e); }
		rendImage.flush();
		return success;
	}


	/**
	 * Creates a ps document of the given views (compiled using latex).
	 * @param file The targeted location.
	 * @return True: the file has been created.
	 * @since 3.0
	 */
	protected boolean exportAsEPS(final File file) {
		//		File psFile;
		//
		//		try{
		//			psFile = LaTeXGenerator.createEPSFile(canvas.getDrawing(), file.getAbsolutePath(), canvas, pstGen);
		//		}catch(final Exception e) {
		//			BadaboomCollector.INSTANCE.add(e);
		//			psFile = null;
		//		}
		//
		//		return psFile!=null && psFile.exists();
		return false;
	}


	/**
	 * Creates a pdf document of the given views (compiled using latex).
	 * @param file The targeted location.
	 * @return True: the file has been created.
	 * @since 3.0
	 */
	protected boolean exportAsPDF(final File file) {
		//		File pdfFile;
		//
		//		try{
		//			pdfFile = LaTeXGenerator.createPDFFile(canvas.getDrawing(), file.getAbsolutePath(), canvas, format==ExportFormat.PDF_CROP, pstGen);
		//		} catch(final Exception e) {
		//			BadaboomCollector.INSTANCE.add(e);
		//			pdfFile = null;
		//		}
		//
		//		return pdfFile!=null && pdfFile.exists();
		return false;
	}


	/**
	 * Exports the drawing as a PST document.
	 * @param file The targeted location.
	 * @return true if the PST document was been successfully created.
	 */
	protected boolean exportAsPST(final File file) {
		boolean ok;

		//		try {
		//			try(final FileWriter fw 	= new FileWriter(file);
		//				final BufferedWriter bw = new BufferedWriter(fw);
		//				final PrintWriter out 	= new PrintWriter(bw)) {
		//				out.println(LaTeXGenerator.getLatexDrawing(pstGen));
		//				ok = true;
		//			}
		//		}catch(final IOException e) {
		//			BadaboomCollector.INSTANCE.add(e);
		ok = false;
		//		}
		return ok;
	}


	/**
	 * Exports the drawing as a BMP picture.
	 * @param file The targeted location.
	 * @return true if the picture was successfully created.
	 */
	protected boolean exportAsBMP(final File file) {
		final BufferedImage rendImage = createRenderedImage();
		boolean success = false;

		try {
			final ImageWriteParam iwparam = new BMPImageWriteParam();
			final ImageWriter iw = ImageIO.getImageWritersByFormatName("bmp").next();//$NON-NLS-1$
			try(final ImageOutputStream ios = ImageIO.createImageOutputStream(file)) {
				iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				iw.setOutput(ios);
				iw.write(null, new IIOImage(rendImage, null, null), iwparam);
				iw.dispose();
				success = true;
			}
		}catch(final IOException e) { BadaboomCollector.INSTANCE.add(e); }
		rendImage.flush();
		return success;
	}


	/**
	 * @return A buffered image that contains given views (not null).
	 * @since 3.0
	 */
	protected BufferedImage createRenderedImage() {
		//		final IPoint tr 	= canvas.getTopRightDrawingPoint();
		//		final IPoint bl 	= canvas.getBottomLeftDrawingPoint();
		//		final int width		= (int)Math.abs(tr.getX()-bl.getX());
		//		final int height 	= (int)Math.abs(bl.getY()-tr.getY());
		//		final BufferedImage bi 	= new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		//		final Graphics2D graphic 	= bi.createGraphics();
		//
		//		graphic.setColor(Color.WHITE);
		//		graphic.fillRect(0, 0, width, height);
		//		graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//		graphic.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		//		graphic.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		//		graphic.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		//		graphic.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		//
		//		graphic.translate(-bl.getX(), -tr.getY());
		//
		//		synchronized(canvas.getViews()){
		//			for(final IViewShape view : canvas.getViews())
		//				view.paint(graphic, null);
		//		}
		//
		//		graphic.dispose();
		//		return bi;
		return null;
	}


	/**
	 * @param dialogBox The file chooser to set.
	 * @since 3.0
	 */
	public void setDialogueBox(final ExportDialog dialogBox) {
		dialogueBox = dialogBox;
	}


	/**
	 * @param expFormat The expFormat to set.
	 * @since 3.0
	 */
	public void setFormat(final ExportFormat expFormat) {
		format = expFormat;
	}


	//	/**
	//	 * @param theCanvas The theCanvas to set.
	//	 * @since 3.0
	//	 */
	//	public void setCanvas(final ICanvas theCanvas) {
	//		canvas = theCanvas;
	//	}


	/**
	 * @param gen The PST generator to use for latex, ps, or pdf exports.
	 * @since 3.0
	 */
	public void setPstGen(final PSTCodeGenerator gen) {
		pstGen = gen;
	}
}
