let stompClient = null;
let currUser = null;
let csrfToken = null;

function connect() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/queue/messages', function (message) {
            showMessage(JSON.parse(message.body));
        });
    });
}

function attachUserClickEvents(csrfToken) {
    const userItems = document.querySelectorAll('.user-item');
    userItems.forEach(item => {
        item.addEventListener('click', function () {
            userItems.forEach(i => i.classList.remove('active'));
            item.classList.add('active');
            const username = item.getAttribute('data-username');
            if (!username) {
                console.error('No username found for clicked item');
                return;
            }
            const chatHeader = document.getElementById('chatHeader');
            chatHeader.textContent = '';
            const h5 = document.createElement('h5');
            h5.className = 'mb-0';
            h5.textContent = username;
            chatHeader.appendChild(h5);
            document.getElementById('recipient').value = username;
            document.getElementById('messageForm').style.display = 'block';
            loadMessages(username, csrfToken);
        });
    });
}

function loadMessages(recipient, csrfToken) {
    if (!recipient) {
        console.error('Recipient is empty or null');
        const chatBox = document.getElementById('chat-box');
        chatBox.textContent = '';
        const errorDiv = document.createElement('div');
        errorDiv.className = 'text-danger';
        errorDiv.textContent = 'No se seleccionó un destinatario';
        chatBox.appendChild(errorDiv);
        return;
    }
    if (!currUser) {
        console.error('currentUser is undefined');
        const chatBox = document.getElementById('chat-box');
        chatBox.textContent = '';
        const errorDiv = document.createElement('div');
        errorDiv.className = 'text-danger';
        errorDiv.textContent = 'Error: usuario actual no definido';
        chatBox.appendChild(errorDiv);
        return;
    }
    console.log('Loading messages for recipient:', recipient);
    const url = `/history?recipient=${encodeURIComponent(recipient)}`;
    console.log('Fetch URL:', url);
    fetch(url, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'X-CSRF-TOKEN': csrfToken
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`Error al cargar mensajes: ${response.status} ${response.statusText}`);
            }
            return response.json();
        })
        .then(messages => {
            const chatBox = document.getElementById('chat-box');
            chatBox.textContent = '';
            messages.forEach(message => {
                showMessage(message);
            });
            chatBox.scrollTop = chatBox.scrollHeight;
        })
        .catch(error => {
            console.error('Error al cargar mensajes:', error);
            const chatBox = document.getElementById('chat-box');
            chatBox.textContent = '';
            const errorDiv = document.createElement('div');
            errorDiv.className = 'text-danger';
            errorDiv.textContent = 'Error al cargar mensajes: ' + error.message;
            chatBox.appendChild(errorDiv);
        });
}

function showMessage(message) {
    console.log('Showing message:', message);
    console.log('message.sender:', message.sender);
    console.log('currUser:', currUser);
    console.log('isOwnMessage:', message.sender === currUser);
    const chatBox = document.getElementById('chat-box');
    const outerDiv = document.createElement('div');
    outerDiv.className = 'mb-3';

    // Comparación insensible a mayúsculas/minúsculas
    const isOwnMessage = message.sender && currUser &&
        message.sender.toLowerCase() === currUser.toLowerCase();

    const innerDiv = document.createElement('div');
    innerDiv.className = `d-flex ${isOwnMessage ? 'justify-content-end' : 'justify-content-start'}`;

    const messageDiv = document.createElement('div');
    messageDiv.className = `p-2 rounded ${isOwnMessage ? 'bg-primary text-white' : 'bg-light'}`;

    const strong = document.createElement('strong');
    strong.textContent = message.sender || 'Unknown';

    const content = document.createTextNode(`: ${message.content || ''} `);

    const small = document.createElement('small');
    small.className = 'text-muted d-block';
    small.textContent = message.timestamp || new Date().toLocaleTimeString();

    messageDiv.appendChild(strong);
    messageDiv.appendChild(content);
    messageDiv.appendChild(small);
    innerDiv.appendChild(messageDiv);
    outerDiv.appendChild(innerDiv);
    chatBox.appendChild(outerDiv);
    chatBox.scrollTop = chatBox.scrollHeight;
}

function sendMessageToUser() {
    console.log('sendMessageToUser llamado');
    const recipient = document.getElementById('recipient').value;
    const content = document.getElementById('content').value;

    if (content && recipient && stompClient) {
        const message = {
            receiver: recipient,
            content: content
        };
        console.log('Sending message:', message);
        stompClient.send('/app/chat', {}, JSON.stringify(message));
        document.getElementById('content').value = '';
    } else {
        console.log('No se puede enviar el mensaje:', { content, recipient, stompClient });
    }
}

document.addEventListener('DOMContentLoaded', async () => {
    csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');

    try {
        const response = await fetch('/users/current', {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            },
            //credentials: 'include'
        });
        if (!response.ok) {
            throw new Error('Error al cargar usuarios: ' + response.status);
        }
        currUser = await response.text();
        console.log('Usuario actual:', currUser);
    } catch (error) {
        console.error('Error al cargar el usuario actual:', error);
    }

    console.log('currUser:', currUser);
    console.log('csrfToken:', csrfToken);

    if (!currUser) {
        console.error('currentUser is undefined at startup');
        const chatBox = document.getElementById('chat-box');
        chatBox.textContent = '';
        const errorDiv = document.createElement('div');
        errorDiv.className = 'text-danger';
        errorDiv.textContent = 'Error: usuario actual no definido';
        chatBox.appendChild(errorDiv);
        return;
    }

    connect();
    const searchForm = document.getElementById('searchForm');
    searchForm.addEventListener('submit', function (event) {
        event.preventDefault();
        const formData = new FormData(searchForm);
        fetch(searchForm.action, {
            method: 'POST',
            body: formData,
            headers: {
                'Accept': 'application/json'
            },
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error en la búsqueda: ' + response.status);
                }
                return response.json();
            })
            .then(data => {
                const userList = document.getElementById('userList');
                userList.textContent = '';
                data.users.forEach(user => {
                    const li = document.createElement('li');
                    li.className = 'list-group-item user-item';
                    li.dataset.username = user.username;
                    li.textContent = user.username;
                    userList.appendChild(li);
                });
                attachUserClickEvents(csrfToken);
            })
            .catch(error => {
                console.error('Error en la búsqueda:', error);
                const userList = document.getElementById('userList');
                userList.textContent = '';
                const li = document.createElement('li');
                li.className = 'list-group-item';
                li.textContent = 'Error al buscar usuarios';
                userList.appendChild(li);
            });
    });

    attachUserClickEvents(csrfToken);

    document.getElementById('sendMessageForm').addEventListener('submit', function (event) {
        console.log('Formulario enviado');
        event.preventDefault();
        sendMessageToUser();
    });

    const chatBox = document.getElementById('chat-box');
    chatBox.scrollTop = chatBox.scrollHeight;
});