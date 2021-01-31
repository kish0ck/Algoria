<template>
  <CCard>
    <CCardBody>
      <!-- {{user_id}} -->
      <!-- {{tableItems}} -->
      <CDataTable
        class="mb-0 table-outline"
        hover
        :items="tableItems"
        :fields="tableFields"
        head-color="light"
        no-sorting
      >
        <td slot="result" class="text-center" slot-scope="{item}">
          <CBadge style="font-size:100%" :color="getBadge(item.result)">{{item.result|resultToText}}</CBadge>
        </td>

        <td slot="rankimg" class="text-center" slot-scope="{item}">
          <div class="c-avatar">
            <!--hover-->
            <v-tooltip bottom>
              <template v-slot:activator="{ on }">
                <img
                  :src="item.level|levelToTierImage"
                  class="c-avatar-img"
                  :title="item.level"
                  v-on="on"
                />
              </template>
              <span>{{item.level|levelToTier}}</span>
            </v-tooltip>
          </div>
        </td>
        <td slot="problem_title" slot-scope="{item}" >
         <div class="text-center"> 
           
          <a :href="'https://www.acmicpc.net/problem/' + item.problem_id " style="color: black"> {{item.problem_title}} 
          <CIcon :content="cilexternalink" size="sm"/>
         
           </a>

           </div>
               
          <!--분류모음
           <div class="small text-muted" v-for(data in item.classification)>{{data.full_name_ko}}</div>
          -->
        </td>
        <td slot="language" slot-scope="{item}" class="text-center">
          <div>{{item.language}}</div>
          <!-- <CIcon :name="item.language.flag" height="25" /> -->
        </td>
        <td slot="time" slot-scope="{item}">
          <div class="clearfix">
            <div class="float-left">
              <strong>{{item.time|intNotNull}} ms</strong>
            </div>
            <div v-if="item.isresultAccept" class="float-right">
              <small class="text-muted">{{item.timeRank|toPercent}}</small>
            </div>
          </div>
          <CProgress
            class="progress-xs"
            v-if="item.isresultAccept"
            v-model="item.timeRank"
            :color="color(item.timeRank)"
          />
        </td>
        <td slot="memory" slot-scope="{item}">
          <div class="clearfix">
            <div class="float-left">
              <strong>{{item.memory|intNotNull}} KB</strong>
            </div>
            <div v-if="item.isresultAccept" class="float-right">
              <small class="text-muted">{{item.memoryRank|toPercent}}</small>
            </div>
          </div>
          <CProgress
            class="progress-xs"
            v-if="item.isresultAccept"
            v-model="item.memoryRank"
            :color="color(item.memoryRank)"
          />
        </td>
        <td slot="activity" slot-scope="{item}">
          <!-- <strong>{{item.activity}}</strong> -->
          <strong>{{item.from_now}}</strong>
        </td>
      </CDataTable>
    </CCardBody>
    <CCardFooter>
      <!-- pagenation -->
      <v-btn
        :style="{
          width: '97%',
          display: 'inline-block'
        }"
        :loading="loading"
        :disabled="loading"
        color="white"
        class="ma-2 grey--text"
        @click="loader"
      >
        {{load_btn_label}}
        <v-icon right dark>mdi-download</v-icon>
      </v-btn>
    </CCardFooter>
  </CCard>
</template>

<script>
import { cilExternalLink } from '@coreui/icons'
import moment from "moment";
import "moment/min/locales";
moment.locale("ko");
import axios from "axios";

import levelToTier from "../../filters/levelToTier.js";
import levelToTierImage from "../../filters/levelToTierImage.js";
import resultToText from "../../filters/resultToText.js";

