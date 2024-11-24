import shared from "./shared.js";

export default {
    template: `

        <div v-if="me != null && admin" class="input-group mb-3">
          <input
                  type="number"
                  v-model="nbPerson"
                  class="form-control"
                  placeholder="nombre de personne"
          />
          <button class="btn btn-primary" @click="populate()">populate</button>
        </div>
        <h2 class="text-center mb-4">CV List</h2>

        <div class="input-group mb-3">
            <input
                type="text"
                v-model="searchBar"
                class="form-control"
                placeholder="Rechercher une personne par nom, prénom ou titre d'activite"
            />
            <button class="btn btn-primary" @click="getPersons(searchBar, 0, 10)">Rechercher</button>
        </div>
        <table style="width: 100%">
            <tr>
                <th style="width: 100%">Nom</th>
                <th></th>
            </tr>
            <tr v-for="person in personsPage.content">
                <td>{{person.firstName}} {{person.lastName}}</td>
                <td>
<!--                    <button v-on:click="deleteMovie(movie.id)">delete</button>-->
<!--                    <button v-on:click="editMovie(movie.id)">edit</button>-->
                    <button class="btn btn-primary" v-on:click="openCV(person.username)">open</button>
                </td>
            </tr>
        </table>
        
              <nav v-if="personsPage">
        <button class="btn btn-primary"
          :disabled="personsPage.number === 0"
          @click="getPersons(searchBar, personsPage.number - 1, 10)">
          Précédent
        </button>
        <span>Page {{ personsPage.number + 1 }} sur {{ personsPage.totalPages }}</span>
        <button class="btn btn-primary"
          :disabled="personsPage.number === personsPage.totalPages - 1"
          @click="getPersons(searchBar, personsPage.number + 1, 10)">
          Suivant
        </button>
      </nav>
    `,
    data() {
        return {
            searchBar: "",
            personsPage: [],
            number: 0,
            totalPages: 0,
            token: null,
            me: null,
            admin:false
        }
    },
    mounted() {
        console.log("mount cv")
        this.getPersons(this.searchBar, 0, 10);
        this.token = sessionStorage.getItem('jwtToken');
        if (this.token) {
            console.log("token", this.token);
            shared.getMe().then(r => {
                if (r) {
                    this.me = r.data;
                    this.admin = this.me.roles.includes("ADMIN");
                }
            });
        }
    },
    methods: {
        getPersons: function(query, page, size) {
            shared.getAxios().get(`/persons?query=${query}&page=${page}&size=${size}`, {
                headers: {
                    'Authorization': `Bearer ${shared.getToken()}`
                }
            })
                .then(r => {
                    console.log("read movies");
                    this.personsPage = r.data;
                    console.log(this.personsPage);
                });
        },
        openCV: function(id) {
            sessionStorage.setItem("personId", id);
            window.location.hash = "#/person-view";
        },
        populate() {
            shared.getAxios().get('/persons/populate?nbPerson='+this.nbPerson, {
                headers: {
                    'Authorization': "Bearer " + shared.getToken()
                }
            })
                .then(r => {
                    console.log("done");
                    this.getPersons(this.searchBar, 0, 10);
                });
        }
    }

}