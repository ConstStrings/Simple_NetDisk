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

@WebServlet("/delete")
@MultipartConfig
public class Delete extends HttpServlet {
    public Delete() throws SQLException {

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

        String fileName = req.getParameter("fileName");

        String userName = (String) session.getAttribute("username");

        String userPath = getServletContext().getRealPath("/upload/") + userName;

        File file = new File(userPath + "\\" + fileName);
        System.out.println("Delete:" + userPath + "\\" + fileName);

        if(file.exists() && file.isFile())
            file.delete();

        resp.sendRedirect(String.format("/Web_war_exploded/home.html?%s",userName));
    }
}
