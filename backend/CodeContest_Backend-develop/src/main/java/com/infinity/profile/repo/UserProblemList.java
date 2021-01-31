package com.infinity.profile.repo;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.ToString;

@Document(collection="submit")
@Getter
public class UserProblemList {
	private String userId;
	private List problems;
}
