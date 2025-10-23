const signupForm = document.getElementById("signupForm");

document.getElementById("signupForm").addEventListener("submit", async function(event) {
  event.preventDefault();

  const username = document.getElementById("username").value.trim();
  const email = document.getElementById("email").value.trim();
  const password = document.getElementById("password").value;
  const confirmPassword = document.getElementById("confirmPassword").value;

  if (!username || !email || !password || !confirmPassword) {
    alert("All fields are required!");
    return;
  }

  if (password !== confirmPassword) {
    alert("Passwords do not match!");
    return;
  }

  alert(`Signup attempt:\nUsername: ${username}\nEmail: ${email}`);

  // Grab user data
  const user = {
    username: username,
    email: email,
    password: password
  };

  try{
    const userResponse = await fetch("http://localhost:8080/api/user/signup", {
      method: "POST",
      headers: {"Content-Type": "application/json"},
      body: JSON.stringify(user)
    });
    
    if (userResponse.status === 201) {
      const userID = await userResponse.json();

      localStorage.setItem("isLoggedIn", "true");
      localStorage.setItem("username", username);
      localStorage.setItem("email", email);
      localStorage.setItem("userID", userID);

      alert("signup successful! Please log in.")
      window.location.href = "login.html";
    } 
    else if (userResponse.status === 409) {
      alert("Username already exists!");
    }

  } catch (err) {
    console.error(err);
    alert(err);
  }

});

function goToDashboard() {
  window.location.href = "index.html";
}
