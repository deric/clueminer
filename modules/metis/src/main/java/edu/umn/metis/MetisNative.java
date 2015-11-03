/**
 *
 * Copyright 1995-2013, Regents of the University of Minnesota
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package edu.umn.metis;

import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.partitioning.api.Partitioning;
import org.clueminer.utils.Props;

/**
 * Download metis: http://glaros.dtc.umn.edu/gkhome/metis/metis/download
 *
 * Run: (ensure you have cmake installed)
 *
 * $ make config shared=1
 *
 * $ make
 *
 * $ sudo make install
 *
 * Headers can be updated using `javah` command from
 *
 * metis/src/main/java
 *
 * execute:
 *
 * javah -jni edu.umn.metis.MetisNative
 *
 *
 * @author deric
 */
public class MetisNative implements Partitioning {

    private static final String name = "METIS (native)";

    /*

     METIS_API(int) METIS_PartGraphRecursive(idx_t *nvtxs, idx_t *ncon, idx_t *xadj,
     idx_t *adjncy, idx_t *vwgt, idx_t *vsize, idx_t *adjwgt,
     idx_t *nparts, real_t *tpwgts, real_t *ubvec, idx_t *options,
     idx_t *edgecut, idx_t *part);

     METIS_API(int) METIS_PartGraphKway(idx_t *nvtxs, idx_t *ncon, idx_t *xadj,
     idx_t *adjncy, idx_t *vwgt, idx_t *vsize, idx_t *adjwgt,
     idx_t *nparts, real_t *tpwgts, real_t *ubvec, idx_t *options,
     idx_t *edgecut, idx_t *part);

     METIS_API(int) METIS_MeshToDual(idx_t *ne, idx_t *nn, idx_t *eptr, idx_t *eind,
     idx_t *ncommon, idx_t *numflag, idx_t **r_xadj, idx_t **r_adjncy);

     METIS_API(int) METIS_MeshToNodal(idx_t *ne, idx_t *nn, idx_t *eptr, idx_t *eind,
     idx_t *numflag, idx_t **r_xadj, idx_t **r_adjncy);

     METIS_API(int) METIS_PartMeshNodal(idx_t *ne, idx_t *nn, idx_t *eptr, idx_t *eind,
     idx_t *vwgt, idx_t *vsize, idx_t *nparts, real_t *tpwgts,
     idx_t *options, idx_t *objval, idx_t *epart, idx_t *npart);

     METIS_API(int) METIS_PartMeshDual(idx_t *ne, idx_t *nn, idx_t *eptr, idx_t *eind,
     idx_t *vwgt, idx_t *vsize, idx_t *ncommon, idx_t *nparts,
     real_t *tpwgts, idx_t *options, idx_t *objval, idx_t *epart,
     idx_t *npart);

     METIS_API(int) METIS_NodeND(idx_t *nvtxs, idx_t *xadj, idx_t *adjncy, idx_t *vwgt,
     idx_t *options, idx_t *perm, idx_t *iperm);

     METIS_API(int) METIS_Free(void *ptr);

     */
    static {

        try {
            //will load library from system path
            System.loadLibrary("metis");
            //System.load("/usr/local/lib/libmetis.so");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load.\n" + e);
        }
    }

    public native int PartGraphRecursive();

    public native int PartGraphKway();

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ArrayList<LinkedList<Node>> partition(int k, Graph g, Props params) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setBisection(Bisection bisection) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
