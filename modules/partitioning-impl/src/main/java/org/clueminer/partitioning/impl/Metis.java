package org.clueminer.partitioning.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
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

    public Metis() {
        this("kway");
    }

    public Metis(String ptype) {
        this.ptype = ptype;
        if (!"kway".equals(ptype) && !"rb".equals(ptype)) {
            throw new InvalidParameterException("Parameter ptype cannot have " + ptype + " value");
        }
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
    public void runMetis(Graph graph, int k) {
        String metis = graph.metisExport(false);
        try (PrintWriter writer = new PrintWriter("inputGraph", "UTF-8")) {
            writer.print(metis);
            writer.close();
            File metisFile = resource("gpmetis");
            //make sure metis is executable
            Process p = Runtime.getRuntime().exec("chmod u+x " + metisFile.getAbsolutePath());
            p.waitFor();
            //run metis
            p = Runtime.getRuntime().exec(metisFile.getAbsolutePath() + " -ptype=" + ptype + " inputGraph " + String.valueOf(k));
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

    public void setPtype(String ptype) {
        this.ptype = ptype;
        if (!"kway".equals(ptype) && !"rb".equals(ptype)) {
            throw new InvalidParameterException("Parameter ptype cannot have " + ptype + " value");
        }
    }

}
