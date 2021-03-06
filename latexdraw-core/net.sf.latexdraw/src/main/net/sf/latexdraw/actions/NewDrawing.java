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

import java.io.File;
import javax.swing.JFileChooser;
import net.sf.latexdraw.instruments.PreferencesSetter;
import org.malai.action.ActionImpl;

/**
 * This action permits to create a new drawing and initialises the application as required.
 */
public class NewDrawing extends ActionImpl implements Modifying {// IOAction<LFrame, JLabel>
	/** The file chooser that will be used to select the location to save. */
	protected JFileChooser fileChooser;

	/** The instrument used that manage the preferences. */
	protected PreferencesSetter prefSetter;

	File currentFolder;


	@Override
	protected void doActionBody() {
//		if(ui.isModified())
//			switch(SaveDrawing.showAskModificationsDialog(ui)) {
//				case JOptionPane.NO_OPTION: //new
//					newDrawing();
//					break;
//				case JOptionPane.YES_OPTION: // save + load
//					final File f = SaveDrawing.showDialog(fileChooser, true, ui, file, currentFolder);
//					if(f!=null) {
//						openSaveManager.save(f.getPath(), ui, progressBar, statusWidget);
//						ui.setModified(false);
//						newDrawing();
//					}
//					break;
//				case JOptionPane.CANCEL_OPTION: // nothing
//					break;
//				default:
//					break;
//			}
//		else newDrawing();
	}


	protected void newDrawing() {
//		ui.reinit();
//		UndoCollector.INSTANCE.clear();
//		ActionsRegistry.INSTANCE.clear();
//		FlyweightThumbnail.clear();
//		try{ prefSetter.readXMLPreferences(); }
//		catch(final Exception exception){ BadaboomCollector.INSTANCE.add(exception); }
	}


	@Override
	public boolean canDo() {
//		return fileChooser!=null && ui!=null && openSaveManager!=null && prefSetter!=null;
		return false;
	}


	@Override
	public void flush() {
		super.flush();
		fileChooser = null;
	}


	/**
	 * @param chooser The file chooser that will be used to select the location to save.
	 * @since 3.0
	 */
	public void setFileChooser(final JFileChooser chooser) {
		fileChooser = chooser;
	}


	/**
	 * @param setter The instrument used that manage the preferences.
	 * @since 3.0
	 */
	public void setPrefSetter(final PreferencesSetter setter) {
		prefSetter = setter;
	}

	public void setCurrentFolder(final File currFolder) {
		currentFolder = currFolder;
	}

	//FIXME to remove
	@Override
	public boolean isRegisterable() {
		return false;
	}
}
