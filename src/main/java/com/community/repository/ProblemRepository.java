package com.community.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.community.entity.Problem;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

    // 👇 votes ke hisab se sorting
    List<Problem> findAllByOrderByVotesDesc();
}
