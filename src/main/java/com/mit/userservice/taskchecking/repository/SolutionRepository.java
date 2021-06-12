package com.mit.userservice.taskchecking.repository;

import com.mit.userservice.taskchecking.model.Solution;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface SolutionRepository extends CrudRepository<Solution, Long> {
    @Query(value = "SELECT s.* FROM solution s" +
            " WHERE s.status_description = :statusDescription", nativeQuery = true)
    List<Solution> getAllSolutions(@Param("statusDescription") String statusDescription);

    @Transactional
    @Modifying
    @Query(value = "UPDATE solution SET status_description = :statusDescription" +
            " WHERE id = :id", nativeQuery = true)
    void changeSolutionStatus(@Param("id")long id, @Param("statusDescription") String statusDescription);
}
