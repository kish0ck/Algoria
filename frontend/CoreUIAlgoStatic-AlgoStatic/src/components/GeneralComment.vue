<template>
  <div>
    <CRow>
      <CCol sm="12" md="12">
        <CCard accent-color="danger">
          <CCardHeader class="card-title header_card">
                <div class="flex display-1 font-weight-bold">취약점 분석</div>
          </CCardHeader>
          <CCardBody class="text-body">
            <span
              style="padding-top: 0.5rem !important; ont-weight: bold; font-size : x-large;"
            >{{id}}</span> 님의 맞은 문제의 수는
            <span
              style="padding-top: 0.5rem !important; font-weight: bold; font-size : large; color: blue !important;"
            >{{collectPcnt}}</span>개이며,
            <br />
            <strong>시도했지만 풀지 못한 문제의 수</strong>는
            <span
              style="padding-top: 0.5rem !important; font-weight: bold; font-size : large; color: #e91e63 !important;"
            >{{xPcnt}}</span>개입니다.
            <br />
            <strong>시도했지만 풀지 못한 문제의 유형</strong>으로는
            <br />
            <span
              style="padding-top: 0.5rem !important; font-weight: bold; font-size : large; color: #e91e63 !important;"
            >{{algo}}</span> 순으로 많으며,
            <br />그 외 틀린 유형으로는
            <span
              style="padding-top: 0.5rem !important; font-weight: bold; font-size : large; color: #e91e63 !important;"
            >{{resultType}}</span>가 많습니다.
          </CCardBody>
        </CCard>
      </CCol>
    </CRow>
  </div>
</template>


<script>
const _SERVER = "http://13.125.147.223:8080";
const axios = require("axios");
export default {
  name: "GeneralComment",
  data() {
    return {
      id: null,
      collectPcnt: 0, //맞은문제수
      xPcnt: 0, // 시도했지만 풀지못한 문제의수
      algo: null, // 많이 틀리는 알고리즘 유형 3가지
      resultType: null //많이 틀리는 유형
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
      .get(`${_SERVER}/user/${this.user_id}/GeneralComment`)
      .then(response => {
        response = response.data;
        this.id = this.user_id;
        console.log(response);
        var UserSubmitInfo = [];
        UserSubmitInfo = response.UserSubmitInfo;
        //  console.log(UserSubmitInfo)
        this.xPcnt = response.Xproblems.length;
        var res = [];
        for (let index = 0; index < UserSubmitInfo.length; index++) {
          res = UserSubmitInfo[index];
          if (res._id == "result-ac") {
            this.collectPcnt = res.problems.length;
          }
        }
        var UseMistakeAlgoInfo = [];
        UseMistakeAlgoInfo = response.UseMistakerAlgoInfo;
        var algoType = "";
        for (let index = 0; index < UseMistakeAlgoInfo.length; index++) {
          res = UseMistakeAlgoInfo[index];
          if (index == UseMistakeAlgoInfo.length - 1) {
            algoType += res._id;
          } else {
            algoType += res._id + ", ";
          }
        }
        console.log(algoType);
        this.algo = algoType;
        for (let index = 0; index < UserSubmitInfo.length; index++) {
          res = UserSubmitInfo[index];
          if (res._id != "result-ac" && res._id != "result-wa") {
            if (res._id == "result-tle") this.resultType = "시간초과";
            if (res._id == "result-rte") this.resultType = "런타임에러";
            if (res._id == "result-ce") this.resultType = "컴파일에러";
            if (res._id == "result-ole") this.resultType = "출력초과";
            if (res._id == "result-pe") this.resultType = "출력형식";
            break;
          }
        }
      });
  }
};
</script>
<style scoped>
.text-body {
  height: auto;
  text-align: center;
  /* background-color: rgba(0, 0, 0, 0.85); */
  padding: 30px;
  color: #f0f0f0;
  font-size: 1.45em;
  line-height: 1.4em;
  font-weight: 500;
  overflow: hidden;
  -webkit-transition: all 0.5s ease-out;
  -moz-transition: all 0.5s ease-out;
  -ms-transition: all 0.5s ease-out;
  -o-transition: all 0.5s ease-out;
  transition: all 0.5s ease-out;
}
</style>
