import shared from "./shared.js";

export default {
    template: `
        <h2>Login</h2>
        <div v-if="fail" class="alert alert-danger">
            username/password incorrect
        </div>
        <form id="auth" method="post">
            <div class="mb-3">
                <label>username :</label>
                <input class="form-control" v-model="loginForm.username"/>
            </div>
            <div class="mb-3">
                <label>password :</label>
                <input class="form-control" v-model="loginForm.password" type="password"/>
            </div>
            <button v-on:click.prevent="login()" class="btn btn-primary">
                Se connecter
            </button>
        </form>
    `,
    data() {
        return {
            axios: null,
            fail: false,
            loginForm: {
                username: null,
                password: null
            }
        }
    },
    mounted: function () {
        this.axios = axios.create({
            baseURL: 'http://localhost:8081/auth/',
            headers: { 'Content-Type': 'application/json' },
        });
        this.axios.interceptors.response.use(
            (response) => {
                this.fail = false;
                return response;
            },
            (error) => {
                if(error.status === 401)
                    this.fail = true;
            }
        );
    },
    methods: {
        login: function() {
            this.axios.post(
                'http://localhost:8080/auth/login?username='+ this.loginForm.username +
                '&password=' + this.loginForm.password
            )
                .then(r => {
                    if (!this.fail) {
                        console.log(r)
                        this.loginForm.username = null;
                        this.loginForm.password = null;
                        shared.setToken(r.data);
                        window.location.hash = "#/"
                    }
                });
        },
    }

}
