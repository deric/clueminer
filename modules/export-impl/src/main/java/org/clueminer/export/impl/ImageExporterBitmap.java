/*
 * Copyright (C) 2011-2018 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.export.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.clueminer.utils.Exportable;
import org.clueminer.utils.ImageExporter;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.WindowManager;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ImageExporter.class)
public class ImageExporterBitmap implements ImageExporter {

    private static ImageExporterBitmap instance;
    private File defaultFolder = null;
    private JFileChooser fileChooser;
    private FileFilter jpegFilter;
    private FileFilter pngFilter;
    private static final String prefKey = "last_folder";

    public static ImageExporterBitmap getDefault() {
        if (instance == null) {
            instance = new ImageExporterBitmap();
        }
        return instance;
    }

    public ImageExporterBitmap() {
    }

    public void export(Exportable panel) {
        if (panel != null) {
            try {
                Preferences p = NbPreferences.root().node("/clueminer/exporter");
                String folder = p.get(prefKey, null);
                if (folder != null) {
                    defaultFolder = new File(folder);
                } else {
                    defaultFolder = null;
                }
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
                    fileChooser.setDialogTitle("Export Image");
                    fileChooser.setAcceptAllFileFilterUsed(true);
                    fileChooser.setCurrentDirectory(defaultFolder);
                    String name = panel.getName();
                    if (name != null) {
                        fileChooser.setSelectedFile(new File(folder + "/" + name));
                    }

                    jpegFilter = new FileFilter() {

                        @Override
                        public boolean accept(File file) {
                            String filename = file.getName();
                            return file.isDirectory() || filename.endsWith(".jpeg") || filename.endsWith(".jpg");
                        }

                        @Override
                        public String getDescription() {
                            return "JPEG (*.jpeg *.jpg)";
                        }
                    };
                    fileChooser.addChoosableFileFilter(jpegFilter);

                    pngFilter = new FileFilter() {

                        @Override
                        public boolean accept(File file) {
                            String filename = file.getName();
                            return file.isDirectory() || filename.endsWith(".png");
                        }

                        @Override
                        public String getDescription() {
                            return "PNG (*.png)";
                        }
                    };
                    fileChooser.addChoosableFileFilter(pngFilter);
                }

                //     fileChooser.setSelectedFile(new File(panel.getName()));
                defaultFolder = fileChooser.getCurrentDirectory();
                p.put(prefKey, fileChooser.getCurrentDirectory().getAbsolutePath());
                if (fileChooser.showSaveDialog(WindowManager.getDefault().getMainWindow()) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    String filename = file.getName();
                    BufferedImage image = panel.getBufferedImage(panel.getWidth(), panel.getHeight());
                    FileFilter fileFilter = fileChooser.getFileFilter();

                    String format;
                    if (fileFilter == jpegFilter) {
                        if (!filename.endsWith(".jpeg") && !filename.endsWith(".jpg")) {
                            file = new File(file.getAbsolutePath() + ".jpg");
                        }
                        format = "jpg";
                    } else {
                        if (!filename.endsWith(".png")) {
                            file = new File(file.getAbsolutePath() + ".png");
                        }
                        format = "png";
                    }

                    Object retval = NotifyDescriptor.YES_OPTION;
                    if (file.exists()) {
                        NotifyDescriptor d = new NotifyDescriptor.Confirmation(
                                "This file already exists. Do you want to overwrite it?",
                                "Overwrite",
                                NotifyDescriptor.YES_NO_OPTION);
                        retval = DialogDisplayer.getDefault().notify(d);
                    }

                    if (retval.equals(NotifyDescriptor.YES_OPTION)) {
                        ImageIO.write(image, format, file);
                    } else {
                        export(panel);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
