package com.infinity.algorecommend.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.convert.MongoTypeMapper;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import com.infinity.feedback.repo.ProblemLevel;

import io.swagger.annotations.ApiOperation;

import com.infinity.feedback.repo.Algorithm;
import com.infinity.feedback.repo.Problem;

@Service
public class ProblemRecommendServce {

	@Autowired
	MongoTemplate mongotemplate; 
	
	/**
	 * 해당 알고리즘 번호에 대응하는 알고리즘 문제중 정답률과 맞은 사람 수가 많은 상위 5개의 알고리즘 문제를 반환한다
	 * @param algorithmId
	 * @return
	 */
	@ApiOperation(value = "해당 알고리즘 번호에 대응하는 알고리즘 문제중 정답률과 맞은 사람 수가 많은 상위 5개의 알고리즘 문제를 반환한다")
	public HashMap<String, Object> recommendAlgoProblem(Integer algorithmId ){
		
		// 해당 알고리즘에 해당하는 문제들만
		MatchOperation whereAlgorithmId = null;
		whereAlgorithmId = Aggregation.match(new Criteria().andOperator(Criteria.where("algorithms.algorithm_id").is(algorithmId)));
		
	
		//  document grouping
		GroupOperation groupByProblem = null;
		groupByProblem = Aggregation.group().push("problem_id").as("problems").addToSet("algorithms").as("algorithm").push("level").as("level");

		//ProjectionOperation projection = Aggregation.project() // projection
				//.and("$_id").as("userId").andExclude("$_id").and("problems").as("problems");

		// aggregation pipeline 구성
		Aggregation agg = Aggregation.newAggregation(whereAlgorithmId, groupByProblem);

		// HashMap으로 매핑
		AggregationResults<HashMap> results = mongotemplate.aggregate(agg, "problem_level",HashMap.class);
		//System.out.println(results.getMappedResults().get(0));
		
		//System.out.println(results.getMappedResults().get(0).get("algorithm"));
	
		//랭크(레벨) 순서랑 문제 번호랑 순서가 같음
		//problem에 레벨(랭크)를 더하기 위한 join 연산
		List<Integer> problemIdList = (List<Integer>) results.getMappedResults().get(0).get("problems");
		List<Integer> problemlevelList = (List<Integer>) results.getMappedResults().get(0).get("level");
		HashMap<Integer, Integer> joinmap =new HashMap<Integer, Integer>(0);
		for (int i = 0; i < problemIdList.size(); i++) {
			joinmap.put( problemIdList.get(i), problemlevelList.get(i)  );
		}
		
		// 배열안의 in 문제번호
		MatchOperation whereProblemId = null;
		//System.out.println(problemIdList);
		whereProblemId = Aggregation.match(new Criteria().where("problem_id").in(problemIdList));

		
		//문제푼수 내림차순 정렬
		SortOperation sorting = null;
		sorting = Aggregation.sort(Sort.Direction.DESC, "n_success"); 
		LimitOperation limitProblemNum = Aggregation.limit(10);
		
		agg = Aggregation.newAggregation(whereProblemId, sorting,limitProblemNum);
		
		AggregationResults<Problem> res2 = mongotemplate.aggregate(agg, "problem", Problem.class);

		List<Problem> recommendProblemList=(List<Problem>) res2.getMappedResults();
		for (Problem problem : recommendProblemList) {
			problem.setLevel(joinmap.get(problem.getProblem_id()));
		}
		
		HashMap<String, Object> resultmap = new HashMap<String, Object>();
		resultmap.put("problems",recommendProblemList );
		resultmap.put("algorithm_id", algorithmId);
		
		
		return resultmap;
	
		
		/* TODO : 나중에 POJO로 변경 */
//		AggregationResults<UserProblemList> test = mongoTemplate.aggregate(agg, "submit", UserProblemList.class);
	
	}

	
	@ApiOperation(value = "해당 알고리즘 번호에 대응하는 알고리즘 문제중 유저가 푼 문제들의 평균 난이도와 같거나 이상인 문제를 추천")
	public HashMap<String, Object> recommendAlgoProblemByUserData(Integer algorithmId, ArrayList<Integer> userSolvedProblemList ){
		
		// 해당 알고리즘에 해당하는 문제들만
		MatchOperation whereAlgorithmId = null;
		whereAlgorithmId = Aggregation.match(new Criteria().andOperator(Criteria.where("algorithms.algorithm_id").is(algorithmId)));
		
	
		//  document grouping
		GroupOperation groupByProblem = null;
		groupByProblem = Aggregation.group().push("problem_id").as("problems").addToSet("algorithms").as("algorithm").push("level").as("level");

		//ProjectionOperation projection = Aggregation.project() // projection
				//.and("$_id").as("userId").andExclude("$_id").and("problems").as("problems");

		// aggregation pipeline 구성
		Aggregation agg = Aggregation.newAggregation(whereAlgorithmId, groupByProblem);

		// HashMap으로 매핑
		AggregationResults<HashMap> results = mongotemplate.aggregate(agg, "problem_level",HashMap.class);
		//System.out.println(results.getMappedResults().get(0));
		
		//System.out.println(results.getMappedResults().get(0).get("algorithm"));
	
		//랭크(레벨) 순서랑 문제 번호랑 순서가 같음
		//problem에 레벨(랭크)를 더하기 위한 join 연산
		List<Integer> problemIdList = (List<Integer>) results.getMappedResults().get(0).get("problems");
		List<Integer> problemlevelList = (List<Integer>) results.getMappedResults().get(0).get("level");
		HashMap<Integer, Integer> joinmap =new HashMap<Integer, Integer>(0);
		for (int i = 0; i < problemIdList.size(); i++) {
			joinmap.put( problemIdList.get(i), problemlevelList.get(i)  );
		}
		
		
		
		// 해당 알고리즘에 속하면서 유저가 푼 문제의 랭크 합
		Integer totalAlgorithmRankSum =0;
		Integer  solvedNum =0;
		for(Integer problem_id: userSolvedProblemList) {
			//유저가 푼 문제번호가 해당 알고리즘에 속하는지 판단
			if (joinmap.containsKey(problem_id)) {
				//평균 난이도(랭크)를 구하기 위해 푼 문제의 랭크 합을 저장
				totalAlgorithmRankSum+=joinmap.get(problem_id); 
				//System.out.println("solved:"+problem_id.toString());
				joinmap.put(problem_id, -1);
				solvedNum++;
			}
		}

		
		//해당 알고리즘 유형의 문제들 중 유저가 푼 문제 제외, 평균랭크 이상의 문제번호만 저장되는 리스트
		ArrayList<Integer> filteredProblemIdList =new ArrayList<Integer>();
		//유저가 푼 특정 알고리즘 유형의 문제들 평균 난이도(랭크)
		Double avgRank = 0.0;
		
		//해당 유형의 알고리즘 문제를 풀어본적 없을때는  유저 데이터 기반 문제 추천을 하지 않고 문제 푼 사람 수로 추천한다
		if (solvedNum==0) {
			
			for (Integer problem_id : problemIdList) {
				if (joinmap.get(problem_id) >= 0) {
					filteredProblemIdList.add(problem_id);
				}
			}
			
		}
		else {
			avgRank=  ((double)totalAlgorithmRankSum/solvedNum);
			//System.out.println("avgrank"+avgRank);
			
			for (Integer problem_id : problemIdList) {
				if (joinmap.get(problem_id) >= avgRank*0.8   ) {
					filteredProblemIdList.add(problem_id);
				}
				
			}
			
		}
				
		
		//System.out.println(filteredProblemIdList);
		
		
		
		// 배열안의 in 문제번호
		MatchOperation whereProblemId = null;
		//System.out.println(problemIdList);
		whereProblemId = Aggregation.match(new Criteria().where("problem_id").in(filteredProblemIdList));

		
		//문제푼수 내림차순 정렬
		SortOperation sorting = null;
		sorting = Aggregation.sort(Sort.Direction.DESC, "n_success"); 
		LimitOperation limitProblemNum = Aggregation.limit(20);
		
		agg = Aggregation.newAggregation(whereProblemId, sorting,limitProblemNum);
		
		AggregationResults<Problem> res2 = mongotemplate.aggregate(agg, "problem", Problem.class);

		List<Problem> recommendProblemList=(List<Problem>) res2.getMappedResults();
		for (Problem problem : recommendProblemList) {
			problem.setLevel(joinmap.get(problem.getProblem_id()));
		}
		
		HashMap<String, Object> resultmap = new HashMap<String, Object>();
		resultmap.put("problems",recommendProblemList );
		resultmap.put("algorithm_id", algorithmId);
		
		
		return resultmap;
	
		
		/* TODO : 나중에 POJO로 변경 */
//		AggregationResults<UserProblemList> test = mongoTemplate.aggregate(agg, "submit", UserProblemList.class);
	
	}
	
	
	//알고리즘 분류정보 목록를 {태그이름,태그정보,알고리즘번호}의 태그정보 리스트로 반환한다
	public List<HashMap<String, String>> getAlgorithmTags() {
		// TODO Auto-generated method stub
		
		List<HashMap<String, String>> tags=new ArrayList<HashMap<String,String>>();
		//모든 알고리즘 분류 목록을 불러온다
		List<Algorithm> algolist = mongotemplate.findAll(Algorithm.class,"algorithm_list");
		for (Algorithm al : algolist) {
			
				HashMap<String, String> keyVal =new HashMap<String, String>();
				keyVal.put("tagname", al.getFull_name_ko());
				keyVal.put("taginfo", al.stringfy());
				keyVal.put("algorithm_id", al.getAlgorithm_id());
				tags.add(keyVal);
				
		}
		
		return tags;
	}
	
