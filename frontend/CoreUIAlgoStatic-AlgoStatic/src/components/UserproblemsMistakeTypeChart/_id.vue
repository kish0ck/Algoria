<template>
  <div class="small">
    <bar-chart :chart-data="datacollection" :chart-option="chartoption"></bar-chart>
  </div>
</template>

<script>
import BarChart from "@/components/Chart/Bar.js";

export default {
  components: {
    BarChart
  },
  data() {
    return {
      datacollection: null,
      chartoption: {
        elements: { line: { tension: 0, borderWidth: 3 } }
      }
    };
  },
  mounted() {
    this.fillData();
  },
  methods: {
    fillData() {
      this.datacollection = {
        //분류
        labels: [
          "그래프 이론",
          "완전 탐색",
          "투포인트",
          "순열조합",
          "이진탐색",
          "이진탐색",
          "이진탐색",
          "이진탐색"
        ],
        datasets: [
          {
            label: "자신",
            backgroundColor: [
              "rgba(255, 99, 132, 0.2)",
              "rgba(255, 159, 64, 0.2)",
              "rgba(255, 205, 86, 0.2)",
              "rgba(75, 192, 192, 0.2)",
              "rgba(54, 162, 235, 0.2)",
              "rgba(153, 102, 255, 0.2)",
              "rgba(201, 203, 207, 0.2)",
              "rgba(62, 50, 57, 0.2)"
            ],
            borderColor: "rgba(179,181,198,1)",
            pointBackgroundColor: "rgba(179,181,198,1)",
            pointBorderColor: "#fff",
            pointHoverBackgroundColor: "#fff",
            pointHoverBorderColor: "rgba(179,181,198,1)",
            data: [
              this.getRandomInt(),
              this.getRandomInt(),
              this.getRandomInt(),
              this.getRandomInt(),
              this.getRandomInt(),
              this.getRandomInt(),
              this.getRandomInt(),
              this.getRandomInt(),
              this.getRandomInt()
            ]
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
  max-width: 600px;
  margin: 150px auto;
}
</style>