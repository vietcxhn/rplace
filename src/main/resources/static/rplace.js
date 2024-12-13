import shared from "./shared.js";

import ColorPalette from "./colorPalette.js";
import Chatbox from "./chatbox.js";
import stompService from "./stompClient.js";

export default {
    components: {
        ColorPalette,
        Chatbox
    },
    template: `
        <h1 class="center">r/Place Revival</h1>
        <div class="bold">Palette de couleurs:</div>
        <color-palette @colorSelected="setColor"></color-palette>
        
        <div class="cooldown center">Cooldown: {{cooldownTime}}s</div>
        
        <div id="zoom-controls">
            <button class="btn btn-primary" @click="zoomIn">Zoomer</button>
            <button class="btn btn-primary" @click="zoomOut">Dezoomer</button>
            <button class="btn btn-primary" @click="resetZoom">Réinitialiser</button>
        </div>
        
        <chatbox></chatbox>
        
        <div class="canvas-container" ref="canvasContainer">
            <div ref="canvasWrapper">
                <canvas id="canvas"></canvas>
            </div>  
        </div>
    `,
    data() {
        return {
            token: "",
            me: null,
            canvasSize: 1000,  // 1000x1000
            pixelSize: 15,
            color: "",
            canvas: null,
            ctx: null,
            maxCooldownTime: 300, // 5 minutes
            cooldownTime: 0,
            zoomLevel: 1,
            zoomStep: 0.1,
            maxZoom: 1,
            minZoom: 0.1,
            isDragging: false,
            dragged: false,
            dragStartX: 0,
            dragStartY: 0,
            scrollStartLeft: 0,
            scrollStartTop: 0,
        }
    },
    mounted() {
        this.token = sessionStorage.getItem("jwtToken");
        if (this.token) {
            shared.getMe().then(async r => {
                if (r) {
                    this.me = r.data;
                    const responseCooldown = await fetch('/api/canvas/cooldown?' + new URLSearchParams({
                        username: this.me.username
                    }).toString());
                    let cooldown = await responseCooldown.json();
                    cooldown = Math.ceil(cooldown/1000);
                    if(cooldown > 0)
                        this.startCooldown(cooldown);
                }
            });
        }
        this.canvas = document.getElementById("canvas");
        this.ctx = this.canvas.getContext("2d");

        // Set canvas size
        this.canvas.width = this.canvasSize * this.pixelSize;
        this.canvas.height = this.canvasSize * this.pixelSize;
        this.adjustZoom(this.zoomLevel);

        this.drawGrid();

        stompService.subscribe('/topic/canvas', (message) => {
            const pixel = JSON.parse(message.body);
            this.updateCanvas(pixel.x, pixel.y, pixel.color);
        }).then(() => console.log("subscribed to /topic/canvas"));;

        const canvasContainer = this.$refs.canvasContainer;

        canvasContainer.addEventListener("mousedown", this.startDrag);
        canvasContainer.addEventListener("mousemove", this.drag);
        canvasContainer.addEventListener("mouseup", this.stopDrag);

        this.canvas.addEventListener('click', this.canvasOnClick);

        this.fetchCanvas().then(r => {
            console.log("done fetch canvas")
        });

    },
    methods: {
        updateCanvas: function(x, y, color) {
            this.ctx.fillStyle = color;
            this.ctx.fillRect(
                x * this.pixelSize + 1,
                y * this.pixelSize + 1,
                this.pixelSize - 2,
                this.pixelSize - 2);
        },
        drawGrid() {
            const width = this.canvas.width;
            const height = this.canvas.height;

            this.ctx.strokeStyle = '#ccc';
            this.ctx.lineWidth = 1;

            // Draw vertical lines
            for (let x = 0; x <= width; x += this.pixelSize) {
                this.ctx.beginPath();
                this.ctx.moveTo(x, 0);
                this.ctx.lineTo(x, height);
                this.ctx.stroke();
            }

            // Draw horizontal lines
            for (let y = 0; y <= height; y += this.pixelSize) {
                this.ctx.beginPath();
                this.ctx.moveTo(0, y);
                this.ctx.lineTo(width, y);
                this.ctx.stroke();
            }
        },
        fetchCanvas: async function() {
            const responseCanvas = await fetch('/api/canvas');

            const pixels = await responseCanvas.json();
            pixels.forEach((pixel) => {
                this.updateCanvas(pixel.x, pixel.y, pixel.color);
            });
        },
        canvasOnClick : function (event) {
            // check if dragged
            if(this.dragged) {
                return;
            }
            if (this.cooldownTime > 0) {
                alert("Veuillez patienter jusqu'à ce que le cooldown soit terminé.");
                return;
            }
            if (!this.me) {
                alert("Veuillez vous connecter pour placer des pixels.");
                return;
            }

            const rect = this.canvas.getBoundingClientRect();
            const x = Math.floor((event.clientX - rect.left) / this.zoomLevel / this.pixelSize);
            const y = Math.floor((event.clientY - rect.top) / this.zoomLevel / this.pixelSize);

            this.sendPixelUpdate(x, y, this.color);

            this.startCooldown(this.maxCooldownTime);
        },
        sendPixelUpdate: function(x, y, color) {
            const message = JSON.stringify({pixel:{ x, y, color }, token: this.token});
            stompService.send('/app/update', {}, message);
        },
        startCooldown: function (cooldownTime) {
            if(this.me.roles.includes("ADMIN"))
                return
            this.cooldownTime = cooldownTime; // Set cooldown time (30 seconds)
            const cooldownInterval = setInterval(() => {
                if (this.cooldownTime > 0) {
                    this.cooldownTime--;
                } else {
                    clearInterval(cooldownInterval);
                }
            }, 1000);
        },
        zoomIn() {
            if (this.zoomLevel < this.maxZoom) {
                this.adjustZoom(this.zoomLevel + this.zoomStep);
            }
        },
        zoomOut() {
            if (this.zoomLevel > this.minZoom) {
                this.adjustZoom(this.zoomLevel - this.zoomStep);
            }
        },
        resetZoom() {
            this.adjustZoom(1);
        },
        adjustZoom(newScale) {
            newScale = Math.round(newScale*10)/10;
            const canvasContainer = this.$refs.canvasContainer;
            const canvasWrapper = this.$refs.canvasWrapper;

            const originalWidth = this.canvas.offsetWidth;
            const originalHeight = this.canvas.offsetHeight;

            // Calculate the scroll offsets before scaling
            const containerRect = canvasContainer.getBoundingClientRect();
            const offsetX = canvasContainer.scrollLeft + containerRect.width/2;
            const offsetY = canvasContainer.scrollTop + containerRect.height/2;

            // Calculate the zoom factor
            const scaleFactor = newScale / this.zoomLevel;

            // Adjust scroll position to keep the canvas centered
            const scrollLeft = offsetX * scaleFactor - offsetX;
            const scrollTop = offsetY * scaleFactor - offsetY;

            // Update the scale and apply the transformation
            this.zoomLevel = newScale;
            this.canvas.style.transform = `scale(${this.zoomLevel})`;

            // Update the wrapper's size to match canvas size
            canvasWrapper.style.width = `${originalWidth * this.zoomLevel}px`;
            canvasWrapper.style.height = `${originalHeight * this.zoomLevel}px`;

            // Adjust scroll position
            canvasContainer.scrollLeft += scrollLeft;
            canvasContainer.scrollTop += scrollTop;
        },
        setColor(color) {
            this.color = color;
        },
        startDrag(event) {
            console.log(event.button)
            // Only allow dragging with the left mouse button (button 0)
            if (event.button !== 0) return;

            this.isDragging = true;
            this.dragged = false;
            const canvasContainer = this.$refs.canvasContainer;

            // Store starting positions
            this.dragStartX = event.clientX;
            this.dragStartY = event.clientY;
            this.scrollStartLeft = canvasContainer.scrollLeft;
            this.scrollStartTop = canvasContainer.scrollTop;

            // Prevent context menu on right-click drag
            event.preventDefault();
        },
        drag(event) {
            if (!this.isDragging) return;

            const canvasContainer = this.$refs.canvasContainer;

            const deltaX = this.dragStartX - event.clientX;
            const deltaY = this.dragStartY - event.clientY;
            // check if dragged to cancel click to place pixel event
            if (deltaX>1||deltaY>1) {
                this.dragged = true;
            }
            canvasContainer.scrollLeft = this.scrollStartLeft + deltaX;
            canvasContainer.scrollTop = this.scrollStartTop + deltaY;
        },
        stopDrag() {
            this.isDragging = false;
        },
    },
    beforeDestroy() {
        const canvasContainer = this.$refs.canvasContainer;
        canvasContainer.removeEventListener("mousedown", this.startDrag);
        canvasContainer.removeEventListener("mousemove", this.drag);
        canvasContainer.removeEventListener("mouseup", this.stopDrag);
        this.canvas.removeEventListener('click', this.canvasOnClick);
    },
}
