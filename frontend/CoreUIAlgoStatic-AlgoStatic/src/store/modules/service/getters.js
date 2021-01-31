import { get } from '@/utils/vuex'

export default {
  GET_OFFLINE: get('is_offline'),
  GET_LOADING: get('loading'),
}
