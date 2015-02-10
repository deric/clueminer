package org.clueminer.evolution.mo;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.config.Parameter;
import org.clueminer.evolution.singlem.SingleMuteIndividual;
import org.uma.jmetal.problem.impl.AbstractGenericProblem;

/**
 *
 * @author Tomas Barton
 */
public class MoProblem extends AbstractGenericProblem<MoSolution> {

    protected final MoEvolution evolution;
    protected Int2ObjectOpenHashMap<String> mapping;

    public MoProblem(MoEvolution evolution) {
        this.evolution = evolution;
        setNumberOfObjectives(evolution.getNumObjectives());
        initializeGenomMapping(evolution.getAlgorithm());
    }

    @Override
    public void evaluate(MoSolution solution) {
        solution.evaluate();
    }

    @Override
    public MoSolution createSolution() {
        return new MoSolution(this, new SingleMuteIndividual(evolution));
    }

    private void initializeGenomMapping(ClusteringAlgorithm algorithm) {
        Parameter[] params = algorithm.getParameters();
        mapping = new Int2ObjectOpenHashMap(params.length);
        int i = 0;
        for (Parameter p : params) {
            mapping.put(i++, p.getName());
        }
    }

    public int getNumVars() {
        return mapping.size();
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

}
