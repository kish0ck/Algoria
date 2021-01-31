<template>
  <v-container fluid grid-list-md :style="{textAlign:'center'}">
    <v-layout row wrap>
      <v-flex xs12 sm12 md12 lg12 x12 align="center" >
      
        <GeneralComment :user_id="user_id"></GeneralComment>
      </v-flex>
      <v-flex xs12 sm12 md12 lg12 x12 align="center">
        <!-- UserproblemsMistakeTypeChart -->
        <UserSubmitLanguageGraph :user_id="user_id"></UserSubmitLanguageGraph>
      </v-flex>
      <v-flex xs12 sm12 md12 lg12 x12 align="center">
        <v-card class="mx-2 pa-2 elevation-3 justify-center" >
          <div class="flex display-1 font-weight-bold">가장 많이 푼 알고리즘 유형</div>
          <!-- UserproblemsMistakeTypeChart -->
          <UserSolvedAlgoTypeGraph :user_id="user_id"></UserSolvedAlgoTypeGraph>
        </v-card>
      </v-flex>
      <v-flex xs12 sm12 md12 lg12 x12 align="center">
        <v-card class="mx-2 pa-2 elevation-3 justify-center">
          <div class="flex display-1 font-weight-bold">가장 많이 실수하는 알고리즘 유형</div>
          <UserproblemsMistakeTypeChart :user_id="user_id"></UserproblemsMistakeTypeChart>
        </v-card>
      </v-flex>
      <v-flex xs12 sm12 md12 lg12 x12 align="center">
        <v-card class="mx-2 pa-2 elevation-3 justify-center">
          <div class="flex display-1 font-weight-bold">코딩테스트 고득점 Kit 알고리즘</div>
          <ProgrammersSort :user_id="user_id"></ProgrammersSort>
        </v-card>
      </v-flex>
    </v-layout>
  </v-container>
</template>


<script>
import UserSubmitLanguageGraph from "@/components/UserLanguageChart/UserSubmitLanguageGraph.vue";
import UserSolvedAlgoTypeGraph from "@/components/UserProblemTypeChart/UserSolvedAlgoTypeGraph.vue";
import UserproblemsMistakeTypeChart from "@/components/UserproblemsMistakeTypeChart/UserproblemsMistakeTypeChart.vue";
import GeneralComment from "@/components/GeneralComment.vue";
import ProgrammersSort from "@/components/ProgrammersSort.vue";

export default {
  props: {
    user_id: {
      type: String,
      default: "-",
      require: true
    }
  },
  data() {
    return {};
  },
  components: {
    ProgrammersSort : ProgrammersSort,
    GeneralComment: GeneralComment,
    UserSubmitLanguageGraph: UserSubmitLanguageGraph,
    UserSolvedAlgoTypeGraph: UserSolvedAlgoTypeGraph,
    UserproblemsMistakeTypeChart: UserproblemsMistakeTypeChart
  }
};
</script>

<style scoped>
.header_card {
  font-weight: 500;
  line-height: 1.2;
}
</style>>
