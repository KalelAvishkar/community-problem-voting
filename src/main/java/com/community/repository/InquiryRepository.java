
package com.community.repository;

import com.community.entity.Inquiry;
import com.community.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    // 🔹 Find all inquiries for a problem
    List<Inquiry> findByProblem(Problem problem);
}
