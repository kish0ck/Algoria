package com.infinity.profile.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/***
 * FileName : UserService.java
 * 
 * @version : 1.0
 * @author : ShimHyeongGwan(Xxings) Comment :
 */
@Slf4j
@Service
public class UserService {

	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private MongoTemplate mongoTemplate;

	private final static boolean notFROM = true;
	private final static boolean notTO = false;

	public HashMap<String, Object> getUserProblemList(String userId, String from, String to) {
		HashMap<String, Object> res = new HashMap<String, Object>();
		List<HashMap> result = new LinkedList<HashMap>();
		if (from == null || to == null) {
			if (from == null && to == null) { // 전체기간 조회
				result = setUserProblemList(userId);
			} else if (to == null) { // ~부터 출력
				result = setUserProblemList(userId, from, notTO);
			} else { // ~까지 출력
				result = setUserProblemList(userId, to, notFROM);
			}
		} else if (from.compareTo(to) <= 0) {
			result = setUserProblemList(userId, from, to);
		} else {
			LOG.info("[INFO] : 잘못된 입력값  [from : {}, to: {}]", from, to);
		}
		if (result != null && result.size() > 0)
			res = result.get(0);
		return res;
	}

	public HashMap<String, Object> getUserProblemType(String userId, String from, String to) {
		List<HashMap> array = null;
		// TODO : MAP REDUCER로 바꿀것
		if (from == null || to == null) {
			if (from == null && to == null) { // 전체기간 조회
				array = setUserProblemList(userId);
			} else if (to == null) { // ~부터 출력
				array = setUserProblemList(userId, from, notTO);
			} else { // ~까지 출력
				array = setUserProblemList(userId, to, notFROM);
			}
		} else if (from.compareTo(to) <= 0) {
			array = setUserProblemList(userId, from, to);
		} else {
			LOG.info("[INFO] : 잘못된 입력값  [from : {}, to: {}]", from, to);
		}

		List<HashMap> result = null;
		Object map = array.get(0);
		Object problems = ((HashMap<String, Object>) map).get("problems");

		if (notEmpty(problems)) { // 비어있지 않다면
			result = setUserProblemType((List<Integer>) problems);
		}
		HashMap<String, Object> res = new HashMap<String, Object>();
		res.put("problems",problems);
		res.put("types",result);
		return res;
	}

	private List<HashMap> setUserProblemType(List<Integer> problems) {
		/*
		 * db.getCollection("problem_level").aggregate( [ { "$unwind": "$algorithms" },
		 * { "$match" : {"problem_id" : {$in : [ 4437, 1000,1001, 1122 ] } } }, {
		 * "$group" : { "_id" : "$algorithms.full_name_ko", "count": { $sum : 1} } }, {
		 * "$project" : { "_id" : 0, "problemType" : "$_id", "count" : "$count" } }, {
		 * "$sort" : { "count" : -1} } ]);
		 */

		// algorithms에 대해서 unwind
		UnwindOperation unwindAlgorithms = null;
		unwindAlgorithms = Aggregation.unwind("algorithms");

		// 배열안의 in 문제번호
		MatchOperation whereProblemId = null;
		whereProblemId = Aggregation.match(new Criteria().where("problem_id").in(problems));

		// 유저별로 document grouping
		GroupOperation groupByProblem = null;
		groupByProblem = Aggregation.group("algorithms.full_name_ko").count().as("count");

		ProjectionOperation projection = Aggregation.project() // projection
				.and("$_id").as("problemType").andExclude("$_id").and("count").as("count");

		SortOperation sortByCount = Aggregation.sort(Direction.DESC, "count");

		// aggregation pipeline 구성
		Aggregation agg = Aggregation.newAggregation(unwindAlgorithms, whereProblemId, groupByProblem, projection,
				sortByCount);

		// HashMap으로 매핑
		AggregationResults<HashMap> results = mongoTemplate.aggregate(agg, "problem_level", HashMap.class);

		/* TODO : 나중에 POJO로 변경 */
//				AggregationResults<UserProblemList> test = mongoTemplate.aggregate(agg, "submit", UserProblemList.class);

		// 결과
		return results.getMappedResults();
	}

