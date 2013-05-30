package org.clueminer.xcalibour.files;

import org.clueminer.xcalibour.data.SpectrumDataset;
import org.clueminer.xcalibour.data.MassSpectrum;
import org.clueminer.interpolation.CubicInterpolator;
import org.clueminer.interpolation.LagrangeInterpolator;
import org.clueminer.math.Interpolator;
import org.jzy3d.plot3d.builder.Mapper;

/**
 *
 * @author Tomas Barton
 */
public class SpectrumMapper extends Mapper {

    private SpectrumDataset<MassSpectrum> dataset;
    private Interpolator interpolator = new CubicInterpolator();

    public void setDataset(SpectrumDataset<MassSpectrum> dataset) {
        this.dataset = dataset;
    }
    
    /**
     * 
     * @param x is just index of time measurement
     * @param y
     * @return 
     */
    @Override
    public double f(double x, double y) {
        int xidx = (int) x;
        
        MassSpectrum inst = dataset.get(xidx);
        return inst.zValueAt(y, dataset.getTimePoints());                       
    }
}
