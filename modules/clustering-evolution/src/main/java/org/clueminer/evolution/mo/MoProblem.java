package org.clueminer.evolution.mo;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.config.Parameter;
import org.clueminer.evolution.singlem.SingleMuteIndividual;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Exceptions;
import org.uma.jmetal.problem.IntegerProblem;
import org.uma.jmetal.problem.impl.AbstractGenericProblem;
import org.uma.jmetal.solution.IntegerSolution;

/**
 *
 * @author Tomas Barton
 */
public class MoProblem extends AbstractGenericProblem<IntegerSolution> implements IntegerProblem {

    private static final long serialVersionUID = 5458227476117018712L;

    protected final MoEvolution evolution;
    protected Int2ObjectOpenHashMap<String> mapping;
    protected int[] lowerLimit;
    protected int[] upperLimit;
    protected Parameter[] params;

    public MoProblem(MoEvolution evolution) {
        this.evolution = evolution;
        setNumberOfObjectives(evolution.getNumObjectives());
        initializeGenomMapping(evolution.getAlgorithm());
    }

    @Override
    public void evaluate(IntegerSolution solution) {
        ((MoSolution) solution).evaluate();
    }

    @Override
    public IntegerSolution createSolution() {
        return (IntegerSolution) new MoSolution(this, new SingleMuteIndividual(evolution));
    }

    private void initializeGenomMapping(ClusteringAlgorithm algorithm) {
        params = algorithm.getParameters();
        mapping = new Int2ObjectOpenHashMap(params.length);
        int i = 0;
        lowerLimit = new int[params.length];
        upperLimit = new int[params.length];
        for (Parameter p : params) {
            try {
                mapping.put(i, p.getName());
                lowerLimit[i] = 0;
                switch (p.getType()) {
                    case STRING:
                        ServiceFactory f = getFactory(p);
                        upperLimit[i] = f.getAll().size();
                        break;
                    case BOOLEAN:
                        upperLimit[i] = 1;
                        break;
                    default:
                        throw new RuntimeException(p.getType() + " is not supported yet");
                }

                i++;
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        setNumberOfVariables(params.length);
    }

    @Override
    public Integer getUpperBound(int index) {
        return upperLimit[index];
    }

    @Override
    public Integer getLowerBound(int index) {
        return lowerLimit[index];
    }

    /**
     * Variable mapped to given index
     *
     * @param index
     * @return
     */
    public String getVar(int index) {
        return mapping.get(index);
    }

    /**
     * Get instance of service factory if available for given parameter
     *
     * @param param
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static ServiceFactory getFactory(Parameter param) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?> clazz = Class.forName(param.getFactory());
        Method meth = clazz.getMethod("getInstance");
        ServiceFactory f = (ServiceFactory) meth.invoke(clazz);
        return f;
    }

}