const _SERVER = "http://13.125.147.223:8080";
export default {
  props: {
    user_id: {
      type: String,
      default: "-"
    },
    dailyData: {
      type: Array
    }
  },
  watch: {
    dailyData: {
      handler() {
        // console.log("click >> " + this.dailyData);
        let tempItems = [...this.dailyData];
        // this.tableItems = this.dailyData;
        var now = Date.now(0);

        // this.tableItems.forEach(element => {
        tempItems.forEach(element => {
          element.rankimg =
            "https://solved.ac/res/tier-small/" + element.level + ".svg";
          element.timeRank = 0;
          element.memoryRank = 0;
          if (element.result == "result-ac") {
            element.isresultAccept = true;
            axios
              .get(
                `${_SERVER}/stastic/user/submit/rank/${element.submission_id}/time/`
              )
              .then(res => {
                element.timeRank = (1 - res.data.rank) * 100;
              })
              .catch(err => console.log(err));
            axios
              .get(
                `${_SERVER}/stastic/user/submit/rank/${element.submission_id}/memory/`
              )
              .then(res => {
                element.memoryRank = (1 - res.data.rank) * 100;
              })
              .catch(err => console.log(err));
          }
          if (element.result == "result-ac") {
            element.isresultAccept = true;
          }
          //let d= moment(element.date+'000',"x").fromNow();
          // console.log("ts:", element.date);
          let d = moment(new Date(parseInt(element.date) * 1000)).fromNow();
          element.from_now = d;
          element.date = moment(new Date(parseInt(element.date) * 1000)).format(
            "YYYY-MM-DD HH:mm:ss"
          );
        });
        // this.$set(this.tableItems,tempItems)
        this.tableItems = tempItems;
        console.log(this)
        this.$forceUpdate();

      }
    }
  },
  data() {
    return {
      //구려;;;
    
      cilexternalink: cilExternalLink,
      test: 50.1,
      testItems: [
        {
          rankimg: "",
          problem_title: "A+B",
          language: "JAVA",
          time: "1",
          memory: "100",
          result: "성공",
          from_now: "10 sec ago",
          _classes: "table-success"
        },
        {
          rankimg: "",
          problem_title: "A+B",
          language: "JAVA",
          time: "1",
          memory: "100",
          result: "실패",
          from_now: "10 sec ago",
          _classes: "table-danger"
        },
        {
          rankimg: "",
          problem_title: "A+B",
          language: "JAVA",
          time: "1",
          memory: "100",
          result: "그외",
          from_now: "10 sec ago",
          _classes: "table-warning"
        }
      ],
      tableItems: [
        {
          rankimg: "",
          problem_title: "A+B",
          language: "JAVA",
          time: "1",
          memory: "100",
          result: "reactAC",
          from_now: "10 sec ago"
        }
      ],
      tableFields: [
        { key: "result", label: "결과", _classes: "text-center" },
        { key: "problem_title", label: "제목", _classes: "text-center" },
        { key: "rankimg", label: "난이도", _classes: "text-center" },
        { key: "language", _classes: "text-center" },
        { key: "time" },
        { key: "memory" },
        { key: "from_now" }
      ],
      loading: false,
      isfocusable: false,
      ishover: true,
      ispopout: true,
      pageno: 1,
      pagination: null,
      load_btn_label: "더 보기"
    };
  },

  mounted() {
    // if (this.dailyData != null) {
    //   this.dailyData = [];
    // }
    this.tableItems = [];
    this.search();
    this.getPaginationInfo().then(response => {
      this.pagination = response;
      this.$forceUpdate();
    });
  },

  methods: {
    calculator(submission_id, attr) {
      let url = `${_SERVER}/stastic/user/submit/rank/${submission_id}/${attr}/`;
      return axios
        .get(url)
        .then(res => {
          return res.data.rank;
        })
        .catch(err => console.log(err));
    },
    color(value) {
      let $color;
      if (value <= 25) {
        $color = "danger";
      } else if (value > 25 && value <= 50) {
        $color = "warning";
      } else if (value > 50 && value <= 75) {
        $color = "success";
      } else if (value > 75 && value <= 100) {
        $color = "info";
      }
      return $color;
    },
    getPaginationInfo() {
      let url = _SERVER + "/recentsubmit/list/pagination/" + this.user_id;

      return axios
        .get(url, {
          onDownloadProgress: progressEvent => {
            let percentCompleted = Math.round(
              (progressEvent.loaded * 100) / progressEvent.total
            );
          }
        })
        .then(response => {
          return response.data;
        })
        .catch(function(error) {
          console.log(error);
        });
    },

    loader: function() {
      this.loading = true;
      if (this.pagination.last_page_num == this.pageno) {
        this.loading = false;
        this.load_btn_label = "마지막 페이지입니다";
        return;
      }

      this.pageno++;
      this.search();
      setTimeout(() => (this.loading = false), 1500);
    },

    search: function(mode) {
      if (mode == "search") this.tableItems = [];

      var now = Date.now(0);

      this.getRecentSubmitRecord().then(response => {
        let newRecordArr = response;
        //this.tableItems=   this.tableItems .concat (response);

        newRecordArr.forEach(element => {
          element.rankimg =
            "https://solved.ac/res/tier-small/" + element.level + ".svg";
          element.timeRank = 0;
          element.memoryRank = 0;
          if (element.result == "result-ac") {
            element.isresultAccept = true;
            axios
              .get(
                `${_SERVER}/stastic/user/submit/rank/${element.submission_id}/time/`
              )
              .then(res => {
                element.timeRank = (1 - res.data.rank) * 100;
              })
              .catch(err => console.log(err));
            axios
              .get(
                `${_SERVER}/stastic/user/submit/rank/${element.submission_id}/memory/`
              )
              .then(res => {
                element.memoryRank = (1 - res.data.rank) * 100;
              })
              .catch(err => console.log(err));
          }

          //let d= moment(element.date+'000',"x").fromNow();
          let d = moment(new Date(parseInt(element.date) * 1000)).fromNow();
          element.from_now = d;
          element.date = moment(new Date(parseInt(element.date) * 1000)).format(
            "YYYY-MM-DD HH:mm:ss"
          );
        });

        this.tableItems = this.tableItems.concat(newRecordArr);

        this.getPaginationInfo().then(response => {
          this.pagination = response;
        });
      });
    },

    getRecentSubmitRecord: function() {
      let url =
        _SERVER + "/recentsubmit/list/" + this.user_id + "/" + this.pageno;
      return axios
        .get(url, {
          onDownloadProgress: progressEvent => {
            let percentCompleted = Math.round(
              (progressEvent.loaded * 100) / progressEvent.total
            );
            //console.log(progressEvent.lengthComputable);
            //console.log(percentCompleted);

            //console.log(progressEvent );
          }
        })
        .then(response => {
          return response.data;
        })
        .catch(function(error) {
          console.log(error);
        });
    },
    success(result) {
      return result === "result-ac";
    },
    //성공 실패 badge 추가!!!!
    getBadge(result) {
      //맞았습니다 : Success
      //result-ac
      //틀렸습니다 : Danger
      //result-wa   / 틀렸습니다.
      //result-mle  / 메모리 초과
      //result-tle  / 시간초과

      //런타임에러 :Warning
      //result-rte
      //등등
      // console.log(result);
      switch (result) {
        case "result-ac":
          return "success";
        case "result-wa":
        case "result-mle":
        case "result-tle":
          return "danger";
        default:
          return "warning";
      }
    }
  },
  filters: {
    resultToText,
    levelToTier,
    levelToTierImage,
    toPercent: function(data) {
      let result =
        data >= 50
          ? "상위 " + (100 - data).toFixed(2) + "%"
          : "하위 " + data.toFixed(2) + "%";
      // console.log(result);
      return result;
    },
    //테이블 컬러 선택
    //구립니다;;;
    addClassColor: function(result) {
      //맞았습니다 : Success
      //result-ac
      //틀렸습니다 : Danger
      //result-wa   / 틀렸습니다.
      //result-mle  / 메모리 초과
      //result-tle  / 시간초과

      //런타임에러 :Warning
      //result-rte
      //등등
      // console.log(result);
      switch (result) {
        case "result-ac":
          return "table-success";
        case "result-wa":
        case "result-mle":
        case "result-tle":
          return "table-danger";
        default:
          return "table-warning";
      }
    },

    // 시간 메모리 없을때 - Null
    intNotNull: function(val) {
      //콤마추가
      return val ? val.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",") : "0";
    }
  }
};
</script>

<style >
@import url("https://unpkg.com/@coreui/icons/css/all.min.css");
</style>