	/**
	 * Object type 변수가 비어있는지 체크
	 * 
	 * @param obj
	 * @return Boolean : true / false
	 */
	public static Boolean empty(Object obj) {
		if (obj instanceof String)
			return obj == null || "".equals(obj.toString().trim());
		else if (obj instanceof List)
			return obj == null || ((List) obj).isEmpty();
		else if (obj instanceof Map)
			return obj == null || ((Map) obj).isEmpty();
		else if (obj instanceof Object[])
			return obj == null || Array.getLength(obj) == 0;
		else
			return obj == null;
	}

	/**
	 * Object type 변수가 비어있지 않은지 체크
	 * 
	 * @param obj
	 * @return Boolean : true / false
	 */
	public static Boolean notEmpty(Object obj) {
		return !empty(obj);
	}

	/**
	 * 사용자가 시도한 문제에 대해서 모두 호출
	 */
	public List<HashMap> setUserProblemList(String userId) {
		/*
		 * db.getCollection("submit").aggregate( [ {"$match" :{ "user_id" : "gudrhks2"
		 * }}, { "$group": { "_id": "$user_id", "problems" : { "$addToSet" :
		 * "$problem_id" } }}, {"$project" : { "_id" : 0, user_id : "$_id", problems : 1
		 * }} ]);
		 */

		// 해당 유저에 대한 제출 기록들만
		MatchOperation whereUserId = null;
		whereUserId = Aggregation.match(new Criteria().andOperator(Criteria.where("user_id").is(userId)));

		// 유저별로 document grouping
		GroupOperation groupByProblem = null;
		groupByProblem = Aggregation.group("user_id").addToSet("problem_id").as("problems");

		ProjectionOperation projection = Aggregation.project() // projection
				.and("$_id").as("userId").andExclude("$_id").and("problems").as("problems");

		// aggregation pipeline 구성
		Aggregation agg = Aggregation.newAggregation(whereUserId, groupByProblem, projection);

		// HashMap으로 매핑
		AggregationResults<HashMap> results = mongoTemplate.aggregate(agg, "submit", HashMap.class);

		/* TODO : 나중에 POJO로 변경 */
//		AggregationResults<UserProblemList> test = mongoTemplate.aggregate(agg, "submit", UserProblemList.class);

		// 결과
		return results.getMappedResults();
	}

	/**
	 * 사용자가 푼 문제에 대해서 한쪽 기간을 정하고 호출
	 */
	public List<HashMap> setUserProblemList(String userId, String dateTime, boolean type) {
		/*
		 * =QUERY= db.getCollection("submit").aggregate( [ {"$match" :{ "user_id" :
		 * "gudrhks2"
		 * 
		 * }}, //FROM {$match : {date: { $gte : ISODate("2019-01-01")/1000) } } }, //TO
		 * {$match : {date: { $lte : ISODate("2019-01-01")/1000) } } }, { "$group": {
		 * "_id": "$user_id", "problems" : { "$addToSet" : "$problem_id" } }},
		 * {"$project" : { "_id" : 0, user_id : "$_id", problems : 1 }} ]);
		 */

		Long ltime;
		try {
			ltime = cvtStringtoDate(dateTime);
		} catch (DateTimeException e) {
			// 숫자 형식이 잘못된경우
			LOG.info("숫자형식이 잘못 됨  / {} :{}", (type == notTO) ? "from" : "to", dateTime);
			return null;
		}

		// 해당 유저에 대한 제출 기록들만
		MatchOperation whereUserId = null;
		if (type == notTO) {
			// ~부터
			whereUserId = Aggregation.match(new Criteria().andOperator(Criteria.where("user_id").is(userId),
					Criteria.where("date").gte(ltime)));
		} else if (type == notFROM) {
			// ~까지
			whereUserId = Aggregation.match(new Criteria().andOperator(Criteria.where("user_id").is(userId),
					Criteria.where("date").lte(ltime)));
		} else { // ERROR
			return null;
		}

		// 유저별로 document grouping
		GroupOperation groupByProblem = null;
		groupByProblem = Aggregation.group("user_id").addToSet("problem_id").as("problems");

		ProjectionOperation projection = Aggregation.project() // projection
				.and("$_id").as("userId").andExclude("$_id").and("problems").as("problems");

		// aggregation pipeline 구성
		Aggregation agg = Aggregation.newAggregation(whereUserId, groupByProblem, projection);

		// HashMap으로 매핑
		AggregationResults<HashMap> results = mongoTemplate.aggregate(agg, "submit", HashMap.class);

		/* TODO : 나중에 POJO로 변경 */
//		AggregationResults<UserProblemList> test = mongoTemplate.aggregate(agg, "submit", UserProblemList.class);

		// 결과
		return results.getMappedResults();
	}

