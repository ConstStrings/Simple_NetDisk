import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Objects;

@WebServlet("/usage")

public class usage extends HttpServlet {
    public usage() throws SQLException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username_req = req.getParameter("username");

        HttpSession session = req.getSession();

        if (session.getAttribute("username") == null)
        {
            resp.sendRedirect("/Web_war_exploded/logout");
            return;
        }

        String realPath = req.getServletContext().getRealPath("/upload/");
        String Path = realPath + session.getAttribute("username");

        File file = new File(Path);

        long size = getDirectorySize(file);
        System.out.println(size);
        PrintWriter out = resp.getWriter();
        out.print(size);
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
