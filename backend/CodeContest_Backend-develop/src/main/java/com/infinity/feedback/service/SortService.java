package com.infinity.feedback.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.google.common.collect.ObjectArrays;
import com.infinity.feedback.repo.Group;
import com.infinity.feedback.repo.GroupRepo;
import com.infinity.profile.service.StasticService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SortService {
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private GroupRepo grouprepo;
	@Autowired
	private StasticService statservice;
	
	public HashMap<String, Object> getUserAlgoType(String userId){ //많이 푼 문제 분류
		HashMap<String, Object> res = new HashMap<String, Object>();
		List<HashMap> array = null;
		MatchOperation whereUserId = null;
		whereUserId = Aggregation.match(new Criteria().andOperator(Criteria.where("user_id").is(userId)));

		MatchOperation whereResult = null;
		whereResult = Aggregation.match(new Criteria().andOperator(Criteria.where("result").is("result-ac")));
		
		// 유저별로 document grouping
		GroupOperation groupByProblem = null;
		groupByProblem = Aggregation.group("user_id").addToSet("problem_id").as("problems");

		ProjectionOperation projection = Aggregation.project() // projection
				.and("$_id").as("userId").andExclude("$_id").and("problems").as("problems");

		// aggregation pipeline 구성
		Aggregation agg = Aggregation.newAggregation(whereUserId, whereResult, groupByProblem, projection);
		// HashMap으로 매핑
		AggregationResults<HashMap> results = mongoTemplate.aggregate(agg, "submit", HashMap.class);
		
		array = results.getMappedResults();
		
		List<HashMap> result = null;
		Object map = array.get(0);
		List<Integer> problems = (List<Integer>) ((HashMap<String, Object>) map).get("problems");

		UnwindOperation unwindAlgorithms2 = null;
		unwindAlgorithms2 = Aggregation.unwind("algorithms");

	
		// 배열안의 in 문제번호
		MatchOperation whereProblemId2 = null;
		whereProblemId2 = Aggregation.match(new Criteria().where("problem_id").in(problems));

		// 유저별로 document grouping
		GroupOperation groupByProblem2 = null;
		groupByProblem2 = Aggregation.group("algorithms.full_name_ko").count().as("count");

		ProjectionOperation projection2 = Aggregation.project() // projection
				.and("$_id").as("problemType").andExclude("$_id").and("count").as("count");

		SortOperation sortByCount2 = Aggregation.sort(Direction.DESC, "count");

		LimitOperation limit2 = Aggregation.limit(5);
		
		// aggregation pipeline 구성
		Aggregation agg2 = Aggregation.newAggregation(unwindAlgorithms2
				,whereProblemId2
				,groupByProblem2
				,projection2,
				sortByCount2
				,limit2
				);

		// HashMap으로 매핑
		AggregationResults<HashMap> results2 = mongoTemplate.aggregate(agg2, "problem_level", HashMap.class);
		List<HashMap> AggregateRes = results2.getMappedResults(); // 결과
		res.put("data", AggregateRes);
		return res;
	}

	public HashMap<String, Object> getUserAlgoMistakeType(String userId) {
		HashMap<String, Object> res = new HashMap<String, Object>();
		List<HashMap> array = null;
		MatchOperation whereUserId = null;
		whereUserId = Aggregation.match(new Criteria().andOperator(Criteria.where("user_id").is(userId)));
		MatchOperation whereResult = null;
		whereResult = Aggregation.match(Criteria.where("result").ne("result-ac"));

		// 유저별로 document grouping
		GroupOperation groupByProblem = null;
		groupByProblem = Aggregation.group("user_id").addToSet("problem_id").as("problems");

		ProjectionOperation projection = Aggregation.project() // projection
				.and("$_id").as("userId").andExclude("$_id").and("problems").as("problems");

		// aggregation pipeline 구성
		Aggregation agg = Aggregation.newAggregation(whereUserId, whereResult, groupByProblem, projection);

		// HashMap으로 매핑
		AggregationResults<HashMap> results = mongoTemplate.aggregate(agg, "submit", HashMap.class);
		
		array = results.getMappedResults();
		
		List<HashMap> result = null;
		Object map = array.get(0);
		List<Integer> problems = (List<Integer>) ((HashMap<String, Object>) map).get("problems");

		UnwindOperation unwindAlgorithms2 = null;
		unwindAlgorithms2 = Aggregation.unwind("algorithms");

	
		// 배열안의 in 문제번호
		MatchOperation whereProblemId2 = null;
		whereProblemId2 = Aggregation.match(new Criteria().where("problem_id").in(problems));

		// 유저별로 document grouping
		GroupOperation groupByProblem2 = null;
		groupByProblem2 = Aggregation.group("algorithms.full_name_ko").count().as("count");

		ProjectionOperation projection2 = Aggregation.project() // projection
				.and("$_id").as("problemType").andExclude("$_id").and("count").as("count");

		SortOperation sortByCount2 = Aggregation.sort(Direction.DESC, "count");
		
		LimitOperation limit2 = Aggregation.limit(5);
		
		
		// aggregation pipeline 구성
		Aggregation agg2 = Aggregation.newAggregation(unwindAlgorithms2
				,whereProblemId2
				,groupByProblem2
				,projection2,
				sortByCount2,
				limit2
				);

		// HashMap으로 매핑
		AggregationResults<HashMap> results2 = mongoTemplate.aggregate(agg2, "problem_level", HashMap.class);
		List<HashMap> AggregateRes = results2.getMappedResults(); // 결과
		HashMap<String, Object> res2 =new HashMap<String, Object>();
		res.put("data", AggregateRes);
		return res;
	}

	public HashMap<String, Object> getUserAlgoMistakeProblem(String userId) {
		HashMap<String, Object> res = new HashMap<String, Object>();
		List<HashMap> array = null;
		MatchOperation whereUserId = null;
		whereUserId = Aggregation.match(new Criteria().andOperator(Criteria.where("user_id").is(userId)));
		MatchOperation whereResult = null;
		whereResult = Aggregation.match(Criteria.where("result").ne("result-ac"));

		// 유저별로 document grouping
		GroupOperation groupByProblem = null;
		groupByProblem = Aggregation.group("user_id").addToSet("problem_id").as("problems");

		ProjectionOperation projection = Aggregation.project() // projection
				.and("$_id").as("userId").andExclude("$_id").and("problems").as("problems");

		// aggregation pipeline 구성
		Aggregation agg = Aggregation.newAggregation(whereUserId, whereResult, groupByProblem, projection);

		// HashMap으로 매핑
		AggregationResults<HashMap> results = mongoTemplate.aggregate(agg, "submit", HashMap.class);
		
		array = results.getMappedResults();
		
		List<HashMap> result = null;
		Object map = array.get(0);
		List<Integer> problems = (List<Integer>) ((HashMap<String, Object>) map).get("problems");

		UnwindOperation unwindAlgorithms2 = null;
		unwindAlgorithms2 = Aggregation.unwind("algorithms");

	
		// 배열안의 in 문제번호
		MatchOperation whereProblemId2 = null;
		whereProblemId2 = Aggregation.match(new Criteria().where("problem_id").in(problems));

		// 유저별로 document grouping
		GroupOperation groupByProblem2 = null;
		groupByProblem2 = Aggregation.group("algorithms.full_name_ko").count().as("count");

		ProjectionOperation projection2 = Aggregation.project() // projection
				.and("$_id").as("problemType").andExclude("$_id").and("count").as("count");

		SortOperation sortByCount2 = Aggregation.sort(Direction.DESC, "count");

		// aggregation pipeline 구성
		Aggregation agg2 = Aggregation.newAggregation(unwindAlgorithms2
				,whereProblemId2
				,groupByProblem2
				,projection2,
				sortByCount2
				);

		// HashMap으로 매핑
		AggregationResults<HashMap> results2 = mongoTemplate.aggregate(agg2, "problem_level", HashMap.class);
		List<HashMap> AggregateRes = results2.getMappedResults(); // 결과
		HashMap<String, Object> res2 =new HashMap<String, Object>();
		res.put("data", AggregateRes);
		return res;
	}

	
	

	/**
	 * 그룹 VS 나 
	 * 그룹에서 사람들이 풀었던 문제들 알고분류 카운팅 / 그룹 사람 수 
	 * => 알고분류별 평균 갯수
	 * 알고분류 5개 받아서 내 평균과 비교 
	 * @param user_id
	 * @return 
	 */
	public HashMap<String, Object> GroupProblemsType(String myId, String yourId ) { //그룹 평균 많이 푼 알고리즘유형 & cnt
		HashMap<String, Object> res = new HashMap<String, Object>();
		// 1. 그룹 아이디 리스트에 있는 문제 푼것들 problems에 문제번호 넣기(개인 중복값없이)
		// 2. 알고리즘 분류 카운팅 해서 위에서 5개 자르고 res넣고, 알고리즘 분류이름 5개 리스트에넣기 
		// 3. 내가 푼문제 중복값빼고 문제 번호 받기 
		// 4. 알고리즘 카운팅해서 비교하기 
		
		
		/*
		 * db.submit.aggregate([ { $match : {user_id:{ $in: ["kistone3", "sdm821",
		 * "gudrhks2"]}}}, { $match : {result:"result-ac"} }, { $group : { "_id" :
		 * "$user_id", "problem_num" : {"$addToSet" : "$problem_id"} } } ])
		 */
		//Group group = grouprepo.findByGroupName(groupName);
		//System.out.println(groupName);
		//System.out.println(group);
		
		//System.out.println(group.getGroup_members().size());
		
		
		ArrayList<String> idTocompare=new ArrayList<String>();
		idTocompare.add(yourId);
		//System.out.println(yourId);
		
		MatchOperation matchIdList = Aggregation.match(Criteria.where("user_id").in(idTocompare));
		MatchOperation matchResult = Aggregation.match(Criteria.where("result").is("result-ac"));
		GroupOperation groupIdProblem = Aggregation.group("user_id").addToSet("problem_id").as("problem_num");
		Aggregation agg1 = Aggregation.newAggregation(matchIdList, matchResult, groupIdProblem);
		AggregationResults<HashMap> result1 = mongoTemplate.aggregate(agg1, "submit", HashMap.class);
		
		List<HashMap> list1 = result1.getMappedResults(); // 임시리스트
		List<Integer> problems = new ArrayList<Integer>(); // 개인당 중복값없는 맞은 문제 (여러번맞췄을경우 제외) => 그룹문제 
		String userids = "";
		
		for (int i = 0, size = list1.size(); i < size; i++) {
			userids = (String) ((HashMap<String, Object>)list1.get(i)).get("_id");
			Object map = list1.get(i);
			List<Integer> p = (List<Integer>) ((HashMap<String, Object>) map).get("problem_num");
			problems.addAll(p);
		}
		
		/*
		 * db.problem_level.aggregate([ { $unwind : "$algorithms"}, { $match :
		 * {problem_id : {$in : [1014, 1005, 1023]}}}, { $group : { "_id" :
		 * "$algorithms.full_name_ko", "count" : {$sum : 1} } } ])
		 */
		UnwindOperation unwindAlgorithms = Aggregation.unwind("algorithms");
		MatchOperation matchProblems = Aggregation.match(Criteria.where("problem_id").in(problems));
		GroupOperation groupAlgoNameKo = Aggregation.group("algorithms.full_name_ko").count().as("count");
		SortOperation sortCount = Aggregation.sort(Direction.DESC, "count");
		LimitOperation limitFive = Aggregation.limit(5);
		Aggregation agg2 = Aggregation.newAggregation(unwindAlgorithms, matchProblems, groupAlgoNameKo, sortCount, limitFive);
		AggregationResults<HashMap> result2 = mongoTemplate.aggregate(agg2, "problem_level", HashMap.class);
		List<HashMap> AggregateResGroup = result2.getMappedResults();
		
		//System.out.println(AggregateResGroup);
		
		for (int i = 0, people=idTocompare.size(); i < 5 ; i++) {
			int cnt = (int) AggregateResGroup.get(i).get("count");
			AggregateResGroup.get(i).replace("count", (double)cnt/people);
		}
		res.put("GroupData", AggregateResGroup);
		
		List<HashMap> list2 = result2.getMappedResults();
		
		List<String> algoList = new ArrayList<String>();
		String algoNamek = "";
		for (int i = 0; i < 5; i++) {
			algoNamek = (String) ((HashMap<String, Object>)list2.get(i)).get("_id");
			algoList.add(algoNamek);
		}
		/*
		 * db.submit.aggregate([ { $match : {user_id: "kistone3"}}, { $match : {result :
		 * "result-ac"}}, { $group : { "_id" : "", "problem_num" : {"$addToSet" :
		 * "$problem_id"} }} ])
		 */
		MatchOperation matchMyid = Aggregation.match(Criteria.where("user_id").is(myId));
		MatchOperation matchMyResult = Aggregation.match(Criteria.where("result").is("result-ac"));
		GroupOperation groupMyProblem = Aggregation.group().addToSet("problem_id").as("problem_num");
		Aggregation agg3 = Aggregation.newAggregation(matchMyid, matchMyResult, groupMyProblem);
		AggregationResults<HashMap> result3 = mongoTemplate.aggregate(agg3, "submit", HashMap.class);
		List<Integer> myProblems = null; // 내가 푼 문제 번호(중복값X)
		List<HashMap> list3 = result3.getMappedResults();
		Object map = list3.get(0);
		myProblems = (List<Integer>) ((HashMap<String, Object>) map).get("problem_num");
		
		/*
		 * db.problem_level.aggregate([ { $match : {problem_id:{$in:[1014, 1005,
		 * 1023]}}}, { $unwind : "$algorithms"}, { $match :
		 * {"algorithms.full_name_ko":{$in:["그래프 이론","비트마스킹"]}}}, { $group : { "_id" :
		 * "$algorithms.full_name_ko", "count" : {$sum : 1} } } ])
		 */
		
		MatchOperation matchMyproblems = Aggregation.match(Criteria.where("problem_id").in(myProblems));
		UnwindOperation unwindMyAlgo = Aggregation.unwind("algorithms");
		MatchOperation matchMyAlgoName = Aggregation.match(Criteria.where("algorithms.full_name_ko").in(algoList));
		GroupOperation groupMyAlgoName = Aggregation.group("algorithms.full_name_ko").count().as("count");
		Aggregation agg4 = Aggregation.newAggregation(
				matchMyproblems 
				,unwindMyAlgo
				,matchMyAlgoName
				,groupMyAlgoName
				);
		AggregationResults<HashMap> result4 = mongoTemplate.aggregate(agg4, "problem_level", HashMap.class);
		List<HashMap> AggregateResMy = result4.getMappedResults();
		res.put("MyData", AggregateResMy);
		return res;
	}

	
	
	/**
	 * 사용자가 백준 성공기록을 언어별로 sort
	 * 
	 * @param user_id
	 * @return 
	 */
	public HashMap<String, Object> getUserLanguage(String userId) {
		MatchOperation matchId = Aggregation.match(Criteria.where("user_id").is(userId));
		MatchOperation matchResult = Aggregation.match(Criteria.where("result").is("result-ac"));
		GroupOperation groupLanguage = Aggregation.group("language").count().as("count");
		SortOperation sortByCount = Aggregation.sort(Direction.DESC, "count");
		Aggregation agg = Aggregation.newAggregation(matchId, matchResult, groupLanguage, sortByCount);
		
		AggregationResults<HashMap> results = mongoTemplate.aggregate(agg, "submit", HashMap.class);
		List<HashMap> list = results.getMappedResults();
//		System.out.println(list.toString());
		
		HashMap<String, Object> res = new HashMap<String, Object>();
		for (int i = 0, size = list.size(); i < size; i++) {
			Object[] arr = list.get(i).values().toArray();
			res.put((String) arr[1], arr[0]);
		}
		
		return res;
	}

	public HashMap<String, Object> IvsYou(String myId, String yourId) {
		List<String> IdList = new ArrayList<String>();
		IdList.add(myId);
		IdList.add(yourId);
		
		/*
		 * db.submit.aggregate([ {$match : {$or:[{user_id :
		 * "kistone3"},{user_id:"sdm821"}]}} ,{$match : {result : "result-ac"}} ,{$group
		 * : {"_id" : "$user_id" ,"problems" : {"$addToSet" : "$problem_id"}}} ])
		 */
		
		MatchOperation matchId = Aggregation.match(Criteria.where("user_id").in(IdList));
		MatchOperation matchResult = Aggregation.match(Criteria.where("result").is("result-ac"));
		GroupOperation groupLanguage = Aggregation.group("user_id").addToSet("problem_id").as("problems");
		Aggregation agg1 = Aggregation.newAggregation(matchId, matchResult, groupLanguage);
		AggregationResults<HashMap> result1 = mongoTemplate.aggregate(agg1, "submit", HashMap.class);
		List<Integer> myProblemList = new ArrayList<Integer>(); //내가 푼문제
		List<Integer> yourProblemList = new ArrayList<Integer>(); // 상대가 푼문제
		String nameStr = "";
		List<HashMap> list1 = result1.getMappedResults();
		//System.out.println(list1);
		for (int i = 0, size = list1.size(); i < size; i++) {
			nameStr = (String) ((HashMap<String, Object>)list1.get(i)).get("_id");
			Object map = list1.get(i);
			if(nameStr.equals(myId)) {
				myProblemList = (List<Integer>) ((HashMap<String, Object>) map).get("problems");
			}else {
				yourProblemList = (List<Integer>) ((HashMap<String, Object>) map).get("problems");
			}
		}
		
		List<Integer> togetherList = new ArrayList<Integer>();//같이푼문제
		if(myProblemList.size()>=yourProblemList.size()) {
			for (int myval : yourProblemList) {
				for (int yourval : myProblemList) {
					if(myval==yourval) {
						togetherList.add(myval);
						break;
					}
				}
			}
		}else {
			for (int yourval : myProblemList) {
				for (int myval : yourProblemList) {
					if(myval==yourval) {
						togetherList.add(yourval);
						break;
					}
				}
			}
		}
		
		//System.out.println(togetherList.toString());
		//System.out.println(togetherList.size());
		
		for (Integer integer : togetherList) {
			
		}
		
		HashMap<String, Object> ret= new HashMap<String, Object>();
		ret.put("data",togetherList);
		return ret;
	
	}

	public HashMap<String, Object> getTextComment(String myId) {
		HashMap<String, Object> res = new HashMap<String, Object>();
		
		/*
		 * db.submit.aggregate([ {$match : {user_id : "kistone3"}} , {$group : {"_id" :
		 * "$result" , "problems" : {"$addToSet" : "$problem_id"}, "count" : {$sum :
		 * 1}}} ])
		 * 
		 */
		MatchOperation match = Aggregation.match(Criteria.where("user_id").is(myId));
		GroupOperation group = Aggregation.group("result").addToSet("problem_id").as("problems").count().as("count");
		SortOperation sort = Aggregation.sort(Direction.DESC, "count");
		Aggregation agg = Aggregation.newAggregation(match, group, sort);
		AggregationResults<HashMap> result = mongoTemplate.aggregate(agg, "submit", HashMap.class);
		res.put("UserSubmitInfo", result.getMappedResults());
		
		/*
		 * db.submit.aggregate([ {$match : {user_id : "kistone3"}} , {$group : {"_id" :
		 * "$problem_id" , "status" : {"$addToSet" : "$result"}}} ])
		 */
		group = Aggregation.group("problem_id").addToSet("result").as("status");
		agg = Aggregation.newAggregation(match, group);
		result = mongoTemplate.aggregate(agg, "submit", HashMap.class);
		List<HashMap> list = result.getMappedResults();
		List<Integer> Xproblems = new ArrayList<Integer>(); // 시도했지만 못푼문제
		
		for (int i = 0; i < list.size(); i++) {
			Object problemNumber = list.get(i).get("_id");
			Object oarr =  list.get(i).get("status");
			String str = oarr.toString();
			if(!str.contains("result-ac")) {
				Xproblems.add((int)problemNumber);
			}
		}
		int Xcnt = Xproblems.size(); // 시도했지만 못푼 문제 개수
		
		res.put("Xproblems", Xproblems);
		
		/*
		 * db.problem_level.aggregate([ { $unwind : "$algorithms"}, { $match :
		 * {problem_id : {$in : [1786, 9376, 1325, 1062, 15684, 17244]}}}, { $group : {
		 * "_id" : "$algorithms.full_name_ko", "count" : {$sum : 1} } }, { $sort :
		 * {count : -1}}, { $limit : 3} ])
		 */
		
		
		
		UnwindOperation unwind = Aggregation.unwind("algorithms");
		MatchOperation matchProblems = Aggregation.match(Criteria.where("problem_id").in(Xproblems));
		GroupOperation groupAlgoNameKo = Aggregation.group("algorithms.full_name_ko").count().as("count");
		sort = Aggregation.sort(Direction.DESC, "count");
		LimitOperation limit = Aggregation.limit(3);
		Aggregation agg2 = Aggregation.newAggregation(unwind, matchProblems, groupAlgoNameKo, sort, limit);
		result = mongoTemplate.aggregate(agg2, "problem_level", HashMap.class);
//		System.out.println(result.getMappedResults());
		res.put("UseMistakerAlgoInfo", result.getMappedResults());
		return res;
	}

	public HashMap<String, Object> ProgrammersSort(String myId) {
		
		MatchOperation matchMyId = Aggregation.match(Criteria.where("user_id").is(myId));
		MatchOperation matchResult = Aggregation.match(Criteria.where("result").is("result-ac"));
		GroupOperation groupIdProblem = Aggregation.group().addToSet("problem_id").as("problem_num");
		Aggregation agg1 = Aggregation.newAggregation(matchMyId, matchResult, groupIdProblem);
		AggregationResults<HashMap> result1 = mongoTemplate.aggregate(agg1, "submit", HashMap.class);
		
		List<HashMap> list1 = result1.getMappedResults(); // 임시리스트
		List<Integer> problems = new ArrayList<Integer>(); // 개인당 중복값없는 맞은 문제 (여러번맞췄을경우 제외) => 그룹문제 
		String userids = "";
		
		for (int i = 0, size = list1.size(); i < size; i++) {
			userids = (String) ((HashMap<String, Object>)list1.get(i)).get("_id");
			Object map = list1.get(i);
			List<Integer> p = (List<Integer>) ((HashMap<String, Object>) map).get("problem_num");
			problems.addAll(p);
		}
//		System.out.println(problems.toString());
		
//		List<String> dlatlProblems = new ArrayList<String>();
//		for (int i = 0; i < problems.size(); i++) {
//			dlatlProblems.add(String.valueOf(problems.get(i)));
//		}
		
		List<String> AlgoProgrammersList = new ArrayList<String>();
		AlgoProgrammersList.add("Hashing"); AlgoProgrammersList.add("Stack");
		AlgoProgrammersList.add("Queue");   AlgoProgrammersList.add("Sorting");
		AlgoProgrammersList.add("Bruteforcing"); AlgoProgrammersList.add("Greedy");
		AlgoProgrammersList.add("DP");		AlgoProgrammersList.add("DFS");
		AlgoProgrammersList.add("BFS");		AlgoProgrammersList.add("Binary search");
		AlgoProgrammersList.add("Graph theory"); AlgoProgrammersList.add("Graph traversal");
		
		MatchOperation matchProblems = Aggregation.match(Criteria.where("problem_id").in(problems));
		UnwindOperation unwindAlgo = Aggregation.unwind("algorithms");
		MatchOperation matchShortEn = Aggregation.match(Criteria.where("algorithms.short_name_en").in(AlgoProgrammersList));
		GroupOperation groupShortEn = Aggregation.group("algorithms.short_name_en").count().as("count");
		Aggregation agg2 = Aggregation.newAggregation(
				matchProblems 
				,unwindAlgo
				,matchShortEn
				,groupShortEn
				);
		AggregationResults<HashMap> result2 = mongoTemplate.aggregate(agg2, "problem_level", HashMap.class);
		HashMap<String, Object> res = new HashMap<String, Object>();
		res.put("Data", result2.getMappedResults());
//		System.out.println(result2.getMappedResults().toString());
//		System.out.println(res.toString());
		return res;
	}
	
	
}
