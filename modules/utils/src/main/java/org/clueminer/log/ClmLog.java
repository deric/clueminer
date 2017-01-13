/*
 * Copyright (C) 2011-2017 clueminer.org
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
package org.clueminer.log;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author deric
 */
public class ClmLog {

    public static void setup() {
        setup(Logger.getGlobal());
    }

    public static void setup(Logger logger) {
        logger.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        Formatter formatter = new ClmFormatter();
        handler.setFormatter(formatter);
        logger.addHandler(handler);
    }

    public static void setup(String lvl) {
        Logger log = LogManager.getLogManager().getLogger("");
        Formatter formater = new ClmFormatter();
        Level level;

        switch (lvl.toUpperCase()) {
            case "INFO":
                level = Level.INFO;
                break;
            case "SEVERE":
                level = Level.SEVERE;
                break;
            case "WARNING":
                level = Level.WARNING;
                break;
            case "ALL":
                level = Level.ALL;
                break;
            case "FINE":
                level = Level.FINE;
                break;
            case "FINER":
                level = Level.FINER;
                break;
            case "FINEST":
                level = Level.FINEST;
                break;
            default:
                throw new RuntimeException("log level " + log + " is not supported");
        }
        setupHandlers(log, level, formater);

        //remove date line from logger
        log.setUseParentHandlers(false);
    }

    private static void setupHandlers(Logger logger, Level level, Formatter formater) {
        for (Handler handler : logger.getHandlers()) {
            handler.setLevel(level);
            handler.setFormatter(formater);
        }
        Logger parentLogger = logger.getParent();
        if (null != parentLogger) {
            for (Handler handler : parentLogger.getHandlers()) {
                handler.setLevel(level);
                handler.setFormatter(formater);
            }
        }
    }

}
