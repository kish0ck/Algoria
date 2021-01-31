import { get } from "@/utils/vuex";

export default {
  GET_USER_ID: get("user_id"),
  GET_USER_INFO: get("user_info"),
  GET_USER_RANK: get("user_info[rank]"),
  GET_USER_PROBLEMS: get("problems"),
  GET_USER_PROBLEMS_TYPE: get("problems_type"),
  GET_USER_PROBLEMS_DATA: get("problems_data"),
  GET_RECENT_DATA: get("recent_submit")
};
