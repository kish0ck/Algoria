// import axios from '@/plugins/axios'
import axios from "axios";

const __SERVER__ = process.env.baseUrl;

export default {
  //action에서 비동기처리를 해야함

  //+service
  //1. (지원하는 사용자인지 확인)? true : false
  initUserChecker: async ({ commit, getters, dispatch }, user_id) => {
    console.log(">>>>>" + user_id)
    if (user_id == "_path") return;
    if (user_id == getters["user/GET_USER_ID"]) return;
    // console.warn(">>>>>"+getters["user/GET_USER_ID"])
    // console.warn(user_id + " <<<< START");
    const isUser = await axios
      .get(`${__SERVER__}/isUser/${user_id}`)
      .then(res => {
        // console.log("initUserChecker");
        return res.data;
      });
    console.warn("USER>>>" + isUser);
    if (isUser) {
      //확인
      await commit("service/TOGGLE_LOADING");
      await commit("user/SET_USER_ID", user_id);
      // console.log(getters["service/GET_OFFLINE"]);
      console.log("로딩 중" + getters["service/GET_LOADING"]);

      await dispatch("initUserInfo");
      await dispatch("initUserProblem");
      // await dispatch("initUserProblemType");
      await dispatch("initUserRecentProblem");
      await commit("service/TOGGLE_LOADING");
      console.log("로딩 완료" + getters["service/GET_LOADING"]);
      commit("service/SET_OFFLINE", false);
    } else {
      commit("service/SET_OFFLINE", true);
    }
  },

  //+user
  //1. 사용자 info 받아오기
  //2. 사용자 문제 정보 받아오기
  initUserProblem: async ({ commit, getters }) => {
    const user_id = getters["user/GET_USER_ID"];
    await axios.get(`${__SERVER__}/user/${user_id}/problems`).then(res => {
      // console.log("initUserProblem");
      commit("user/SET_USER_PROBLEMS", res.data.problems);
    });
  },

  initUserProblemType: async ({ commit, getters }) => {
    const user_id = getters["user/GET_USER_ID"];
    await axios.get(`${__SERVER__}/user/${user_id}/problemsType`).then(res => {
      // console.log("initUserProblemType");
      commit("user/SET_USER_PROBLEMS_TYPE", res.data);
    });
  },

  //3. 사용자 각종 컴포넌트 정보들 받아오기 ( 순위 정할수 있으면 해볼것 )
  initUserInfo: async ({ commit, getters }) => {
    const user_id = getters["user/GET_USER_ID"];
    await axios.get(`${__SERVER__}/user/${user_id}`).then(res => {
      // console.log("initUserInfo");
      commit("user/SET_USER_INFO", res.data.data);
    });
  },

  initUserRecentProblem: async ({ commit, getters }) => {
    const user_id = getters["user/GET_USER_ID"];
    await axios
      .get(`${__SERVER__}/recentsubmit/list/${user_id}/1`)
      .then(res => {
        // console.log("initUserRecentProblem");
        commit("user/SET_RECENT_DATA", res.data);
      });
  }
};
