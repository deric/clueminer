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
package org.clueminer.utils.exec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 * Loader of text/binary files from java classpath
 *
 * Inspired by
 * @link http://stackoverflow.com/questions/3923129/get-a-list-of-resources-from-classpath-directory
 *
 * @author deric
 */
public abstract class ResourceLoader {

    public static final String OS = System.getProperty("os.name").toLowerCase();

    public abstract Enumeration<URL> searchURL(String path) throws IOException;

    /**
     * Try loading file from default package
     *
     * @param path
     * @return
     */
    public abstract File resource(String path);

    /**
     * for all elements of java.class.path get a Collection of resources Pattern
     * pattern = Pattern.compile(".*"); gets all resources
     *
     * @param needle first part of path
     * @param packageName hint a package name to search
     * @return the resources in the order they are found
     */
    public Collection<String> getResources(String needle, String packageName) {
        final List<String> retval = new LinkedList<>();
        final String classPath = System.getProperty("java.class.path", ".");
        String pathSeparator;
        //platform independent regexp
        Pattern pattern = Pattern.compile("(.*)" + needle);
        if (isWindows()) {
            try {
                Enumeration<URL> en = searchURL(packageName);
                if (en.hasMoreElements()) {
                    URL metaInf = en.nextElement();
                    File fileMetaInf = Utilities.toFile(metaInf.toURI());
                    browseFiles(retval, fileMetaInf, pattern);
                }
            } catch (IOException | URISyntaxException ex) {
                Exceptions.printStackTrace(ex);
            }
            if (retval.size() > 0) {
                return retval;
            }
            pathSeparator = ";";
        } else {
            pathSeparator = ":";
        }
        //when running from IDE we can use classpath a directly read files from disk
        final String[] classPathElements = classPath.split(pathSeparator);
        for (final String element : classPathElements) {
            retval.addAll(getResources(element, pattern, packageName));
        }
        if (retval.isEmpty()) {
            //last resort, when compiled into JAR
            loadFromJar(retval, pattern, packageName);
        }
        return retval;
    }

    /**
     * Resource packed in jar is not possible to open directly, this method uses
     * a .tmp file which should be on exit deleted
     *
     * @param path
     * @param prefix
     * @param hintPackage
     * @return
     */
    public File resource(String path, String prefix, String hintPackage) {
        String resource = prefix + File.separatorChar + path;
        File file;

        URL url = getClass().getResource(resource);
        if (url == null) {
            //probably on Windows
            Collection<String> res = getResources(path, hintPackage);
            if (res.isEmpty()) {
                throw new RuntimeException("could not find binary! Was searching for: " + resource + ", path: " + path);
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
            long time = System.currentTimeMillis();
            file = File.createTempFile("resource-" + time, ".tmp");
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

    public void printStdout(Process p) {
        System.out.println(readStdout(p));
    }

    public String readStdout(Process p) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return sb.toString();
    }

    public void printStderr(Process p) {
        System.out.println(readStderr(p));
    }

    public String readStderr(Process p) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line;
            while ((line = input.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return sb.toString();
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

    private static void browseFiles(final List<String> retval, File fileMetaInf, final Pattern pattern) {
        File[] files = fileMetaInf.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                browseFiles(retval, f, pattern);
            } else {
                String fileName = f.getAbsolutePath();
                final boolean accept = pattern.matcher(fileName).matches();
                if (accept) {
                    retval.add(fileName);
                }
            }
        }
    }

    private static Collection<String> getResources(final String element, final Pattern pattern, String packageName) {
        final List<String> retval = new LinkedList<>();
        final File file = new File(element);
        if (file.isDirectory()) {
            retval.addAll(getResourcesFromDirectory(file, pattern));
        } else {
            if (file.exists()) {
                retval.addAll(findResourcesInJarFile(file, pattern, packageName));
            } else {
                System.err.println("can't open file: " + file);
            }
        }
        return retval;
    }

    /**
     * Find resources matching given pattern in a JAR file
     *
     * @param file
     * @param pattern
     * @return
     */
    private static Collection<String> findResourcesInJarFile(final File file, final Pattern pattern, String packageName) {
        final List<String> retval = new LinkedList<>();

        String jarFilePath = file.getAbsolutePath();
        try (JarFile jar = new JarFile(file)) {
            if (!packageName.isEmpty() && !file.getAbsolutePath().contains(packageName)) {
                //skip this jar
                return retval;
            }
            JarEntry entry;
            String fileName;
            Enumeration<JarEntry> enumer = jar.entries();
            while (enumer.hasMoreElements()) {
                entry = enumer.nextElement();
                fileName = entry.getName();
                if (pattern.matcher(fileName).matches()) {
                    //don't add directories
                    if (!fileName.endsWith("/")) {
                        retval.add("jar:" + jarFilePath + "!" + fileName);
                    }
                }
            }

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return retval;
    }

    private static Collection<String> getResourcesFromDirectory(
            final File directory,
            final Pattern pattern) {
        final List<String> retval = new LinkedList<>();
        final File[] fileList = directory.listFiles();
        for (final File file : fileList) {
            if (file.isDirectory()) {
                retval.addAll(getResourcesFromDirectory(file, pattern));
            } else {
                try {
                    final String fileName = file.getCanonicalPath();
                    final boolean accept = pattern.matcher(fileName).matches();
                    if (accept) {
                        retval.add(fileName);
                    }
                } catch (final IOException e) {
                    throw new Error(e);
                }
            }
        }
        return retval;
    }

    public static boolean isWindows() {
        return (OS.contains("win"));
    }

    public static boolean isMac() {
        return (OS.contains("mac"));
    }

    public static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
    }

    public static boolean isSolaris() {
        return (OS.contains("sunos"));
    }

    /**
     * List resources from compiled JAR and choose those matching some pattern
     *
     * @param retval
     * @param pattern
     */
    private void loadFromJar(List<String> retval, Pattern pattern, String folder) {
        try {
            Enumeration<URL> en = searchURL(folder);
            if (en.hasMoreElements()) {
                URL metaInf = en.nextElement();
                File file;

                String path = metaInf.getPath();
                String jarFilePath = path.substring(path.indexOf(":") + 1, path.indexOf("!"));
                jarFilePath = URLDecoder.decode(jarFilePath, "UTF-8");
                file = new File(jarFilePath);

                try (JarFile jar = new JarFile(file)) {
                    JarEntry entry;
                    String fileName;
                    Enumeration<JarEntry> enumer = jar.entries();
                    while (enumer.hasMoreElements()) {
                        entry = enumer.nextElement();
                        fileName = entry.getName();
                        if (pattern.matcher(fileName).matches()) {
                            //don't add directories
                            if (!fileName.endsWith("/")) {
                                retval.add("jar:" + jarFilePath + "!" + fileName);
                            }
                        }
                    }

                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
