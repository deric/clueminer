package org.clueminer.evolution;

import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.evolution.Evolution;
import org.clueminer.colors.ColorBrewer;
import org.clueminer.dataset.api.ColorGenerator;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Lookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractEvolution implements Evolution {

    protected int generations;
    protected ColorGenerator cg = new ColorBrewer();
    protected ClusterEvaluation external;
    protected ClusteringAlgorithm algorithm;
    protected boolean maximizedFitness;
    protected ProgressHandle ph;
    protected transient InstanceContent instanceContent;
    protected transient Lookup lookup;

    @Override
    public boolean isMaximizedFitness() {
        return maximizedFitness;
    }

    public int getGenerations() {
        return generations;
    }

    @Override
    public void setGenerations(int generations) {
        this.generations = generations;
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public void setColorGenerator(ColorGenerator cg) {
        this.cg = cg;

    }

    @Override
    public ColorGenerator getColorGenerator() {
        return cg;
    }

    @Override
    public void setProgressHandle(ProgressHandle ph) {
        this.ph = ph;
    }
}
