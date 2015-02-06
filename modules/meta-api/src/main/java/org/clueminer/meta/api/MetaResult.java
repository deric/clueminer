package org.clueminer.meta.api;

import java.io.Serializable;

/**
 * Simple class for exchanging clustering results
 *
 * @author Tomas Barton
 */
public class MetaResult implements Serializable {

    private static final long serialVersionUID = -6611537651936473664L;

    String template;

    double score;

    /**
     * number of partitions
     */
    int k;

    public MetaResult(int k, String template, double score) {
        this.k = k;
        this.template = template;
        this.score = score;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

}
