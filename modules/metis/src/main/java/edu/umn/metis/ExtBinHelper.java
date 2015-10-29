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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Collection;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.openide.util.Exceptions;

/**
 * Helper for running external binaries
 *
 * @author deric
 * @param <E>
 */
public class ExtBinHelper<E extends Instance> {

    private static final String lineEnd = "\n";
    private static final String space = " ";
    protected static final String prefix = "/org/clueminer/partitioning/impl";

    /**
     * Write dataset into a file as space separated values
     *
     * @param dataset
     * @param file
     * @throws FileNotFoundException
     */
    public void exportDataset(Dataset<E> dataset, File file) throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
            sb.append(dataset.size()).append(space).append(dataset.attributeCount()).append(lineEnd);
            writer.write(sb.toString());

            for (E instance : dataset) {
                sb = new StringBuilder();
                for (int i = 0; i < instance.size(); i++) {
                    if (i > 0) {
                        sb.append(space);
                    }
                    sb.append(String.valueOf(instance.get(i)));
                }
                sb.append(lineEnd);
                writer.write(sb.toString());
            }

        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Resource packed in jar is not possible to open directly, this method uses
     * a .tmp file which should be on exit deleted
     *
     * @param path
     * @return
     */
    public File resource(String path) {
        String resource = prefix + File.separatorChar + path;
        File file;
        URL url = Metis.class.getResource(resource);
        if (url == null) {
            //probably on Windows
            Collection<String> res = ResourceLoader.getResources(path);
            if (res.isEmpty()) {
                throw new RuntimeException("could not find metis binary!");
            }
            String fullPath = res.iterator().next();
            file = new File(fullPath);
            if (file.exists()) {
                return file;
            }
            //non existing URL
            //no classpath, compiled as JAR
            //if path is in form: "jar:path.jar!resource/data"
            int pos = fullPath.lastIndexOf("!");
            if (pos > 0) {
                resource = fullPath.substring(pos + 1);
                if (!resource.startsWith("/")) {
                    //necessary for loading as a stream
                    resource = "/" + resource;
                }
            }
            return loadResource(resource);
        }

        if (url.toString().startsWith("jar:")) {
            return loadResource(resource);
        } else {
            file = new File(url.getFile());
        }
        return file;
    }

    protected File loadResource(String resource) {
        File file = null;
        try {
            InputStream input = getClass().getResourceAsStream(resource);
            file = File.createTempFile("metis", ".tmp");
            try (OutputStream out = new FileOutputStream(file)) {
                int read;
                byte[] bytes = new byte[1024];

                while ((read = input.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
            }
            file.deleteOnExit();
        } catch (IOException ex) {
            System.err.println(ex.toString());
        }
        return file;
    }

    public void readStdout(Process p) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void readStderr(Process p) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line;
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public static String safeName(String name) {
        return name.toLowerCase().replace(" ", "_");
    }

    public static String readFile(File file) throws FileNotFoundException, IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        }
    }
}
