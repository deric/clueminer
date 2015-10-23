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
import java.io.UnsupportedEncodingException;
import org.clueminer.dataset.api.Instance;
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

    private static final String name = "hMETIS + FF";

    protected int ubFactor = 5;
    protected int nruns = 10;//default value used by smetis
    protected int rtype = 1;// 1-3
    protected int ctype = 1;// 1-5
    protected int vcycle = 1;// 0-3
    protected int reconst = 0;// 0-1
    protected int dbglvl = 0;
    private final ExtBinHelper<Instance> helper;
    private boolean debug = false;

    public HMetis() {
        helper = new ExtBinHelper();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String runMetis(Graph graph, int k) {
        long current = System.currentTimeMillis();
        File file = new File("inputGraph-" + current);
        String filename = file.getName();
        try {
            graph.hMetisExport(file, false);
            File metisFile = getBinary();
            //make sure metis is executable
            Process p = Runtime.getRuntime().exec("chmod ugo+x " + metisFile.getAbsolutePath());
            p.waitFor();

            //run metis
            String space = " ";
            StringBuilder sb = new StringBuilder(metisFile.getAbsolutePath());
            sb.append(" ").append(filename).append(" ")
                    .append(String.valueOf(k)).append(space)
                    .append(String.valueOf(ubFactor)).append(space)
                    .append(String.valueOf(nruns)).append(space)
                    .append(String.valueOf(ctype)).append(space)
                    .append(String.valueOf(rtype)).append(space)
                    .append(String.valueOf(vcycle)).append(space)
                    .append(String.valueOf(reconst)).append(space)
                    .append(String.valueOf(dbglvl));

            p = Runtime.getRuntime().exec(sb.toString());
            p.waitFor();
            if (debug) {
                System.out.println("cmd: " + sb.toString());
                System.out.println("exit code: " + p.exitValue());
                if (p.exitValue() != 1) {
                    //System.out.println(ExtBinHelper.readFile(file));
                }

                helper.readStdout(p);
                helper.readStderr(p);
            }
            file.delete();

        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException | InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
        return filename;
    }

    protected File getBinary() {
        return helper.resource("hmetis");
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
