import { set, toggle } from '@/utils/vuex'

export default {
  SET_USER_ID: set('user_id'),
  SET_USER_INFO: set('user_info'),
  SET_USER_RANK: set('user_info.rank'),
  SET_USER_PROBLEMS: set('problems'),
  SET_USER_PROBLEMS_TYPE: set('problems_type'),
  SET_USER_PROBLEMS_DATA: set('problems_data'),
  SET_RECENT_DATA: set('recent_submit'),
}
