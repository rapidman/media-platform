package com.mediaplatform.web;

import com.mediaplatform.manager.ConfigBean;
import com.mediaplatform.manager.file.FileStorageManager;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * User: timur
 * Date: 12/2/12
 * Time: 3:14 PM
 */
public class ResourceServlet extends HttpServlet {
    @Inject
    private ConfigBean configBean;

    @Inject
    private FileStorageManager fileStorageManager;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String uri = req.getRequestURI().replaceAll(req.getContextPath(), "").replaceAll(configBean.getResourceServletMapping(), "");
        File requestedFile = new File(fileStorageManager.getFileStorageDir(), uri);

        String contentType = (req.getParameter("download") == null? getServletContext().getMimeType(requestedFile.getName()): "application/force-download");
        resp.setContentType(contentType);

        byte[] buffer = new byte[1024];
        InputStream in = new FileInputStream(requestedFile);
        OutputStream out = resp.getOutputStream();
        try {
            int bytesRead;
            while ((bytesRead = in.read(buffer))>0){
                out.write(buffer, 0, bytesRead);
            }
        } finally {
            out.close();
            in.close();
        }
    }

}
