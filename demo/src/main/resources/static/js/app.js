async function updateHeader() {
  const headerActions = document.querySelector(".header-actions");
  const profileCircle = document.querySelector(".profile-circle");

  const loggedIn = await isAuthenticated();

  if (loggedIn) {
    headerActions.style.display = "none";
    profileCircle.style.display = "flex";
  } else {
    headerActions.style.display = "flex";
    profileCircle.style.display = "none";
  }
}

// Run after DOM loads
document.addEventListener("DOMContentLoaded", async () => {
  loadBooks();
  updateHeader();
});

let currentPage = 0;
const pageSize = 9; 

//book-cards on front page
async function loadBooks(page = 0) {
    try {
        const response = await fetch(`/api/listing?page=${page}&size=${pageSize}`);
        const data = await response.json(); // array of bookListingDTO objects

        const resultsContainer = document.getElementById("resultsContainer");
        resultsContainer.innerHTML = ""; 

        // HATEOAS PagedModel: actual items are in _embedded.bookListingDTOList
        const books = data._embedded?.bookListingDTOList || [];

        books.forEach(bookListingDTO => {
            const card = document.createElement("div");
            card.className = "book-card";

            card.innerHTML = `
                <h3>${bookListingDTO.book.title}</h3>
                <p><strong>Author:</strong> ${bookListingDTO.book.author}</p>
                <p><strong>Condition:</strong> ${bookListingDTO.condition}</p>
                <p><strong>Type:</strong> ${bookListingDTO.transactionType}${bookListingDTO.price ? ` ($${bookListingDTO.price})` : ''}</p>
            `;
            
            const button = document.createElement("button");
            button.textContent = "Request book";
            button.addEventListener("click", () => requestBook(bookListingDTO));
            card.appendChild(button);

            resultsContainer.appendChild(card);
        });
        
        // Update pagination info
        currentPage = data.page?.number || 0;
        const totalPages = data.page?.totalPages || 1;

        // Enable/disable next/prev buttons
        document.getElementById("prevBtn").disabled = currentPage <= 0;
        document.getElementById("nextBtn").disabled = currentPage >= totalPages - 1;

        document.getElementById("pageInfo").textContent = `${currentPage + 1} of ${totalPages}`;

    } catch (err) {
        console.error("Failed to load books:", err);
    }
}

async function requestBook(bookListingDTO) {

    if (!isAuthenticated()) {
        alert("You must be logged in to request a book.");
        return;
    }
    
    const request = {
      listingId: bookListingDTO.id,
      createdAt: new Date().toISOString()
    };
    try {
        const response = await fetch("/api/request", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(request),
            credentials: "include"
        });

        if (!response.ok) {
            if (response.status === 401) 
              return alert("Unauthorized. Please log in again.");

            throw new Error(await response.text());
        }

        const data = await response.json();
        alert(`Book request sent successfully! ${data || ""}`);

    } catch (err) {
        console.error(err);
        alert("Failed to request book. Please try again later.");
    }
}
  
function nextPage() {
    loadBooks(currentPage + 1);
}

function prevPage() {
    loadBooks(currentPage - 1);
}

function goToLogin(){
  window.location.href = "/login.html";
}
function goToSignup(){
  window.location.href = "/signup.html";
}

