<%@ include file="/WEB-INF/jsp/header.jsp"%>

<div id="myApp">
  <div class="container">

    <nav class="navbar navbar-expand-lg navbar-light bg-light">
      <a class="navbar-brand" href="#/">CV</a>
      <a v-if="(me == null)" class="navbar-brand" href="#/login">Se connecter</a>
      <a v-if="(me != null)" class="navbar-brand" href="#" v-on:click.prevent="logout()">Se deconnecter</a>
      <a v-if="(me != null)" class="navbar-brand" href="#/create-person">Creer nouvelle personne</a>
      <a v-if="(me != null)" class="navbar-brand" href="#" v-on:click.prevent="getMe()">{{me.username}}</a>
    </nav>

    <component :is="currentView" />

  </div>
</div>
<script type="module">

  import CV from './cv.js';
  import login from './login.js';
  import personView from './person-view.js';
  import me from './me.js';
  import personEdit from './edit-person.js';
  import newPerson from './new-person.js';
  import activityEdit from './edit-activity.js';
  import NotFound from './notFound.js';

  import shared from "./shared.js";


  const routes = {
    '/': CV,
    '/person-view': personView,
    '/me': me,
    '/person-edit': personEdit,
    '/activity-edit': activityEdit,
    '/create-person': newPerson,
    '/new-activity': activityEdit,
    '/login': login
  }

  const app = Vue.createApp( {
    data() {
      return {
        currentPath: window.location.hash,
        token: sessionStorage.getItem('jwtToken'),
        nbPerson: 0,
        me: null,
        admin:false
      }
    },
    computed: {
      currentView() {
        return routes[this.currentPath.slice(1) || '/'] || NotFound
      }
    },
    mounted() {
      window.addEventListener('hashchange', this.update);
      this.update();
    },
    methods : {
      update() {
        this.currentPath = window.location.hash;
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
      logout() {
        shared.getAxios().get(
                'http://localhost:8080/auth/logout', {
                  headers: {
                    'Authorization':"Bearer "+ this.token
                  }
                }
        ).then(()=> {
          sessionStorage.removeItem("jwtToken");
          this.token = null;
          this.me = null;
          window.location.hash = "#/";
        })
      },
      getMe() {
        sessionStorage.setItem("personId", this.me.username);
        window.location.hash = "#/me";
      },
    populate() {
      shared.getAxios().get('/persons/populate?nbPerson='+ this.nbPerson, {
        headers: {
          'Authorization': "Bearer " + sessionStorage.getItem("jwtToken")
        }
      })
              .then(r => {
                console.log("done");
              });
    }
    }
  });

  app.mount('#myApp');
</script>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>