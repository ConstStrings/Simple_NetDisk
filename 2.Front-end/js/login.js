let error = window.location.search.slice(1);

console.log(error);

function signup()
{
    window.location.replace("/Web_war_exploded/signup.html")   
}

document.getElementById('signbtn').onclick = signup;

if (error == "error")
{
    document.getElementById("error").innerText = "Incorrect Username or Password"
}