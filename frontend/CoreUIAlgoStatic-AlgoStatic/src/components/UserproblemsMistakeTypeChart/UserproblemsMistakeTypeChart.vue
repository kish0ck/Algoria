<template>
  <div class="small">
    <bar-chart :chart-data="datacollection" :options="chartoption"></bar-chart>
  </div>
</template>

<script>
import BarChart from "@/components/Chart/Bar.js";
const axios = require("axios");

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

const _SERVER = "http://13.125.147.223:8080";
export default {
  components: {
    "bar-chart": BarChart
  },
  props: {
    user_id: {
      type: String,
      default: "-"
    }
  },
  data() {
    return {
      datacollection: null,
      chartoption: opt
    };
  },
  mounted() {
    this.makechart(this.user_id);

    // this.fillData();
  },
  watch: {
    // user_id() {
    //   this.makechart(this.user_id);
    // }
  },
  methods: {
    makechart: function(user_id) {
      axios
        .get(`${_SERVER}//user/${user_id}/problemsMistakeType`)
        .then(response => {
          response = response.data;
          var datacollect = [];
          var dataVal = [];
          for (const key in response) {
            for (let index = 0; index < 5; index++) {
              // console.log(
              //   response[key][index].problemType +
              //     " " +
              //     response[key][index].count
              // );
              var val = response[key][index].count;
              datacollect.push([response[key][index].problemType]);
              dataVal.push(val);
            }
          }
          // this.datacollection.labels = datacollect;
          // this.datasets.data=dataVal;
          console.log(datacollect);
          console.log(dataVal);
          this.datacollection = {
            //분류
            labels: datacollect,
            datasets: [
              {
                label: "자신",
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
                data: dataVal
              }
            ]
          };
        });
    }
  }
};
</script>

<style scoped>
.small {
  max-width: 300px;
  margin:0 auto; 
  
}
</style>