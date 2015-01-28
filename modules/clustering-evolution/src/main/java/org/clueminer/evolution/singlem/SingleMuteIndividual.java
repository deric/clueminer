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
                        Method inst = clazz.getMethod("getInstance", ServiceFactory.class);
                        try {
                            ServiceFactory f = (ServiceFactory) inst.invoke(clazz);
                            System.out.println("possibilities: " + f.getAll().size());
                        } catch (IllegalAccessException ex) {
                            Exceptions.printStackTrace(ex);
                        } catch (IllegalArgumentException ex) {
                            Exceptions.printStackTrace(ex);
                        } catch (InvocationTargetException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    } catch (ClassNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (NoSuchMethodException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (SecurityException ex) {
                        Exceptions.printStackTrace(ex);
                    }

                    System.out.println("factory: " + p.getFactory());
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
