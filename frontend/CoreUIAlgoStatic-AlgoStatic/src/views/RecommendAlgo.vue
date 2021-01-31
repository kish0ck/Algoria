<template>
  <div class="algorecommend">
    <CCard accent-color="info">
      <CCardHeader>
        <div class="flex display-1 font-weight-bold">

          최근 6시간동안 유저들이 많이 시도한 문제들

        </div>
      </CCardHeader>
      <CCardBody>
        <!-- {{user_id}} -->
        <blockquote class="blockquote">

          <p>최근 6시간동안 백준 유저들이 많이 시도한 문제들입니다.</p>

          <p></p>
        </blockquote>

        <v-container fluid grid-list-md>
          <v-layout row wrap>
            <v-flex
              xs12
              sm6
              md3
              v-for="(item, index) in mostTryingProblemlist"
              :key="index"
            >
              <v-card height="100%">
                <v-card-title class="font-weight-bold">
                  <div>
                    <v-img
                      width="20"
                      height="20"
                      :src="
                        'https://solved.ac/res/tier-small/' +
                          item.level +
                          '.svg'
                      "
                      aspect-ratio="1"
                      contain
                    ></v-img>
                  </div>
                  {{ item.problem_title }}
                </v-card-title>

                <v-card-text class="text-primary">
                  <div>맞은사람 : {{ item.n_success }}</div>
                  <div>정답비율: {{ item.n_success_rate }}%</div>
                </v-card-text>

                <v-card-actions>
                  <v-btn
                    text
                    :href="'https://www.acmicpc.net/problem/' + item.problem_id"
                    target="_blank"
                  >
                    <v-icon dark>mdi-pencil</v-icon>문제풀러가기
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-flex>
          </v-layout>
        </v-container>
      </CCardBody>
    </CCard>

    <CCard accent-color="info">
      <CCardHeader>
        <div class="flex display-1 font-weight-bold">알고리즘 문제 추천!</div>
      </CCardHeader>
      <CCardBody>
        <v-container fluid>
          <v-row align="center">
            <v-col cols="12">
              <v-autocomplete
                v-model="value"
                v-on:input="insert"
                :items="items"
                item-text="taginfo"
                label="알고리즘을 입력세요"
                multiple
                return-object
                clearable
                open-on-clear
                hide-details
                solo
                hide-selected
                no-data-text="해당하는 알고리즘 분류가 없습니다"
                @change="searchInput = ''"
                :search-input.sync="searchInput"
              >
                <template v-slot:selection="data">
                  <v-chip
                    v-bind="data.attrs"
                    :input-value="data.selected"
                    close
                    @click="data.select"
                    @click:close="remove(data.index)"
                  >
                    <strong>{{ data.item.tagname }}</strong>
                  </v-chip>
                </template>
              </v-autocomplete>
            </v-col>
          </v-row>
        </v-container>

        <v-expand-transition>
          <v-list v-if="value.length > 0" class="white lighten-3">
            <v-list-item v-for="(field, i) in value" :key="i">
              <v-list-item-content>
                <v-list-item-title>
                  <div class="flex display-1 font-weight-bold">
                    {{ field.tagname }}
                  </div>
                </v-list-item-title>
                <v-list-item-subtitle
                  v-text="field.taginfo"
                ></v-list-item-subtitle>
                <v-container fluid grid-list-md>
                  <v-layout row wrap v-if="field.problem_list != null">
                    <v-flex
                      xs12
                      sm6
                      md3
                      v-for="(item, index) in field.problem_list.problems"
                      :key="index"
                    >
                      <v-card height="100%">
                        <v-card-title class="font-weight-bold">
                          <div>
                            <v-img
                              width="20"
                              height="20"
                              :src="
                                'https://solved.ac/res/tier-small/' +
                                  item.level +
                                  '.svg'
                              "
                              aspect-ratio="1"
                              contain
                            ></v-img>
                          </div>
                          {{ item.problem_title }}
                        </v-card-title>

                        <v-card-text class="text-primary">
                          <div>맞은사람 : {{ item.n_success }}</div>
                          <div>정답비율: {{ item.n_success_rate }}%</div>
                        </v-card-text>

                        <v-card-actions>
                          <v-btn
                            text
                            :href="
                              'https://www.acmicpc.net/problem/' +
                                item.problem_id
                            "
                            target="_blank"
                          >
                            <v-icon dark>mdi-pencil</v-icon>문제풀러가기
                          </v-btn>
                        </v-card-actions>
                      </v-card>
                    </v-flex>
                  </v-layout>
                </v-container>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-expand-transition>

        <!-- {{user_id}} -->
        <blockquote class="blockquote">
          <p>
            검색하고자 하는 알고리즘을 풀었던 실력을 기반으로 알고리즘 문제를
            추천해줍니다.
          </p>
          <p>알고리즘 실력 측정 기준 :</p>
          <p>
            이미 풀었던 문제들의 평균 난이도(랭크)과 비슷하거나 그 이상의 문제를
            추천
          </p>
          <p>
            예시) 푼 문제들 중 DP에 해당하는 문제의 평균 난이도가 골드2일 경우
            골드2의 80%수준인 골드1 이상의 문제를 추천
          </p>
          <p>
            풀었던 문제는 제외되고 해당 알고리즘 유형의 문제를 한번도 풀지
            않았다면 푼 사람이 많은 순으로 문제를 추천합니다
          </p>
        </blockquote>
      </CCardBody>
    </CCard>
  </div>
