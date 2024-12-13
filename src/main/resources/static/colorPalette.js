export default {
    template: `
    <div id="color-palette">
        <div
            v-for="color in colors"
            :key="color"
            :style="{ backgroundColor: color }"
            class="color"
            :class="{ selected: color === selectedColor }"
            @click="selectColor(color)"
        ></div>
    </div>
    `,
    data() {
        return {
            colors: [],
            selectedColor: "",
        };
    },
    mounted: async function() {
        const resonse = await fetch('/api/canvas/color-palette');
        this.colors = await resonse.json();
        this.selectColor("#000000");
    },
    methods: {
        selectColor(color) {
            this.selectedColor = color;
            // Emit event to parent
            this.$emit("colorSelected", color);
        },
    },
}