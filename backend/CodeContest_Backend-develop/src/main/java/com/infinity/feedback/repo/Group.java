package com.infinity.feedback.repo;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@ToString
@Document(collection = "group")
public class Group {
	
	@Id
	private String _id;
	private String group_name="";
	private List<String> group_members;
	private Integer group_id=0;
}