</template>
<script>

const _SERVER = "http://13.125.147.223:8080";
//const _SERVER = "http://localhost:8080";


const axios = require("axios");
export default {
  name: "RecommendAlgo",
  props: {
    user_id: {
      type: String,
      default: "-",
      required: true
    }
  },
  data: () => ({
    items: [],
    searchInput: "",
    value: [],
    mostTryingProblemlist: []
  }),
  mounted() {
    this.getEntireAlgorithmTagInfo().then(response => {
      // console.log("mounted :: "+response);
      this.items = response;
    });

    this.getRecentMostTryingProblems().then(response => {
      // console.log("mounted :: "+response);
      this.mostTryingProblemlist = response;
    });


  },

  watch: {
    value() {
      /*
      this.values.forEach(element => {
        this.getRecommendProblemsByAlgoId(element.algorithm_id).then(
          response => {
            element.problem_list = response;
          }
        );
      });
    */
    }
  },
  methods: {
    remove(index) {
      // console.log(index);
      if (index >= 0) this.value.splice(index, 1);
    },

    insert: function() {
      let idx = this.value.length;
      if (idx == 0) return;
      this.value[idx - 1].problem_list = null;
      this.getRecommendProblemsByAlgoId(
        this.user_id,
        this.value[idx - 1].algorithm_id
      ).then(response => {
        console.log("teststs");
        this.value[idx - 1].problem_list = response;
        this.$forceUpdate();
      });
    },

    getEntireAlgorithmTagInfo: function() {
      let url = `${_SERVER}/recommend/algorithms/tags`;

      return axios
        .get(url, {
          onDownloadProgress: progressEvent => {
            let percentCompleted = Math.round(
              (progressEvent.loaded * 100) / progressEvent.total
            );
            console.log(progressEvent.lengthComputable);
            console.log(percentCompleted);
          }
        })
        .then(response => {
          // console.log(response.data);
          return response.data;
        })
        .catch(function(error) {
          console.log(error);
        });
    },

    getRecommendProblemsByAlgoId: function(user_id, algorithm_id) {
      let url =
        `${_SERVER}/feedback/recommend/user/algorithm/` +
        user_id +
        "/" +
        algorithm_id;

      return axios
        .get(url, {
          onDownloadProgress: progressEvent => {
            let percentCompleted = Math.round(
              (progressEvent.loaded * 100) / progressEvent.total
            );
            console.log(progressEvent.lengthComputable);
            console.log(percentCompleted);
          }
        })
        .then(response => {
          console.log(" :: " + response.data);
          return response.data;
        })
        .catch(function(error) {
          console.log(error);
        });
    },

    getRecentMostTryingProblems: function() {
      let now_timestamp =  new Date()*1;
      let from_timestamp = new Date(now_timestamp - 6 * 60 * 60 * 1000)*1;

      let url =
        `${_SERVER}/recommend/problem/trymost/` +
        from_timestamp +
        "/" +
        now_timestamp;

      console.log( now_timestamp, from_timestamp);

       return axios
        .get(url, {
          onDownloadProgress: progressEvent => {
            let percentCompleted = Math.round(
              (progressEvent.loaded * 100) / progressEvent.total
            );
            console.log(progressEvent.lengthComputable);
            console.log(percentCompleted);
          }
        })
        .then(response => {
          // console.log(response.data);
          return response.data;
        })
        .catch(function(error) {
          console.log(error);
        });

    }
  },
  filters: {
    percentage: function(value) {
      if (!value) return 0;
      value += "";
      console.log(value);
      value = parseFloat(value);
      value *= 100;
      return value.toFixed(2);
    }
  }
};
</script>
