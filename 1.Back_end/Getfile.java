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

@WebServlet("/getfile")
@MultipartConfig
public class Getfile extends HttpServlet {
    public Getfile() throws SQLException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username_req = req.getParameter("username");

        HttpSession session = req.getSession();

        if (session.getAttribute("username") == null)
        {
            resp.sendRedirect("/Web_war_exploded/logout");
            //req.getRequestDispatcher("/Web_war_exploded/logout").forward(req, resp);
            return;
        }

        String realPath = req.getServletContext().getRealPath("/upload/");
        String Path = realPath + session.getAttribute("username");
        System.out.println(Path);
        File file = new File(Path);
        File[] files = file.listFiles();

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/html;charset=UTF-8");

        PrintWriter out = resp.getWriter();
        if(files != null) {
            for (int i = 0; i < files.length; i++) {
                out.print(files[i].getName());
                out.print(',');
            }
            out.flush();
        }
    }

    protected String read_cookies(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                String name = c.getName();
                String value = c.getValue();
                System.out.println("Cookie Name: " + name + ", Value: " + value);
                if (Objects.equals(name, "username"))
                {
                    System.out.println("Find cookies!");
                    return value;
                }
            }
        } else {
            System.out.println("No cookies found.");
        }

        return "NotFound";
    }

}
