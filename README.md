## Simple NetDisk

### 效果展示

**成功实现了用户注册、登录、文件上传、文件展示、文件下载、文件删除、密码重置。验证码发送、空间限制等基本功能。这些功能均通过Java后端和JavaScript前端的协同工作实现，保证了用户体验的流畅性和系统的稳定性。基本完成了一个网盘应有功能，且经过一段时间的实际使用，并没有发现严重bug。**

**后端部分代码使用Java编写，数据库采用MySQL，服务器基于Tomcat。前端部分涉及HTML,CSS以及JavaScript。效果如下：**

<img src=".\3.imgs\image-20240702223301986.png" alt="image-20240702223301986" style="zoom:25%;" />

<img src=".\3.imgs\image-20240702223325165.png" alt="image-20240702223325165" style="zoom:25%;" />

<img src=".\3.imgs\image-20240702223342615.png" alt="image-20240702223342615" style="zoom:25%;" />

<img src=".\3.imgs\image-20240702223400516.png" alt="image-20240702223400516" style="zoom:25%;" />

<img src=".\3.imgs\image-20240702223422406.png" alt="image-20240702223422406" style="zoom:25%;" />

### 基本功能实现

### **1.** **登录及注册功能**

#### 1.1 注册功能实现原理

**注册功能主要分为几个步骤，首先是从浏览器获取用户输入的信息，这一部分由前端完成。前端将获取到的信息发送到后端接口后，后端程序在数据库中检索是否已经存在用户名或邮箱，若已经存在，则在前端页面对用户进行提示，若不存在，则在数据库中添加对应的用户。用户添加完成后，将页面重定向至登录页面。**

#### 1.2 注册功能的后端实现

**注册逻辑由Java类SignUp实现，首先使用Post方法接收表单数据：**

```java
String username = req.getParameter("username");
String email = req.getParameter("email");
String password = req.getParameter("password");
System.out.println(username + " " + email + " " + password);
```

**创建函数`protected int GetMySQL(String username,String email,String password)`对用户输入的信息在数据库中进行查找，该函数返回状态码，若返回值为1，说明数据库中无记录，创建新用户；若返回值为2，说明邮箱存在重复；若返回值为3，说明用户名存在重复。根据不同的状态码，后端将会向前端页面发送相应的信息：**

```java
int status = GetMySQL(username,email,password);
if(status==1)
    resp.sendRedirect("/Web_war_exploded/login.html");
else if(status==2)
    resp.sendRedirect("/Web_war_exploded/signup.html?emailisused");
else
    resp.sendRedirect("/Web_war_exploded/signup.html?nameisused");
```

**在MySQL数据库中查询的核心逻辑如下(以查询用户名为例)：**

```java
sql = String.format("SELECT name, COUNT(*) AS element_count\n" +
                        "FROM users\n" +
                        "WHERE name = '%s'\n" +
                        "GROUP BY name;\n"
                ,username);

        try {
            rs = statement.executeQuery(sql);
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
                    count_username = (String)rs.getObject(1); 
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println(count_username);
```

**在上述代码中，通过判断字符串`count_username`是否为空，即可判断数据库中是否存在用户输入的用户名，对于邮箱的判断同理**

#### 1.3 注册功能的前端实现

**HTML中包含一个表单，用户填写表单后将数据发送到后端接口`/Web_war_exploded/signup`。在提交表单后，JavaScript处理后端的返回信息并做出相应的显示。此外，JavaScript还对用户输入的信息进行判断，如果不符合格式（如密码过短或两次输入的密码不一致），将不允许用户将表单提交。**

<img src=".\3.imgs\image-20240702172833169.png" style="zoom: 25%;" />

**注册页面效果展示（每一个输入栏下都有对应的错误信息提示，根据不同的情况自动显示）**

#### 1.4 登录功能的后端实现

**原理与注册功能类似，创建Java类login，新增一些功能。首先是对SQL注入漏洞进行防护，对敏感字段进行删除，防止用户对SQL数据库进行操作：**

```java
String sql = "SELECT name FROM users WHERE passwd = ? and email = ?";
PreparedStatement ps = conn.prepareStatement(sql);
ps.setObject(1,password);
ps.setObject(2,email);
```

