package org.clueminer.clustering.api;

import java.lang.reflect.Field;
import java.util.Collection;
import org.clueminer.clustering.api.config.annotation.Param;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.distance.api.DistanceMeasure;
import org.netbeans.api.progress.ProgressHandle;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractClusteringAlgorithm implements ClusteringAlgorithm {

    protected DistanceMeasure distanceMeasure;
    protected ColorGenerator colorGenerator;
    protected ProgressHandle ph;

    public static final String DISTANCE = "distanceMeasure";

    @Override
    public DistanceMeasure getDistanceFunction() {
        return distanceMeasure;
    }

    @Override
    public void setDistanceFunction(DistanceMeasure dm) {
        this.distanceMeasure = dm;
    }

    @Override
    public ColorGenerator getColorGenerator() {
        return colorGenerator;
    }

    @Override
    public void setColorGenerator(ColorGenerator colorGenerator) {
        this.colorGenerator = colorGenerator;
    }

    @Override
    public void setProgressHandle(ProgressHandle ph) {
        this.ph = ph;
    }

    public Collection<Param> getParameters() {

        for (Field field : getClass().getDeclaredFields()) {
            Class type = field.getType();
            String name = field.getName();
            Param[] annotations = field.getAnnotationsByType(Param.class);
            for (Param p : annotations) {
                System.out.println("p: " + p.name() + " type: " + type.getName() + " field: " + name);
            }
        }

        return null;
    }

}
