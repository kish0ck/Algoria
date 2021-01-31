<template>
  <div class="small">
    <bar-chart :chart-data="datacollection" :options="chartoption"></bar-chart>
  </div>
</template>

<script>
var opt = {
  responsive: true,
  scales: {
    yAxes: [
      {
        display: true,
        ticks: {
          beginAtZero: true,
          min: 0
        }
      }
    ]
  },
  title: {
    display: true,
    text: name
  },
  legend: {
    display: false
  },
  tooltips: {
    mode: "index",
    intersect: false
  },
  hover: {
    mode: "nearest",
    intersect: true
  }
};

import BarChart from "@/components/Chart/Bar.js";
const axios = require("axios");

const _SERVER = "http://13.125.147.223:8080";
export default {
  name: "UserSolvedAlgoTypeGraph",
  components: {
    "bar-chart": BarChart
  },
  data() {
    return {
      datacollection: {},
      algoTypeLabels: [],
      algoTypeDataArr: [],
      chartoption: opt
    };
  },
  props: {
    user_id: {
      type: String,
      default: "-"
    }
  },
  watch: {
    // user_id() {
    //   this.makechart(this.user_id);
    // }
  },

  mounted() {
    this.makechart(this.user_id);
  },
  methods: {
    makechart: function(user_id) {
      this.algoTypeLabels = [];
      this.algoTypeDataArr = [];

      this.getUserAlgoType().then(response => {
        let algotypedata = response.data;
        console.log(algotypedata);
        algotypedata.forEach(element => {
          let count = element.count;
          let problemType = element.problemType;
          this.algoTypeLabels.push(problemType);
          this.algoTypeDataArr.push(count);
          this.fillData();
        });

        console.log(this.chartoption);
      });
    },

    //유저가 가장 많이푼 알고리즘 분류 데이터를 가져옴
    getUserAlgoType: function() {
      let url = `${_SERVER}/user/${this.user_id}/getUserAlgoType`;
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

    fillData() {
      this.datacollection = {
        //분류
        labels: this.algoTypeLabels,
        datasets: [
          {
            label: [],
            backgroundColor: [
              "rgba(255, 99, 132, 0.2)",
              "rgba(255, 159, 64, 0.2)",
              "rgba(255, 205, 86, 0.2)",
              "rgba(75, 192, 192, 0.2)",
              "rgba(54, 162, 235, 0.2)"
            ],
            borderColor: "rgba(179,181,198,1)",
            pointBackgroundColor: "rgba(179,181,198,1)",
            pointBorderColor: "#fff",
            pointHoverBackgroundColor: "#fff",
            pointHoverBorderColor: "rgba(179,181,198,1)",
            data: this.algoTypeDataArr
          }
        ]
      };
    },
    getRandomInt() {
      return Math.floor(Math.random() * (50 - 5 + 1)) + 5;
    }
  }
};
</script>

<style scoped>
.small {
  max-width: 300px;
  text-align: center;
  margin:0 auto; 
}
</style>