**之后在数据库中对用户输入的邮箱和密码进行匹配，若匹配成功则写入cookies并跳转页面：**

```java
write_cookies(req,resp,user_name);
session.setAttribute("username",user_name);
resp.sendRedirect(String.format("/Web_war_exploded/home.html?username=%s",user_name));
```

#### 1.5 登录功能的前端实现

**前端执行的功能与注册类似，将用户输入发送到后端接口，并对错误信息进行提示：**

<img src=".\3.imgs\image-20240702174711146.png" alt="image-20240702174711146" style="zoom:25%;" />

**登录页面效果展示，前端所有HTML，CSS，JavaScript均为手工制作**

### **2.** **文件上传功能**

#### 2.1 文件上传的后端实现

**后端提供接口Upload，为防止用户直接通过接口上传文件，文件路径中的用户名直接从cookies中读取，拼接为上传路径：**

```java
String realPath = req.getServletContext().getRealPath("/upload/");
String Path = realPath + session.getAttribute("username");
File file = new File(Path);
```

 **写入文件时先检查是否存在用户文件夹，若不存在，则先创建用户文件夹：**

```java
if (!Files.exists(path)) {
    Files.createDirectories(path);
}
myfile.write(realPath + username + "/" + fileName);
```

**此外，上传时还将判断上传的文件是否超过用户剩余的储存空间（储存空间额度将在后面介绍），若超出，则返回错误信息：**

```java
long occupy_space = myfile.getSize()+getDirectorySize(file);

if (occupy_space >= 1024 * 1024 * 1024)
{
    resp.sendRedirect(String.format("/Web_war_exploded/home.html?username=%s&error=space_limit",username));
    return;
}
```

**上述代码中获取当前占用空间的方法：**

```java
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
```

#### 2.2 文件上传的前端实现

**在主页点击Upload按钮即可显示文件上传悬浮窗，实现文件上传：**

<img src=".\3.imgs\image-20240702185252769.png" alt="image-20240702185252769" style="zoom:67%;" />

### **3.** **文件展示功能**

#### 3.1 文件展示功能实现原理

**前端向后端发送请求，获取文件列表。后端根据cookies中的用户名在upload文件夹中查找用户文件夹，将文件名发送到前端。前端将获取到的文件按照块在主页上排列。**

#### 3.2 文件展示功能的后端实现

**文件展示后端功能由创建的Java类`getfile`实现，首先定位用户文件夹：**

```java
String realPath = req.getServletContext().getRealPath("/upload/");
String Path = realPath + session.getAttribute("username");
System.out.println(Path);
File file = new File(Path);
File[] files = file.listFiles();
```

 **将该文件目录下所有文件的文件名发送到前端：**

```java
PrintWriter out = resp.getWriter();
if(files != null) {
    for (int i = 0; i < files.length; i++) {
        out.print(files[i].getName());
        out.print(',');
    }
    out.flush();
}
```

#### 3.3 文件展示的前端实现

**在设计中，文件将以单独的小方块在主页上显示，用户点击对应的文件块即可显示该文件的操作悬浮窗，该部分的前端实现较为复杂，是花费时间较多的一部分**

**首先，在主页加载时，使用Post方法向后端请求文件列表：**

```javascript
function postData() {
  const url = '/Web_war_exploded/getfile'; 
  const data = 'username=' + encodeURIComponent(UserName); 

  fetch(url, {
    method: 'POST', 
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded' 
    },
    body: data 
  })
  .then(response => {
    if (response.redirected)
    {
      window.location.href = '/Web_war_exploded/logout'; 
    }
    else if (response.ok)
    {
      return response.text();
    }
    else
    {
      throw new Error('请求失败');
    }
  })
  .then(data => {
    console.log('Response from server:', data);
    handlePost(data);
  })
  .catch(error => {
    console.error('Error:', error);
  });
}
```

**获取到的文件名之间按逗号进行分割，定义函数`function handlePost(data)`提取单独的文件名，获取文件个数，并分别调用函数处理文件块创建和logo显示：**

