package com.infinity.feedback.repo;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Document(collection = "submit")
@Getter
public class Sort {
	private String userId;
	private List problems;
}
