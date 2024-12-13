import shared from "./shared.js";
import stompService from "./stompClient.js";

export default {
    template: `
    <div id="chatbox">
        <button class="btn btn-primary" @click="toggleChatBox">
          {{ chatBoxVisible ? 'Fermer Chat' : 'Ouvrir Chat' }}
        </button>
        <div v-if="chatBoxVisible" id="chat-container">
            <div id="chat-box">
                <div class="chat-message" v-for="(message, index) in chatMessages" :key="index">
                    <span><b>{{ message.username }}:</b> {{ message.content }}</span>
                </div>                    
            </div>
            <div class="input-group mb-3 chat-input">
                <input @keyup.enter="sendMessage" class="form-control" 
                    type="text" id="chat-input" v-model="newMessage" 
                    placeholder="Type your message..."/>
                <div class="input-group-append">
                    <button class="btn btn-primary" @click="sendMessage">Send</button>          
                </div>
            </div>
        </div> 
    </div>
    `,
    data() {
        return {
            token: "",
            newMessage: "",
            chatMessages: [],
            chatBoxVisible: false,
        };
    },
    mounted() {
        stompService.subscribe('/topic/chat',  (message) => {
            const chatMessage = JSON.parse(message.body);
            this.chatMessages.push(chatMessage);
            this.scrollBottom();
        }).then(() => console.log("subscribed to /topic/chat"));
        this.token = sessionStorage.getItem("jwtToken");

        this.fetchMessage().then((r) => {
            console.log("done fetch message");
        })
    },
    methods: {
        async fetchMessage() {
            const responseChat = await fetch('/api/chat/chat-history');

            const chats = await responseChat.json();
            for (const chat of chats) {
                this.chatMessages.push(chat);
            }
        },
        sendMessage: function() {
            if (!this.token) {
                alert("Veuillez vous connecter pour chatter.");
                return;
            }
            if (this.newMessage.trim() !== "") {
                const chatMessage = {
                    token: this.token,
                    content: this.newMessage
                };
                stompService.send("/app/chat", {}, JSON.stringify(chatMessage))
                    .then(()=> console.log("sent to /app/chat"));
                this.newMessage = "";
            }
        },
        toggleChatBox() {
            this.chatBoxVisible = !this.chatBoxVisible;
            if (this.chatBoxVisible)
                this.scrollBottom();
        },
        scrollBottom: function() {
            Vue.nextTick(() => {
                const chatBox = document.getElementById("chat-box");
                chatBox.scrollTop = chatBox.scrollHeight;
            });
        },
    },
}