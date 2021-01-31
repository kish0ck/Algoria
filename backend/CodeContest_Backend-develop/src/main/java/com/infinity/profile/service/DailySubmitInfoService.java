package com.infinity.profile.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.sql.Date;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scripting.support.StaticScriptSource;
import org.springframework.stereotype.Service;

import com.infinity.feedback.repo.Algorithm;
import com.infinity.feedback.repo.Problem;
import com.infinity.feedback.repo.ProblemLevel;
import com.infinity.profile.repo.Profile;

import ch.qos.logback.classic.Logger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DailySubmitInfoService {

	@Autowired
	private MongoTemplate mongoTemplate;

	// 깃허브 잔디밭 같이 기간에 대해 일별로 얼마나 문제를 풀었는지(제출했는지)에 대한 기록 반환
	public List<HashMap> getDailySubmitStasticsInfo(String userId, String from, String to) {
		//System.out.println("test");

		// 시작날짜를 타임스탬프로 바꾸기
		LocalDate fromDate = LocalDate.parse(from);
		// //System.out.println(fromDate);
		fromDate = fromDate.minusDays(1);

		Timestamp timestampFrom = Timestamp.valueOf(fromDate.atStartOfDay());
		Long tsFrom = timestampFrom.getTime();
		// //System.out.println(timestampFrom.getTime());

		// 끝날짜를 타임스탬프로 바꾸기
		LocalDate toDate = LocalDate.parse(to);
		toDate = toDate.plusDays(1);
		// //System.out.println(toDate)
		Timestamp timestampTo = Timestamp.valueOf(toDate.atStartOfDay());
		Long tsTo = timestampTo.getTime();

		//System.out.println(fromDate);
		//System.out.println(toDate);

		ProjectionOperation firstProjection = Aggregation.project() // projection -어떤 필드만 쓸건지 , date를 날짜로 집계하기 위한
																	// projection
				.and("user_id").as("userId").and("submission_id").as("submissionId")
				.andExpression("  divide(date,86400) - mod(divide(date,86400),1)").as("date");

		MatchOperation whereTargetUser = null; // 해당 유저에 대해서
		whereTargetUser = Aggregation.match(new Criteria().andOperator(Criteria.where("userId").is(userId)));

		MatchOperation whereDateFrom = null; // 시작일부터
		whereDateFrom = Aggregation.match(new Criteria().andOperator(Criteria.where("date").gt(tsFrom / 1000)));
		MatchOperation whereDateTo = null; // 종료일까지
		whereDateTo = Aggregation.match(new Criteria().andOperator(Criteria.where("date").lt(tsTo / 1000)));

		//System.out.println(tsFrom);

		GroupOperation groupByDate = null; // 날짜별로 document grouping

		groupByDate = Aggregation.group("date").push("submissionId").as("submit_history").count().as("value");

		SortOperation sorting = null;
		sorting = Aggregation.sort(Sort.Direction.ASC, "date"); // 비교할 속성에 대해 정렬

		Aggregation agg = Aggregation.newAggregation( // aggregation pipeline 구성
				whereDateFrom, whereDateTo, firstProjection, whereTargetUser, groupByDate, sorting

		);
		AggregationResults<HashMap> results = mongoTemplate.aggregate(agg, "submit", HashMap.class); // HashMap으로 매핑

		List<HashMap> AggregateRes = results.getMappedResults(); // 결과

		//System.out.println(AggregateRes);

		for (HashMap<String, Object> dayInfo : AggregateRes) {

			Long timestamp = ((Double) dayInfo.get("_id")).longValue() * 86400;
			// new Timestamp(timestamp ).toLocalDateTime().toLocalDate()
			//System.out.println(timestamp);
			dayInfo.put("date", timestamp);
		}

		return AggregateRes;

	}

	
	
	/**
	 * 깃허브 잔디밭 같이 기간에 대해 일별로 얼마나 문제를 풀었는지(제출했는지)에 대한 기록 반환 타임스탬프 기반, vue 컴포넌트에서 쓸
	 * json 구조로 변경
	 * 
	 * @param userId
	 * @param from
	 * @param to
	 * @return
	 */
	public HashMap<String, Object> getDailySubmitStasticsInfoV2(String userId, Long from, Long to) {

		Timestamp timestampFrom = new Timestamp(from);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestampFrom.getTime());
		cal.add(Calendar.DAY_OF_MONTH, -1);
		timestampFrom = new Timestamp(cal.getTime().getTime());
		Long tsFrom = timestampFrom.getTime();
		// //System.out.println(timestampFrom.getTime());

		// 끝날짜를 타임스탬프로 바꾸기
		Timestamp timestampTo = new Timestamp(to);
		cal = Calendar.getInstance();
		cal.setTimeInMillis(timestampTo.getTime());
		cal.add(Calendar.DAY_OF_MONTH, +1);
		timestampTo = new Timestamp(cal.getTime().getTime());
		Long tsTo = timestampTo.getTime();

		ProjectionOperation firstProjection = Aggregation.project() // projection -어떤 필드만 쓸건지 , date를 날짜로 집계하기 위한
																	// projection
				.and("user_id").as("userId").and("submission_id").as("submissionId")
				.andExpression("  divide(date,86400) - mod(divide(date,86400),1)").as("date");

		MatchOperation whereTargetUser = null; // 해당 유저에 대해서
		whereTargetUser = Aggregation.match(new Criteria().andOperator(Criteria.where("userId").is(userId)));

		MatchOperation whereDateFrom = null; // 시작일부터
		whereDateFrom = Aggregation.match(new Criteria().andOperator(Criteria.where("date").gt(tsFrom / 1000)));
		MatchOperation whereDateTo = null; // 종료일까지
		whereDateTo = Aggregation.match(new Criteria().andOperator(Criteria.where("date").lt(tsTo / 1000)));

		//System.out.println(tsFrom);

		GroupOperation groupByDate = null; // 날짜별로 document grouping

		groupByDate = Aggregation.group("date").push("submissionId").as("submit_history").count().as("cnt");

		Aggregation agg = Aggregation.newAggregation( // aggregation pipeline 구성
				whereDateFrom, whereDateTo, firstProjection, whereTargetUser, groupByDate);
		AggregationResults<HashMap> results = mongoTemplate.aggregate(agg, "submit", HashMap.class); // HashMap으로 매핑
		List<HashMap> AggregateRes = results.getMappedResults(); // aggregation 결과

		HashMap<String, Object> resultMap = new HashMap<String, Object>(); //json으로 반환할 데이터를 담는 hashmap 
		
		
		HashMap<String, Integer> dateDataMap = new HashMap<String, Integer>();
		HashMap<String, Object> submitHistoryMap = new HashMap<String, Object>();
		
		
		for (HashMap<String, Object> dayInfo : AggregateRes) {
			
			Long timestamp = ((Double) dayInfo.get("_id")).longValue() * 86400;
			Integer cnt = (Integer) dayInfo.get("cnt");
			
			HashMap<String, Object> DaySubmitHistory=new HashMap<String, Object>();
			

			DaySubmitHistory.put( "submit_history", dayInfo.get("submit_history")  );
			
			dateDataMap.put( timestamp.toString(), cnt);
			submitHistoryMap.put(timestamp.toString(), DaySubmitHistory);

		}
		resultMap.put("dateData",dateDataMap);
		resultMap.put("submit_history",submitHistoryMap);
		return resultMap ;

	}

}
