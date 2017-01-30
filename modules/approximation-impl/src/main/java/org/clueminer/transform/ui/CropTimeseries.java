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
package org.clueminer.transform.ui;

import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.flow.api.AbsFlowNode;
import org.clueminer.flow.api.FlowNode;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 */
@ServiceProvider(service = FlowNode.class)
public class CropTimeseries extends AbsFlowNode implements FlowNode {

    private final Logger LOG = LoggerFactory.getLogger(DatasetTransformationFlow.class);
    private Timeseries<? extends ContinuousInstance> transform;
    public static final String NAME = "crop timeseries";

    public static final String PROP_NAME = "crop";

    public CropTimeseries() {
        inputs = new Class[]{Timeseries.class};
        outputs = new Class[]{Timeseries.class};
        panel = new CropTimeseriesUI();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Object[] execute(Object[] inputs, Props props) {
        checkInputs(inputs);
        Object[] ret = new Object[1];

        Timeseries<? extends ContinuousInstance> data = (Timeseries<? extends ContinuousInstance>) inputs[0];
        LOG.info("dataset size: {}", data.size());
        LOG.info("dataset has {} attributes", data.attributeCount());

        if (data.isEmpty() || data.attributeCount() == 0) {
            throw new RuntimeException("dataset is empty!");
        }
        String transformation = props.get(PROP_NAME, "");

        //make sure we don't have old data
        transform = null;
        //check if there's preloaded dataset available
        transform = (Timeseries<? extends ContinuousInstance>) data.getChild(transformation);
        if (transform == null) {
            //run analysis
            transform = cropData(data, props);
        }
        LOG.info("output dataset has {} attributes", transform.attributeCount());
        ret[0] = transform;
        return ret;
    }

    private Timeseries<? extends ContinuousInstance> cropData(Timeseries<? extends ContinuousInstance> data, Props props) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
