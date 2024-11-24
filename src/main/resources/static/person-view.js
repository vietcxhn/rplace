import shared from "./shared.js";

export default {
    template: `
        <h2 class="text-center mb-4">Détails de la Personne</h2>

        <div v-if="person" class="card shadow-sm mb-4">
            <div class="card-body">
                <h5 class="card-title">{{ person.firstName }} {{ person.lastName }}</h5>
                <p class="card-text">
                    <strong>Email : </strong>
                    <a :href="'mailto:' + person.email">{{ person.email }}</a>
                </p>
                <p class="card-text" v-if="person.website">
                    <strong>Site Web : </strong>
                    <a :href="getValidUrl(person.website)" target="_blank">{{ person.website }}</a>
                </p>
                <p class="card-text">
                    <strong>Date de naissance : </strong>{{ formatDate(person.birthDate) }}
                </p>
                <button
                    v-if="me && (me.username == personId || admin)"
                    class="btn btn-warning"
                    @click="editPerson(personId)">
                    Modifier
                </button>
                <button
                    v-if="admin"
                    class="btn btn-danger"
                    @click="deletePerson(personId)">
                    Supprimer
                </button>
            </div>
        </div>
        <div style="display: flex; justify-content: space-between">
        <h2>Liste des Activités</h2>
                <button
                    v-if="me && person && (me.username == personId || admin)"
                    class="btn btn-primary"
                    @click="addActivity(personId)">
                    ajouter
                </button>
        
</div>
        <ul v-if="activities && activities.content.length > 0" style="padding-left: 0">
            <div v-for="activity in activities.content" :key="activity.id" class="card mb-3 shadow-sm">
        
                <div class="card-body">
                    <h5 class="card-title">{{ activity.activityYear }} - {{ activity.title }}</h5>
                    <div>Nature : {{ getNature(activity.nature) }}</div>
                    <p class="card-text" v-if="activity.description">
                        {{ activity.description }}
                    </p>
                    <a v-if="activity.website"
                        :href="getValidUrl(activity.website)"
                        target="_blank"
                        class="btn btn-primary">
                        Visiter le site Web
                    </a>
                    <button
                        v-if="me && person && (me.username == personId || admin)"
                        class="btn btn-warning"
                        @click="editActivity(activity.id)">
                        Modifier
                    </button>
                    <button
                        v-if="me && person && (me.username == personId || admin)"
                        class="btn btn-danger"
                        @click="deleteActivity(activity.id)">
                        Supprimer
                    </button>
                </div>
            </div>
        </ul>
        <p v-else>Aucune activité trouvée.</p>
        
              <nav v-if="activities && activities.totalPages > 1">
        <button class="btn btn-primary"
          :disabled="activities.number === 0"
          @click="getActivities(personId, activities.number - 1, 10)">
          Précédent
        </button>
        <span>Page {{ activities.number + 1 }} sur {{ activities.totalPages }}</span>
        <button class="btn btn-primary"
          :disabled="activities.number === activities.totalPages - 1"
          @click="getActivities(personId, activities.number + 1, 10)">
          Suivant
        </button>
      </nav>
    `,
    data() {
        return {
            token: null,
            person: null,
            personId: null,
            activities: null,
            me: null,
            admin: false,
            activityNatures : shared.shared.activityNatures
        }
    },
    mounted() {
        this.token = shared.getToken();
        if (this.token)
            shared.getMe().then(r => {
                this.me = r.data;
                this.admin = this.me.roles.includes("ADMIN");
            });
        this.personId = sessionStorage.getItem("personId")
        this.getPerson(this.personId);
        this.getActivities(this.personId, 0, 10);
    },
    methods: {
        getPerson: function(id) {
            shared.getAxios().get('/persons/' + id, {
                headers: {
                    'Authorization': `Bearer ${this.token}`
                }
            })
                .then(r => {
                    console.log("getPerson")
                    this.person = r.data;
                    console.log(this.person);
                });
        },
        getActivities: function(username, page, size) {
            shared.getAxios().get("/activities?username="+username+"&page="+page+"&size="+size, {
                headers: {
                    'Authorization': `Bearer ${this.token}`
                }
            })
                .then(r => {
                    console.log("getPerson")
                    this.activities = r.data;
                    console.log(this.person);
                });
        },
        formatDate(dateString) {
            // Formater la date en dd/mm/yyyy
            const date = new Date(dateString);
            return `${String(date.getDate()).padStart(2, "0")}/${String(
                date.getMonth() + 1
            ).padStart(2, "0")}/${date.getFullYear()}`;
        },
        getValidUrl(website) {
            if (!website.startsWith('http://') && !website.startsWith('https://')) {
                return 'https://' + website;
            }
            return website;
        },
        getNature(nature) {
            const match = this.activityNatures.find(e => e.value === nature);
            return match ? match.key : null;
        },
        editPerson(personId) {
            console.log("edit", personId);
            sessionStorage.setItem("editingPersonId", personId);
            window.location.hash = "#/person-edit"
        },
        deletePerson(personId) {
            console.log("delete", personId)
            let confirm = window.confirm("vous etes sur?");
            if (confirm) {
                shared.getAxios().delete('/persons/' + personId, {
                    headers: {
                        'Authorization': `Bearer ${this.token}`
                    }
                }).then(r => {
                    window.location.hash = "#/"
                });
            }
        },
        addActivity(personId) {
            console.log("add activity");
            sessionStorage.setItem("editingPersonId", personId);
            sessionStorage.setItem("newActivity", "true");
            window.location.hash = "#/activity-edit"
        },
        editActivity(activityId) {
            console.log("edit activity", activityId);
            sessionStorage.setItem("editingActivityId", activityId);
            sessionStorage.setItem("newActivity", "false");
            window.location.hash = "#/activity-edit"
        },
        deleteActivity(activityId) {
            console.log("delete", activityId)
            let confirm = window.confirm("vous etes sur?");
            if (confirm) {
                shared.getAxios().delete('/activities/' + activityId, {
                    headers: {
                        'Authorization': `Bearer ${this.token}`
                    }
                }).then(r => {
                    this.getActivities(this.personId, 0, 10);
                });
            }
        },
    }

}