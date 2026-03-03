package com.community.repository;

import com.community.entity.Problem;
import com.community.entity.ProblemImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProblemImageRepository extends JpaRepository<ProblemImage, Long> {

    // Find all images of a problem
    List<ProblemImage> findByProblem(Problem problem);
}