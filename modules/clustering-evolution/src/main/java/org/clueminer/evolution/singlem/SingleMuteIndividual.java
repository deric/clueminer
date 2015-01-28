package org.clueminer.evolution.singlem;

import java.util.Random;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.config.Parameter;
import org.clueminer.clustering.api.evolution.Evolution;
import org.clueminer.evolution.multim.MultiMuteIndividual;

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
                    System.out.println("type: " + p.getType().toString());
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
