package com.infinity.feedback.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infinity.feedback.repo.Group;
import com.infinity.feedback.service.SortService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@CrossOrigin(origins = "*")
@Api(value = "sort", description = "feedback sort controller")
@RestController
public class SortController {

	@Autowired
	SortService service;

	@ApiOperation(value = "많이 푼 문제 분류")
	@GetMapping("/user/{userId}/getUserAlgoType")
	public HashMap<String, Object> getUserAlgoType(@PathVariable String userId) {
		HashMap<String, Object> result = service.getUserAlgoType(userId);
		return result;
	}

	@ApiOperation(value = "그룹 VS 나 ")
	// json형식으로 user_id : [] , vsUser : [] 받아서 vsUser의 많이 푼 알고리즘 문제 기준으로
	@RequestMapping(method = RequestMethod.GET, path = "/user/{userId}/GroupProblemsType/{groupName}")
	public HashMap<String, Object> GroupProblemsType(@PathVariable String groupName,
			 @PathVariable String userId) {
//		System.out.println(groupName);
//		System.out.println(useridlist);
		//JSONObject jobj = new JSONObject();
		//Group v = new Group();
		//jobj.put("group", groupName);
		//jobj.put("VsUserIds", useridlist);
		//v.setGroup_name((String) jobj.get("group"));
		//v.setGroup_members((List<String>) jobj.get("VsUserIds"));
		HashMap<String, Object> result = service.GroupProblemsType( userId, groupName );
		return result;
	}

	@ApiOperation(value = "많이 실수하는 분류(시도횟수많은순)")
	@GetMapping("/user/{userId}/problemsMistakeType")
	public HashMap<String, Object> getUserAlgoMistakeType(@PathVariable String userId) {
		HashMap<String, Object> result = service.getUserAlgoMistakeType(userId);
		return result;
	}

	@ApiOperation(value = "많이 실수한 문제(시도횟수많은순)")
	@GetMapping("/user/{userId}/getUserAlgoMistakeProblem")
	public HashMap<String, Object> getUserAlgoMistakeProblem(@PathVariable String userId) {
		HashMap<String, Object> result = service.getUserAlgoMistakeProblem(userId);
		return result;
	}
	
	@ApiOperation(value = "사용자 언어별 문제 정답 수")
	@GetMapping("/user/{userId}/getUserLanguage")
	public HashMap<String, Object> getUserLanguage(@PathVariable String userId) {
		HashMap<String, Object> result = service.getUserLanguage(userId);
		return result;
	}
	
	@ApiOperation(value = "나 VS 너")
	@GetMapping("/user/{myId}/vs/{yourId}")
	public HashMap<String, Object> IvsYou(@PathVariable String myId, @PathVariable String yourId) {
		HashMap<String, Object> result = service.IvsYou(myId, yourId);
		return result;
	}
	
	@ApiOperation(value = "총평")
	@GetMapping("/user/{myId}/GeneralComment")
	public HashMap<String, Object> getTextComment(@PathVariable String myId) {
		HashMap<String, Object> result = service.getTextComment(myId);
		return result;
	}
	
	@ApiOperation(value = "피드백:코딩테스트 고득점 Kit")
	@GetMapping("/user/{myId}/ProgrammersSort")
	public HashMap<String, Object> ProgrammersSort(@PathVariable String myId) {
		HashMap<String, Object> result = service.ProgrammersSort(myId);
		return result;
	}
}
