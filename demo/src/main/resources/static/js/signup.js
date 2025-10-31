const signupForm = document.getElementById("signupForm");

document.getElementById("signupForm").addEventListener("submit", async function(event) {
  event.preventDefault();

  const name = document.getElementById("name").value.trim();
  const surname = document.getElementById("surname").value.trim();

  const username = document.getElementById("username").value.trim();
  const email = document.getElementById("email").value.trim();
  const password = document.getElementById("password").value;
  const confirmPassword = document.getElementById("confirmPassword").value;

  if (!name || !surname || !username || !email || !password || !confirmPassword) {
    alert("All fields are required!");
    return;
  }

  if (!isValidEmailDomain(email)) {
    alert("Your email domain is not allowed :)");
    return;
  }

  if (password !== confirmPassword) {
    alert("Passwords do not match!");
    return;
  }

//  alert(`Signup attempt:\nUsername: ${username}\nEmail: ${email}`);

  // Grab user data
  const user = {
    name: name,
    surname: surname,
    username: username,
    email: email,
    password: password
  };

  try{
    const userResponse = await fetch("/api/user/signup", {
      method: "POST",
      headers: {"Content-Type": "application/json"},
      body: JSON.stringify(user)
    });
    
    if (userResponse.status === 201) {
      const userID = await userResponse.json();

      alert("signup successful! Please log in.")
      window.location.href = "login.html";
    } 
    else if (userResponse.status === 409) {
      alert("Username already exists!");
    }
    else if (userResponse.status === 400) {
         const errors = await userResponse.json();
         let msg = "Validation errors:\n";
         for (const field in errors) {
           msg += `${field}: ${errors[field]}\n`;
         }
         alert(msg);
     }

  } catch (err) {
    console.error(err);
    alert(err);
  }

});

function goToDashboard() {
  window.location.href = "index.html";
}

function isValidEmailDomain(email) {
  const regex = /^[\w.-]+@aua\.am$/i;
  return regex.test(email);
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
