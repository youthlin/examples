package com.youthlin.demo.mvc.servlet;

import com.youthlin.debug.JavaClassExecutor;
import com.youthlin.debug.compiler.JavaCompilerForString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * 创建: youthlin.chen
 * 时间: 2018-03-12 11:33
 */
@MultipartConfig
@WebServlet(urlPatterns = "/upload")
public class FileUploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1149128606773053562L;
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        try {
            Part bytes = request.getPart("bytes");
            if (bytes != null) {
                InputStream in = bytes.getInputStream();
                int size = in.available();
                byte[] classBytes = new byte[size];
                int read = in.read(classBytes);
                LOGGER.info("read {} bytes", read);
                if (size > 0 && read > 0) {
                    String result = JavaClassExecutor.execute(classBytes);
                    resp.getWriter().println(result);
                    return;
                }
            }
        } catch (IOException | ServletException e) {
            LOGGER.error("", e);
        }


        String code = request.getParameter("code");
        String fileName = request.getParameter("fileName");
        String cp = request.getParameter("cp");
        String result = "";
        if (code != null && !code.isEmpty()) {
            if (JavaCompilerForString.supportCompiler()) {
                String classpath = "";
                if (cp != null && !cp.isEmpty()) {
                    classpath = cp + JavaClassExecutor.getPathSeparator();
                }
                classpath += JavaClassExecutor.getClasspath();
                StringWriter out = new StringWriter();
                byte[] bytes = JavaCompilerForString.compile(fileName, code, classpath, out);
                if (bytes.length > 0) {
                    result = JavaClassExecutor.execute(bytes);
                } else {
                    result = out.toString();
                }
            } else {
                LOGGER.warn("服务器不支持即时编译");
                result = "服务器不支持即时编译";
            }
        }
        LOGGER.info("result:----------------------\n{}\n----------------------", result);
        resp.getWriter().println(result);
    }
}
