package com.infinity.feedback.repo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Algorithm {
	String full_name_en;
	String aliases;
	String full_name_ko;
	String algorithm_id;
	String short_name_en;

	public String stringfy() {

		return full_name_ko + "-" + 
		
				(aliases==null?",":""+aliases+",")+
				
				((full_name_en.contentEquals(short_name_en)) ? full_name_en : full_name_en + "," + short_name_en);
				
				
				
	}
}