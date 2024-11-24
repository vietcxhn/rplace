import shared from "./shared.js";

export default {
    template: `
        <div>Login</div>
        <form id="auth" method="post">
            <div class="mb-3">
                <label>username :</label>
                <input class="form-control" v-model="loginForm.username"/>
            </div>
            <div class="mb-3">
                <label>password :</label>
                <input class="form-control" v-model="loginForm.password" type="password"/>
            </div>
            <button v-on:click.prevent="login()" class="ms-2 btn btn-primary">
                login
            </button>
        </form>
    `,
    data() {
        return {
            loginForm: {
                username: null,
                password: null
            }
        }
    },
    methods: {
        login: function() {
            shared.getAxios().post(
                'http://localhost:8080/auth/login?username='+ this.loginForm.username +
                '&password=' + this.loginForm.password
            )
                .then(r => {
                    console.log(r)
                    this.loginForm.username = null;
                    this.loginForm.password = null;
                    shared.setToken(r.data);
                    window.location.hash = "#/"
                });
        },
    }

}
