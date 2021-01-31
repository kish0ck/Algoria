package com.infinity.profile.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import com.infinity.feedback.repo.Algorithm;
import com.infinity.feedback.repo.Problem;
import com.infinity.feedback.repo.ProblemLevel;
import com.infinity.profile.repo.Profile;
import com.infinity.profile.repo.SubmitRepo;

@Service
public class ProfileService {
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private SubmitRepo repo;

	public List<Profile> pSfind20(String user_id) throws ClientProtocolException, IOException, ParseException {
		String qstr = String.format("{ user_id : { $eq : '%s' } }", user_id);
		BasicQuery query1 = new BasicQuery(qstr);
		String pageNo = "1";
		int iPage = Integer.parseInt(pageNo);
		Pageable p = PageRequest.of((iPage - 1) * 20, iPage * 20);
		List<Profile> res = mongoTemplate.find(query1.with(p), Profile.class, "submit");
//		System.out.println(res.size());
		System.out.println();
		// https://eddyplusit.tistory.com/51
		String USER_AGENT = "Mozila/5.0";
		String GET_URL = "https://api.solved.ac/problem_level.php?id=";

		for (int i = 0; i < res.size(); i++) {
			Profile pf = res.get(i);
			String URL = GET_URL + pf.getProblem_id();

			// http client 생성
			CloseableHttpClient httpClient = HttpClients.createDefault();

			// get 메서드와 URL 설정
			HttpGet httpGet = new HttpGet(URL);

			// agent 정보 설정
			httpGet.addHeader("User-Agent", USER_AGENT);
			httpGet.addHeader("Content-type", "application/json");

			// get 요청
			CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
			String json = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

			// 파싱
			JSONParser jsonParse = new JSONParser();
			JSONObject jsonObj = (JSONObject) jsonParse.parse(json);
			res.get(i).setLevel(String.valueOf(jsonObj.get("level")));

			JSONArray algoArr = (JSONArray) jsonObj.get("algorithms");

			String classification = ""; // 알고리즘 분류 String값
			for (int j = 0; j < algoArr.size(); j++) {
				JSONObject algoObject = (JSONObject) algoArr.get(j);
				if (j == 0)
					classification += algoObject.get("full_name_ko");
				else {
					classification += ",";
					classification += algoObject.get("full_name_ko");
				}
			}
			//res.get(i).setClassification(classification);

			int problem_num = res.get(i).getProblem_id();
			Query query2 = new Query();
			query2.addCriteria(Criteria.where("problem_id").is( problem_num));
			List<Profile> pf2 = mongoTemplate.find(query2, Profile.class, "problem");

			
			
			res.get(i).setProblem_title(pf2.get(0).getProblem_title());

			if (res.get(i).getResult().equals("result-ac")) {
				
				
				
				int curProblem_id = res.get(i).getProblem_id();
				String curSubmission_id = res.get(i).getSubmission_id();
				String curUser_id = res.get(i).getUser_id();
				
				System.out.println(res.get(i));
				
				//pCfind20Detail(curProblem_id, curUser_id, curSubmission_id);
				// 1) 메모리,시간 % 그래프 => 동민 형관쓰 코드로
				// 2) 다른 사람 시도횟수 VS 내시도횟수
				// 3) 코드길이
				// 4) 채점으로 넘어가기 (내 코드보기 URL주소로 버튼)
			}
		}

		return res;
	}