```javascript
function handlePost(data)
{
  var str = data;
  fruits = str.split(",");
  let file_quantity = fruits.length;
  console.log(file_quantity);
  listupload(file_quantity);
  listlogo(fruits, file_quantity);
}
```

**通过文件数量，调整上传文件图标所在位置，保证上传图标位于最后一个文件之后：**

```javascript
function listupload(file_quantity)
{
  file_quantity -= 1;
  let left_p = file_quantity % 9;
  let top_p = (file_quantity - left_p) / 9;
  let upload_p = document.getElementById('upload');
  upload_p.style.position = 'absolute';
  upload_p.style.left = `${(left_p+1)*10}vw`
  upload_p.style.top = `${10 + (top_p * 20)}vh`
}
```

**通过循环，为每一个文件创建box，每一行包含9个box,当文件数量大于9个是，能自动换行：**

```javascript
function listlogo(fruits,file_quantity)
{
  for (let i = 0; i < file_quantity - 1; i++)
  {
    creatfileblock(fruits[i], i);
  }
}
function creatfileblock(filePath, index)
{
  var divBox = document.createElement("div");
  divBox.classList.add('fileblock');
  let filetype = load_logo(splitPath(filePath));
  divBox.innerHTML = ` <img src="./imgs/logo_${filetype}.png" alt="" id="logo"><p style="display: inline-block;">${splitPath(filePath)}</p>`
  let left_p = index % 9;
  let top_p = (index - left_p) / 9;
  divBox.style.position = 'absolute';
  divBox.style.left = `${(left_p+1)*10}vw`
  divBox.style.top = `${10 + (top_p * 20)}vh`
  divBox.style.textAlign = 'center';

  divBox.addEventListener('click', function () {
    toggleFloatingWindow2();
    downloadfile(index);
  });  
  document.body.appendChild(divBox);
}
```

**要为不同类型的文件加载不同的图标，需要判断文件的后缀类型，在上面的代码中，图标文件的名字`logo_${filetype}.png`为变量，`function load_logo(filename)`决定应该加载哪一种图标：**

```javascript
function load_logo(filename)
{
  let types = ['code', 'folder', 'pdf', 'png', 'jpg', 'doc', 'txt', 'folder', 'zip', 'mp4', 'ppt','docx','exe'];
  let suffix = filename.split(".");
  if (suffix == null)
  {
    return 'folder';
  }
  let type = suffix[1];
  if (type == 'c' || type == 'cpp' || type == 'java' || type == 'py' || type == 'js' || type == 'html' || type == 'css' || type == 'php' || type == 'sql' || type == 'json' || type == 'h' || type == 'hpp' || type == 'xml' || type == 'sh' || type == 'bat')
    type = 'code';
  console.log(type);
  if (types.includes(type)) {
    return type;
  }
  else {
    return 'unknow';
  }
}
```

**文件展示功能前端效果展示：**

<img src=".\3.imgs\image-20240702193220623.png" alt="image-20240702193217474" style="zoom:25%;" />

### **4.** **文件下载功能**

#### 4.1 文件下载功能的后端实现

**创建Java类`Download`从而实现文件下载。首先读取cookies获取用户名，防止越权访问。拼接目标文件路径，当目标文件存在且为文件对象时，以流形式将文件发送到用户端：**

```java
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
```

 #### 4.2 文件下载功能的前端实现

**用户单击文件块时，会弹出文件操作悬浮窗，显示对当前文件可进行的操作（下载，删除）。要实现该操作，在创建文件块时，为`divbox`设置点击事件，index为文件块的编号，每一个文件块被点击时，会向 `downloadfile()`传递index，从而实现不同文件的下载：**

```javascript
divBox.addEventListener('click', function () {
    toggleFloatingWindow2();
    downloadfile(index);
  })
```

**当用户点击下载按钮时，先通过index定位点击事件的来源（哪一个文件被点击），再向后端接口发送下载请求：**

