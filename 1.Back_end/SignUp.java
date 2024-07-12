import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;

@WebServlet("/signup")
public class SignUp extends HttpServlet {
    public SignUp() throws SQLException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        System.out.println(username + " " + email + " " + password);
        try {
            int status = GetMySQL(username,email,password);
            if(status==1)
            {
                resp.sendRedirect("/Web_war_exploded/login.html");
            }
            else if(status==2)
            {
                resp.sendRedirect("/Web_war_exploded/signup.html?emailisused");
            }
            else
            {
                resp.sendRedirect("/Web_war_exploded/signup.html?nameisused");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected int GetMySQL(String username,String email,String password)
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
            conn = DriverManager.getConnection(url, "root", "**********");//填自己的信息
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Statement statement = null;
        try {
            statement = conn.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String sql = String.format("SELECT email, COUNT(*) AS element_count\n" +
                "FROM users\n" +
                "WHERE email = '%s'\n" +
                "GROUP BY email;\n"
                ,email);

        try {
            ResultSet rs = statement.executeQuery(sql);// 对于更新、删除、修改操作等不需要返回结果的情况，可直接使用
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        ResultSet rs = null;
        try {
            rs = statement.executeQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String count_email = "";
        try {
            if (rs.next()) {
                try {
                    count_email = (String)rs.getObject(1); // 注意，JDBC中下标索引从1开始}
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println(count_email);

        sql = String.format("SELECT name, COUNT(*) AS element_count\n" +
                        "FROM users\n" +
                        "WHERE name = '%s'\n" +
                        "GROUP BY name;\n"
                ,username);

        try {
            rs = statement.executeQuery(sql);// 对于更新、删除、修改操作等不需要返回结果的情况，可直接使用
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        rs = null;
        try {
            rs = statement.executeQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String count_username = "";
        try {
            if (rs.next()) {
                try {
                    count_username = (String)rs.getObject(1); // 注意，JDBC中下标索引从1开始}
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println(count_username);

        if(count_email.isEmpty() && count_username.isEmpty())
        {
            sql = String.format("INSERT into users SET name= '%s', passwd = '%s', email = '%s'"
                    ,username,password,email);
            statement.executeUpdate(sql);
            return 1;
        }
        if (!count_email.isEmpty())
            return 2;
        if (!count_username.isEmpty())
            return 3;
        return 2;
    }
}
