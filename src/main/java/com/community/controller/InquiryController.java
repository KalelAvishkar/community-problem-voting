package com.community.controller;

import com.community.entity.Inquiry;
import com.community.entity.Problem;
import com.community.entity.User;
import com.community.repository.InquiryRepository;
import com.community.repository.ProblemRepository;
import com.community.repository.UserRepository;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/inquiries")
@CrossOrigin(origins = "*")
public class InquiryController {

    private final InquiryRepository inquiryRepository;
    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;

    public InquiryController(InquiryRepository inquiryRepository,
                             ProblemRepository problemRepository,
                             UserRepository userRepository) {
        this.inquiryRepository = inquiryRepository;
        this.problemRepository = problemRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/{problemId}")
    public Inquiry createInquiry(@PathVariable Long problemId,
                                 @RequestBody Inquiry request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        Inquiry inquiry = new Inquiry(
                user.getName(),
                user.getEmail(),
                request.getPhoneNumber(),
                request.getMessage(),
                problem
        );

        inquiry.setCreatedAt(LocalDateTime.now());

        return inquiryRepository.save(inquiry);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public List<Inquiry> getAllInquiries() {
        return inquiryRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/problem/{problemId}")
    public List<Inquiry> getInquiriesByProblem(@PathVariable Long problemId) {

        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        return inquiryRepository.findByProblem(problem);
    }
}