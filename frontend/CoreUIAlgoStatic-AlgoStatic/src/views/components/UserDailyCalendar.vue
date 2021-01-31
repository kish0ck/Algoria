
<template>
  <CRow>
    <CCol md="12">
      <CCard>
        <CCardBody>
          <CRow>
            <CCol sm="5">
              <h4 id="traffic" class="card-title mb-0">DailyChart</h4>
              <div class="small text-muted">{{startDate|getFormatDate}} ~ {{endDate|getFormatDate}}</div>
            </CCol>
          </CRow>
          <v-bottom-sheet v-model="sheet" inset scrollable>
            <v-sheet class="text-center">
              <v-btn class="mt-6" text color="error" @click="sheet = !sheet">close</v-btn>

              <v-card style="height: 500px;" class="my-3 scroll">
                <UserSubmitList :dailyData="clicked_day_data"></UserSubmitList>
              </v-card>
            </v-sheet>
          </v-bottom-sheet>

          <div style="text-align:center;" id="domainDynamicDimension-a"></div>
        </CCardBody>
       
      </CCard>
    </CCol>
  </CRow>
</template>


<script>
import CalHeatMap from "cal-heatmap";
import UserSubmitList from "@/views/components/UserSubmitList.vue";

const axios = require("axios");
const _SERVER = "http://13.125.147.223:8080";

export default {
  components: {
    UserSubmitList: UserSubmitList
    // "submit-graph": submitGraph,
    // "recode-list": recordList
  },
  props: {
    user_id: {
      type: String,
      default: "-",
      required: true
    }
  },
  name: "user-calendar",
  data() {
    return {
      clicked_day_data: [],
      sheet: false,
      dialog: false,
      cal: null,
      counter: 0, //date.setMonth(date.getMonth() + months);
      startDate: new Date(new Date().setMonth(new Date().getMonth() - 6)),
      endDate: new Date(),
      submit_history: null,
      dateData: {},

      submit_data: []
    };
  },
  filters: {
    getFormatDate(date) {
      var year = date.getFullYear(); //yyyy
      var month = 1 + date.getMonth(); //M
      month = month >= 10 ? month : "0" + month; //month 두자리로 저장
      var day = date.getDate(); //d
      day = day >= 10 ? day : "0" + day; //day 두자리로 저장
      // console.log(year);
      return year + "-" + month + "-" + day;
    }
  },

  watch: {
    user_id: {
      deep: true,
      handler() {
        this.print();
      }
    }
  },
  mounted() {
    this.print();
  },
  methods: {
    async setAsync() {
      this.print();
    },
    print() {
      var $self = this;
      let url =
        _SERVER +
        "/stastic/dailysubmit/v2/" +
        this.user_id +
        "/" +
        this.startDate.getTime() +
        "/" +
        this.endDate.getTime();
      axios
        .get(url)
        .then(response => {
          response = response.data;

          this.dateData = response["dateData"];

          this.submit_history = response.submit_history;

          var cal = new CalHeatMap();
          document.getElementById("domainDynamicDimension-a").innerHTML = "";
          cal.init({
            start: this.startDate, // January, 1st 2000
            itemSelector: "#domainDynamicDimension-a",
            domainDynamicDimension: false,
            // previousSelector: "#domainDynamicDimension-previous",
            // nextSelector: "#domainDynamicDimension-next",
            range: 7,
            domain: "month",
            subDomain: "day",
            cellSize: 20,
            cellPadding: 5,
            domainLabelFormat: "%y년 %m월",
            width: "100%",
            // height: "auto",
            data: this.dateData,
            highlight: ["now"],
            displayLegend: false,
            onClick: function(date, nb) {
              // console.log($self.submit_history);
              // console.log(date);
              $self.sheet = true;
              var submit_history = $self.submit_history;
              // console.log("클릭:", submit_history);

              let day_submit_history = [];
              try {
                day_submit_history =
                  submit_history[date.getTime() / 1000 + 32400][
                    "submit_history"
                  ];
              } catch (error) {
                day_submit_history = [];
              }

              var res;
              $self.getSubmissionInfoArr(day_submit_history).then(data => {
                res = data;
                console.log("제출기록", res);
                //console.log(typeof res);

                $self.clicked_day_data = res;
              });
            }
          });
          cal.paint();

          //  this.dateData =response.dateData;

          this.submit_history = response.submit_history;
        })
        .catch(function(error) {
          console.log(error);
        });
    },
    getSubmissionInfoArr: function(submission_history) {
      //submission_history  : 숫자 배열
      // console.log(submission_history);

      let url = `${_SERVER}/find/submssion/list/`;
      return axios
        .post(url, submission_history)
        .then(response => {
          return response.data;
        })
        .catch(function(error) {
          console.log(error);
        });
    }
  }
};
</script>

<style scoped>
@import "http://cdn.jsdelivr.net/cal-heatmap/3.3.10/cal-heatmap.css";
.scroll {
  overflow-y: auto;
}
</style>