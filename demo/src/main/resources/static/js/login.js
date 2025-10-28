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

// email verification modal 
const modal = document.getElementById("verificationModal");
const verifyBtn = document.querySelector(".verify-btn");
const closeBtn = document.querySelector(".close-btn");

verifyBtn.onclick = async () => {
    modal.style.display = "block";
}
closeBtn.onclick = () => modal.style.display = "none";
window.onclick = (event) => { if(event.target === modal) modal.style.display = "none"; }

const form = document.getElementById("verifyUserForm");
const code = document.getElementById("code");

// change this to send code 
form.addEventListener("submit", async function(e) {
  e.preventDefault();  

  // Grab data
  const book = {
    title: document.getElementById("title").value,
    author: document.getElementById("author").value,
    year: document.getElementById("year").value || null,
    genre: document.getElementById("genre").value || null
  };

  try{
    const bookResponse = await fetch("/api/book", {
      method: "POST",
      headers: {"Content-Type": "application/json"},
      body: JSON.stringify(book),
      credentials: "include"
    });

    if (bookResponse.status === 400) {
        const errors = await bookResponse.json();
        let msg = "Validation errors:\n";
        for (const field in errors) {
            msg += `${field}: ${errors[field]}\n`;
        }
        alert(msg);
    }
    else if(!bookResponse.ok){
     throw new Error("Book creation failed");
    }

    const bookId = await bookResponse.json();
    
  } catch (err) {
    console.error(err);
    alert(err);
  }

  modal.style.display = "none";
  form.reset();
});


