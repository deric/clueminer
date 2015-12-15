/*
 * The hMETIS package is copyrighted by the Regents of the University of
 * Minnesota. It is meant to be used solely for educational, research, and
 * benchmarking purposes by non-profit institutions and US government agencies
 * only. Other organizations are allowed to use hMETIS for evaluation purposes
 * only. The software may not be sold or redistributed. One may make copies of
 * the software for their use provided that the copies, are not sold or
 * distributed, are used under the same terms and conditions. As unestablished
 * research software, this code is provided on an ``as is'' basis without
 * warranty of any kind, either expressed or implied. The downloading, or
 * executing any part of this software constitutes an implicit agreement to
 * these terms. These terms and conditions are subject to change at any time
 * without prior notice.
 */
package edu.umn.metis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.api.Graph;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.partitioning.api.Partitioning;
import org.clueminer.utils.Props;
import org.clueminer.utils.SystemInfo;
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

    public static final String UFACTOR = "ufactor";
    public static final String NRUNS = "nruns";
    public static final String PTYPE = "ptype";
    public static final String RTYPE = "rtype";
    public static final String CTYPE = "ctype";
    public static final String OTYPE = "otype";

    protected int vcycle = 1;// 0-3
    protected int reconst = 0;// 0-1
    protected int dbglvl = 0;
    private final ExtBinHelper<Instance> helper;
    private boolean debug = false;
    private static File metisFile = null;

    public HMetis() {
        helper = new ExtBinHelper();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String runMetis(Graph graph, int k, Props params) {
        long current = System.currentTimeMillis();
        File file = new File("inputGraph-" + current);
        String filename = file.getName();
        Process p;
        try {
            graph.hMetisExport(file, false);
            if (metisFile == null) {
                //fetch the file just once
                metisFile = getBinary();
                System.out.println("metis path: " + metisFile.getAbsolutePath());
            }

            //run metis
            String space = " ";
            StringBuilder sb = new StringBuilder(metisFile.getAbsolutePath());
            sb.append(" ").append(filename).append(" ")
                    .append(String.valueOf(k)).append(space)
                    .append("-ufactor=").append(String.valueOf(params.getDouble(UFACTOR, 5.0))).append(space)
                    .append("-nruns=").append(String.valueOf(params.getInt(NRUNS, 10))).append(space)
                    .append("-ptype=").append(String.valueOf(params.get(PTYPE, "rb"))).append(space)
                    .append("-otype=").append(String.valueOf(params.get(OTYPE, "cut"))).append(space);
            if (params.containsKey(CTYPE)) {
                sb.append("-ctype=").append(params.get(CTYPE)).append(space);
            }
            if (params.containsKey(RTYPE)) {
                sb.append("-rtype=").append(params.get(RTYPE)).append(space);
            }

            //sb.append(String.valueOf(vcycle)).append(space);
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

    protected File getBinary() throws IOException, InterruptedException {
        File f = helper.resource("hmetis2");
        if (!f.exists()) {
            throw new RuntimeException("file " + f.getAbsolutePath() + "does not exist!");
        }
        if (SystemInfo.isLinux()) {
            //make sure metis is executable
            Process p = Runtime.getRuntime().exec("chmod ugo+x " + f.getAbsolutePath());
            p.waitFor();
            helper.readStdout(p);
            helper.readStderr(p);
        }
        return f;
    }

    public void cleanup() {
        if (metisFile != null && metisFile.exists()) {
            System.out.println("deleting " + metisFile.getAbsolutePath());
            metisFile.deleteOnExit();
        }
    }

    @Override
    public void setBisection(Bisection bisection) {
        //TODO: not supported by hmetis?
    }

}
