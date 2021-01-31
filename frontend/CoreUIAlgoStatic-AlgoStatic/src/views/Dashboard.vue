<template>
  <div>
    <UserRank :user_id="user_id"/>
    <UserInfo :user_id="user_id"/>
    <UserDailyCalendar :user_id="user_id"/>
    <UserSubmitList :user_id="user_id"/>
  </div>
</template>

<script>
import UserRank from "./components/UserRank";
import UserInfo from "./components/UserInfo";
import UserDailyCalendar from "./components/UserDailyCalendar";
import UserSubmitList from "./components/UserSubmitList";

export default {
  name: "Dashboard",
  props: {
    user_id: {
      type: String,
      default: '-',
      required : true
    }
  },
  components: {
    UserRank,
    UserInfo,
    UserDailyCalendar,
    UserSubmitList

  },
};
</script>
