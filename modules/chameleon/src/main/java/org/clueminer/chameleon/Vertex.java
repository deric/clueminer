/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.clueminer.chameleon;

/**
 *
 * @author tomas
 */
public class Vertex implements Comparable<Vertex> {
    
    public int degree;
    public int index;
    public boolean used;
    public int cluster;
    
    public Vertex(int index, int degree) {
        this.degree = degree;
        this.index = index;
        used = false;
    }
    
    @Override
    public int compareTo(Vertex compareVertex) {
        int compareDegree = compareVertex.degree;
        return compareDegree - this.degree;
    }
}
