import shared from "./shared.js";

export default {
    template: `
        <h2>S'inscrire</h2>
        <div v-if="fail" class="alert alert-danger">
            username existe deja
        </div>
            <form id="app" method="post" novalidate="true">
                <div class="mb-3">
                    <label>Nom d'utilisateur :</label>
                    <input v-model="editable.username" class="form-control"
                           v-bind:class="{'is-invalid':errors.username}"/>
                    <div v-if="(errors.username)" class="alert alert-warning">
                        {{errors.username}}
                    </div>
                </div>
                <div class="mb-3">
                    <label>Mot de passe :</label>
                    <input v-model="editable.password" class="form-control"
                           v-bind:class="{'is-invalid':errors.password}" type="password"/>
                    <div v-if="(errors.password)" class="alert alert-warning">
                        {{errors.password}}
                    </div>
                </div>
                <div class="mb-3">
                    <button v-on:click.prevent="signup()" class="ms-2 btn btn-primary">
                        S'inscrire
                    </button>
                </div>
            </form>
    `,
    data() {
        return {
            axios: null,
            editable: {
                username: null,
                password: null
            },
            errors: {},
            new: false,
            fail: false,
            newUsername: ""
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
                if (error.status === 400)
                    this.fail = true;
            }
        );
    },
    methods: {
        signup: function() {
            this.errors = {};
            this.axios.post(
                'http://localhost:8080/auth/signup', this.editable, {
                }
            )
                .then(r => {
                    if (Object.keys(r.data).includes("token")) {
                        shared.setToken(r.data.token);
                        window.location.hash = "#/"
                        return;
                    }
                    this.errors = r.data;
                    if (Object.keys(this.errors).length === 0) {
                        this.success = true;
                        this.fail = false;
                        this.newUsername = this.editable.username
                    }
                })

        },
    }
}