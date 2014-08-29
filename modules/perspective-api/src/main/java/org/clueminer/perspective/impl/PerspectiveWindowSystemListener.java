package org.clueminer.perspective.impl;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import org.openide.windows.WindowManager;
import org.openide.windows.WindowSystemEvent;
import org.openide.windows.WindowSystemListener;

/**
 *
 * @author Tomas Barton
 */
public class PerspectiveWindowSystemListener implements WindowSystemListener {

    private Dimension lastDimension = null;
    private Integer lastState = null;
    private Point lastLocation = null;

    @Override
    public void beforeLoad(WindowSystemEvent event) {
    }

    @Override
    public void afterLoad(WindowSystemEvent event) {
        Frame mainWindow = WindowManager.getDefault().getMainWindow();
        if (mainWindow != null) {
            if (lastDimension != null) {
                mainWindow.setSize(lastDimension);
            }
            if (lastLocation != null) {
                mainWindow.setLocation(lastLocation);
            }
            if (lastState != null) {
                mainWindow.setState(lastState);
            }
        }
    }

    @Override
    public void beforeSave(WindowSystemEvent event) {
        Frame mainWindow = WindowManager.getDefault().getMainWindow();
        if (mainWindow != null) {
            lastDimension = mainWindow.getSize();
            lastLocation = mainWindow.getLocation();
            lastState = mainWindow.getExtendedState();
        }
    }

    @Override
    public void afterSave(WindowSystemEvent event) {
    }
}
