package org.clueminer.xcalibour.files;

import java.util.ArrayList;
import java.util.List;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;

public class MyOrthoGrid extends OrthonormalGrid {

    public MyOrthoGrid(Range xyrange, int xysteps) {
        super(xyrange, xysteps);
    }

    public MyOrthoGrid(Range xrange, int xsteps, Range yrange, int ysteps) {
        super(xrange, xsteps, yrange, ysteps);
    }

    @Override
    public List<Coord3d> apply(Mapper mapper) {
        double xstep = xrange.getRange() / (double) (xsteps - 1);
        double ystep = yrange.getRange() / (double) (ysteps - 1);

        List<Coord3d> output = new ArrayList<Coord3d>(xsteps * ysteps);

        double filter = 1e4;
        for (int xi = 0; xi < xsteps; xi++) {
            for (int yi = 0; yi < ysteps; yi++) {
                double x = xrange.getMin() + xi * xstep;
                double y = yrange.getMin() + yi * ystep;
                double z = mapper.f(x, y);
                if (z > filter) {
                    output.add(new Coord3d(x, y, z));
                }
            }
        }
        return output;
    }
    /* The former method that implied an ever centered surface.
	  
     public List<Coord3d> apply(Mapper mapper) {
     double xstep = xrange.getRange() / (double)xsteps;
     double ystep = yrange.getRange() / (double)ysteps;
	
     List<Coord3d> output = new ArrayList<Coord3d>((xsteps-1)*(ysteps-1));
	
     for(int xi=-(xsteps-1)/2; xi<=(xsteps-1)/2; xi++){
     for(int yi=-(ysteps-1)/2; yi<=(ysteps-1)/2; yi++){
     output.add( new Coord3d(xi*xstep, yi*ystep, mapper.f(xi*xstep, yi*ystep) ) );
     }
     }
     return output;
     }*/
}