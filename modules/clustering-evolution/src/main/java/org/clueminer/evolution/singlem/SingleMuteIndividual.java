package org.clueminer.evolution.singlem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.config.Parameter;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.multim.MultiMuteIndividual;
import org.clueminer.utils.ServiceFactory;

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
        if (params.length == 0) {
            throw new RuntimeException("no parameters to mutate");
        }
        int id = rand.nextInt(params.length);
        Parameter p = params[id];
        System.out.println("========= mutating param: " + p.getName());

        switch (p.getType()) {
            case STRING:
                if (p.getFactory().isEmpty()) {
                    throw new RuntimeException("expected an factory for " + p.getName());
                }

                try {
                    ServiceFactory f = getFactory(p);
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
                    System.out.println("==== mutated " + p.getName() + " from: " + prev + " to: " + list[idx]);

                } catch (ClassNotFoundException | NoSuchMethodException |
                        SecurityException | IllegalAccessException |
                        IllegalArgumentException | InvocationTargetException ex) {
                    throw new RuntimeException("factory '" + p.getName() + "' was not found");
                }
                break;
            case BOOLEAN:
                //simply inverse the value
                genom.putBoolean(p.getName(), !genom.getBoolean(p.getName()));
                break;

            default:
                throw new UnsupportedOperationException("unsupported type: " + p.getType());
        }

        genom.put(AgglParams.LINKAGE, linkage(rand));
    }

    @Override
    public SingleMuteIndividual deepCopy() {
        SingleMuteIndividual newOne = new SingleMuteIndividual(this);
        return newOne;
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
