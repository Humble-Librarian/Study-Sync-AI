package com.studysync.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/local-pdf/*")
public class PdfServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(400, "No filename specified");
            return;
        }

        String filename = pathInfo.substring(1);
        try {
            filename = URLDecoder.decode(filename, StandardCharsets.UTF_8.name());
        } catch (Exception ignored) {}

        String dataDir = getServletContext().getInitParameter("studysync.data.dir");
        if (dataDir == null || dataDir.trim().isEmpty()) {
            dataDir = "C:/StudySync_Data"; // Default
        }

        File file = new File(dataDir, "pdfs/" + filename);
        if (!file.exists()) {
            resp.sendError(404, "File not found: " + file.getAbsolutePath());
            return;
        }

        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");

        try (FileInputStream in = new FileInputStream(file);
             OutputStream out = resp.getOutputStream()) {
             byte[] buffer = new byte[8192];
             int bytesRead;
             while ((bytesRead = in.read(buffer)) != -1) {
                  out.write(buffer, 0, bytesRead);
             }
        }
    }
}
