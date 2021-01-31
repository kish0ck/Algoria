package com.infinity.profile.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infinity.profile.repo.Profile;
import com.infinity.profile.service.ProfileService;
import com.infinity.profile.service.RecentSubmitRecordListService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@CrossOrigin("*")
@RestController
@Api(value = "profile", description = "profile controller")
public class ProfileController {

	@Autowired
	private ProfileService service;
	
	@Autowired
	private RecentSubmitRecordListService recentListservice;

	@GetMapping("/isUser/{id}")
	@ApiOperation("사용자의 id가 제출기록이 있는지 없는지 => boolean")
	public boolean pCisUser(@PathVariable String id) throws IOException, ParseException {
		return service.pSisUser(id);
	}
	
	@GetMapping("/find20/{id}")
	@ApiOperation("사용자의 id의 최근 20개 제출정보를 가져옴")
	public List<Profile> pCfind20(@PathVariable String id) throws IOException, ParseException {
		return service.pSfind20(id);
	}
	
	@ApiOperation("사용자 제출기록의 정보를 리턴")
	@GetMapping("/find20/{userId}/detail/{problemNo}/{submissionId}")
	public String[] pCfind20Detail( @PathVariable String userId, @PathVariable Integer problemNo,@PathVariable String submissionId) throws IOException, ParseException {
		return service.pCfind20Detail(problemNo, userId, submissionId);
	}
	
	@PostMapping("/find/submssion/list")
	public Object findManySumissionInfoBySubmissionIdList(
			@RequestBody List<Integer> submissionIdList ){
		List<Integer> arr=new ArrayList<Integer>();
		arr.addAll( (List<Integer>) submissionIdList);
		return service.getSubmissionArrayBySumbissionId(arr);
	}
	/**
	 *  유저에 대해 최근 제출한 기록 20개를 반환
	 * @param id
	 * @param pagenum
	 * @return
	 */
	@GetMapping("/recentsubmit/list/{id}/{pagenum}")
	public Object RecentSubmitRecordListService(@PathVariable String id, @PathVariable Integer pagenum){
		return recentListservice.getRecentSumbitRecordPage(id,pagenum);
	}
	@GetMapping("/recentsubmit/list/pagination/{id}")
	public Object getRecentSubmitRecordPaginationInfo(@PathVariable String id) {
		return recentListservice.getpageInfo(id);
	}
	
	/**
	 * 실시간 업데이트 기능 인데 안됨 
	 * @param id
	 * @return
	 */
	@PutMapping("/recentsubmit/list/update/{userId}")
	public Object updateUserSubmitRecord(@PathVariable String userId) {
		
		Integer UserRecentSubmissionId = recentListservice.getUSerRecentSubmissionId(userId);
		
		
		return recentListservice.getUSerRecentSubmissionId(userId);
		
	}
	
	@GetMapping("/test")
	@ApiOperation("테스트~!")
	public String test() {
		return "test";
	}
}
