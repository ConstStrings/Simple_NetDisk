import com.mysql.cj.Session;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;

import junit.framework.TestCase;
import org.junit.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

@WebServlet("/login")
public class TestServlet extends HttpServlet {
    public TestServlet() throws SQLException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        System.out.println(email + " " + password);
        HttpSession session = req.getSession();

        String user_name;

        try {
            user_name = GetMySQL(email,password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if(!Objects.equals(user_name, "NotFound"))
        {
            write_cookies(req,resp,user_name);
            session.setAttribute("username",user_name);
            resp.sendRedirect(String.format("/Web_war_exploded/home.html?username=%s",user_name));
        }
        else
        {
            resp.sendRedirect("/Web_war_exploded/login.html?error");
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.sendRedirect("/Web_war_exploded/login.html");
    }

    protected String GetMySQL(String email,String password)
        throws Exception{
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");// 连接数据库
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        String url = "jdbc:mysql://localhost:3306/mypan?" +
                "useUnicode=true&characterEncoding=utf8";
        Connection conn;
        try {
            conn = DriverManager.getConnection(url, "root", "*********");//填自己的信息
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Statement statement = null;
        try {
            statement = conn.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String sql = "SELECT name FROM users WHERE passwd = ? and email = ?";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setObject(1,password);
        ps.setObject(2,email);

        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            if (rs.next()) {
                try {
                    String userName = (String)rs.getObject(1); // 注意，JDBC中下标索引从1开始}
                    System.out.println(userName);
                    return userName;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "NotFound";
    }

    protected void write_cookies(HttpServletRequest req, HttpServletResponse resp, String username) throws IOException {
        Cookie cookie = new Cookie("username", username);
        cookie.setMaxAge(20 * 60);
        resp.addCookie(cookie);
    }

    @Test
    public void TestQueryLoginInfo() throws Exception {
        String email = "99999999@qq.com";
        String passwd = "12345678";
        TestCase Assertions = null;
        Assertions.assertEquals(GetMySQL(email, passwd), "String");
    }
}


