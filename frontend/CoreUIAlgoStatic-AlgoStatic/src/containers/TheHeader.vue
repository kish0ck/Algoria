<template>
  <CHeader fixed with-subheader light>
    <CToggler in-header class="ml-3 d-lg-none" v-c-emit-root-event:toggle-sidebar-mobile />
    <CToggler in-header class="ml-3 d-md-down-none" v-c-emit-root-event:toggle-sidebar />
    <CHeaderNav class="d-md-down-none mr-auto">
      <CHeaderNavItem class="px-3">
        <CHeaderNavLink to="dashboard" exact>Dashboard</CHeaderNavLink>
      </CHeaderNavItem>
      <CHeaderNavItem class="px-3">
        <CHeaderNavLink to="feedback" exact>Feedback</CHeaderNavLink>
      </CHeaderNavItem>
      <CHeaderNavItem class="px-3">
        <CHeaderNavLink to="recommend" exact>Recommend</CHeaderNavLink>
      </CHeaderNavItem>
    </CHeaderNav>
    <!-- <CHeaderNav class="mr-4">
      <CHeaderNavItem class="mr-4">
        <CHeaderNavLink>
          <v-text-field
            :append-icon-cb="() => {}"
            placeholder=" search..."
            single-line
            color="white"
            hide-details
            v-model="searchQuery"
            @keyup.enter="submit()"
            style="width:200px"
          ></v-text-field>
        </CHeaderNavLink>
      </CHeaderNavItem>
    </CHeaderNav> -->
    <CSubheader class="px-3">
      <CBreadcrumbRouter class="border-0" />
    </CSubheader>
  </CHeader>
</template>

<script>
import TheHeaderDropdownAccnt from "./TheHeaderDropdownAccnt";

export default {
  name: "TheHeader",
  components: {
    TheHeaderDropdownAccnt
  },
  data() {
    return {
      // searchQuery: ""
    };
  },
  methods:{
    // submit() {
    //   //대문자 -> 소문자
    //   this.$router.push("/" + this.searchQuery.toLowerCase());
    // }
  }
};
</script>

<style>
</style>