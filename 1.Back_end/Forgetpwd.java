import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

@WebServlet("/forgetpwd")
public class Forgetpwd extends HttpServlet {
    public Forgetpwd() throws SQLException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String code = req.getParameter("code");
        try {
            if(GetMySQL(email))
            {
                int code_value = findCode(req,email);
                System.out.println(code_value);
                if(code_value==Integer.parseInt(code))
                {
                    Resetpwd(email,password);
                    resp.sendRedirect("/Web_war_exploded/login.html");
                }
                else
                {
                    resp.sendRedirect("/Web_war_exploded/forgetpwd.html?wrongcode");
                    System.out.println("Wrong Code");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected int findCode(HttpServletRequest req,String email)
    {
        long timestamp = System.currentTimeMillis();
        String realPath = req.getServletContext().getRealPath("/docs/codes.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(realPath))) {
            //ArrayList<String> lines = null;
            String line = null;
            while ((line = br.readLine()) != null) {
                //lines.add(line);
                System.out.println(line);
                String[] parts = line.split(",");
                if(parts[0].equals(email) && (timestamp-Long.parseLong(parts[2]))<=300000)
                {
                    return Integer.parseInt(parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    protected boolean GetMySQL(String email)
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
            conn = DriverManager.getConnection(url, "root", "*************");// Your own password
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Statement statement = null;
        try {
            statement = conn.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String sql = "SELECT name FROM users WHERE email = ?";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setObject(1,email);

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
                    return true;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private void Resetpwd(String email,String password) throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");// 连接数据库
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        String url = "jdbc:mysql://localhost:3306/mypan?" +
                "useUnicode=true&characterEncoding=utf8";
        Connection conn;
        try {
            conn = DriverManager.getConnection(url, "root", "***********");// Your own password
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Statement statement = null;
        try {
            statement = conn.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String sql = "UPDATE users SET passwd = ? WHERE email = ?";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setObject(1,password);
        ps.setObject(2,email);

        ResultSet rs = null;
        try {
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

