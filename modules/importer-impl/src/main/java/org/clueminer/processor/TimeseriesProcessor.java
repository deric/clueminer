/*
 * Copyright (C) 2011-2015 clueminer.org
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
package org.clueminer.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.plugin.TimeseriesDataset;
import org.clueminer.gui.msg.NotifyUtil;
import org.clueminer.io.importer.api.AttributeDraft;
import org.clueminer.processor.spi.Processor;
import org.clueminer.types.TimePoint;
import org.openide.util.NbBundle;

/**
 *
 * @author deric
 */
public class TimeseriesProcessor extends AbstractProcessor implements Processor {

    private static final Logger logger = Logger.getLogger(TimeseriesProcessor.class.getName());

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(TimeseriesProcessor.class, "TimeseriesProcessor.displayName");
    }

    @Override
    protected Dataset<? extends Instance> createDataset(ArrayList<AttributeDraft> inputAttr) {
        return new TimeseriesDataset(container.getInstanceCount());
    }

    @Override
    protected Map<Integer, Integer> attributeMapping(ArrayList<AttributeDraft> inputAttr) {
        //set attributes
        Map<Integer, Integer> inputMap = new HashMap<>();

        TimePoint tp[] = new TimePointAttribute[inputAttr.size()];
        AttributeDraft attrd;
        for (int i = 0; i < tp.length; i++) {
            attrd = inputAttr.get(i);
            try {
                String name = attrd.getName();
                double pos = Double.valueOf(name);
                tp[i] = new TimePointAttribute(i, (long) pos, pos);
                inputMap.put(attrd.getIndex(), i);
            } catch (NumberFormatException e) {
                NotifyUtil.warn("time attribute error", "failed to parse '" + attrd.getName() + "' as a number", true);
            }
        }
        ((Timeseries) dataset).setTimePoints(tp);

        return inputMap;
    }

}
