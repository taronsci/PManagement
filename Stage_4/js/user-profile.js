document.addEventListener("DOMContentLoaded", () => {
    const username = localStorage.getItem("username") || "";
    const email = localStorage.getItem("email") || "";

    document.getElementById("username").value = username;
    document.getElementById("email").value = email;

  loadMyListings();
  loadMyRequests(); //requests I have made
  loadRequestsReceived(); //requests for my listings 

});

let currentPageListings = 0;
let currentPageRequestsR = 0;
let currentPageRequestsM = 0;
const pageSize = 3; 

async function loadMyListings(page = 0) {
    try {
        const userId = localStorage.getItem("userID");
        const response = await fetch(`http://localhost:8080/api/listing/${userId}?page=${page}&size=${pageSize}`);
        const data = await response.json();

        const resultsContainer = document.getElementById("myListings");
        resultsContainer.innerHTML = ""; 

        const books = data._embedded?.bookListingDTOList || [];

        books.forEach(bookListingDTO => {
            const card = document.createElement("div");
            card.className = "book-card";

            card.innerHTML = `
                <h3>${bookListingDTO.book.title}</h3>
                <p><strong>Author:</strong> ${bookListingDTO.book.author}</p>
                <p><strong>Condition:</strong> ${bookListingDTO.condition}</p>
                <p><strong>Type:</strong> ${bookListingDTO.transactionType}${bookListingDTO.price ? ` ($${bookListingDTO.price})` : ''}</p>
                <p><strong>ListingStatus:</strong> ${bookListingDTO.status}</p>
            `;

            if(bookListingDTO.status == "Pending"){
                const removebutton = document.createElement("button");
                removebutton.textContent = "Remove Listing";
                removebutton.addEventListener("click", () => removeListing(bookListingDTO.id)); 
                card.appendChild(removebutton);
            }
            
            resultsContainer.appendChild(card);
        });
        
        currentPageListings = data.page?.number || 0;
        const totalPages = data.page?.totalPages || 1;

        document.getElementById("prevBtn").disabled = currentPageListings <= 0;
        document.getElementById("nextBtn").disabled = currentPageListings >= totalPages - 1;

        document.getElementById("page1Info").textContent = `${currentPageListings + 1} of ${totalPages}`;

    } catch (err) {
        console.error("Failed to load books:", err);
    }
}
function nextPageL() {
    loadMyListings(currentPageListings + 1);
}
function prevPageL() {
    loadMyListings(currentPageListings - 1);
}

async function loadRequestsReceived(page = 0) {
    try {
        const userId = localStorage.getItem("userID");
        const response = await fetch(`http://localhost:8080/api/request/${userId}?page=${page}&size=${pageSize}`);
        const data = await response.json();

        const resultsContainer = document.getElementById("requests");
        resultsContainer.innerHTML = ""; 

        const books = data._embedded?.bookRequestDTOList || [];

        books.forEach(bookRequestDTO => {
            const card = document.createElement("div");
            card.className = "book-card";

            const date = new Date(bookRequestDTO.createdAt);
            const formattedDate = date.toLocaleString();

            card.innerHTML = `
                <h3>${bookRequestDTO.book.title}</h3>
                <p><strong>Author:</strong> ${bookRequestDTO.book.author}</p>
                <p><strong>Requester:</strong> ${bookRequestDTO.requesterUsername}</p>
                <p><strong>ListingStatus:</strong> ${bookRequestDTO.status}</p>
                <p><strong>RequestedAt:</strong> ${formattedDate}</p>
            `;

            if(bookRequestDTO.status == "Pending"){
                const rejectbutton = document.createElement("button");
                rejectbutton.textContent = "Reject Request";

                const acceptbutton = document.createElement("button");
                acceptbutton.textContent = "Accept Request";

                acceptbutton.addEventListener("click", async () => {
                        acceptbutton.disabled = true;
                        rejectbutton.disabled = true;
                    await respondToRequest(bookRequestDTO.id, true);
                });
                rejectbutton.addEventListener("click", async () => {
                    acceptbutton.disabled = true;
                    rejectbutton.disabled = true;
                    await respondToRequest(bookRequestDTO.id, false);
                });

                card.appendChild(acceptbutton);
                card.appendChild(rejectbutton);
            }
        
            resultsContainer.appendChild(card);
        });
        
        currentPageRequestsR = data.page?.number || 0;
        const totalPages = data.page?.totalPages || 1;

        document.getElementById("prevBtnR").disabled = currentPageRequestsR <= 0;
        document.getElementById("nextBtnR").disabled = currentPageRequestsR >= totalPages - 1;

        document.getElementById("pageInfoR").textContent = `${currentPageRequestsR + 1} of ${totalPages}`;

    } catch (err) {
        console.error("Failed to load books:", err);
    }
}

