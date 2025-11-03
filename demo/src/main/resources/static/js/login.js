document.getElementById("loginForm").addEventListener("submit", async function(event) {
  event.preventDefault();

  const username = document.getElementById("username").value;
  const password = document.getElementById("password").value;

  if (!(username && password)) {
    alert("Please fill in all fields!");
    return;
  } 
//  alert(`Login attempt with:\nUsername: ${username}\nPassword: ${password}`);

  // Grab user data
    const user = {
      username: username,
      password: password
    };

  try{
     const userResponse = await fetch("/api/user/login", {
       method: "POST",
       headers: {
         "Content-Type": "application/x-www-form-urlencoded"
       },
       body: new URLSearchParams({username, password}),
       credentials: "include"
     });

    if (userResponse.ok) {
      alert(`User "${username}" registered successfully!`);
      goToDashboard();
    }
    else if (userResponse.status === 403) {
          alert("Account not enabled! Check email");
    }
    else if (userResponse.status === 401) {
      alert("Incorrect username or password!");
    } 

  } catch (err) {
    console.error(err);
    alert(err);
  }
});

function goToDashboard() {
  window.location.href = "/index.html";
}

//alert for signing in
const params = new URLSearchParams(window.location.search);
const status = params.get("status");

if (status === "success"){
  alert("Account verified successfully! Please log in");
}else if (status === "error"){
  alert("Invalid or expired token! Try signing in again");
}


