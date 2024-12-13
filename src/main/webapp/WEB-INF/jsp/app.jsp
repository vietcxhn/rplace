<%@ include file="/WEB-INF/jsp/header.jsp"%>

<div id="myApp">
  <div class="container">
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
      <a class="navbar-brand" href="#">r/place</a>
      <a v-if="(me == null)" class="navbar-brand" href="#/login">Se connecter</a>
      <a v-if="(me != null)" class="navbar-brand" href="#">User connecte: {{me.username}}</a>
      <a v-if="(me == null)" class="navbar-brand" href="#/signup">S'inscrire</a>
      <a v-if="(me != null)" class="navbar-brand" href="#" v-on:click.prevent="logout()">Se deconnecter</a>
    </nav>
    <component :is="currentView" />

  </div>
</div>

<script type="module">

  import rplace from './rplace.js';
  import login from './login.js';
  import signup from './signup.js';
  import NotFound from './notFound.js';

  import shared from "./shared.js";


  const routes = {
    '/': rplace,
    '/login': login,
    '/signup': signup,
  }

  const app = Vue.createApp( {
    data() {
      return {
        currentPath: window.location.hash,
        token: sessionStorage.getItem('jwtToken'),
        nbPerson: 0,
        me: null
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
        console.log(window.location.hash)
        this.currentPath = window.location.hash;
        this.token = sessionStorage.getItem('jwtToken');

        console.log("token");
        if (this.token) {
          console.log("token", this.token);
          shared.getMe().then(r => {
            if (r) {
              this.me = r.data;
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
          window.location.hash = "#/login";
        })
      }
    }
  });

  app.mount('#myApp');
</script>



<%@ include file="/WEB-INF/jsp/footer.jsp"%>