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
package org.clueminer.partitioning.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import org.clueminer.graph.api.Graph;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.partitioning.api.Partitioning;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 * See hMETIS documentation for detailed options explanation
 *
 * TODO: currently we're calling directly Linux x64 binaries, make it platform
 * independent
 *
 * @author deric
 */
@ServiceProvider(service = Partitioning.class)
public class HMetis extends AbstractMetis implements Partitioning {

    private static final String name = "hMETIS";

    private int ubFactor = 5;
    private int nruns = 10;//default value used by smetis
    private int rtype = 1;// 1-3
    private int ctype = 1;// 1-5
    private int vcycle = 1;// 0-3
    private int reconst = 0;// 0-1
    private int dbglvl = 0;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void runMetis(Graph graph, int k) {
        String metis = graph.hMetisExport(false);
        try (PrintWriter writer = new PrintWriter("inputGraph", "UTF-8")) {
            writer.print(metis);
            writer.close();
            File metisFile = resource("hmetis");
            //make sure metis is executable
            Process p = Runtime.getRuntime().exec("chmod ugo+x " + metisFile.getAbsolutePath());
            p.waitFor();
            //run metis
            String space = " ";
            StringBuilder sb = new StringBuilder(metisFile.getAbsolutePath());
            sb.append(" inputGraph ")
                    .append(String.valueOf(k)).append(space)
                    .append(String.valueOf(ubFactor)).append(space)
                    .append(String.valueOf(nruns)).append(space)
                    .append(String.valueOf(ctype)).append(space)
                    .append(String.valueOf(rtype)).append(space)
                    .append(String.valueOf(vcycle)).append(space)
                    .append(String.valueOf(reconst)).append(space)
                    .append(String.valueOf(dbglvl));
            System.out.println("cmd: " + sb.toString());
            p = Runtime.getRuntime().exec(sb.toString());
            p.waitFor();
            readStdout(p);
            File file = new File("inputGraph");
            file.delete();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException | InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void setBisection(Bisection bisection) {
        //TODO: not supported by hmetis?
    }

    public int getUbFactor() {
        return ubFactor;
    }

    public void setUbFactor(int ubFactor) {
        this.ubFactor = ubFactor;
    }

}
