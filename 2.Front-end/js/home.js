params = getQueryParams();

let UserName = params.username;
let error_info = params.error;

console.log("User:" + UserName);
console.log("Error:" + error_info);

if (error_info === "space_limit")
{
  alert("No enough space for uploading files!");
  window.location.replace("/Web_war_exploded/home.html?username=" + UserName);
}

postData();

submit_name();

update_req();

var clickresp = 0;

function getQueryParams() {
  const queryString = window.location.search.slice(1);
  const params = {};

  queryString.split('&').forEach(param => {
      const [key, value] = param.split('=');
      params[decodeURIComponent(key)] = decodeURIComponent(value);
  });

  return params;
}

function submit_name()
{
  name_sub = document.getElementById('username_sub');
  name_sub.value = UserName;
}

document.getElementById("welcome").innerText = "Welcome " + UserName;

document.getElementById("exit").onclick = back_welcome;

function back_welcome()
{
    window.location.replace("/Web_war_exploded/logout")  
}

document.getElementById('setting').onclick = function () {
    window.location.replace("/Web_war_exploded/setting.html") 
}

document.getElementById('menu').onclick = function () {
    window.location.replace("/Web_war_exploded/menu.html") 
}

var floatingWindow = document.getElementById('floatingWindow');

var uplaod_logo = document.getElementById("upload");

uplaod_logo.addEventListener('click', toggleFloatingWindow);

var uplaod_close = document.getElementById("close-button");

uplaod_close.addEventListener('click',toggleFloatingWindow);

// 显示/隐藏悬浮窗口的函数
function toggleFloatingWindow() {
  if (floatingWindow.style.display === 'none') {
    floatingWindow.style.display = 'block';
  } else {
    floatingWindow.style.display = 'none';
  }
}


var download_close = document.getElementById("close-button2");

download_close.addEventListener('click', toggleFloatingWindow2);

var floatingWindow2 = document.getElementById('floatingWindow2');

function toggleFloatingWindow2() {
  if (floatingWindow2.style.display === 'none') {
    floatingWindow2.style.display = 'block';
  } else {
    floatingWindow2.style.display = 'none';
  }
}

// 使悬浮窗口可拖动
var isDragging = false;
var offsetX, offsetY;

// 当鼠标按下时开始拖动
floatingWindow.querySelector('.titleBar').addEventListener('mousedown', function(e) {
  isDragging = true;
  offsetX = e.clientX - floatingWindow.offsetLeft;
  offsetY = e.clientY - floatingWindow.offsetTop;
});

// 当鼠标移动时拖动窗口
document.addEventListener('mousemove', function(e) {
  if (isDragging) {
    floatingWindow.style.left = (e.clientX - offsetX) + 'px';
    floatingWindow.style.top = (e.clientY - offsetY) + 'px';
  }
});

// 当鼠标松开时停止拖动
document.addEventListener('mouseup', function() {
  isDragging = false;
});



var offsetX2, offsetY2;

floatingWindow2.querySelector('.titleBar2').addEventListener('mousedown', function(e) {
  isDragging = true;
  offsetX2 = e.clientX - floatingWindow2.offsetLeft;
  offsetY2 = e.clientY - floatingWindow2.offsetTop;
});


document.addEventListener('mousemove', function(e) {
  if (isDragging) {
    floatingWindow2.style.left = (e.clientX - offsetX2) + 'px';
    floatingWindow2.style.top = (e.clientY - offsetY2) + 'px';
  }
});

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


var fruits;
function handlePost(data)
{
  var str = data;
  fruits = str.split(",");
  let file_quantity = fruits.length;
  console.log(file_quantity);
  listupload(file_quantity);
  listlogo(fruits, file_quantity);
}

function listlogo(fruits,file_quantity)
{
  for (let i = 0; i < file_quantity - 1; i++)
  {
    creatfileblock(fruits[i], i);
  }
}

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

function splitPath(Path)
{
  var lastIndex = Path.lastIndexOf('\\');
  var resultSubstring = Path.substring(lastIndex + 1);
  return resultSubstring;
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


function download_req(username, filename)
{
  const url = '/Web_war_exploded/download' 
  const data = 'username=' + encodeURIComponent(username) +'&fileName=' + encodeURIComponent(filename)

  fetch(url, {
      method: 'POST', 
      headers: {
          'Content-Type': 'application/x-www-form-urlencoded' 
      },
      body: data 
  })
  .then(response => {
    if (!response.ok) {
        throw new Error('Network response was not ok');
    }
    return response.blob();
})
  .then(blob => {
    const url = window.URL.createObjectURL(new Blob([blob]));
    const a = document.createElement('a');
    a.href = url;
    a.download = filename; // Use the provided fileName
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
})
  .catch(error => {

      console.error('Error:', error);
  });
}

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

function downloadfile(index)
{
  console.log(index);
  let download_name = document.getElementById('download_name');
  download_name.innerHTML = splitPath(fruits[index]);
  let download_btn = document.getElementById('download_btn');
  download_btn.onclick = download_req.bind(null, UserName, splitPath(fruits[index]));
  delete_btn.onclick = delete_req.bind(null, UserName, splitPath(fruits[index]));
}

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

var total_usage = 1024 * 1024 * 102;

function update_usage(used)
{
  let usage_num = document.getElementById('us_num');
  console.log('Used:', used);
  usage_num.innerText = `${(used / (total_usage / 100)).toFixed(0)}%`;
  let usage_box = document.getElementById('usage');
  usage_box.style.height = `${500 * used / (total_usage)}px`;
}