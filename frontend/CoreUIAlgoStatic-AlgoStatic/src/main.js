import Vue from 'vue'
import App from './App'
import router from './router'
import VueTypedJs from 'vue-typed-js'
import CoreuiVue from '@coreui/vue'
import { iconsSet as icons } from './assets/icons/icons.js'
import vuetify from './plugins/vuetify';
import 'roboto-fontface/css/roboto/roboto-fontface.css'
import '@mdi/font/css/materialdesignicons.css'
import store from './store'

Vue.config.performance = true
Vue.use(CoreuiVue)
Vue.use(VueTypedJs)
new Vue({
  el: '#app',
  router,
  icons,
  template: '<App/>',
  vuetify,
  store,

  components: {
    App
  }
})
