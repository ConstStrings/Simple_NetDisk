import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.Test;

import java.io.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@WebServlet("/sendmail")
public final class Sendmail extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email = req.getParameter("email");
        try {
            if(GetMySQL(email) && findCode(req,email))
            {
                Random random = new Random();
                int randomnum = random.nextInt(999999 - 100000 + 1) + 100000;
                sendtest(email,randomnum);
                long timestamp = System.currentTimeMillis();
                String code = email+","+Integer.toString(randomnum)+","+String.valueOf(timestamp);
                String realPath = req.getServletContext().getRealPath("/docs/");
                writecode(code,realPath);
            }
            else
            {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.setContentType("text/html;charset=UTF-8");
                PrintWriter out = resp.getWriter();
                out.print("wrongemail");
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void sendtest(String user,int code) throws MessagingException {
        String topic = "You are Resetting Your Password";
        String msg=String.format("Dear User,\nThank you for using our service.\nYour verification code is: \n<h1 style=\"color: rgb(2, 150, 255); font-size: xx-large;\">%d</h1> Please enter this code in the verification input field to complete the process.\nThis verification code is only valid for this session.\nIf you did not request this code, please disregard this email.\nBest regards.",code);
        Session session_mail = createSession();
        Sendmsg(session_mail,user,topic,msg);
    }

    public static Session createSession() {

        //填写你自己的信息
        String username = "***************";//	邮箱发送账号
        String password = "***************";//	邮箱授权码

        Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.qq.com");//	SMTP主机名

        props.put("mail.smtp.port", "587");//	主机端口号
        props.put("mail.smtp.auth", "true");//	是否需要用户认证
        props.put("mail.smtp.starttls.enable", "false");//	启用TlS加密

        Session session = Session.getInstance(props,new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // TODO Auto-generated method stub
                return new PasswordAuthentication(username,password);
            }
        });

        session.setDebug(true);
        return session;
    }

    public void Sendmsg(Session session,String receive,String topic,String msg) throws MessagingException {

        MimeMessage message = new MimeMessage(session);
        message.setSubject(topic);
        message.setText(msg);
        message.setFrom(new InternetAddress("**********@qq.com"));//填自己的信息
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receive));

        Transport.send(message);
        System.out.println("Send done");
    }

    protected String gettime()
    {
        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String formattedDateTime = now.format(formatter);

        return formattedDateTime;
    }

    protected boolean findCode(HttpServletRequest req,String email)
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
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    protected void writecode(String code,String RealPath) throws IOException {
        FileWriter f = null;
        BufferedWriter f1 = null;

        try {
            Path path = Paths.get(RealPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            f = new FileWriter(RealPath+"/codes.txt",true);
            f1 = new BufferedWriter(f);

            f1.write(code);
            f1.newLine();

        } catch (Exception e) {
            // TODO: handle exception
        }finally {//如果没有catch 异常，程序最终会执行到这里
            try {
                f1.close();
                f.close();//关闭文件
            } catch (Exception e2) {
                // TODO: handle exception
            }
        }
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
                    String userName = (String)rs.getObject(1);
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
}