	/**
	 * 사용자가 푼 문제에 대해서 양쪽 기간을 정하고 호출
	 */
	public List<HashMap> setUserProblemList(String userId, String from, String to) {
		/*
		 * =QUERY= db.getCollection("submit").aggregate( [ {"$match" :{ "user_id" :
		 * "gudrhks2"
		 * 
		 * }}, //FROM {$match : {date: { $gte : ISODate("2019-01-01")/1000) } } }, //TO
		 * {$match : {date: { $lte : ISODate("2019-01-01")/1000) } } }, { "$group": {
		 * "_id": "$user_id", "problems" : { "$addToSet" : "$problem_id" } }},
		 * {"$project" : { "_id" : 0, user_id : "$_id", problems : 1 }} ]);
		 */

		Long tsFrom, tsTo;
		MatchOperation whereUserId = null;
		try {
			tsFrom = cvtStringtoDate(from);
			tsTo = cvtStringtoDate(to);
		} catch (DateTimeException e) {
			// 숫자 형식이 잘못된경우
			LOG.info("숫자형식이 잘못 됨  / from :{}, to : {}", from, to);
			return null;
		}

		// 해당 유저에 대한 제출 기록들만
		whereUserId = Aggregation.match(new Criteria().andOperator(Criteria.where("user_id").is(userId),
				Criteria.where("date").gte(tsFrom), Criteria.where("date").lte(tsTo)));

		// 유저별로 document grouping
		GroupOperation groupByProblem = null;
		groupByProblem = Aggregation.group("user_id").addToSet("problem_id").as("problems");

		ProjectionOperation projection = Aggregation.project() // projection
				.and("$_id").as("userId").andExclude("$_id").and("problems").as("problems");

		// aggregation pipeline 구성
		Aggregation agg = Aggregation.newAggregation(whereUserId, groupByProblem, projection);

		// HashMap으로 매핑
		AggregationResults<HashMap> results = mongoTemplate.aggregate(agg, "submit", HashMap.class);

		/* TODO : 나중에 POJO로 변경 */
//		AggregationResults<UserProblemList> test = mongoTemplate.aggregate(agg, "submit", UserProblemList.class);

		// 결과
		return results.getMappedResults();
	}

	private final static String userAPI = "https://api.solved.ac/user_information.php";
	private final static String USER_AGENT = "Mozilla/5.0";
	private static int responseCode;

	/***
	 * Comment :
	 * 
	 * @version : 1.0
	 * @tags : @param userId : 사용자의 아이디
	 * @tags : @return : application/json
	 * @tags : @throws Exception
	 * @date : 2020. 1. 22.
	 */
	@SuppressWarnings("finally")
	public static HashMap<String, Object> sendGetData(String userId) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(userAPI).queryParam("id", userId);
		HashMap<String, Object> res = new HashMap<String, Object>();
		JsonNode resultNode = null;
		try {
			URL url = new URL(builder.toUriString());
			// connection Settings
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", USER_AGENT);

			// 요청
			responseCode = con.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close(); // print result
//		System.out.println("HTTP 응답 코드 : " + responseCode);
//		System.out.println("HTTP body : " + response.toString());

			// JSON 형태 반환값 처리
			ObjectMapper mapper = new ObjectMapper();
			resultNode = mapper.readTree(response.toString());

		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {

		} finally {
			res.put("data", resultNode);
			return res;
		}
	}

	private Long cvtStringtoDate(String date) {
		// 시작날짜를 타임스탬프로 바꾸기
		LocalDate ldate = LocalDate.parse(date);
		Timestamp tsdate = Timestamp.valueOf(ldate.atStartOfDay());
		Long longDate = tsdate.getTime();
		return longDate / 1000;
	}

}
