<template>
  <v-snackbar v-model="show" :timeout="timeout">
    <!-- <v-snackbar v-model="snackbar" :timeout="timeout" :color="color"> -->
    '{{ msg }}'는 없는 사용자입니다.
  </v-snackbar>
</template>

<script>
export default {
  props: {
    msg: {
      type: String,
      default: null
    }
  },
  data: () => ({
    show: true,
    timeout: 5000
    // color : 'info'
  })
};
</script>

<style>
</style>