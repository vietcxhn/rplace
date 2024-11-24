import shared from "./shared.js";

export default {
    template: `
    
            <form v-if="(editable != null)" id="app" method="post" novalidate="true">

                <div class="mb-3">
                    <label>Titre :</label>
                    <input v-model="editable.title" class="form-control"
                           v-bind:class="{'is-invalid':errors.title}"/>
                    <div v-if="(errors.title)" class="alert alert-warning">
                        {{errors.title}}
                    </div>
                </div>
                <div class="mb-3">
                    <label for="activityNature" class="form-label">Nature</label>
                    <select
                      id="activityNature"
                      v-model="editable.nature"
                      class="form-select"
                      required
                    >
                      <option value="" disabled>Choisissez une nature</option>
                      <option
                        v-for="natureSelect in activityNatures"
                        :key="natureSelect.key"
                        :value="natureSelect.value"
                      >
                        {{ natureSelect.key }}
                      </option>
                    </select>
                </div>
                <div class="mb-3">
                    <label>Année :</label>
                    <input v-model="editable.activityYear" class="form-control"
                           v-bind:class="{'is-invalid':errors.activityYear}" number/>
                    <div v-if="(errors.activityYear)" class="alert alert-warning">
                        {{errors.activityYear}}
                    </div>
                </div>
                <div class="mb-3">
                    <label>Description :</label>
                    <textarea v-model="editable.description" rows="5" cols="50"
                              class="form-control"></textarea>
                </div>
                <div class="mb-3">
                    <label>Website :</label>
                    <input v-model="editable.website" class="form-control"
                           v-bind:class="{'is-invalid':errors.website}" number/>
                    <div v-if="(errors.website)" class="alert alert-warning">
                        {{errors.website}}
                    </div>
                </div>
                <div class="mb-3">
                    <button v-on:click.prevent="submitActivity()" class="ms-2 btn btn-primary">
                        Sauvegarder
                    </button>
                    <button v-on:click="listMovies()" class="ms-2 btn btn-primary">
                        Annuler
                    </button>
                </div>
            </form>
    `,
    data() {
        return {
            activityNatures: shared.shared.activityNatures,
            editable: null,
            errors: {},
            new: false,
        }
    },
    mounted: function () {
        this.new = sessionStorage.getItem("newActivity") === "true";
        console.log(this.new);
        if (!this.new)
            this.getActivity(sessionStorage.getItem("editingActivityId"));
        else {
            this.editable = {
                id: null,
                activityYear: null,
                nature: null,
                title: null,
                description: null,
                website: null,
            };
        }
    },
    methods: {

        getActivity: function(id) {
            shared.getAxios().get('/activities/' + id, {
                headers: {
                    'Authorization': `Bearer ${shared.getToken()}`
                }
            })
                .then(r => {
                    console.log(r.data);
                    this.editable = r.data;
                    console.log(this.editable);
                });
        },
        submitActivity () {
            this.errors = {}
            let request = this.new ?
                shared.getAxios().post('/activities?personId='+sessionStorage.getItem("editingPersonId"), this.editable, {
                    headers: {
                        'Authorization': `Bearer ${shared.getToken()}`
                    }
                }) :
                shared.getAxios().put('/activities/' + sessionStorage.getItem("editingActivityId"), this.editable, {
                    headers: {
                        'Authorization': `Bearer ${shared.getToken()}`
                    }
                })

            request.then(r => {
                console.log("status", r.status)
                this.errors = r.data
                if(Object.keys(this.errors).length === 0) {
                    this.editable = null;
                    window.location.hash = "#/person-view"
                    sessionStorage.removeItem("newActivity")
                }
            });
        },
        cancel() {
            window.location.hash = "#/person-view"
        }
    }
}