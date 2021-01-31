<template>
<div class="ccard">
  <h5 class="card-header">국내에서 코딩테스트를 가장 많이 운영해온 프로그래머스 팀이 코딩테스트 결과를 분석해서 만든 고득점 Kit. 코딩테스트에 자주 나오는 유형, 사람들이 많이 틀리는 유형을 간추렸습니다.</h5>
  
    <v-container fluid grid-list-md>
          <v-layout row wrap>
            <v-flex
              xs12
              sm6
              md3
              v-for="(item, index) in algo_data_arr"
              :key="index"
            >
              <v-card height="100%">
                <v-card-title class="font-weight-bold justify-center">
                 
                  {{ item.name }}
                </v-card-title>

                <v-card-text class="text-primary">
                  <div v-if="item.count>=0">내가 푼 문제 수 : {{ item.count }}</div>

                </v-card-text>
                
               
        
              </v-card>
            </v-flex>
          </v-layout>
        </v-container>
  
  
  <div class="card-body">
    <pie-chart v-if="chartload" :data="chartData" :options="chartOptions"  ></pie-chart>
  </div>
</div>
</template>


<script>
const _SERVER = "http://13.125.147.223:8080";
//const _SERVER = "http://localhost:8080";
const axios = require("axios");
import PieChart from "@/components/Chart/Pie.js";

export default {
  name: "ProgrammersSort",
  components:{
    'pie-chart':PieChart
  },
  data() {
    return {

      chartload:false,
      chartOptions: {
        hoverBorderWidth: 5,
        hoverBackgroundColor:'red',
            responsive: true,
                    maintainAspectRatio: false
      },
      chartData: {
        hoverBorderWidth: 5,
        labels: [],
        datasets: [
          {
            label: "알고리즘 분류",
            backgroundColor: [
              "rgba(255, 0, 0, 0.4)",
              "rgba(255, 159, 64, 0.6)",
              "rgba(255, 205, 86, 0.6)",
              "rgba(75, 192, 192, 0.6)",
              "rgba(54, 162, 235, 0.6)",

              "rgba(215, 100, 240, 0.6)",
              "rgba(63, 246, 39, 0.5)",
              "rgba(255, 118, 38, 0.6)",

               "rgba(39, 63, 246, 0.5)",
              "rgba(231, 84, 172, 0.5)",
              "rgba(20, 132, 250, 0.5)",
      
            ],
           
            pointBackgroundColor: "rgba(179,181,198,1)",
            pointBorderColor: "#fff",
            pointHoverBackgroundColor: "#fff",
            pointHoverBorderColor: "rgba(179,181,198,1)",
            data: []
          }
        ]
      },
    
      id: null,
      Hashing: { name : "해싱" , count : 0},
      Queue : { name : "큐" , count : 0},
      Bruteforcing : { name : "완전 탐색" , count : 0},
      DP : { name : "동적 계획법" , count : 0},
      BFS : { name : "BFS:너비 우선 탐색" , count : 0},
      Graph : { name : "그래프" , count : 0},
      Stack : { name : "스택" , count : 0},
      Sorting : { name : "정렬" , count : 0},
      Greedy : { name : "그리디" , count : 0},
      DFS : { name : "DFS:깊이 우선 탐색" , count : 0},
      Binary_search:{ name : "이분탐색" , count : 0},
      reserved:{ name : "추가예정입니다" , count : -1},
      algo_data_arr:[]

    };
  },
  props: {
    user_id: {
      type: String,
      default: "-"
    }
  },
  mounted() {
    axios
      .get(`${_SERVER}/user/${this.user_id}/ProgrammersSort`)
      .then(response => {
        this.id = this.user_id;
        // console.log(response);
        var data = [];
        data = response.data;
        // console.log(data)
        var res = [];
      
        for (const key in data) {
            if (data.hasOwnProperty(key)) {
                const element = data[key];
               
                for (let index = 0; index < element.length; index++) {
                
                     if(element[index]._id=="Queue") this.Queue.count=element[index].count;
                    if(element[index]._id=="Greedy")this.Greedy.count=element[index].count;
                    if(element[index]._id=="Hashing")this.Hashing.count=element[index].count;
                    if(element[index]._id=="Binary search")this.Binary_search.count=element[index].count;
                    if(element[index]._id=="Bruteforcing")this.Bruteforcing.count=element[index].count;
                    if(element[index]._id=="DP")this.DP.count=element[index].count;
                    if(element[index]._id=="BFS")this.BFS.count=element[index].count;
                    if(element[index]._id=="Graph theory")this.Graph.count+=element[index].count;
                    if(element[index]._id=="Graph traversal")this.Graph.count+=element[index].count;
                    if(element[index]._id=="Stack")this.Stack.count=element[index].count;
                    if(element[index]._id=="Sorting")this.Sorting.count=element[index].count;
                    if(element[index]._id=="DFS")this.DFS.count=element[index].count;
                     
                }

               this.algo_data_arr.push(this.Hashing);
               this.algo_data_arr.push(this.Queue);
               this.algo_data_arr.push(this.Bruteforcing);
               this.algo_data_arr.push(this.DP);
               this.algo_data_arr.push(this.BFS);
               this.algo_data_arr.push(this.Graph);
               this.algo_data_arr.push(this.Stack);
               this.algo_data_arr.push(this.Sorting);
               this.algo_data_arr.push(this.Greedy);
               this.algo_data_arr.push(this.DFS);
               this.algo_data_arr.push(this.Binary_search);
               this.algo_data_arr.push(this.reserved);

               

              
    
                
            }
        }

         this.algo_data_arr.forEach(algo => {
               

                if(algo.count>=0){
                 this.chartData.labels.push(algo.name);
                 this.chartData.datasets[0].data.push(algo.count);
  
                }
                 // 


                  
               });

        this.chartload=true;


      });
  },
  methods: {
       dynamicColors : function() {
            var r = Math.floor(Math.random() * 255);
            var g = Math.floor(Math.random() * 255);
            var b = Math.floor(Math.random() * 255);
            return "rgba(" + r + "," + g + "," + b + ",0.4)";
         }
 
  },
};
</script>


