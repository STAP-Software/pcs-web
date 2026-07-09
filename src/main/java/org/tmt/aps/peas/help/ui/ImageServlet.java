package org.tmt.aps.peas.help.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.common.MessageGenerator;

/**
 * Servlet used to serve images from /help/images/ directory.
 * @author smichaels
 *
 */
@WebServlet("/help/images/*")
public class ImageServlet extends HttpServlet {
	
	private static final long serialVersionUID = 5208313459464331786L;
	
	Logger logger = Logger.getLogger(this.getClass());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String filename = request.getPathInfo().substring(1);
		String propertiesPath = System.getProperty("org.tmt.aps.peas.peasPropertiesPath");

		File file = new File(propertiesPath + File.separator + "help" + File.separator + "images" + File.separator + filename);

        response.setHeader("Content-Type", getServletContext().getMimeType(filename));
        response.setHeader("Content-Length", String.valueOf(file.length()));
        response.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");
        
        try {
            OutputStream os = response.getOutputStream();
            byte[] buf = new byte[8192];
            InputStream is = new FileInputStream(file);
            int c = 0;
            while ((c = is.read(buf, 0, buf.length)) > 0) {
                os.write(buf, 0, c);
                os.flush();
            }
            os.close();
            is.close();
        } catch (IOException e) {
        	logger.error(MessageGenerator.generateMessage("generic.error"), e);
        }
        
    }

}