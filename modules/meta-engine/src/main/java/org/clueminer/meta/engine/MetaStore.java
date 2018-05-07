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
package org.clueminer.meta.engine;

import org.clueminer.meta.api.MetaStorage;
import org.clueminer.meta.api.MetaStorageFactory;
import org.clueminer.project.api.ProjectController;
import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for accessing data backends
 *
 * @author deric
 */
public class MetaStore {

    private static final Logger LOG = LoggerFactory.getLogger(MetaStore.class);

    public static MetaStorage fetchStorage() {
        MetaStorage storage = null;
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        if (pc != null) {
            storage = pc.getCurrentProject().getLookup().lookup(MetaStorage.class);
        } else {
            LOG.error("missing project controller");
        }
        if (storage == null) {
            LOG.info("meta storage not set, using default one");
            storage = MetaStorageFactory.getInstance().getDefault();
        }
        return storage;
    }

}
