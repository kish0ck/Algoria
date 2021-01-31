<template>
  <CRow>
    <CCol>
      <CRow>
        <CCol sm="4" md="4">
          <CWidgetBrand
            color="twitter"
            :right-header="user_id"
            right-footer="BOJ"
            :left-header="user_info.level|levelToTier"
            left-footer="Rank"
          >
            <h1 class="display-1" style="height:100px; line-height: 100px;">BaekJoon</h1>
          </CWidgetBrand>
        </CCol>
        <CCol sm="4" md="4">
          <CWidgetBrand
            color="linkedin"
            right-header="-"
            right-footer="CodeForce"
            left-header="-"
            left-footer="Rank"
          >
            <h1 class="display-1" style="height:100px; line-height: 100px;">CodeForce</h1>
          </CWidgetBrand>
        </CCol>
        <CCol sm="4" md="4">
          <CWidgetBrand
            color="facebook"
            right-header="-"
            right-footer="TopCoder"
            left-header="-"
            left-footer="Rank"
          >
            <h1 class="display-1" style="height:100px; line-height: 100px;">TopCoder</h1>
          </CWidgetBrand>
        </CCol>
      </CRow>
    </CCol>
  </CRow>
</template>

<script>
import levelToTier from '@/filters/levelToTier.js'
import axios from "axios";
export default {
  props: {
    user_id: {
      type: String,
      default: "-"
    }
  },
  data() {
    return {
      user_info: {}
    };
  },

  mounted() {
    return axios
      .get(`http://13.125.147.223:8080/user/${this.user_id}`)
      .then(res => {
        this.user_info = res.data.data;
        return res.data.dta;
      });
  },
  filters:{
    levelToTier
  }
  // asyncData() {
  // return axios
  //   .get(`http://13.125.147.223:8080/user/${user_id}`)
  //   .then(res => {
  //     return { user_info: res.data.data };
  //   });
  // }
};
</script>

