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
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import com.infinity.feedback.repo.ProblemLevel;
import com.infinity.profile.repo.Profile;
import com.infinity.profile.repo.SubmitRepo;
import com.infinity.algorecommend.controller.RecommendProblemController;
import com.infinity.algorecommend.service.ProblemRecommendServce;
import com.infinity.feedback.repo.Algorithm;
import com.infinity.feedback.repo.Problem;
import org.springframework.data.domain.Sort;
/**
 * 20개 최근 전적 리스트 반환하는 서비스 : find20 개선판 pagenum에 해당하는 page만큼의 전적 수를 반환!
 * 
 * @author 신동민
 *
 */
@Service
public class RecentSubmitRecordListService {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private SubmitRepo repo;
	

	private final int recordPerPage = 20;

	/*
	 * 유저의 가장 최근 제출번호를 가져온다
	 */
	public Integer getUSerRecentSubmissionId(String userId) {

		ProjectionOperation projection = Aggregation.project() // projection
				.and("user_id").as("userId").and("submission_id").as("submissionId");

		MatchOperation whereUserId = null;// 해당 유저 제출기록만
		whereUserId = Aggregation.match(new Criteria().andOperator(Criteria.where("userId").is(userId)));

		GroupOperation groupRecentSubmissionId = null; // 가장 최근 제출번호를 구한다
		groupRecentSubmissionId = Aggregation.group().max("submissionId").as("recentSumissionId");

		Aggregation aggCountUserNum = Aggregation.newAggregation( // aggregation pipeline 구성
				projection, whereUserId, groupRecentSubmissionId);
		
		AggregationResults<HashMap> aggRecentSubmissionId = mongoTemplate.aggregate(aggCountUserNum, "submit",
				HashMap.class); // HashMap으로 매핑
		List<HashMap> AggregateRes = aggRecentSubmissionId.getMappedResults(); // 결과
		return (Integer) AggregateRes.get(0).get("recentSumissionId");

	}

	// pagination 관련정보 (전체 문서 개수, 마지막 페이지 )를 반환한다
	public HashMap<String, Long> getpageInfo(String user_id) {
		String qstr = String.format("{ user_id : { $eq : '%s' } }", user_id);
		BasicQuery query = new BasicQuery(qstr);
		// 마지막 페이지 번호를 찾기 위해 유저의 제출 기록 개수를 카운트
		Long totalRecordsNum = mongoTemplate.count(query, Profile.class, "submit");
		Long lasgPageNum = (long) Math.ceil((double) totalRecordsNum / 20);
		HashMap<String, Long> paginationInfo = new HashMap<String, Long>();
		paginationInfo.put("last_page_num", lasgPageNum);
		paginationInfo.put("total_record_num", totalRecordsNum);
		return paginationInfo;
	}

	/**
	 * 해당 user의 최근 20개 제출기록을 반환하는 함수 (problem , problem_level, submit document를
	 * join)
	 * 
	 * @param user_id
	 * @param pagenum
	 * @return
	 */
	public List<Profile> getRecentSumbitRecordPage(String user_id, Integer pagenum) {
		String qstr = String.format("{ user_id : { $eq : '%s' } }", user_id);
		BasicQuery query1 = new BasicQuery(qstr);

		Integer iPage = pagenum; // 조회할 페이지

		Pageable pageable = PageRequest.of(iPage - 1, recordPerPage);
		Sort sort =Sort.by(Sort.Order.desc("submission_id"));
				
		
		List<Profile> res = mongoTemplate.find(query1.with(sort).with(pageable), Profile.class, "submit");

		for (int i = 0; i < res.size(); i++) {
			try {
			
			Profile pf = res.get(i);
			Integer problem_num = res.get(i).getProblem_id(); // 제출한 문제들중 한 문제에 대해

			Query queryProblemLevel = new Query();
			queryProblemLevel.addCriteria(Criteria.where("problem_id").is(problem_num));
			ProblemLevel problemLevel = mongoTemplate.findOne(queryProblemLevel, ProblemLevel.class, "problem_level");
			
			// 문제에 대한 정보 : 문제레벨, 이름, 알고리즘 분류 등을 가져온다

			
			// 문제에 대한 정보 : 문제 이름 등을 가져온다 - 개선할사항/이슈 : 이거 두개 합쳐서 db에 저장하면 안되나?
			Query getProblemInfo = new Query();
			getProblemInfo.addCriteria(Criteria.where("problem_id").is(problem_num));
			Problem problem = mongoTemplate.findOne(getProblemInfo, Problem.class, "problem");
			
			res.get(i).setProblem_title(problem.getProblem_title());
			
			// 문제 레벨 (\solvedac 레벨) 설정
			res.get(i).setLevel(problemLevel.getLevel());
			// 문제 태그(알고리즘 분류) 설정
			res.get(i).setClassification((ArrayList<Algorithm>) problemLevel.getAlgorithms());
			}
			catch (Exception e) {
				// TODO: handle exception
			}

		}

		return res;
	}

}
