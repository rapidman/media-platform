package com.mediaplatform.util;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: timur
 * Date: 12/9/12
 * Time: 3:12 PM
 */
public class RunShellCmdHelper {
    private static ExecutorService service = Executors.newFixedThreadPool(100);
    public static final String COMMAND = "ffmpeg";


    private RunShellCmdHelper() {
    }

    public static void publish(final String mediaFilePath,final String streamName, final RtmpPublishFormat format) {
        service.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try {
                    List<String> commands = new ArrayList<String>();
                    commands.add(COMMAND);
                    for(String arg: format.getArgs()){
                        if(arg.equals("%MEDIA_FILE%")){
                            commands.add(mediaFilePath);
                        }else if(arg.startsWith("rtmp")){
                            commands.add(String.format(arg, streamName));
                        }else{
                            commands.add(arg);
                        }

                    }
                    ProcessBuilder builder = new ProcessBuilder(commands);
                    builder.directory(null);
                    run(builder);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("ERROR.RUNNING.CMD");
                }
                return null;
            }
        });
    }

    public static void dropStream(final String streamName, final String controlServiceUrl){
        service.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try {
                    List<String> commands = new ArrayList<String>();
                    commands.add("curl");
                    commands.add(String.format(controlServiceUrl, streamName));
                    ProcessBuilder builder = new ProcessBuilder(commands);
                    builder.directory(null);
                    run(builder);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("ERROR.RUNNING.CMD");
                }
                return null;
            }
        });
    }

    private static void run(ProcessBuilder builder) throws IOException {
        final Process process = builder.start();
        InputStreamReader isr = new InputStreamReader(process.getInputStream());
        InputStreamReader errstr = new InputStreamReader(process.getErrorStream());
        try {
            readStream(isr);
            readStream(errstr);
        } finally {
            IOUtils.closeQuietly(isr);
            IOUtils.closeQuietly(errstr);
        }

    }

    private static void readStream(InputStreamReader isr) throws IOException {
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }
}
