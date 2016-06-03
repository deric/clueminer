/*
 * Copyright (C) 2011-2016 clueminer.org
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
package org.clueminer.clustering.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;
import org.clueminer.clustering.api.config.Parameter;
import org.clueminer.clustering.api.config.annotation.Param;
import org.clueminer.clustering.params.AlgParam;
import org.clueminer.clustering.params.ParamType;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.DataStandardization;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;
import org.netbeans.api.progress.ProgressHandle;

/**
 * Each clustering algorithm should inherit from this class, in order to be able
 * to define its parameters.
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public abstract class Algorithm<E extends Instance, C extends Cluster<E>> implements ClusteringAlgorithm<E, C> {

    // don't mutate distance by default - most of evaluation metrics are not
    // adjusted for this
    //@Param(name = AlgParams.DIST,
    //       factory = "org.clueminer.distance.api.DistanceFactory",
    //       type = org.clueminer.clustering.params.ParamType.STRING)
    protected Distance distanceFunction;

    //standartization method that is used as part of preprocessing
    @Param(name = AlgParams.STD,
           factory = "org.clueminer.dataset.api.DataStandardizationFactory",
           type = org.clueminer.clustering.params.ParamType.STRING)
    protected DataStandardization std;

    //apply logarithm to all values
    @Param(name = AlgParams.LOG,
           type = org.clueminer.clustering.params.ParamType.BOOLEAN)
    protected boolean logScale;

    protected ColorGenerator colorGenerator;
    protected ProgressHandle ph;

    public static final String DISTANCE = "distance";

    /**
     * Whether to use logarithmic scaling - boolean parameter
     */
    public static final String LOG = "log-scale";

    /**
     * Cluster label for outliers or noises.
     */
    public static final int OUTLIER = Integer.MAX_VALUE;

    public static final String OUTLIER_LABEL = "noise";

    @Override
    public Distance getDistanceFunction() {
        return distanceFunction;
    }

    @Override
    public void setDistanceFunction(Distance dm) {
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
                        ParamType type;
                        if (p.type() != ParamType.NULL) {
                            type = p.type();
                        } else {
                            //auto-detection
                            switch (field.getType().getName()) {
                                case "double":
                                    type = ParamType.DOUBLE;
                                    break;
                                case "int":
                                    type = ParamType.INTEGER;
                                    break;
                                case "String":
                                    type = ParamType.STRING;
                                    break;
                                default:
                                    throw new RuntimeException("unknown type " + field.getType().getName());
                            }
                        }
                        Parameter out = new AlgParam(paramName, type, p.description(), p.factory());
                        switch (type) {
                            case DOUBLE:
                            case INTEGER:
                                out.setMin(p.min());
                                out.setMax(p.max());
                                break;
                        }
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
