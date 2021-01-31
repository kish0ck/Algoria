package com.infinity.feedback.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * group 
 * @author sdm82
 *
 */
@Repository
public interface GroupRepo extends MongoRepository<Group, String> {

	@Query("{group_name:{ '$eq':?0 }}")
	Group findByGroupName( String GroupName);
	
	
}
