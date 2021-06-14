package com.mit.userservice.taskchecking.repository;

import com.mit.userservice.taskchecking.model.Test;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRepository extends CrudRepository<Test,Long> {

    @Query(value = "SELECT * from test WHERE problem_id = :problemId", nativeQuery = true)
    List<Test> getAllTestsForProblem(@Param("problemId") long problemId);
}
