package com.infinity.profile.controller;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infinity.profile.service.UserService;

import io.swagger.annotations.ApiOperation;

/***
 * 
 * FileName : UserController.java
 * 
 * @version : 1.0
 * @author : ShimHyeongGwan(Xxings)
 * @Comment : 각종 통계(특정 유저에 대한 문제의 기록 / 분류에 대해서 restapi를 담당하는 controller)
 */
@CrossOrigin(origins = "*")
@RestController
public class UserController {

	private final static boolean notFROM = true;
	private final static boolean notTO = false;

	private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserService service;

	/*
	 * 테스트케이스 작동여부 출력
	 */
	@ApiOperation(value = "테스트케이스 작동여부 출력")
	@GetMapping("/user/test")
	public HashMap<String, Object> getControllerTest() {

		return null;
	}

	/*
	 * 유저의 백준 id / 문제수 / 랭크 호출
	 */
	@ApiOperation(value = "유저의 백준 id / 문제수 / 랭크 호출")
	@GetMapping("/user/{userId}")
	public HashMap<String, Object> getUserInfo(@PathVariable String userId) {

		return service.sendGetData(userId);
	}

	/**
	 * 유저가 각 기간 당, 알고리즘 푼 문제 번호를 호출한다.
	 * 
	 * @author Xxings
	 * @param userId : 타겟 유저 아이디
	 * @return
	 */
	@ApiOperation(value = "유저가 각 기간 당, 알고리즘 푼 문제 번호를 호출한다. ")
	@GetMapping("/user/{userId}/problems")
	public HashMap<String,Object> getUserProblemList(@PathVariable String userId,
			@RequestParam(value = "from", required = false) String from,
			@RequestParam(value = "to", required = false) String to) {
		
		return service.getUserProblemList(userId, from, to);
	}



	
	
		

}
