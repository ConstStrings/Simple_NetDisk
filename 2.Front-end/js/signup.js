let used = window.location.search.slice(1);

console.log(used);

if (used == "emailisused")
{
    document.getElementById("used").innerText = "The Email Has Been Used"
}

if (used == "nameisused")
{
    document.getElementById("nameused").innerText = "The Username Has Been Used"
}

var pwd = document.getElementById("userpassword");
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

var pwdre = document.getElementById("userpassword_repeat");
pwdre.onchange = pwdreonchange;
    
function pwdreonchange()
{
    console.log("reonchange");
    let pwdre_value = pwdre.value;
    if (pwdre_value != document.getElementById("userpassword").value)
    {
        document.getElementById("checkrepeat").innerText = "Password Inconsistency"
    }
    else
    {
        document.getElementById("checkrepeat").innerText = ""
    }
}

var inputElement = document.getElementById("userpassword");
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
    if (document.getElementById("userpassword").value == document.getElementById("userpassword_repeat").value) {
        flag = 0;
        if (document.getElementById("userpassword").value.trim() === "")
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
