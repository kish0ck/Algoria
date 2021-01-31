package com.infinity.profile.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmitRepo extends MongoRepository<Profile, String> {


	@Query("{submission_id:{ '$in':?0 }}")
	List<Profile> findbySubmissionIdList(List<Integer> submissionIdList);
	@Query("{submission_id:{ '$eq':?0 }}")
	Profile findbySubmissionId( Integer submissionId);
	
	
}
