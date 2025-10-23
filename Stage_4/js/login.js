document.getElementById("loginForm").addEventListener("submit", async function(event) {
  event.preventDefault();

  const username = document.getElementById("username").value;
  const password = document.getElementById("password").value;

  if (!(username && password)) {
    alert("Please fill in all fields!");
    return;
  } 
  alert(`Login attempt with:\nUsername: ${username}\nPassword: ${password}`);

  // Grab user data
    const user = {
      username: username,
      password: password
    };

  try{
    // const userResponse = await fetch("http://localhost:8080/api/user/login", {
    //   method: "POST",
    //   headers: {
    //     "Content-Type": "application/x-www-form-urlencoded"
    //   },
    //   body: new URLSearchParams({
    //     username: user.username,
    //     password: user.password
    //   }),
    //   credentials: "include"
    // });
    const userResponse = await fetch("http://localhost:8080/api/user/login", {
      method: "POST",
      headers: {"Content-Type": "application/json"},
      body: JSON.stringify(user)
    });

    if (userResponse.status === 200) {
      const userID = await userResponse.json();

      alert(`User "${username}" registered successfully!`);
      localStorage.setItem("isLoggedIn", "true");
      localStorage.setItem("username", username);
      localStorage.setItem("userID", userID);

      goToDashboard();
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
  window.location.href = "index.html";
}



