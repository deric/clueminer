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
package org.clueminer.transform.ui;

import org.clueminer.approximation.api.DataTransform;
import org.clueminer.approximation.api.DataTransformFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.flow.api.AbsFlowNode;
import org.clueminer.flow.api.FlowError;
import org.clueminer.flow.api.FlowNode;
import org.clueminer.utils.Props;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 */
@ServiceProvider(service = FlowNode.class)
public class DatasetTransformationFlow extends AbsFlowNode implements FlowNode {

    private final Logger LOG = LoggerFactory.getLogger(DatasetTransformationFlow.class);
    private Dataset<? extends Instance> transform;
    public static final String NAME = "timeseries transformation";

    public static final String PROP_NAME = "transformation";

    public DatasetTransformationFlow() {
        inputs = new Class[]{Timeseries.class};
        outputs = new Class[]{Dataset.class};
        panel = new DatasetTransformationUI();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Object[] execute(Object[] input, Props params) throws FlowError {
        checkInputs(input);
        Object[] ret = new Object[1];

        Dataset<? extends Instance> data = (Dataset<? extends Instance>) input[0];
        LOG.info("dataset size: {}", data.size());
        LOG.info("dataset has {} attributes", data.attributeCount());

        if (data.isEmpty() || data.attributeCount() == 0) {
            throw new FlowError("dataset is empty!");
        }
        String transformation = params.get(PROP_NAME, "");

        //make sure we don't have old data
        transform = null;
        //check if there's preloaded dataset available
        transform = data.getChild(transformation);
        if (transform == null) {
            //run analysis
            transform = runPreprocessing(data, transformation);
        }
        LOG.info("output dataset has {} attributes", transform.attributeCount());
        ret[0] = transform;

        return ret;
    }

    private Dataset<? extends Instance> runPreprocessing(final Dataset<? extends Instance> data, String datasetTransform) {

        DataTransformFactory df = DataTransformFactory.getInstance();
        final DataTransform trans = df.getProvider(datasetTransform);
        final Dataset<? extends Instance> output = trans.createDefaultOutput(data);

        final ProgressHandle ph = ProgressHandle.createHandle("Running preprocessing");
        trans.analyze(data, output, ph);
        return output;
    }
}
