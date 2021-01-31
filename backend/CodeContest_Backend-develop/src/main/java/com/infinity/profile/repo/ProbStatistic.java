package com.infinity.profile.repo;

import lombok.Data;

@Data
public class ProbStatistic {

	Integer problemId;//문제번호
	String language; //제출언어
	Double memory; //메모리평균
	Double time;//시간평균
	Integer codeLength; //코드길이평균
	Double s;
	Double submitCnt;
	

}