```javascript
function downloadfile(index)
{
  console.log(index);
  let download_name = document.getElementById('download_name');
  download_name.innerHTML = splitPath(fruits[index]);
  let download_btn = document.getElementById('download_btn');
  download_btn.onclick = download_req.bind(null, UserName, splitPath(fruits[index]));
  delete_btn.onclick = delete_req.bind(null, UserName, splitPath(fruits[index]));
}
```

**文件下载功能前端效果展示：**

<img src=".\3.imgs\image-20240702200059852.png" alt="image-20240702200059852" style="zoom:25%;" />

### **5.** **其他功能**

如果实现了其他功能，如文件删除、分片上传等，可将相关功能在此介绍，介绍过程可以结合系统截图及关键代码进行阐述。

#### 5.1 文件删除功能

**删除按钮与下载按钮均在文件操作悬浮窗中，单击目标文件后出现。点击下载后以Post方法向后端发送删除请求：**

```javascript
function delete_req(username, filename)
{
  const url = '/Web_war_exploded/delete' 
  const data = 'username=' + encodeURIComponent(username) +'&fileName=' + encodeURIComponent(filename) 

  fetch(url, {
      method: 'POST', 
      headers: {
          'Content-Type': 'application/x-www-form-urlencoded' 
      },
      body: data 
  })
  .then(response => response.text())
  .then(data => {

    console.log('Response from server:', data);
    window.location.reload();
  })
  .catch(error => {

      console.error('Error:', error);
  });
}
```

**后端接收到请求后，从cookies中读取用户名，判断目标文件存在后执行删除操作：**

```java
String fileName = req.getParameter("fileName");

String userName = (String) session.getAttribute("username");

String userPath = getServletContext().getRealPath("/upload/") + userName;

File file = new File(userPath + "\\" + fileName);
System.out.println("Delete:" + userPath + "\\" + fileName);

if(file.exists() && file.isFile())
    file.delete();
```

**文件删除后对页面重定向，刷新主页**

#### 5.2 密码重置与验证码功能

**当用户需要对密码修改时，需要密码重置功能。作为身份验证，需要向用户注册时的邮箱发送验证码，验证成功后才能修改密码。**

**当用户输入邮箱后，即可点击发送验证码，此时向后端以Post方法发送请求，发送功能由Java类`Sendmail`实现，验证码识别和密码修改功能由Java类`Forgrtpwd`实现。首先，在SQL数据库中查找用户，若存在该用户则返回true：**

```java
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
```

**若用户存在，调用QQ邮箱API发送验证码邮件：**

```java
public void sendtest(String user,int code) throws MessagingException {
    String topic = "You are Resetting Your Password";
    String msg=String.format("Dear User,\nThank you for using our service.\nYour verification code is: \n<h1 style=\"color: rgb(2, 150, 255); font-size: xx-large;\">%d</h1> Please enter this code in the verification input field to complete the process.\nThis verification code is only valid for this session.\nIf you did not request this code, please disregard this email.\nBest regards,\nConstString.",code);
    Session session_mail = createSession();
    Sendmsg(session_mail,user,topic,msg);
}

public static Session createSession() {

    String username = "320723****@qq.com";//	邮箱发送账号
    String password = "*****************";//	邮箱授权码

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
    message.setFrom(new InternetAddress("320723****@qq.com"));
    message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receive));

    Transport.send(message);
    System.out.println("Send done");
}
```

**为了后续的验证码识别，需要将随机生成的验证码和当前的系统时间写入到本地文件`codes.txt`中（验证码5分钟内有效）：**

```java
protected String gettime()
{
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String formattedDateTime = now.format(formatter);
    return formattedDateTime;
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
    }finally {
        try {
            f1.close();
            f.close();
        } catch (Exception e2) {
        }
    }
}
```

**验证码将在文件中以如下形式保存(邮箱，验证码，时间戳)：**

<img src=".\3.imgs\image-20240702214926989.png" alt="image-20240702214926989" style="zoom:50%;" />

**当用户填写完信息提交表单时，使用`findCode()`查找用户的验证码是否正确且在有效期内：**

```java
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
```

**若用户验证码认证成功，使用函数`Resetpwd(email,password)`重置密码，否则向前端页面发送错误提示。（函数见源代码）**