//searching 
async function performSearch(page = 0) {
  const query = document.getElementById("searchBar").value;
  const type = document.getElementById("filterType").value;

  try {
        const response = await fetch(`/api/listing/search?query=${encodeURIComponent(query)}&type=${encodeURIComponent(type)}&page=${page}&size=${pageSize}`,
        {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        // Parse JSON response
        const data = await response.json();

        const resultsContainer = document.getElementById("resultsContainer");
        resultsContainer.innerHTML = "";

        // HATEOAS PagedModel: actual items are in _embedded.bookListingDTOList
        const books = data._embedded?.bookListingDTOList || [];

        books.forEach(bookListingDTO => {
            const card = document.createElement("div");
            card.className = "book-card";

            card.innerHTML = `
                <h3>${bookListingDTO.book.title}</h3>
                <p><strong>Author:</strong> ${bookListingDTO.book.author}</p>
                <p><strong>Condition:</strong> ${bookListingDTO.condition}</p>
                <p><strong>Type:</strong> ${bookListingDTO.transactionType}${bookListingDTO.price ? ` ($${bookListingDTO.price})` : ''}</p>
            `;

            const button = document.createElement("button");
            button.textContent = "Request book";
            button.addEventListener("click", () => requestBook(bookListingDTO));
            card.appendChild(button);

            resultsContainer.appendChild(card);
        });

        // Update pagination info
        currentPage = data.page?.number || 0;
        const totalPages = data.page?.totalPages || 1;

        // Enable/disable next/prev buttons
        document.getElementById("prevBtn").disabled = currentPage <= 0;
        document.getElementById("nextBtn").disabled = currentPage >= totalPages - 1;

        document.getElementById("pageInfo").textContent = `${currentPage + 1} of ${totalPages}`;

    } catch (err) {
        console.error("Failed to perform search:", err);
    }
}


async function goToProfile() {
    const loggedIn = await isAuthenticated();
    if(!loggedIn){
        alert("ERROR");
    }
    window.location.href = "/user-profile.html";
}

async function isAuthenticated() {
    try {
        const res = await fetch("/api/user/check", {
            method: "GET",
            credentials: "include"
        });
        return res.ok
    } catch {
        return false;
    }
}


// Modal stuff
const modal = document.getElementById("registerModal");
const registerBtn = document.querySelector(".register-btn");
const closeBtn = document.querySelector(".close-btn");

registerBtn.onclick = async () => {
    const loggedIn = await isAuthenticated();
    if (!loggedIn) {
        alert("You must be logged in to register a book!");
        return;
    }
    modal.style.display = "block";
}
closeBtn.onclick = () => modal.style.display = "none";
window.onclick = (event) => { if(event.target === modal) modal.style.display = "none"; }

const form = document.getElementById("registerBookForm");
const transactionSelect = document.getElementById("transaction");
const priceInput = document.getElementById("price");
const rentalInput = document.getElementById("rentalDuration");

form.addEventListener("submit", async function(e) {
  e.preventDefault();

  const transaction = transactionSelect.value;

  // Conditional validation
  if(transaction === "SELL" && !priceInput.value){
    alert("Price is required for Sell transactions!");
    priceInput.focus();
    return;
  }
  if(transaction === "RENT"){
    if(!priceInput.value){
      alert("Price is required for Rent transactions!");
      priceInput.focus();
      return;
    }
    if(!rentalInput.value){
      alert("Rental duration is required for Rent transactions!");
      rentalInput.focus();
      return;
    }
  }

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

    const listing = {
      bookId: bookId,
      condition: document.getElementById("condition").value,
      transactionType: transaction,
      price: priceInput.value || null,
      rentalDuration: rentalInput.value || null,
    };

    const listingResponse = await fetch("/api/listing/create", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(listing),
      credentials: "include"
    });

    if(listingResponse.status.ok){
      alert(`Book "${book.title}" with id "${bookId}" registered successfully!`);
//      alert("Listing successfully created")
      modal.style.display = "none";
      form.reset();
    }
    else if (listingResponse.status === 400) {
        const errors = await listingResponse.json();
        let msg = "Validation errors:\n";
        for (const field in errors) {
            msg += `${field}: ${errors[field]}\n`;
        }
        alert(msg);
    } else if(!listingResponse.ok){
        alert(listingResponse.status);
        throw new Error("Listing creation failed");
    }

    
  } catch (err) {
    console.error(err);
    alert(err);
  }

  modal.style.display = "none";
  form.reset();
});


