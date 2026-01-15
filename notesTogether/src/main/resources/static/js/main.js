'use strict';

var loginPage = document.querySelector("#login-page");
var loginForm = document.querySelector("#login-form");
var emailInput = document.querySelector("#email");
var passwordInput = document.querySelector("#password");

var notesPage = document.querySelector("#notes-page");
var notesList = document.querySelector("#notes-list");

var notePage = document.querySelector("#note-page");
var messageTitleInput = document.querySelector("#message-title");
var messageContentInput = document.querySelector("#message-content");
var messageArea = document.querySelector("#message-area");

var stompClient = null;
var email = null;
var token = null;
var currentNoteId = null;
var myNotes = null;
var currentNote = null;

loginForm.addEventListener("submit", connect, true);

document.getElementById("save-btn").addEventListener("click", () => sendNoteAction("SAVE"));
document.getElementById("update-btn").addEventListener("click", () => sendNoteAction("UPDATE"));
document.getElementById("typing-btn").addEventListener("click", () => sendNoteAction("TYPING"));
document.getElementById("create-note-btn").addEventListener("click", () => startNewNote());

async function startNewNote() {
    try {
        const response = await fetch("http://localhost:8080/api/notes",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify({
                    actorEmail: email,
                    noteId: null,
                    title: "new",
                    content: "new",
                    action: "CREATE"
                })
            });

        if (!response.ok) return new Error("Login failed for some reason");

        const result = await response.json();
        currentNote = result.data;
        currentNoteId = result.data.id;

        const li = document.createElement("li");
        li.classList.add("note");

        li.textContent = JSON.stringify({
            actorEmail: email,
            noteId: currentNoteId,
            title: currentNote.noteVersions[0].title,
            content: currentNote.noteVersions[0].content,
            action: "CREATE"
        }, null, 2);
        li.style.whiteSpace = "pre-wrap";

        messageArea.appendChild(li);

        loginPage.classList.add("hidden");
        notesPage.classList.add("hidden");
        notePage.classList.remove("hidden");
    } catch (e) {
        console.error(e);
        alert("Create note failed");
    }
}

function sendNoteAction(action) {
    if (!stompClient) return;

    if (action === "SAVE") saveMessage();
    if (action === "UPDATE") updateMessage();
    if (action === "TYPING") typingMessage();
}

async function connect(event) {
    event.preventDefault();
    console.log("connect() fired");

    email = emailInput.value.trim();
    const password = passwordInput.value.trim();
    if (!email || !password) return;

    try {
        const response = await fetch("http://localhost:8080/api/users/login",
            {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, password })
            });

        if (!response.ok) return new Error("Login failed for some reason");

        const result = await response.json();
        token = result.data.token;

        loginPage.classList.add("hidden");
        notesPage.classList.remove("hidden");
        notePage.classList.add("hidden");

        await populateNotesPage();
        await connectWebsocket();
    } catch (e) {
        console.error(e);
        alert("Login failed");
    }
}

async function populateNotesPage() {
    try {
        const response = await fetch(`http://localhost:8080/api/notes?userEmail=${email}`,
            {
                method: "GET",
                headers: {
                    "Authorization": `Bearer ${token}`
                }
            });

        if (!response.ok) return new Error("Population failed");

        const result = await response.json();
        const notes = result.data;

        notesList.innerHTML = "";
        myNotes = notes;

        notes.forEach(function (note) {
            const li = document.createElement("li");
            li.classList.add("note");

            li.textContent = JSON.stringify(note, null, 2);
            li.style.whiteSpace = "pre-wrap";
            li.addEventListener("click", () => {
                currentNoteId = note.id;
                openNote(currentNoteId);
            });

            notesList.appendChild(li);
            currentNote = note;
        });
    } catch (e) {
        console.error(e);
        alert("Population failed");
    }
}

function openNote() {
    stompClient.subscribe(`/topic/public/${currentNoteId}`, onNoteReceived);
    stompClient.send(
        '/app/note.join',
        {},
        JSON.stringify({
            actorEmail: email,
            noteId: currentNoteId,
            title: null,
            content: null,
            action: "JOIN"
        }));

    loginPage.classList.add("hidden");
    notesPage.classList.add("hidden");
    notePage.classList.remove("hidden");

    messageTitleInput.value = "";
    messageContentInput.value = "";
}

function connectWebsocket() {
    const socket = new SockJS("http://localhost:8080/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect(
        { Authorization: `Bearer ${token}` },
        onConnected,
        onError
    );
}

function onConnected() {
    console.log("WebSocket connected");
}

function onError() {
    alert("Could not connect to WebSocket server. Please refresh this page and try again");
}

function onNoteReceived(payload) {
    const note = JSON.parse(payload.body);

    const li = document.createElement("li");

    if (note.type === "JOIN") {
        alert(`${note.actorEmail} joined the note`);
    } else if (note.type === "TYPING") {
        alert(`${note.actorEmail} is typing`);
    } else if (note.type === "UPDATE") {
        alert(`${note.actorEmail} made an update`);

        li.classList.add("note");

        li.textContent = JSON.stringify(note, null, 2);
        li.style.whiteSpace = "pre-wrap";

        messageArea.appendChild();
        alert(`${note.actorEmail} stopped typing`);
    } else if (note.type === "SAVE") {
        alert(`${note.actorEmail} made a save`);

        li.classList.add("note");

        li.textContent = JSON.stringify(note, null, 2);
        li.style.whiteSpace = "pre-wrap";

        messageArea.appendChild();
        alert(`${note.actorEmail} stopped typing`);
    }
    messageArea.scrollTop = messageArea.scrollHeight;
}

function saveMessage() {
    if (!currentNoteId) return;

    stompClient.send(
        "/app/note.save",
        {},
        JSON.stringify({
            actorEmail: email,
            noteId: currentNoteId,
            title: messageTitleInput.value,
            content: messageContentInput.value,
            action: "SAVE"
        })
    );
}

function updateMessage() {
    if (!currentNoteId) return;

    stompClient.send(
        "/app/note.update",
        {},
        JSON.stringify({
            actorEmail: email,
            noteId: currentNoteId,
            title: messageTitleInput.value,
            content: messageContentInput.value,
            action: "UPDATE"
        })
    );
}

function typingMessage() {
    if (!currentNoteId) return;

    stompClient.send(
        "/app/note.typing",
        {},
        JSON.stringify({
            actorEmail: email,
            noteId: currentNoteId,
            action: "TYPING"
        })
    );
}