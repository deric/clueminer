/*
 * == hMETIS
 *
 * The hMETIS package is copyrighted by the Regents of the University of
 * Minnesota. It is meant to be used solely for educational, research,
 * and benchmarking purposes by non-profit institutions and US government
 * agencies only. Other organizations are allowed to use hMETIS for evaluation
 * purposes only.
 * The software may not be sold or redistributed. One may make copies of the
 * software for their use provided that the copies, are not sold or distributed,
 * are used under the same terms and conditions.
 * As unestablished research software, this code is provided on an ``as is''
 * basis without warranty of any kind, either expressed or implied.
 * The downloading, or executing any part of this software constitutes an
 * implicit agreement to these terms. These terms and conditions are subject
 * to change at any time without prior notice.
 *
 *
 * == METIS
 *
 * Copyright 1995-2013, Regents of the University of Minnesota
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */
package edu.umn.cluto;

import org.clueminer.clustering.api.Configurator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;

/**
 *
 * @author deric
 */
public class ClutoConfig<E extends Instance> implements Configurator<E> {

    private static ClutoConfig instance;

    private ClutoConfig() {

    }

    public static ClutoConfig getInstance() {
        if (instance == null) {
            instance = new ClutoConfig();
        }
        return instance;
    }

    @Override
    public void configure(Dataset<E> dataset, Props params) {
        //TODO
    }

}
