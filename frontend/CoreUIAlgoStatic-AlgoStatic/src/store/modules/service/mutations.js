import { set, toggle } from '@/utils/vuex'

//스테이트 업데이트는 뮤테이션만...
export default {
  SET_OFFLINE : set('is_offline'),
  TOGGLE_LOADING : toggle('loading')
}
