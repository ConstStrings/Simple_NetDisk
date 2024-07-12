import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

@WebServlet("/upload")
@MultipartConfig
public class Upload extends HttpServlet {
    public Upload() throws SQLException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();

        if (session.getAttribute("username") == null)
        {
            resp.sendRedirect("/Web_war_exploded/logout");
            return;
        }

        Part myfile = req.getPart("file");
        String fileName = req.getParameter("filename");
        String username = req.getParameter("username");
        if (fileName.isEmpty()) {
            fileName = myfile.getSubmittedFileName();
        }

        String realPath = req.getServletContext().getRealPath("/upload/");
        String Path = realPath + session.getAttribute("username");
        File file = new File(Path);

        long occupy_space = myfile.getSize()+getDirectorySize(file);

        if (occupy_space >= 1024 * 1024 * 102)
        {
            resp.sendRedirect(String.format("/Web_war_exploded/home.html?username=%s&error=space_limit",username));
            return;
        }

        Path path = Paths.get(Path);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        System.out.println(username + " " + fileName + " " + realPath);
        myfile.write(realPath + username + "/" + fileName);

        resp.sendRedirect(String.format("/Web_war_exploded/home.html?username=%s",username));
    }

    public static long getDirectorySize(File directory) {
        long size = 0;
        // Get all files and subdirectories
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    size += file.length(); // Add file size
                } else {
                    size += getDirectorySize(file); // Recursively add directory size
                }
            }
        }
        return size;
    }
}
