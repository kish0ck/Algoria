import Vue from "vue";
import Router from "vue-router";
// @TODO : action으로 변경
import axios from "axios";
const _SERVER = "http://13.125.147.223:8080";
// Containers
const TheContainer = () => import("@/containers/TheContainer");

// Views
const Search = () => import("@/views/Search");
const Dashboard = () => import("@/views/Dashboard");
const Feedback = () => import("@/views/FeedbackPage");
const Recommend = () => import("@/views/RecommendAlgo");

// Views - Pages
const Page404 = () => import("@/views/pages/Page404");
const Page500 = () => import("@/views/pages/Page500");
const Login = () => import("@/views/pages/Login");
const Register = () => import("@/views/pages/Register");

Vue.use(Router);

export default new Router({
  mode: "hash", // https://router.vuejs.org/api/#mode
  // mode: "history",
  linkActiveClass: "active",
  scrollBehavior: () => ({ y: 0 }),
  routes: configRoutes()
});

function configRoutes() {
  return [
    {
      path: "/",
      name: "Search",
      component: Search,
      //q로 유저 정보 리턴
      props: (route) => ({ msg: route.query.msg })
    },
    {
      path: "/:user_id",
      redirect: "/:user_id/dashboard",
      name: "Home",
      beforeEnter: function(to, from, next) {
        // 인증 값 검증 로직 추가
        let user_id = to.params.user_id;
        axios.get(`${_SERVER}/isUser/${user_id}`).then(res => {
          let flag = res.data;
          if (flag) next();
          else {
            // console.log("not Allows User")
            next(`/?msg=${user_id}`)
          }
        });
      },
      component: TheContainer,
      children: [
        {
          path: "dashboard",
          name: "Dashboard",
          component: Dashboard,
          props: true
        },
        {
          path: "feedback",
          name: "Feedback",
          component: Feedback,
          props: true
        },
        {
          path: "Recommend",
          name: "Recommend",
          component: Recommend,
          props: true
        }
      ]
    },
    {
      path: "/pages",
      redirect: "/pages/404",
      name: "Pages",
      component: {
        render(c) {
          return c("router-view");
        }
      },
      children: [
        {
          path: "404",
          name: "Page404",
          component: Page404
        },
        {
          path: "500",
          name: "Page500",
          component: Page500
        },
        {
          path: "login",
          name: "Login",
          component: Login
        },
        {
          path: "register",
          name: "Register",
          component: Register
        }
      ]
    }
  ];
}
