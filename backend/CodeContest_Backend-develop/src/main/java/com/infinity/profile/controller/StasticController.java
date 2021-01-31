package com.infinity.profile.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infinity.profile.repo.Profile;
import com.infinity.profile.service.DailySubmitInfoService;
import com.infinity.profile.service.ProfileService;
import com.infinity.profile.service.StasticService;

import io.swagger.annotations.ApiOperation;

/*
 * 각종 통계(특정 문제에 대한 유저들 평균 기록, 등의 정보를 제공하는 restapi를 담당하는 controller)
 */
@CrossOrigin(origins = "*")
@RestController
public class StasticController {
	@Autowired
	private StasticService service;
	@Autowired
	private DailySubmitInfoService dailySubmintInfoservice;
	
	/*
	 * 한 문제에 대한 특정 유저 평균 기록을 제공하는 api
	 */
	@ApiOperation("한 문제에 대한 특정 유저 평균 기록을 제공하는 api")
	@GetMapping("/stastic/problem/user/{userId}/{problemId}/{codelang}")
	public HashMap<String, Object> 
	getProblemUserStasticsInfo(@PathVariable Integer problemId, @PathVariable String userId, @PathVariable String codelang ){
		return service.getProblemUserStasticsInfo(problemId, userId, codelang);
	}
	/*
	 * 한 문제를 푼 전체 유저 평균 기록을 제공하는 api
	 */
	@ApiOperation("한 문제를 푼 전체 유저 평균 기록을 제공하는 api")
	@GetMapping("/stastic/problem/avguser/{problemId}/{codelang}")
	public HashMap<String, Object> getProblemUserAvgStasticsInfo(@PathVariable Integer problemId,@PathVariable String codelang ){
		return service.getProblemStasticsInfo(problemId, codelang);
	}
	
	/*
	 * 한 문제를 푸는데 사용한 언어 통계
	 */
	@ApiOperation("한 문제를 푸는데 사용한 언어 통계")
	@GetMapping("/stastic/problem/distribution/codelang/{problemId}")
	public HashMap<String, Object> getProblemSubmitLanguageStasticsInfo(@PathVariable Integer problemId ){
		return service.getProblemSubmitCodeLanguageStasticsInfo(problemId);
	}
	
	/**
	 * 일별 제출기록을 반환하는 api - 깃허브 잔디밭같은거
	 * @param userId : 타겟 유저 아이디
	 * @param from : 시작날짜 포맷 :yyyy-mm-dd
	 * @param to : 끝날짜 포맷 :yyyy-mm-dd
	 */
	@ApiOperation("일별 제출기록을 반환하는 api - 깃허브 잔디밭같 [ LIST ]")
	@GetMapping("/stastic/dailysubmit/{userId}/{from}/{to}")
	public List<HashMap> getDailySubmitInfo(@PathVariable String userId, @PathVariable String from,   @PathVariable String to   ){
		return dailySubmintInfoservice.getDailySubmitStasticsInfo(userId, from, to);
	}
	
	@ApiOperation("v2 - 일별 제출기록을 반환하는 api - 깃허브 잔디밭같은거 [JSON]")
	@GetMapping("/stastic/dailysubmit/v2/{userId}/{from}/{to}")
	public HashMap<String, Object> getDailySubmitInfo(@PathVariable String userId, @PathVariable Long from,   @PathVariable  Long to   ){
		return dailySubmintInfoservice.getDailySubmitStasticsInfoV2(userId, from, to);
	}
	
	/**
	 * 
	 * @param userId
	 * @param problemId
	 * @param attr
	 * @param codelang
	 * @return
	 */
	
	@GetMapping("/stastic/problem/rank/{userId}/{problemId}/{attr}/{codelang}")
	public HashMap<String, Object> getProblemUser(@PathVariable String userId,@PathVariable Integer problemId,@PathVariable String attr,  @PathVariable String codelang ){
		return service.getProblemUserSubmitResultRank(userId,problemId, codelang, attr);
	}

	
	
	
	@ApiOperation("제출기록의 메모리나 시간이 해당 문제를 푼 전체 유저 대비 상위  몇%인지 알려주는 api")
	@GetMapping("/stastic/user/submit/rank/{submissionId}/{attr}/")
	public HashMap<String, Object> getUserSubmitResultRank(@PathVariable Integer submissionId,@PathVariable String attr ){
		return service.getUserSubmitResultRank( submissionId, attr);
	}
	
	
	
	
}
