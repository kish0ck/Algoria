package com.infinity.profile.repo;

import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.infinity.feedback.repo.Algorithm;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Document(collection = "submit")
@Getter
@Setter
@ToString
public class Profile {

	@Id
	private String _id;
	
	private String level=""; // 난이도
	private ArrayList<Algorithm> classification; // 알고분류
	private String problem_title=""; // 문제제목
	
	private String submission_id=""; //제출번호
	private String user_id=""; // 유저 아이디
	private Integer problem_id=0; //문제 번호
	private String result; // 성공여부
	private String memory="0"; // 메모리 KB
	private String time="0"; // 시간 ms
	private String language=""; // 언어
	private String length=""; // 코드길이
	private String date=""; // 제출한 후 시간
	

}