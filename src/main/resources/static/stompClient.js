const stompService = {
    stompClient: null,
    isConnected: false,
    connectPromise: null,

    connect() {
        if (this.isConnected) {
            return Promise.resolve();
        }
        if (this.connectPromise) {
            return this.connectPromise;
        }
        this.connectPromise = new Promise((resolve, reject) => {

            const socket = new SockJS('/ws');
            this.stompClient = Stomp.over(socket);

            this.stompClient.debug = null;

            this.stompClient.connect({}, () => {
                console.log('WebSocket connected');
                this.isConnected = true;
                resolve();
            }, (error) => {
                console.error('WebSocket connection failed:', error);
                this.isConnected = false;
                reject(error);
            });
        });
        return this.connectPromise;
    },
    subscribe(destination, callback) {
        return this.connect().then(() => {
            this.stompClient.subscribe(destination, callback);
        });
    },
    send(destination, headers = {}, body = '') {
        return this.connect().then(() => {
            this.stompClient.send(destination, headers, body);
        });
    },
};

export default stompService;