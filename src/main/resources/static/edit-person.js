import shared from "./shared.js";

export default {
    template: `
    
            <form v-if="(editable != null)" id="app" method="post" novalidate="true">

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
                    <button v-on:click.prevent="submitPerson()" class="ms-2 btn btn-primary">
                        Sauvegarder
                    </button>
                    <button v-on:click="cancel()" class="ms-2 btn btn-primary">
                        Annuler
                    </button>
                </div>
            </form>
    `,
    data() {
        return {
            editable: null,
            errors: {},
            new: false,
        }
    },
    mounted: function () {
        this.getPerson(sessionStorage.getItem("editingPersonId"));
    },
    methods: {

        getPerson: function(id) {
            shared.getAxios().get('/persons/' + id, {
                headers: {
                    'Authorization': `Bearer ${shared.getToken()}`
                }
            })
                .then(r => {
                    console.log(r.data);
                    this.editable = r.data;
                    this.editable.birthDate = this.formatDate(this.editable.birthDate);
                    console.log(this.editable);
                });
        },
        formatDate(dateString) {
            // Formater la date en dd/mm/yyyy
            const date = new Date(dateString);
            return `${date.getFullYear()}-${
                String(date.getMonth() + 1).padStart(2, "0")}-${
                String(date.getDate()).padStart(2, "0")}`;
        },
        submitPerson () {
            this.errors = {}
            let request = this.new ?
                shared.getAxios().post('/persons/', this.editable, {
                    headers: {
                        'Authorization': `Bearer ${shared.getToken()}`
                    }
                }) :
                shared.getAxios().put('/persons/' + sessionStorage.getItem("editingPersonId"), this.editable, {
                    headers: {
                        'Authorization': `Bearer ${shared.getToken()}`
                    }
                })

            request.then(r => {
                console.log("status", r.status)
                this.errors = r.data
                if(Object.keys(this.errors).length === 0) {
                    this.editable = null;
                    window.location.hash = "#/person-view";
                    this.new = false;
                }
            });
        },
        cancel() {
            window.location.hash = "#/person-view";
        }
    }
}