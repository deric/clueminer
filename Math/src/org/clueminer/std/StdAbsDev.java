package org.clueminer.std;

import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Standardisation.class)
public class StdAbsDev extends StdDev {
    private static String name = "Standardised measurement";
    
    @Override
    public String getName(){
        return name;
    }
    
    @Override
   protected void computeDev(double[][] a, double[] avg, double[] dev, int m, int n){
        int i, j;
        //square computeDev from average
        for(j=0; j<n; j++){
            for(i=0; i <m; i++){
                dev[j] += Math.abs(a[i][j] - avg[j]);
            }
            //according to some statisticians using (m-1) is more precise than just m
            dev[j] = Math.sqrt(dev[j]/(m-1));
        }
    }
}
