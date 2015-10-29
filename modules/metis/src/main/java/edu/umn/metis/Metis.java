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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Graph;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.partitioning.api.Partitioning;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 * Partitioning via Metis software. In order to use the class, path to gpmetis
 * software has to be specified in the metisPath variable.
 *
 * @author Tomas Bruna
 */
@ServiceProvider(service = Partitioning.class)
public class Metis extends AbstractMetis implements Partitioning {

    private String ptype;
    private final ExtBinHelper<Instance> helper;

    public Metis() {
        this("kway");
    }

    public Metis(String ptype) {
        this.ptype = ptype;
        if (!"kway".equals(ptype) && !"rb".equals(ptype)) {
            throw new InvalidParameterException("Parameter ptype cannot have " + ptype + " value");
        }
        helper = new ExtBinHelper<>();
    }

    @Override
    public String getName() {
        return "Metis";
    }

    @Override
    public void setBisection(Bisection bisection) {
        // cannot change in Metis
    }

    @Override
    public String runMetis(Graph graph, int k) {
        String metis = graph.metisExport(false);
        long current = System.currentTimeMillis();
        File file = new File("inputGraph-" + current);
        String path = file.getName();
        try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
            writer.print(metis);
            writer.close();
            File metisFile = helper.resource("gpmetis");
            //make sure metis is executable
            Process p = Runtime.getRuntime().exec("chmod u+x " + metisFile.getAbsolutePath());
            p.waitFor();
            //run metis
            p = Runtime.getRuntime().exec(metisFile.getAbsolutePath() + " -ptype=" + ptype + " " + path + " " + String.valueOf(k));
            p.waitFor();
            helper.readStdout(p);
            file.delete();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException | InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
        return path;
    }

    public void setPtype(String ptype) {
        this.ptype = ptype;
        if (!"kway".equals(ptype) && !"rb".equals(ptype)) {
            throw new InvalidParameterException("Parameter ptype cannot have " + ptype + " value");
        }
    }

}
