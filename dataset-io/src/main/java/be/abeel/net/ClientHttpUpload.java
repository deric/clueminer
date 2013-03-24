package be.abeel.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Utility class to easily upload files to an URL.
 * 
 * @author Thomas Abeel
 * 
 */
public class ClientHttpUpload {

    /**
     * Uploads a file to a particular URL. The reply of the server will be
     * returned by this method.
     * 
     * @param tmpFile
     *            file to store
     * @param url
     *            URL to store data to
     * @return the reply of the server
     * @throws IOException
     */
    public static String upload(File tmpFile, URL url) throws IOException {

        /* Used to store the reply from the server */
        StringBuffer reply = new StringBuffer();

        URLConnection uc = url.openConnection();
        /* Build HTTP request */
        ClientHttpRequest request = new ClientHttpRequest(uc);
        String file = "file";
        String filename = tmpFile.getName();
        InputStream fileinput = new FileInputStream(tmpFile);
        request.setParameter(file, filename, fileinput);

        /* Read server reply */
        InputStream serverInput = request.post();
        BufferedReader in = new BufferedReader(new InputStreamReader(serverInput));
        String line = in.readLine();
        while (line != null) {
            reply.append(line+"\n");
            line = in.readLine();
         
        }
        in.close();

        return reply.toString();

    }

}