	//해당 id의 알고리즘 태그정보를 {태그이름,태그정보,알고리즘번호} 반환한다
	public HashMap<String, String> getAlgorithmTagByAlgorithmId(Integer algorithmId) {
		// TODO Auto-generated method stub
		
		List<HashMap<String, String>> tags=new ArrayList<HashMap<String,String>>();
		//모든 알고리즘 분류 목록을 불러온다
		
		Criteria crieria =new Criteria("algorithm_id");
		crieria.is(algorithmId);//해당 알고리즘 번호인지 
		
		Query query =new Query(crieria);
		
		Algorithm algo = mongotemplate.findOne(query, Algorithm.class,"algorithm_list");
		
				HashMap<String, String> keyVal =new HashMap<String, String>();
				keyVal.put("tagname", algo.getFull_name_ko());
				keyVal.put("taginfo", algo.stringfy());
				keyVal.put("algorithm_id", algo.getAlgorithm_id());
		return keyVal;
	}
	
	/**
	 * 
	 * @return
	 */
	
	public Object getRecentMostTryingProblems( Long from, Long to) {

		Timestamp timestampFrom = new Timestamp(from);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestampFrom.getTime());
		cal.add(Calendar.SECOND, -1);
		timestampFrom = new Timestamp(cal.getTime().getTime());
		Long tsFrom = timestampFrom.getTime();
		// //System.out.println(timestampFrom.getTime());

