package com.mit.userservice.taskchecking.repository;

import com.mit.userservice.taskchecking.Solution;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolutionRepository extends CrudRepository<Solution, Long> {
    @Query(value = "SELECT s.* FROM solution s WHERE s.solution_status = status_description", nativeQuery = true)
    public List<Solution> getAllSolutions();
}
