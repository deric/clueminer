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
package org.clueminer.dataset.std;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.flow.api.AbsFlowNode;
import org.clueminer.flow.api.FlowError;
import org.clueminer.flow.api.FlowNode;
import org.clueminer.std.Scaler;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 * @param <E>
 */
@ServiceProvider(service = FlowNode.class)
public class StdFlow<E extends Instance> extends AbsFlowNode implements FlowNode {

    private static final Logger LOG = LoggerFactory.getLogger(StdFlow.class);
    public static final String NAME = "data normalization";
    private final DataScaler ds;

    public StdFlow() {
        ds = new DataScaler();
        panel = new StdFlowUI();
        inputs = new Class[]{Dataset.class};
        outputs = new Class[]{Dataset.class};
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Object[] execute(Object[] inputs, Props params) throws FlowError {
        checkInputs(inputs);
        Object[] ret = new Object[1];
        String method = params.get("std", Scaler.NONE);
        boolean logscale = params.getBoolean("log-scale", false);
        LOG.info("normalizing data {}, logscale: {}",
                method, logscale);
        Dataset<E> dataset = (Dataset<E>) inputs[0];
        Dataset<E> norm = ds.standartize(dataset, method, logscale);
        ret[0] = norm;
        LOG.debug("output dataset  {}x{}", dataset.size(), dataset.attributeCount());
        LOG.info("finished normalization");
        return ret;
    }

}
