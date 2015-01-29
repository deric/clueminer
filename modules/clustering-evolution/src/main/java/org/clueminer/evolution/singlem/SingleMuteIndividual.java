package org.clueminer.evolution.singlem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.config.Parameter;
import org.clueminer.clustering.api.evolution.Evolution;
import org.clueminer.evolution.multim.MultiMuteIndividual;
import org.clueminer.utils.ServiceFactory;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class SingleMuteIndividual extends MultiMuteIndividual {

    private Random rand = new Random();

    public SingleMuteIndividual(Evolution evolution) {
        super(evolution);
        System.out.println("SM ind: " + genom.toString());
    }

    /**
     * Copying constructor
     *
     * @param parent
     */
    private SingleMuteIndividual(SingleMuteIndividual parent) {
        this.evolution = parent.evolution;
        this.algorithm = parent.algorithm;
        this.genom = parent.genom.copy();

        this.fitness = parent.fitness;
    }

    @Override
    public void mutate() {
        //TODO: choose only one mutation
        Parameter[] params = getAlgorithm().getParameters();
        if (params.length > 0) {
            int id = rand.nextInt(params.length);
            Parameter p = params[id];
            System.out.println("========= mutating param: " + p.getName());

            switch (p.getType()) {
                case STRING:
                    if (p.getFactory().isEmpty()) {
                        throw new RuntimeException("expected an factory for " + p.getName());
                    }

                    try {
                        Class<?> clazz = Class.forName(p.getFactory());
                        Method meth = clazz.getMethod("getInstance");
                        try {
                            ServiceFactory f = (ServiceFactory) meth.invoke(clazz);
                            String[] list = f.getProvidersArray();
                            String prev = genom.get(p.getName());
                            int i = 0;
                            int idx;
                            do {
                                //choose random number between 0 and number of possible providers
                                idx = rand.nextInt(list.length);
                                //we have 3 tries to find different value than previous one
                                i++;
                            } while (list[idx].equals(prev) && i < 3);
                            genom.put(p.getName(), list[idx]);
                            System.out.println("mutated " + p.getName() + " from: " + prev + " to: " + list[idx]);
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    } catch (ClassNotFoundException | NoSuchMethodException | SecurityException ex) {
                        Exceptions.printStackTrace(ex);
                    }

                    break;
            }


            /*    java.lang.reflect.Method method;
             try {
             method = obj.getClass().getMethod(methodName);
             } catch (SecurityException e) {
             // ...
             } catch (NoSuchMethodException e) {
             // ...
             }*/
            genom.putBoolean(AgglParams.LOG, logscale(rand));
            genom.put(AgglParams.STD, std(rand));
            genom.put(AgglParams.LINKAGE, linkage(rand));
            genom.put(AgglParams.DIST, distance(rand));
        } else {
            System.out.println("WARN: no params available");
        }
    }

    @Override
    public SingleMuteIndividual deepCopy() {
        SingleMuteIndividual newOne = new SingleMuteIndividual(this);
        return newOne;
    }

}
