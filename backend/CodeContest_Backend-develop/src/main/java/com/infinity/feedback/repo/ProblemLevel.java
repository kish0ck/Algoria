package com.infinity.feedback.repo;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Document(collection = "problem")
@Getter
@Setter
@ToString
public class ProblemLevel {

	@Id
	String _id;
	Integer problem_id=0;
	String level="";
	Integer n_success=0;
	Integer n_submit=0;
	String is_boj="";
	List<Algorithm> algorithms;
	


}
