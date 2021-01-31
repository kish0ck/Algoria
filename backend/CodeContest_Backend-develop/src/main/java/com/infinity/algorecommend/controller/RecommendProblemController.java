package com.infinity.algorecommend.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infinity.feedback.repo.ProblemLevel;
import com.infinity.algorecommend.service.ProblemRecommendServce;
import com.infinity.feedback.repo.Problem;
import com.infinity.profile.repo.Profile;
import com.infinity.profile.service.ProfileService;
import com.infinity.profile.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*")
@Api(value = "feedback", description = "recommend problems controller")
public class RecommendProblemController {

	@Autowired
	ProblemRecommendServce service;
	@Autowired 
	UserService userService;
	
	@ApiOperation("문제와 유사한 문제를 추천해주는 api : 유저기반 아님")
	@GetMapping("/feedback/recommend/{algorithmId}")
	public HashMap<String, Object> recommendSolvableProblems(@PathVariable Integer algorithmId) throws IOException, ParseException {
		return service.recommendAlgoProblem(algorithmId);
	}
	@ApiOperation("특정 유저가 푼 해당 알고리즘 문제들의 평균 난이도 이상의 문제들을 추천해주는 api")
	@GetMapping("/feedback/recommend/user/algorithm/{userId}/{algorithmId}")
	public HashMap<String, Object> recommendSolvableProblemsByUserData(@PathVariable String userId, @PathVariable Integer algorithmId) throws IOException, ParseException {
		//유저가 푼 문제번호 목록 반환
		ArrayList<Integer> userSolvedProblemList =(ArrayList<Integer>) userService.getUserProblemList(userId, null,null).get("problems");
		return service.recommendAlgoProblemByUserData(algorithmId, userSolvedProblemList);
	}
	
	
	@ApiOperation("알고리즘 전체 목록 반환  api")
	@GetMapping("recommend/algorithms/tags")
	public List<HashMap<String, String>> getEntireAlgorithmTags() throws IOException, ParseException {
		return service.getAlgorithmTags();
	}
	
	@ApiOperation("최근 특정 기간동안(from-to) 사람들이 많이 풀고 있는 알고리즘 문제들 반환")
	@GetMapping("recommend/problem/trymost/{from}/{to}")
	public Object recommendRecentMostTryProblems( @PathVariable Long from,   @PathVariable  Long to   ){
		return service.getRecentMostTryingProblems(from, to);
	}
	
	
	
}
