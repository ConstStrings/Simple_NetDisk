import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

@WebServlet("/download")
@MultipartConfig
public class Download extends HttpServlet {
    public Download() throws SQLException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        service1(req,resp);
    }

    protected void service1(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String fileName = req.getParameter("fileName");
        String userName = req.getParameter("username");

        fileName = removeDots(fileName);

        HttpSession session = req.getSession();
        if (session.getAttribute("username") == null)
        {
            resp.sendRedirect("/Web_war_exploded/logout");
            return;
        }

        String userPath = getServletContext().getRealPath("/upload/") + session.getAttribute("username");

        File file = new File(userPath + "\\" + fileName);
        System.out.println("Download:" + userPath + "\\" + fileName);

        if (file.exists() && file.isFile()) {
            resp.setContentType("application/x-msdownload");
            resp.setHeader("Content-Disposition", "attachment;filename=\"" + file.getName() + "\"");


            FileInputStream is = new FileInputStream(file);
            ServletOutputStream os = resp.getOutputStream();
            byte[] temp = new byte[1024];
            int len = 0;
            while ((len = is.read(temp)) != -1) {
                os.write(temp, 0, len);
            }
            os.close();
            is.close();
        }
        else {
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().write("<h2>File Request Fail!</h2>");
            resp.getWriter().close();
        }
    }
    public static String removeDots(String input)
    {
        return input.replaceAll("\\.\\.", "");
    }
}
