package org.clueminer.evolution.singlem;

import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.evolution.Evolution;
import org.clueminer.evolution.multim.MultiMuteIndividual;

/**
 *
 * @author Tomas Barton
 */
public class SingleMuteIndividual extends MultiMuteIndividual {

    public SingleMuteIndividual(Evolution evolution) {
        super(evolution);
    }

    @Override
    public void mutate() {
        //TODO: choose only one mutation
        genom.putBoolean(AgglParams.LOG, logscale(rand));
        genom.put(AgglParams.STD, std(rand));
        genom.put(AgglParams.LINKAGE, linkage(rand));
        genom.put(AgglParams.DIST, distance(rand));
    }

}
