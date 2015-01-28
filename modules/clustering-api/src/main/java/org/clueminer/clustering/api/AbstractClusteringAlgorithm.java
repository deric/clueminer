package org.clueminer.clustering.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;
import org.clueminer.clustering.api.config.Parameter;
import org.clueminer.clustering.api.config.annotation.Param;
import org.clueminer.clustering.params.AlgParam;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.distance.api.DistanceMeasure;
import org.netbeans.api.progress.ProgressHandle;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractClusteringAlgorithm implements ClusteringAlgorithm {

    @Param(name = AgglParams.DIST,
           factory = "org.clueminer.distance.api.DistanceFactory",
           type = org.clueminer.clustering.params.ParamType.STRING)
    protected DistanceMeasure distanceFunction;
    protected ColorGenerator colorGenerator;
    protected ProgressHandle ph;

    public static final String DISTANCE = "distanceMeasure";

    @Override
    public DistanceMeasure getDistanceFunction() {
        return distanceFunction;
    }

    @Override
    public void setDistanceFunction(DistanceMeasure dm) {
        this.distanceFunction = dm;
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

    /**
     * Get all algorithm parameters that could be modified.
     *
     * @return all algorithm parameters
     */
    @Override
    public Parameter[] getParameters() {
        Collection<Parameter> res = new LinkedList<>();
        Class<?> clazz = getClass();
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                //Class type = field.getType();
                //from JDK8: field.getAnnotationsByType(Param.class);
                Annotation[] annotations = field.getDeclaredAnnotations();
                for (Annotation anno : annotations) {
                    if (anno instanceof Param) {
                        Param p = (Param) anno;
                        String paramName = p.name();
                        if (paramName.isEmpty()) {
                            paramName = field.getName();
                        }
                        Parameter out = new AlgParam(paramName, p.type(), p.description(), p.factory());
                        res.add(out);
                    }
                }
            }
            //go to parent class
            clazz = clazz.getSuperclass();
        }
        return res.toArray(new Parameter[res.size()]);
    }

}
