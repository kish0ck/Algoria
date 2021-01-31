package com.infinity.profile.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.sql.Date;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scripting.support.StaticScriptSource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.infinity.profile.repo.Profile;
import com.infinity.profile.repo.SubmitRepo;
import com.mongodb.BasicDBObject;

import ch.qos.logback.classic.Logger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StasticService {

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private SubmitRepo submitrepo;

	/**
	 * 어떤 문제에 대해 성공한 한 유저의 제출 통계기록을 반환
	 * 
	 * @param problemId
	 * @param userId
	 * @param codelang
	 * @return
	 */

	public HashMap<String, Object> getProblemUserStasticsInfo(Integer problemId, String userId, String codelang) {

		ProjectionOperation projection = Aggregation.project() // projection
				.and("user_id").as("userId").and("problem_id").as("problemId").and("time").as("time").and("memory")
				.as("memory").and("language").as("language").and("length").as("length").and("result").as("result");

		MatchOperation whereProblemId = null;// 해당 문제에 대한 제출기록들만
		whereProblemId = Aggregation.match(new Criteria().andOperator(Criteria.where("problemId").is(problemId)));
		MatchOperation whereCodelanguage = null;
		whereCodelanguage = Aggregation.match(new Criteria().andOperator(Criteria.where("language") // 해당 언어에 대한 제출기록들만
				.is(codelang)));

		MatchOperation whereResultAccepted = null; // 맞은결과만
		whereResultAccepted = Aggregation.match(new Criteria().andOperator(Criteria.where("resarr").in("result-ac")));

		MatchOperation whereTargetUserId = null; // 해당 유저 제출기록만
		whereTargetUserId = Aggregation.match(new Criteria().andOperator(Criteria.where("userId").is(userId)));

		GroupOperation groupByUser = null; // 유저별로 document grouping
		groupByUser = Aggregation.group().sum("memory").as("memory").sum("time").as("time").push("result").as("resarr")
				.sum("length").as("length");
		
		//시간순 정렬 
		SortOperation sortByDate = null;
		sortByDate = Aggregation.sort(Direction.DESC, "date");

		Aggregation agg = Aggregation.newAggregation( // aggregation pipeline 구성
				projection, whereProblemId, whereCodelanguage, whereTargetUserId, groupByUser, whereResultAccepted, sortByDate

		);

		Long now = System.currentTimeMillis();

		AggregationResults<HashMap> results = mongoTemplate.aggregate(agg, "submit", HashMap.class); // HashMap으로 매핑

		List<HashMap> AggregateRes = results.getMappedResults(); // 결과
		Long end = System.currentTimeMillis();

		// 통계기록
		Integer trycountsum = 0;
		Integer time = 0;
		Integer codelen = 0;
		Integer memory = 0;
		Integer userNum = AggregateRes.size();
		Double avgTryCount = 0.00;

		if (userNum == 0)
			userNum = 1;

		//System.out.println(end - now);
		for (HashMap<String, Object> doc : AggregateRes) {
			//System.out.println(doc);
			ArrayList<String> resArr = (ArrayList<String>) doc.get("resarr");

			Integer trycount = 0;
			Integer successcount = 0;
			for (String r : resArr) { // 성공하기 전까지의 시도 수를 체크
				if (!r.equals("result-ac")) {
					trycount++;
				} else
					successcount++;
			}
			trycountsum += trycount;

			time += (Integer) doc.get("time") / successcount;
			memory += (Integer) doc.get("memory") / successcount;
			codelen += (Integer) doc.get("length") / (successcount + trycount);
		}

//		System.out.println(trycountsum);
//		System.out.println(AggregateRes.size());
		avgTryCount = (double) (trycountsum + userNum) / userNum;
		Double successRate = (double) AggregateRes.size() / (double) (AggregateRes.size() + trycountsum);
//		System.out.println(successRate);

		HashMap<String, Object> resultdoc = new HashMap<String, Object>();
		resultdoc.put("avgtime", time / userNum);
		resultdoc.put("avgmemory", memory / userNum);
		resultdoc.put("avagcodlen", codelen / userNum);
		resultdoc.put("avgtrycount", avgTryCount);
		resultdoc.put("successrate", successRate);
		resultdoc.put("succesusernum", userNum);

		// return null;
		return resultdoc;

	}

	public HashMap<String, Object> getProblemSubmitCodeLanguageStasticsInfo(Integer problemId) {
		ProjectionOperation projection = Aggregation.project() // projection
				.and("user_id").as("userId").and("problem_id").as("problemId").and("language").as("language")
				.and("result").as("result");

		MatchOperation whereProblemId = null;// 해당 문제에 대한 제출기록들만
		whereProblemId = Aggregation.match(new Criteria().andOperator(Criteria.where("problemId").is(problemId)));

		MatchOperation whereResultAccepted = null; // 맞은결과만
		whereResultAccepted = Aggregation.match(new Criteria().andOperator(Criteria.where("result").is("result-ac")));

		GroupOperation groupByUser = null; // 유저별로 document grouping
		groupByUser = Aggregation.group("language").count().as("cnt");

		Aggregation agg = Aggregation.newAggregation( // aggregation pipeline 구성
				projection, whereProblemId, whereResultAccepted, groupByUser

		);
		AggregationResults<HashMap> results = mongoTemplate.aggregate(agg, "submit", HashMap.class); // HashMap으로 매핑

		List<HashMap> AggregateRes = results.getMappedResults(); // 결과
		HashMap<String, Object> res = new HashMap<String, Object>();
		res.put("data", AggregateRes);
		return res;

	}

	/**
	 * 어떤 문제에 대해 성공한 유저들의 제출 통계기록을 반환
	 * 
	 * @param problemId
	 * @param codelang
	 * @return
	 */

	public HashMap<String, Object> getProblemStasticsInfo(Integer problemId, String codelang) {

		/*
		 * 정답 비율 = (문제를 맞은 사람의 수) / (문제를 맞은 사람의 수 + (문제를 맞은 각 사람이 그 문제를 맞기 전까지 틀린 횟수의 총
		 * 합)) × 100 틀린 횟수는 "맞았습니다!!" 이외의 결과를 받은 횟수를 의미한다.
		 */
		// System.out.println(res.size());

		ProjectionOperation projection = Aggregation.project() // projection
				.and("user_id").as("userId").and("problem_id").as("problemId").and("time").as("time").and("memory")
				.as("memory").and("language").as("language").and("length").as("length").and("result").as("result");

		MatchOperation whereProblemId = null;// 해당 문제에 대한 제출기록들만
		whereProblemId = Aggregation.match(new Criteria().andOperator(Criteria.where("problemId").is(problemId)));

		MatchOperation whereCodelanguage = null;
		whereCodelanguage = Aggregation.match(new Criteria().andOperator(Criteria.where("language") // 해당 언어에 대한 제출기록들만
				.is(codelang)));

		MatchOperation whereResultAccepted = null; // 맞은결과만
		whereResultAccepted = Aggregation.match(new Criteria().andOperator(Criteria.where("resarr").in("result-ac")));

		GroupOperation groupByUser = null; // 유저별로 document grouping
		groupByUser = Aggregation.group("userId").sum("memory").as("memory").sum("time").as("time").push("result")
				.as("resarr").sum("length").as("length").push("language").as("langarr");

		Aggregation agg = Aggregation.newAggregation( // aggregation pipeline 구성
				projection, whereProblemId, whereCodelanguage, groupByUser, whereResultAccepted

		);

		Long now = System.currentTimeMillis();

		AggregationResults<HashMap> results = mongoTemplate.aggregate(agg, "submit", HashMap.class); // HashMap으로 매핑

		List<HashMap> AggregateRes = results.getMappedResults(); // 결과
		Long end = System.currentTimeMillis();

		// 통계기록
		Integer trycountsum = 0;
		Integer time = 0;
		Integer codelen = 0;
		Integer memory = 0;
		Integer userNum = AggregateRes.size();
		Double avgTryCount = 0.00;

		if (userNum == 0)
			userNum = 1;

		//System.out.println(end - now);
		for (HashMap<String, Object> doc : AggregateRes) {
			// System.out.println(doc);
			ArrayList<String> resArr = (ArrayList<String>) doc.get("resarr");

			Integer trycount = 0;
			Integer successcount = 0;
			for (String r : resArr) { // 성공하기 전까지의 시도 수를 체크
				if (!r.equals("result-ac")) {
					trycount++;
				} else
					successcount++;
			}
			trycountsum += trycount;

			time += (Integer) doc.get("time") / successcount;
			memory += (Integer) doc.get("memory") / successcount;
			codelen += (Integer) doc.get("length") / (successcount + trycount);
		}

		avgTryCount = (double) (trycountsum + userNum) / userNum;
		Double successRate = (double) AggregateRes.size() / (double) (AggregateRes.size() + trycountsum);
		HashMap<String, Object> resultdoc = new HashMap<String, Object>();
		resultdoc.put("avgtime", time / userNum);
		resultdoc.put("avgmemory", memory / userNum);
		resultdoc.put("avagcodlen", codelen / userNum);
		resultdoc.put("avgtrycount", avgTryCount);
		resultdoc.put("successrate", successRate);
		resultdoc.put("succesusernum", userNum);

		// return null;
		return resultdoc;
	}

	/**
	 * problemId에 해당하는 문제를 푼 유저의 성적(실행시간, 메모리)가 문제를 푼 유저 전체에 대해 상위 몇%인지 계산해주는 api
	 * 
	 * @param problemId 문제 번호
	 * @param codelang  제출 언어
	 * @param attr      비교할 속성 (시간 : time, 메모리 : memory)
	 * @return
	 */
	public HashMap<String, Object> getProblemUserSubmitResultRank(String userId, Integer problemId, String codelang,
			String attr) {

		/*
		 * aggreagation 2번 씀(문제푼사람 수 N, 개인의 랭킹 rank 구할때) 한개로 합치거나 더 좋은 방법이 있으면 수정 바람
		 */

		ProjectionOperation projection = Aggregation.project() // projection
				.and("user_id").as("userId").and("problem_id").as("problemId").and("time").as("time").and("memory")
				.as("memory").and("language").as("language").and("length").as("length").and("result").as("result");

		MatchOperation whereProblemId = null;// 해당 문제에 대한 제출기록들만
		whereProblemId = Aggregation.match(new Criteria().andOperator(Criteria.where("problemId").is(problemId)));
		MatchOperation whereCodelanguage = null;
		whereCodelanguage = Aggregation.match(new Criteria().andOperator(Criteria.where("language") // 해당 언어에 대한 제출기록들만
				.is(codelang)));
		MatchOperation whereResultAccepted = null; // 맞은결과만
		whereResultAccepted = Aggregation.match(new Criteria().andOperator(Criteria.where("result").is("result-ac")));

		GroupOperation groupCountUsers = null; // 유저별로 grouping
		groupCountUsers = Aggregation.group().count().as("N");

		Aggregation aggCountUserNum = Aggregation.newAggregation( // aggregation pipeline 구성
				projection, whereProblemId, whereCodelanguage, whereResultAccepted, groupCountUsers);

		SortOperation sorting = null;
		sorting = Aggregation.sort(Sort.Direction.ASC, attr); // 비교할 속성에 대해 정렬

		GroupOperation groupByUser = null; // 유저별로 document grouping
		BasicDBObject pushobj = new BasicDBObject // group push에 들어갈 필드 설정
		("user_id", "$userId").append(attr, "$" + attr).append("submission_id", "$submissionId");
		groupByUser = Aggregation.group().push(pushobj).as("users"); // 유저 배열 만들기

		UnwindOperation unwind = null;
		unwind = Aggregation.unwind("users", "ranking");// uwind

		MatchOperation whereUserId = null; // 해당 유저만
		whereUserId = Aggregation.match(new Criteria().andOperator(Criteria.where("users.user_id").is(userId)));

		Aggregation agg = Aggregation.newAggregation( // aggregation pipeline 구성
				projection, whereProblemId, whereCodelanguage, whereResultAccepted, sorting, groupByUser, unwind,
				whereUserId

		);

		AggregationResults<HashMap> aggCountRes = mongoTemplate.aggregate(aggCountUserNum, "submit", HashMap.class); // HashMap으로
																														// 매핑
		List<HashMap> AggregateRes = aggCountRes.getMappedResults(); // 결과
		//System.out.println(AggregateRes);

		Integer N = (Integer) AggregateRes.get(0).get("N");

		AggregationResults<HashMap> aggRes = mongoTemplate.aggregate(agg, "submit", HashMap.class); // HashMap으로 매핑
		AggregateRes = aggRes.getMappedResults(); // 결과

		HashMap<String, Object> resultdoc = new HashMap<String, Object>();
		resultdoc.put("rank", AggregateRes);

		Long rank = (Long) AggregateRes.get(0).get("ranking");
		//System.out.println(rank);
		//System.out.println(N);
		Double rankrange = (double) ((rank + N * 0.001) / N); // rank가 0등부터 시작해서 0이 아닌 값이면서 아주 작은 값을 주어 상위 %의 범위를 최대한 좁힘

		resultdoc.put("userinfo", AggregateRes.get(0).get("users"));
		resultdoc.put("rank", rankrange);
		// return null;
		return resultdoc;
	}


	
	
	

	/**
	 * 유저가의 제출기록의 성적이(실행시간, 메모리) 문제를 푼 유저 전체에 대해 상위 몇%인지 계산해주는 api 제출기록 번호에 대해
	 * 
	 * @param submissionId 제출 번호
	 * @param codelang     제출 언어
	 * @param attr         비교할 속성 (시간 : time, 메모리 : memory)
	 * @return
	 */
	public HashMap<String, Object> getUserSubmitResultRank( Integer submissionId, String attr) {

		
		/*제출기록으로부터 데이터를 가져온다*/
		Profile submissionResult = submitrepo.findbySubmissionId(submissionId);
		String userId = submissionResult.getUser_id();
		Integer problemId = submissionResult.getProblem_id();
		String codelang = submissionResult.getLanguage();
		Integer userTime = new Integer(submissionResult.getTime());
		Integer userMemory = new Integer(submissionResult.getMemory());

		ProjectionOperation projection = Aggregation.project() // projection
				.and("user_id").as("userId").and("problem_id").as("problemId").and("time").as("time").and("memory")
				.as("memory").and("language").as("language").and("length").as("length").and("result").as("result");

		MatchOperation whereProblemId = null;// 해당 문제에 대한 제출기록들만
		whereProblemId = Aggregation.match(new Criteria().andOperator(Criteria.where("problemId").is(problemId)));
		MatchOperation whereCodelanguage = null;
		whereCodelanguage = Aggregation.match(new Criteria().andOperator(Criteria.where("language") // 해당 언어에 대한 제출기록들만
				.is(codelang)));
		MatchOperation whereResultAccepted = null; // 맞은결과만
		whereResultAccepted = Aggregation.match(new Criteria().andOperator(Criteria.where("result").is("result-ac")));

		SortOperation sorting = null;
		sorting = Aggregation.sort(Sort.Direction.ASC, attr); // 비교할 속성에 대해 정렬

		GroupOperation groupByUser = null; // atrr값을 배열로 
		groupByUser = Aggregation.group().push(attr).as("attr_arr"); // 유저 배열 만들기

		Aggregation agg = Aggregation.newAggregation( // aggregation pipeline 구성
				projection, 
				whereProblemId,
				whereCodelanguage, 
				whereResultAccepted,
				groupByUser
		);

		AggregationResults<HashMap> aggRes = mongoTemplate.aggregate(agg, "submit", HashMap.class); // HashMap으로 매핑
		List<HashMap> AggregateRes = aggRes.getMappedResults(); // 결과

		HashMap<String, Object> resultdoc = new HashMap<String, Object>();
	

		
		ArrayList<Integer> dataAarr = ((ArrayList<Integer>) AggregateRes.get(0).get("attr_arr"));
		dataAarr.sort(null);
		
		//비교할 유저 데이터
				Integer userdata=0;
				switch (attr) {
				case "time" :
					userdata=userTime;
					break;
				case "memory":
					userdata=userMemory;
					break;
				default:
					break;
				}
				
				
		//System.out.println( (double)dataAarr.indexOf(userdata)/dataAarr.size());
	
		Double rankrange = ((double)dataAarr.indexOf(userdata)/dataAarr.size()); // rank가 0등부터 시작해서 0이 아닌 값이면서 아주 작은 값을 주어 상위 %의 범위를 최대한 좁힘

		resultdoc.put("user_id", userId );
		resultdoc.put(attr, userdata);
		resultdoc.put("rank", rankrange);
		
		return resultdoc;
	}

}