		// 끝날짜를 타임스탬프로 바꾸기
		Timestamp timestampTo = new Timestamp(to);
		cal = Calendar.getInstance();
		cal.setTimeInMillis(timestampTo.getTime());
		cal.add(Calendar.SECOND, +1);
		timestampTo = new Timestamp(cal.getTime().getTime());
		Long tsTo = timestampTo.getTime();

		ProjectionOperation firstProjection = Aggregation.project() // projection -어떤 필드만 쓸건지
				.and("user_id").as("userId").and("submission_id").as("submissionId").and("problem_id").as("problemId")
				.and("data").as("date");

		
		
		MatchOperation whereDateFrom = null; // 시작일부터
		whereDateFrom = Aggregation.match(new Criteria().andOperator(Criteria.where("date").gt(tsFrom / 1000)));
		MatchOperation whereDateTo = null; // 종료일까지
		whereDateTo = Aggregation.match(new Criteria().andOperator(Criteria.where("date").lt(tsTo / 1000)));
		
		SortOperation sorting = null;
		sorting = Aggregation.sort(Sort.Direction.DESC, "trycount"); 
		
		
		//System.out.println(tsFrom);

		GroupOperation groupByProblem = null; // 해당 기간동안 푼 문제별로 group
		groupByProblem = Aggregation.group("problemId").count().as("trycount");
		Aggregation agg = Aggregation.newAggregation( // aggregation pipeline 구성
				whereDateFrom, whereDateTo, firstProjection,groupByProblem,sorting);
		AggregationResults<HashMap> results = mongotemplate.aggregate(agg, "submit", HashMap.class); // HashMap으로 매핑
		
		List<HashMap> AggregateRes = results.getMappedResults(); // aggregation 결과
		AggregateRes =AggregateRes.subList(0,50); //개수 50개로 제한
		
		ArrayList<Integer> 	problemIdList =new ArrayList<Integer>();
		for (HashMap p : AggregateRes) {
			problemIdList.add(  (int)p.get("_id"));
		}
		
		
		MatchOperation whereProblemId = null;
		whereProblemId = Aggregation.match(new Criteria().where("problem_id").in(problemIdList));
		agg = Aggregation.newAggregation(whereProblemId);
		
		//유저들이 많이 푼 문제들 목록
		AggregationResults<Problem> resProblems = mongotemplate.aggregate(agg, "problem", Problem.class);
		//return res2.getMappedResults();
	
		agg= Aggregation.newAggregation(whereProblemId);
		//유저들이 많이 푼 문제들의 레벨 목록 
		AggregationResults<ProblemLevel> resProblemLevels = mongotemplate.aggregate(agg, "problem_level", ProblemLevel.class);
		
		
		List<Problem> resultList =resProblems.getMappedResults();
		List<ProblemLevel> levelList =  resProblemLevels.getMappedResults();
		
		
		for (int i = 0; i < resultList.size(); i++) {
			try {
				resultList.get(i).setLevel(  Integer.parseInt( levelList.get(i).getLevel())   );
			} catch (Exception e) {
				resultList.get(i).setLevel(0);
				continue;
			}
		}
		
		
		return resultList;
		

	}

	
	

}
