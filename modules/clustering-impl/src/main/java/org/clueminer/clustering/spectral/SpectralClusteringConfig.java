/*
 * Copyright (C) 2011-2018 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.clustering.spectral;

import org.clueminer.clustering.api.Configurator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 *
 * @author mikusmi1
 * @param <E>
 */
public class SpectralClusteringConfig<E extends Instance> implements Configurator<E> {
    
    private static SpectralClusteringConfig instance;
    
    private SpectralClusteringConfig(){
    
    }
    
    public static SpectralClusteringConfig getInstance(){
            if(instance == null){
                instance = new SpectralClusteringConfig();
            }
            return instance;  
    }
    
    @Override
    public void configure(Dataset<E> dataset, Props params){
        int k = (int) Math.sqrt(dataset.size() / 2);
        if (k < 2) {
            //smaller k doesn't make sense
            k = 2;
        }
        params.putInt(SpectralClustering.K, k);
    }   
}
