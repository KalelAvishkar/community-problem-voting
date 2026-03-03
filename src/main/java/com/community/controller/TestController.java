package com.community.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.nio.file.*;

import com.community.entity.*;
import com.community.repository.*;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/problems")
@CrossOrigin(origins = "*")
public class TestController {

    private static final String UPLOAD_DIR =
            "C:/Users/kalel/Downloads/community-problem-voting/uploads/";

    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final ProblemImageRepository problemImageRepository;

    public TestController(ProblemRepository problemRepository,
                          UserRepository userRepository,
                          VoteRepository voteRepository,
                          ProblemImageRepository problemImageRepository) {
        this.problemRepository = problemRepository;
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
        this.problemImageRepository = problemImageRepository;
    }

    // ================= GET ALL =================
    @GetMapping
    public List<Problem> getAllProblems() {
        return problemRepository.findAllByOrderByVotesDesc();
    }

    // ================= CREATE PROBLEM =================
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/create")
    public Problem createProblem(@RequestParam String title,
                                 @RequestParam String description,
                                 @RequestParam String location) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Problem problem = new Problem();
        problem.setTitle(title);
        problem.setDescription(description);
        problem.setLocation(location);
        problem.setCreatedBy(user);
        problem.setCreatedAt(LocalDateTime.now());
        problem.setVotes(0);
        problem.setStatus(ProblemStatus.PENDING);

        return problemRepository.save(problem);
    }

    // ================= ADMIN UPDATE STATUS =================
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/admin/status/{id}")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam ProblemStatus status) {

        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        problem.setStatus(status);
        problemRepository.save(problem);

        return "Status updated to " + status;
    }

    // ================= ADMIN DELETE =================
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/admin/delete/{id}")
    public String deleteProblem(@PathVariable Long id) {

        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        List<ProblemImage> images =
                problemImageRepository.findByProblem(problem);

        for (ProblemImage image : images) {
            try {
                Path path = Paths.get(UPLOAD_DIR, image.getFileName());
                Files.deleteIfExists(path);
            } catch (Exception ignored) {}
        }

        problemImageRepository.deleteAll(images);
        problemRepository.delete(problem);

        return "Problem deleted successfully";
    }

    // ================= ADD PHOTOS (🔥 FINAL FIX) =================
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/{id}/photos")
    public String addPhotos(@PathVariable Long id,
                            @RequestParam("photos") MultipartFile[] photos) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            for (MultipartFile photo : photos) {

                if (!photo.isEmpty()) {

                    String filename = UUID.randomUUID() + "_" + photo.getOriginalFilename();

                    Path filePath = Paths.get(UPLOAD_DIR, filename);
                    Files.write(filePath, photo.getBytes());

                    ProblemImage image = new ProblemImage();
                    image.setFileName(filename);
                    image.setUploadedBy(user);

                    // 🔥 VERY IMPORTANT (Use helper method)
                    problem.addImage(image);

                    problemImageRepository.save(image);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Photo upload failed");
        }

        return "Photos uploaded successfully";
    }

    // ================= DELETE IMAGE (USER OWN + ADMIN ANY) =================
    @DeleteMapping("/images/{imageId}")
    public String deleteImage(@PathVariable Long imageId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ProblemImage image = problemImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        boolean isAdmin = currentUser.getRole().name().equals("ROLE_ADMIN");
        boolean isOwner = image.getUploadedBy().getId().equals(currentUser.getId());

        // 🔥 SECURITY RULE
        if (!isAdmin && !isOwner) {
            throw new RuntimeException("You are not allowed to delete this image");
        }

        try {
            Path path = Paths.get(UPLOAD_DIR, image.getFileName());
            Files.deleteIfExists(path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        problemImageRepository.delete(image);

        return "Image deleted successfully";
    }

    // ================= VOTE =================
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PutMapping("/{id}/vote")
    public String voteProblem(@PathVariable Long id) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        if (voteRepository.findByUserAndProblem(user, problem).isPresent()) {
            return "Already voted";
        }

        Vote vote = new Vote(user, problem);
        voteRepository.save(vote);

        problem.setVotes(problem.getVotes() + 1);
        problemRepository.save(problem);

        return "Vote added successfully";
    }

    // ================= UNDO VOTE =================
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PutMapping("/{id}/undo-vote")
    public String undoVote(@PathVariable Long id) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        Vote vote = voteRepository.findByUserAndProblem(user, problem)
                .orElseThrow(() -> new RuntimeException("You have not voted yet"));

        voteRepository.delete(vote);

        problem.setVotes(problem.getVotes() - 1);
        problemRepository.save(problem);

        return "Vote removed successfully";
    }
}