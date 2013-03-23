package org.clueminer.xcalibour.files;

import org.jzy3d.plot3d.builder.Mapper;

/**
 *
 * @author Tomas Barton
 */
public class SpectrumMapper extends Mapper{
    
    private SpectrumDataset<MassSpectrum> dataset;

    public void setDataset(SpectrumDataset<MassSpectrum> dataset) {
        this.dataset = dataset;
    }
    
    
    @Override
    public double f(double x, double y) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
