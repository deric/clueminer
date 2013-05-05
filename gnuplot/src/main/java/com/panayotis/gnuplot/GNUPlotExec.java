/*
 * GNUPlotExec.java
 *
 * Created on October 14, 2007, 1:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.panayotis.gnuplot;

import com.panayotis.iodebug.Debug;
import com.panayotis.gnuplot.terminal.GNUPlotTerminal;
import com.panayotis.io.FileUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Object representing the low level gnuplot executable. This object is used in the plot() method of GNUPlot.
 * @author teras
 */
class GNUPlotExec {

    private static final transient String DEFAULT_PATH = FileUtils.findPathExec("gnuplot");
    private transient File gnuplotexec;
    private boolean ispersist;
    private final static String[] persistcommand = {"path", "-persist"};
    private final static String[] nopersist = {"path"};

    /**
     * Create a new GNUPlotExec object with  defaultΩ gnuplot path. Under POSIX environment,
     * it is able to automatically find gnuplot executable in the $PATH
     * @throws java.io.IOException if something went wrong and it is impossible to continue without further notive
     */
    GNUPlotExec() throws IOException {
        this(null);
    }

    /**
     * Create a new GNUPlotExec object with a specific gnuplot path.
     * @param path Path of gnuplot executable
     * @throws java.io.IOException if the gnuplot executable is not found
     */
    GNUPlotExec(String path) throws IOException {
        if (path == null) {
            path = DEFAULT_PATH;
        }
        setGNUPlotPath(path);
        ispersist = true;
    }

    /**
     * Set the desired path for gnuplot executable.
     * @param path Filename of gnuplot executable
     * @throws java.io.IOException gnuplot is not found, or not valid
     */
    void setGNUPlotPath(String path) throws IOException {
        File file = FileUtils.getExec(path);
        if (file != null) {
            gnuplotexec = file;
        } else {
            throw new IOException("GnuPlot executable \"" + path + "\" not found.");
        }
    }

    /**
     * Retrieve the file path of gnuplot
     * @return The gnuplot file path
     */
    String getGNUPlotPath() {
        return gnuplotexec.getPath();
    }

    /**
     * Retrieves the command which will actually send to gnuplot, if we perform a plot
     * with the given parameters to the selected terminal. <br>
     * This method is used for debugging purposes.
     * @return The commands to send to the gnuplot executable
     */
    public String getCommands(GNUPlotParameters par, GNUPlotTerminal terminal) {
        // Could cache these commands..
        return par.getPlotCommands(terminal);
    }

    /**
     * Plot using specific parameters and selected terminal.
     *
     * @param par The parameters to use
     * @param terminal The terminal to use
     * @throws com.panayotis.gnuplot.GNUPlotException throw if something goes wrong
     */
    void plot(GNUPlotParameters par, GNUPlotTerminal terminal) throws GNUPlotException {
        try {
            final GNUPlotTerminal term = terminal;  // Use this thread-aware variable instead of "terminal"
            final String comms = getCommands(par, term); // Get the commands to send to gnuplot
            final Messages msg = new Messages();    // Where to store messages from output threads

            /* Display plot commands to send to gnuplot */
            GNUPlot.getDebugger().msg("** Start of plot commands **", Debug.INFO);
            GNUPlot.getDebugger().msg(comms, Debug.INFO);
            GNUPlot.getDebugger().msg("** End of plot commands **", Debug.INFO);

            /* It's time now to start the actual gnuplot application */
            String[] command;
            if (ispersist) {
                command = persistcommand;
            } else {
                command = nopersist;
            }
            command[0] = getGNUPlotPath();
            {
                String cmdStr = "";
                for (String cmd : command) {
                    cmdStr += cmd + " ";
                }
                GNUPlot.getDebugger().msg("exec(" + cmdStr + ")", Debug.INFO);
            }
            final Process proc = Runtime.getRuntime().exec(command);

            /* Windows buffers DEMAND asynchronus read & write */

            /* Thread to process the STDERR of gnuplot */
            Thread err_thread = new Thread() {

                public void run() {
                    BufferedReader err = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
                    StringBuffer buf = new StringBuffer();
                    String line;
                    try {
                        while ((line = err.readLine()) != null) {
                            line = parseErrorLine(line, "gnuplot> plot");
                            line = parseErrorLine(line, "multiplot> plot");
                            line = line.replace("input data ('e' ends) >", "").trim();   // Remove entries having the "input data" prompt
                            if (line.equals("^")) {
                                line = "";
                            }  // Ignore line with error pointer
                            if (!line.equals("")) {     // Only take care of not empty lines
                                if (line.indexOf(GNUPlotParameters.ERRORTAG) >= 0) {
                                    msg.error = "Error while parsing \'plot\' arguments.";    // Error was found in plot command
                                    break;
                                }
                                buf.append(line).append('\n');
                            }
                        }
                        err.close();
                        msg.output = buf.toString(); // Store output stream
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            };
            /* Thread to process the STDOUT of gnuplot */
            err_thread.start();
            Thread out_thread = new Thread() {

                public void run() {
                    msg.process = term.processOutput(proc.getInputStream());    // Execute terminal specific output parsing
                }
            };
            out_thread.start();

            /* We utilize the current thread for gnuplot execution */
            OutputStreamWriter out = new OutputStreamWriter(proc.getOutputStream());
            out.write(comms);
            out.flush();
            out.close();


            try {
                proc.waitFor(); // wait for process to finish
                out_thread.join();  // wait for output (terminal related) thread to finish
                err_thread.join();  // wait for error (messages) output to finish
            } catch (InterruptedException ex) {
                throw new GNUPlotException("Interrupted execution of gnuplot");
            }

            /* Find the error message, if any, with precendence to the error thread */
            String message = null;
            if (msg.error != null) {
                message = msg.error;
            } else {
                message = msg.process;
            }

            /* Determine if error stream should be dumbed or not */
            int level = Debug.VERBOSE;
            if (message != null) {
                level = Debug.ERROR;
            }
            GNUPlot.getDebugger().msg("** Start of error stream **", level);
            GNUPlot.getDebugger().msg(msg.output, level);
            GNUPlot.getDebugger().msg("** End of error stream **", level);

            /* Throw an exception if an error occured */
            if (message != null) {
                throw new GNUPlotException(message);
            }

        } catch (IOException ex) {
            throw new GNUPlotException("IOException while executing \"" + getGNUPlotPath() + "\":" + ex.getLocalizedMessage());
        }

    }

    void setPersist(boolean persist) {
        ispersist = persist;
    }

    private String parseErrorLine(String error, String tag) {
        if (error.startsWith(tag) && error.endsWith(GNUPlotParameters.NOERROR_COMMAND)) {
            StringBuffer bf = new StringBuffer("Error in:");
            bf.append(error.substring(tag.length(), error.length()-GNUPlotParameters.NOERROR_COMMAND.length()));
            return bf.toString();
        }
        return error;
    }

    private class Messages {

        String output = "";
        String error = null;
        String process = null;
    }
}
