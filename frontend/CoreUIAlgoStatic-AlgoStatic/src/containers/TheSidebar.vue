<template>
  <CSidebar fixed :minimize="minimize" :show.sync="show">
    <CSidebarBrand
      :imgFull="{ width: 180, height: 60, alt: 'Logo', src: 'img/Menu_800_400.png'}"
      :imgMinimized="{ width: 180, height: 60, alt: 'Logo', src: 'img/Menu_800_400.png'}"
      :wrappedInLink="{ href: '/'}"
    ></CSidebarBrand>
    <br />
    <CRenderFunction flat :content-to-render="nav" />
    <!-- <CSidebarMinimizer
      class="d-md-down-none"
      @click.native="minimize = !minimize"
    />-->
  </CSidebar>
</template>

<script>
import nav from "./_nav";

export default {
  name: "TheSidebar",
  data() {
    return {
      minimize: false,
      nav,
      show: "responsive"
    };
  },
  mounted() {
    this.$root.$on("toggle-sidebar", () => {
      const sidebarOpened = this.show === true || this.show === "responsive";
      this.show = sidebarOpened ? false : "responsive";
    });
    this.$root.$on("toggle-sidebar-mobile", () => {
      const sidebarClosed = this.show === "responsive" || this.show === false;
      this.show = sidebarClosed ? true : "responsive";
    });
  }
};
</script>