	public String[] pCfind20Detail(int curProblem_id, String curUser_id, String curSubmission_id) {
		// 0유저아디 , 1제출번호, 2문제번호, 3평균코드길이, 4내코드길이, 5평균시도횟수, 6내시도횟수
		
		String[] strArr = new String[7];
		strArr[0] = curUser_id;
		strArr[1] = curSubmission_id;
		strArr[2] = String.valueOf(curProblem_id);
		
		double avgLen = 0; // 코드평균길이
		MatchOperation match1 = Aggregation.match(Criteria.where("problem_id").is(curProblem_id));
		MatchOperation match2 = Aggregation.match(Criteria.where("result").is("result-ac"));
		ProjectionOperation project = Aggregation.project()
										.and("user_id").as("user_id")
										.and("length").as("length")
										.and("submission_id").as("submission_id");
		Aggregation agg1 = Aggregation.newAggregation(
				match1,
				match2,
				project
				);
		AggregationResults<HashMap> result1 = mongoTemplate.aggregate(agg1, "submit", HashMap.class);
		
		List<HashMap> aggRes1 = result1.getMappedResults();
		int sumLen = 0;
		int myLen = 0; // 내코드길이
		for (HashMap<String, Object> doc : aggRes1) {
			Object obj1 = doc.get("length");
			String stro1 = obj1.toString();
			if(stro1.equals(""))
				continue;
			int docLen = Integer.parseInt(stro1);
			sumLen+= docLen;
			
			Object obj2 = doc.get("submission_id");
			String stro2 = obj2.toString();
			if(stro2.equals(curSubmission_id)) {
				myLen = Integer.parseInt(stro1);
			}
		}
		avgLen = sumLen/(double)aggRes1.size();
		strArr[3] = String.valueOf(avgLen);
		strArr[4] = String.valueOf(myLen);
		////////////////////////////////////////
		
		MatchOperation match22 = Aggregation.match(Criteria.where("problem_id").is(curProblem_id));
		GroupOperation group22 = Aggregation.group("user_id").count().as("sum");
		
		Aggregation agg = Aggregation.newAggregation(
				match22,
				group22
				);
		AggregationResults<HashMap> results22 = mongoTemplate.aggregate(agg, "submit", HashMap.class);
		
		List<HashMap> aggRes22 = results22.getMappedResults();
		
		double userCnt = aggRes22.size(); //총 유저수
		double totalSum = 0; //총 시도횟수
		String myTryCnt = "";
		for (HashMap<String, Object> doc : aggRes22) {
			Object obj1 = doc.get("sum");
			String stro1 = obj1.toString();
			totalSum += Double.valueOf(stro1).doubleValue();
			Object obj2 = doc.get("_id");
			String stro2 = obj2.toString();
			if(stro2.equals(curUser_id)) {
				myTryCnt = stro1;
			}
		}
		
		strArr[5] = String.valueOf(totalSum/userCnt); 
		strArr[6] = myTryCnt;
//		System.out.println(Arrays.toString(strArr));
		return strArr;
	}
	
	
	/**
	 * 제출번호에 해당하는 제출기록들을 반환 
	 * @param submissionIdList
	 * @return
	 */
	
	public List<Profile> getSubmissionArrayBySumbissionId(List<Integer> submissionIdList){
		
		
			List<Profile> profileList=	repo.findbySubmissionIdList(submissionIdList);
		for (Profile submitRecord : profileList) {
			
			// 문제에 대한 정보 : 문제레벨, 이름, 알고리즘 분류 등을 가져온다
				
			try {
				
				Query queryProblemLevel = new Query();
				queryProblemLevel.addCriteria(Criteria.where("problem_id").is(submitRecord.getProblem_id()));
				ProblemLevel problemLevel = mongoTemplate.findOne(queryProblemLevel, ProblemLevel.class, "problem_level");
				// 문제에 대한 정보 : 문제 이름 등을 가져온다 - 개선할사항/이슈 : 이거 두개 합쳐서 db에 저장하면 안되나?
				Query getProblemInfo = new Query();
				getProblemInfo.addCriteria(Criteria.where("problem_id").is(submitRecord.getProblem_id()));
				Problem problem = mongoTemplate.findOne(getProblemInfo, Problem.class, "problem");

				// 문제 레벨 (\solvedac 레벨) 설정
				submitRecord.setLevel(problemLevel.getLevel());

			
				// 문제 태그(알고리즘 분류) 설정
				submitRecord.setClassification((ArrayList<Algorithm>) problemLevel.getAlgorithms());
				submitRecord.setProblem_title(problem.getProblem_title());
			
			}
			catch (Exception e) {
				// TODO: handle exception
			}
		
			
			
			
		}
		return profileList;
		
		
	}


	/**
	 * 검색할 아이디가 submit collection에  기록이 있는지 없는지 확인
	 * 있으면 true, 없으면 false로 반환 
	 * @param id
	 * @return 
	 */
	public boolean pSisUser(String id) {
		 Query query = new Query(Criteria.where("user_id").is(id));
		 int cnt = (int)mongoTemplate.count(query, "submit");
		 System.out.println(cnt);
		 if(cnt==0) {
			 return false;
		 }else {
			 return true;
		 }
	}


}
