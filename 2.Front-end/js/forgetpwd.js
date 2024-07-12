let error = window.location.search.slice(1);
console.log(error);

if (error == "wrongcode")
{
    document.getElementById("checkcode").innerText = "Incorrect Code"
}

if (error == "wrongemail")
{
    document.getElementById("checkemail").innerText = "Email not Registered"
}

var getbtn = document.getElementById('getcode');

getbtn.onclick = function () {
    postData(document.getElementById('email').value);

    updateCountdown();
}

function postData(email) {
    const url = '/Web_war_exploded/sendmail'; // 后端接口的URL
    const data = 'email=' + encodeURIComponent(email); // 要发送的文本数据
  
    fetch(url, {
      method: 'POST', // 请求方法为POST
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded' 
      },
      body: data 
    })
    .then(response => {
      if (response.redirected)
      {
        console.log(window.location.href);
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
      if (data == "wrongcode")
      {
        document.getElementById("checkcode").innerText = "Incorrect Code"
      }
      else if (data == "wrongemail")
      {
        document.getElementById("checkemail").innerText = "Email not Registered"
      }
    })
    .catch(error => {
      // 请求失败时的处理逻辑
      console.error('Error:', error);
    });
}
  
var pwd = document.getElementById("password");
pwd.onchange = pwdonchange;
    
function pwdonchange()
{
    console.log("onchange");
    let pwd_value = pwd.value;
    if (pwd_value.length < 8)
    {
        document.getElementById("checklength").innerText = "Password is too short"
    }
    else
    {
        document.getElementById("checklength").innerText = ""
    }
}

var pwdre = document.getElementById("password_re");
pwdre.onchange = pwdreonchange;
    
function pwdreonchange()
{
    console.log("reonchange");
    let pwdre_value = pwdre.value;
    if (pwdre_value != document.getElementById("password").value)
    {
        document.getElementById("checkrepeat").innerText = "Password Inconsistency"
    }
    else
    {
        document.getElementById("checkrepeat").innerText = ""
    }
}

var inputElement = document.getElementById("password");
var textLength = 0;
inputElement.addEventListener("input", function(event) {

  var inputText = inputElement.value;

  textLength = inputText.length;
});

var form = document.getElementById("myform");
var flag = 0;

form.addEventListener("submit", function(event) {
    event.preventDefault();
    console.log(textLength);
    if (document.getElementById("password").value == document.getElementById("password_re").value) {
        flag = 0;
        if (document.getElementById("password").value.trim() === "")
        {
            document.getElementById("checklength").innerText = "Password Can't Be Empty"
            event.preventDefault();
            flag = 1;
        }
        if (document.getElementById("email").value.trim() === "")
        {
            document.getElementById("used").innerText = "Email Can't Be Empty"
            event.preventDefault();
            flag = 1;
        }
        if (textLength < 8)
        {    
            event.preventDefault();
            flag = 1;
        }
        if (flag == 0)
        { 
            form.submit();
        }
    }
});

var countdownElement = document.getElementById('getcode');

var countdownDuration = 5 * 60 * 1000;


var startTime = new Date().getTime();


function updateCountdown() {

  countdownElement.disabled = true;
  countdownElement.style.backgroundColor = "rgb(163, 163, 163)";
    var currentTime = new Date().getTime();
    var elapsedTime = currentTime - startTime;
    var remainingTime = countdownDuration - elapsedTime;

    var minutes = Math.floor((remainingTime % (1000 * 60 * 60)) / (1000 * 60));
    var seconds = Math.floor((remainingTime % (1000 * 60)) / 1000);

    countdownElement.innerHTML = minutes + ": " + seconds + " ";

    if (remainingTime > 0) {
        setTimeout(updateCountdown, 1000);
    } else {
        countdownElement.innerHTML = "Get Again";
    }
}

