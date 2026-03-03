package com.community.repository;

import com.community.entity.Vote;
import com.community.entity.User;
import com.community.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByUserAndProblem(User user, Problem problem);

    long countByProblem(Problem problem);

    void deleteByUserAndProblem(User user, Problem problem);
}
