package com.infinity.feedback.repo;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Document(collection = "problem")
@Getter
@Setter
@ToString
public class Problem  {

	@Id
	String _id;
	Integer problem_id;
	String problem_title;
	Integer n_success;
	Integer n_submit;
	String is_boj;
	Integer level;
	Double n_success_rate;
	/*
	public Problem(String _id, Integer problem_id, String problem_title, Integer n_success, Integer n_submit,
			String is_boj) {
		super();
		this._id = _id;
		this.problem_id = problem_id;
		this.problem_title = problem_title;
		this.n_success = n_success;
		this.n_submit = n_submit;
		this.is_boj = is_boj;
		this.SuccessRate=(double)n_success/n_submit; 
	}
	*/
	
	
}
