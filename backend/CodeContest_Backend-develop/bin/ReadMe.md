##  **C**ode**C**ontent(BackGround) 

---

**= Contribute =**

+ 신동민

+ 심형관

+ 홍기석

**= OverView =**

 알고리즘 사용자의 제출기록(약 17,000,000)을 토대로하여, **개인화**에 맞춘 문제 분석 & 피드백 & 추천을 진행합니다.

+ Trello : [https://trello.com/b/2QTBVWtu]

**= Prerequisites =**

WebCrawler :

 1. 제출결과를 주기적으로 크롤링하여 DB에 반영 [ 제출번호 idx를 통해 요청하는 서버에 부하 감소] 

    [unit 1min]

 2. 새로운 문제, 새로운 문제 분류에 대해서 기간을 정하여 가져옴

    [unit 1day]

DB :

	+ 제출기록, 회원정보 등의 document를 담고 있는 NoSQL DB
	+ Json형태의 document 저장, aggreagtation을 통한 실시간 시간 데이터 분석

RestAPI :

+ DB에서 관련 데이터를 가져와 서비스에 필요한 rest api 제공

  ### profile-controller : profile controller 

  | GE**T** | **/find20/{id}pCfind20**                                     |      |
  | ------- | ------------------------------------------------------------ | ---- |
  | **GET** | **/find20/{userId}/detail/{problemNo}/{submissionId}pCfind20Detail** |      |
  | **GET** | **/test**                                                    |      |
  ### stastic-controller : Stastic Controller
  | GE**T** | **/stastic/dailysubmit/{userId}/{from}/{to}getDailySubmitInfo** |
  | ------- | ------------------------------------------------------------ |
  | **GET** | **/stastic/problem/avguser/{problemId}/{codelang}getProblemUserAvgStasticsInfo** |
  | **GET** | **/stastic/problem/distribution/codelang/{problemId}getProblemSubmitLanguageStasticsInfo** |
  | **GET** | **/stastic/problem/rank/{userId}/{problemId}/{attr}/{codelang}getProblemUser** |
  | **GET** | **/stastic/problem/user/{u**serId}/{problemId}/{codelang}getProblemUserStasticsInfo |

  ### user-controller 
  | GET     | **/user/test**                  |
  | ------- | ------------------------------- |
  | **GET** | **/user/{userId}**              |
  | **GET** | **/user/{userId}/problems**     |
  | **GET** | **/user/{userId}/problemsType** |

**= Technical stack =**

Python

mongoDB

Spring

**= SetUP =**

**= Build =**

**= Test =**

**= Run =**

**= Deploy =**

