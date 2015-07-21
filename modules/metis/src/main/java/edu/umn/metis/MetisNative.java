/*
 * Copyright (C) 2011-2015 clueminer.org
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
package edu.umn.metis;

import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.partitioning.api.Partitioning;

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
    public ArrayList<LinkedList<Node>> partition(int k, Graph g) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setBisection(Bisection bisection) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
