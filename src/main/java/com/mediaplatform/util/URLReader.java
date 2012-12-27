package com.mediaplatform.util;

import org.apache.commons.io.IOUtils;
import org.jboss.solder.logging.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * User: timur
 * Date: 12/13/12
 * Time: 7:53 PM
 */
public class URLReader {
    private static Logger logger = Logger.getLogger(URLReader.class);
    private URLReader(){}

    public static String read(String url) {
        BufferedReader in = null;
        StringBuffer sb = new StringBuffer();
        try {
            URL oracle = new URL(url);
            in = new BufferedReader(
                    new InputStreamReader(oracle.openStream(), "UTF-8"));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }
            return sb.toString();
        } catch (Exception e) {
            String msg = "Error while reading url";
            logger.error(msg, e);
            throw new RuntimeException(msg, e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
}
