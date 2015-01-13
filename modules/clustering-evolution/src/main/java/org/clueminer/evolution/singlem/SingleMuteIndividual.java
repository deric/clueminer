package org.clueminer.evolution.singlem;

import java.util.Random;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.config.annotation.Param;
import org.clueminer.clustering.api.evolution.Evolution;
import org.clueminer.evolution.multim.MultiMuteIndividual;

/**
 *
 * @author Tomas Barton
 */
public class SingleMuteIndividual extends MultiMuteIndividual {

    private Random rand;

    public SingleMuteIndividual(Evolution evolution) {
        super(evolution);
        rand = new Random();
    }

    @Override
    public void mutate() {
        //TODO: choose only one mutation

        Param[] params = getAlgorithm().getParameters();
        int id = rand.nextInt(params.length);


        genom.putBoolean(AgglParams.LOG, logscale(rand));
        genom.put(AgglParams.STD, std(rand));
        genom.put(AgglParams.LINKAGE, linkage(rand));
        genom.put(AgglParams.DIST, distance(rand));
    }

}
