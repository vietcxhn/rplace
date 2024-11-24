let a = axios.create({
    baseURL: 'http://localhost:8080/api/',
    headers: { 'Content-Type': 'application/json' },
});
a.interceptors.response.use(
    (response) => {
        // If the request is successful, just return the response
        return response;
    },
    (error) => {
        console.log(error)
        if (error.status === 403 || error.status === 401) {
            sessionStorage.removeItem("jwtToken");
            window.location.hash = "#/";
        }

    }
);

const shared = Vue.reactive({
    axios: a,
    activityNatures: [
        {
            key: "Experience professionnelle",
            value: "PROFESSIONAL_EXPERIENCE"
        },{
            key: "Education",
            value: "EDUCATION"
        },{
            key: "Projet",
            value: "PROJECT"
        },{
            key: "Autre",
            value: "OTHER"
        }
    ],
    me: null,
});

export default {
    getMe() {
        return shared.axios.get('http://localhost:8080/auth/me', {
            headers: {
                'Authorization': "Bearer " + sessionStorage.getItem("jwtToken")
            }
        })
    },
    getAxios() {
        return shared.axios;
    },
    getToken() {
        return sessionStorage.getItem("jwtToken");
    },
    setToken(token) {
        return sessionStorage.setItem("jwtToken", token);
    },
    shared,
};