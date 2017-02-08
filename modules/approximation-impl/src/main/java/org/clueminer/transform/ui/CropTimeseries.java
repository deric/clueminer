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

import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.flow.api.AbsFlowNode;
import org.clueminer.flow.api.FlowError;
import org.clueminer.flow.api.FlowNode;
import org.clueminer.types.TimePoint;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * From given input timeseries dataset will create a new one that represents
 * original data subset.
 *
 * @author deric
 */
@ServiceProvider(service = FlowNode.class)
public class CropTimeseries<E extends ContinuousInstance> extends AbsFlowNode implements FlowNode {

    private final Logger LOG = LoggerFactory.getLogger(DatasetTransformationFlow.class);
    private Timeseries<? extends ContinuousInstance> transform;
    public static final String NAME = "crop timeseries";

    public static final String PROP_NAME = "crop";
    public static final String CROP_START = "crop_start";
    public static final String CROP_END = "crop_end";

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
    public Object[] execute(Object[] inputs, Props props) throws FlowError {
        checkInputs(inputs);
        Object[] ret = new Object[1];

        Timeseries<E> data = (Timeseries<E>) inputs[0];
        LOG.info("dataset size: {}", data.size());
        LOG.info("dataset has {} attributes", data.attributeCount());

        if (data.isEmpty() || data.attributeCount() == 0) {
            throw new RuntimeException("dataset is empty!");
        }
        String transformation = props.toJson();

        //make sure we don't have old data
        transform = null;
        //check if there's preloaded dataset available
        LOG.info("checking for cached {}", transformation);
        transform = (Timeseries<E>) data.getChild(transformation);
        if (transform == null) {
            LOG.debug("Cache miss. computing crop");
            //run analysis
            transform = cropData(data, props);
            data.addChild(transformation, (Dataset) transform);
        }
        LOG.info("output dataset has {} attributes", transform.attributeCount());
        ret[0] = transform;
        return ret;
    }

    private Timeseries<E> cropData(Timeseries<E> data, Props props) {
        double startPos = props.getDouble(CROP_START, 0.0);
        double endPos = props.getDouble(CROP_END, 0.0);

        if (startPos == endPos) {
            LOG.info("no cropping. missing start and end");
            return data;
        }
        int start = findTimepoint(startPos, 0, data);
        int end = findTimepoint(endPos, start, data);
        if (start < 0 || end < 0) {
            LOG.info("no cropping. Invalid start and end marks");
            return data;
        }
        LOG.info("start = {}, end = {}", start, end);

        TimePoint[] timePoints = data.getTimePoints();
        int size = end - start + 1;

        TimePointAttribute[] pointsNew = new TimePointAttribute[size];
        //hardlink references from source array to destination array
        System.arraycopy(timePoints, start, pointsNew, 0, size);

        long startTime = pointsNew[0].getTimestamp();
        double cropStartPos = timePoints[start].getPosition();
        for (int i = 0; i < size; i++) {
            pointsNew[i].setTimestamp(pointsNew[i].getTimestamp() - startTime);
            //subtract position of point where crop starts
            pointsNew[i].setPosition(timePoints[start + i].getPosition() - cropStartPos);
        }
        LOG.info("cropping data to interval [{},{}]", start, end);
        LOG.info("input data size: {}", data.size());
        Timeseries<E> output = (Timeseries<E>) data.duplicate();
        output.setTimePoints(pointsNew);
        for (int i = 0; i < data.size(); i++) {
            E inst = (E) data.get(i).crop(start, end);
            inst.setParent(output);
            output.add(inst);
        }
        //make sure the original data is accessible
        output.setParent(data);

        return (Timeseries<E>) output;
    }

    /**
     * Searches for timepoint with closest position
     *
     * @param pos
     * @param from start index for search
     * @param data
     * @return
     */
    private int findTimepoint(double pos, int from, Timeseries<? extends ContinuousInstance> data) {
        TimePoint[] tp = data.getTimePoints();
        double delta, minDelta = Double.MAX_VALUE;
        int minPos = -1;
        for (int i = from; i < tp.length; i++) {
            if (pos == tp[i].getPosition()) {
                return i;
            }
            delta = Math.abs(pos - tp[i].getPosition());
            if (delta <= minDelta) {
                minDelta = delta;
                minPos = i;
            }
        }

        return minPos;
    }

}
