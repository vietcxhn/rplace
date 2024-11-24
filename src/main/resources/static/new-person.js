import shared from "./shared.js";

export default {
    template: `
        <div v-if="success" class="alert alert-info">
            creer {{newUsername}} avec succes
        </div>
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
                    <label>Prénom :</label>
                    <input v-model="editable.firstName" class="form-control"
                           v-bind:class="{'is-invalid':errors.firstName}"/>
                    <div v-if="(errors.firstName)" class="alert alert-warning">
                        {{errors.firstName}}
                    </div>
                </div>
                <div class="mb-3">
                    <label>Nom :</label>
                    <input v-model="editable.lastName" class="form-control"
                           v-bind:class="{'is-invalid':errors.lastName}"/>
                    <div v-if="(errors.lastName)" class="alert alert-warning">
                        {{errors.lastName}}
                    </div>
                </div>
                <div class="mb-3">
                    <label>Email :</label>
                    <input v-model="editable.email" class="form-control"
                           v-bind:class="{'is-invalid':errors.email}"/>
                    <div v-if="(errors.email)" class="alert alert-warning">
                        {{errors.email}}
                    </div>
                </div>
                <div class="mb-3">
                    <label>Date de naissance :</label>
                    <input v-model="editable.birthDate" class="form-control"
                           v-bind:class="{'is-invalid':errors.birthDate}" type="date"/>
                    <div v-if="(errors.birthDate)" class="alert alert-warning">
                        {{errors.birthDate}}
                    </div>
                </div>
                <div class="mb-3">
                    <label>Website :</label>
                    <input v-model="editable.website" class="form-control"
                           v-bind:class="{'is-invalid':errors.website}"/>
                    <div v-if="(errors.website)" class="alert alert-warning">
                        {{errors.website}}
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
                    <button v-on:click.prevent="create()" class="ms-2 btn btn-primary">
                        Creer
                    </button>
                </div>
            </form>
    `,
    data() {
        return {
            axios: null,
            editable: {
                username: null,
                firstName: null,
                lastName: null,
                email: null,
                birthDate: null,
                website: null,
                password: null
            },
            errors: {},
            new: false,
            success: false,
            fail: false,
            newUsername: ""
        }
    },
    mounted: function () {
        this.axios = axios.create({
            baseURL: 'http://localhost:8081/api/',
            timeout: 1000,
            headers: { 'Content-Type': 'application/json' },
        });
        this.axios.interceptors.response.use(
            (response) => {
                return response;
            },
            (error) => {
                this.success = false;
                this.fail = true;
            }
        );
    },
    methods: {
        create: function() {
            this.errors = {};
            this.axios.post(
                'http://localhost:8080/api/persons', this.editable, {
                    headers: {
                        'Authorization': `Bearer ${shared.getToken()}`
                    }
                }
            )
                .then(r => {
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