function nextPageR() {
    loadRequestsReceived(currentPageRequestsR + 1);
}

function prevPageR() {
    loadRequestsReceived(currentPageRequestsR - 1);
}

async function loadMyRequests(page = 0) {
    try {
        const userId = localStorage.getItem("userID");
        const response = await fetch(`http://localhost:8080/api/request/my/${userId}?page=${page}&size=${pageSize}`);
        const data = await response.json();

        const resultsContainer = document.getElementById("myresults");
        resultsContainer.innerHTML = ""; 

        const books = data._embedded?.bookRequestDTOList || [];

        books.forEach(bookRequestDTO => {
            const card = document.createElement("div");
            card.className = "book-card";

            const date = new Date(bookRequestDTO.createdAt);
            const formattedDate = date.toLocaleString();

            card.innerHTML = `
                <h3>${bookRequestDTO.book.title}</h3>
                <p><strong>Author:</strong> ${bookRequestDTO.book.author}</p>
                <p><strong>Owner:</strong> ${bookRequestDTO.ownerUsername}</p>
                <p><strong>ListingStatus:</strong> ${bookRequestDTO.status}</p>
                <p><strong>RequestedAt:</strong> ${formattedDate}</p>
            `;

            if(bookRequestDTO.status == "Pending"){
                const button = document.createElement("button");
                button.textContent = "Remove Request";
                button.addEventListener("click", () => removeRequest(bookRequestDTO.id));
                card.appendChild(button);
            }

            resultsContainer.appendChild(card);
        });
        
        currentPageRequestsM = data.page?.number || 0;
        const totalPages = data.page?.totalPages || 1;

        document.getElementById("prevBtnM").disabled = currentPageRequestsM <= 0;
        document.getElementById("nextBtnM").disabled = currentPageRequestsM >= totalPages - 1;

        document.getElementById("pageInfoM").textContent = `${currentPageRequestsM + 1} of ${totalPages}`;

    } catch (err) {
        console.error("Failed to load books:", err);
    }
}

function nextPageM() {
    loadMyRequests(currentPageRequestsM + 1);
}

function prevPageM() {
    loadMyRequests(currentPageRequestsM - 1);
}

async function removeListing(id) {
    try {
        const response = await fetch(`http://localhost:8080/api/listing/${id}`, {
            method: 'DELETE'
        });

        if (response.status == 204) {
            console.log("Listing deleted");
        }

    } catch (err) {
        console.error("Failed to remove listing:", err);
    }
}

async function removeRequest(id) {
    try {
        const response = await fetch(`http://localhost:8080/api/request/${id}`, {
            method: 'DELETE'
        });

        if (response.status == 204) {
            console.log("Request deleted");
        } else if(response.status == 404) {
            alert("Error removing request");
        }

    } catch (err) {
        console.error("Failed to remove request:", err);
    }
}

async function respondToRequest(id, accepted) {
    try {
        const response = await fetch(`http://localhost:8080/api/request/${id}/status/${accepted}`, {
            method: 'PATCH'
        });

        
        if (response.status == 204) {
            console.log("Request updated");
        } else if(response.status == 404) {
            alert("Error updating request");
        }
        
    } catch (err) {
        console.error("Failed to respond to request:", err);
    }
}


function goToProfile() {
  window.location.href = "user-profile.html";
}

document.getElementById("editProfileForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const payload = {
        id: localStorage.getItem("userID"), 
        username: document.getElementById('username').value.trim(),
        email: document.getElementById('email').value.trim()
    };

    try {
        const response = await fetch('http://localhost:8080/api/user/update', {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const msg = await response.text();
            throw new Error(`Update failed (${response.status}): ${msg}`);
        }

        const updatedUser = await response.json(); // expected UserDTO

        // Update form values with returned data
        if (updatedUser.username) {
            document.getElementById('username').value = updatedUser.username;
        }
        if (updatedUser.email) {
            document.getElementById('email').value = updatedUser.email;
        }

        alert('Profile updated successfully!');
    } catch (err) {
        console.error(err);
        alert(err.message);
    }
});



document.getElementById("logoutBtn").addEventListener("click", function() {
  
  // Clear login state
  logout();
  // Redirect to dashboard
  goToDashboard();
});


function goToDashboard() {
  window.location.href = "index.html";
}


function logout() {
    fetch("http://localhost:8080/api/user/logout", {
        method: "POST",
        credentials: "include" // include cookies
    })
    .then(res => {
        if(res.ok) {
            alert("Logged out successfully");
            currentUser = null;
            localStorage.removeItem("isLoggedIn");
            window.location.href = "index.html";
        }
    })
    .catch(err => console.error("Logout failed", err));
}

function logout() {
    localStorage.removeItem("isLoggedIn");
    localStorage.removeItem("username");
    localStorage.removeItem("email");
}
