<template>
  <div class="small">
    <bar-chart :chart-data="datacollection" :chart-option="chartoption"></bar-chart>
    <button @click="fillData()">Randomize</button>
  </div>
</template>

<script>
import BarChart from "@/components/Chart/Bar.js";
const axios = require("axios");
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
    axios.get('http://localhost:8080/user/kistone3/getUserAlgoType')
    .then(response=>{
      response = response.data;
      var datacollect = [];
      var dataVal = [];
      for (const key in response) {
        for (let index = 0; index < 5; index++) {
          console.log(response[key][index].problemType+" "+response[key][index].count)
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
        labels : datacollect,
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
            data : dataVal
          }
        ]
      }


    })
    
    // this.fillData();
  },
  
};
</script>

<style>
.small {
  max-width: 600px;
  margin: 150px auto;
}
</style>