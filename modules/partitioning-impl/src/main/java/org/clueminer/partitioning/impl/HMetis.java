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
 *
 * @author deric
 */
@ServiceProvider(service = Partitioning.class)
public class HMetis extends AbstractMetis implements Partitioning {

    private static final String name = "hMETIS";

    private int ubFactor = 5;

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
            File metisFile = resource("shmetis");
            //make sure metis is executable
            Process p = Runtime.getRuntime().exec("chmod u+x " + metisFile.getAbsolutePath());
            p.waitFor();
            //run metis
            p = Runtime.getRuntime().exec(metisFile.getAbsolutePath() + " inputGraph " + String.valueOf(k) + " " + String.valueOf(ubFactor));
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