**此外，前端还对密码格式合法性进行判断，并显示5分钟的验证码有效期，密码重置页面展示：**

<img src=".\3.imgs\image-20240702205408667.png" alt="image-20240702205408667" style="zoom:25%;" />

**验证码邮件展示：**

<img src=".\3.imgs\Screenshot_20240712_152125.jpg" alt="Screenshot_20240702_205554" style="zoom: 33%;" />

#### 5.3 使用额度功能

**给予用户使用空间限制，并将占用的空间在主页左侧显示，避免单个用户占用过多空间。空间上限可以手动调整，后续可以升级为付费会员等服务**

**后端接口由Java类`usage`实现，获取用户文件夹所占用的字节数，发送到前端：**

```java
String realPath = req.getServletContext().getRealPath("/upload/");
String Path = realPath + session.getAttribute("username");
File file = new File(Path);
long size = getDirectorySize(file);
PrintWriter out = resp.getWriter();
out.print(size);
```

**其中，`getDirectorySize(file)`函数具体已在文件上传功能部分说明**

**前端部分通过将接收到的占用字节数与用户额度进行比较，显示占用百分比并绘制柱状图：**

```javascript
var total_usage = 1024 * 1024 * 102;

function update_req(username)
{
  const url = '/Web_war_exploded/usage' 
  const data = 'username=' + encodeURIComponent(username)
  var used = 0
  fetch(url, {
      method: 'POST', 
      headers: {
          'Content-Type': 'application/x-www-form-urlencoded' 
      },
      body: data 
  })
  .then(response => response.text())
    .then(data => {
      used = Number(data);
      update_usage(used);
  })
  .catch(error => {
      console.error('Error:', error);
  });
  return used;
}

function update_usage(used)
{
  let usage_num = document.getElementById('us_num');
  console.log('Used:', used);
  usage_num.innerText = `${(used / (total_usage / 100)).toFixed(0)}%`;
  let usage_box = document.getElementById('usage');
  usage_box.style.height = `${500 * used / (total_usage)}px`;
}
```

**实现效果见：文件展示功能前端效果展示**

#### 5.4 退出登录功能

**通过调用后端接口统一管理退出功能，调用时销毁cookies。在出现异常或者手动点击时调用：**

```java
HttpSession session = req.getSession();
session.invalidate();
System.out.println("User logout");
resp.sendRedirect("/Web_war_exploded/welcome.html");
```

### 安全攻防实现

#### 1.漏洞描述

**在Windows系统中 “../”代表上一级文件夹，意味着即使使用cookies中保存的用户名作为目录，用户也能越权访问其他文件夹**

#### 2.利用过程及效果

**首先通过API工具，以Post方法向登录接口发送登录信息，目的是获取cookies：**

<img src=".\3.imgs\image-20240702215423747.png" alt="image-20240702215423747" style="zoom: 33%;" />

**当前登录用户为`2023190902036`，此时向后端`download`接口发送下载请求，请求文件为`../ConstString/Probability and Statistics.pdf`，即其他用户`ConstString`的文件：

<img src=".\3.imgs\image-20240702215711753.png" alt="image-20240702215711753" style="zoom:33%;" />

**可见即使登录用户不是`ConstString`，服务器仍将`ConstString`的文件发送，造成了数据泄露。两个用户的文件夹关系如下：**

<img src=".\3.imgs\image-20240702220028003.png" alt="image-20240702220028003" style="zoom:80%;" />

#### 3.漏洞修复

**不允许下载路径中出现“.."字符。通过正则表达式将“..”替换为空字符：**

```java
fileName = removeDots(fileName); 

public static String removeDots(String input) 
 {
        return input.replaceAll("\\.\\.", "");
 }
```

**漏洞修复后再次发送请求：**

<img src=".\3.imgs\image-20240702220951191.png" alt="image-20240702220951191" style="zoom: 33%;" />

**返回`<h2>File Request Fail!</h2>`，无法获取文件，漏洞修复成功**

### 声明

**本项目为电子科技大学课程作业，仅供学习使用，如有漏洞或问题欢迎指出。如有侵权，请联系邮箱：conststrings@gmail.com删除**





