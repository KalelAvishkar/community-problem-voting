package com.community.config;

import com.community.entity.Problem;
import com.community.repository.ProblemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadProblems(ProblemRepository problemRepository) {
        return args -> {

            // 🔥 IF TABLE EMPTY THEN ONLY INSERT
            if (problemRepository.count() == 0) {

                List<Problem> problems = List.of(
                        new Problem("Road broken", "Potholes near market", "Main Road"),
                        new Problem("Water leakage", "Pipe leaking since days", "Sector 12"),
                        new Problem("Street light not working", "Dark road at night", "Park Lane"),
                        new Problem("Garbage overflow", "Bins not cleaned", "Bus Stand"),
                        new Problem("Drain blockage", "Water overflow in rains", "Old City"),
                        new Problem("Traffic signal broken", "Accidents happening", "Cross Road"),
                        new Problem("Illegal parking", "Road always blocked", "Mall Area"),
                        new Problem("No footpath", "People walk on road", "Highway"),
                        new Problem("Open manhole", "Very dangerous", "School Road"),
                        new Problem("Water shortage", "No supply in morning", "Sector 5"),
                        new Problem("Noise pollution", "Loudspeakers daily", "Temple Street"),
                        new Problem("Stray dogs issue", "Dog bites reported", "Colony A"),
                        new Problem("Mosquito problem", "Dengue cases rising", "River Side"),
                        new Problem("Broken sewer", "Bad smell", "Market Area"),
                        new Problem("Unauthorized construction", "Road narrowing", "Lane 3")
                );

                problemRepository.saveAll(problems);
                System.out.println("✅ 15 DEFAULT PROBLEMS INSERTED");
            } else {
                System.out.println("ℹ️ Problems already exist, skipping DataLoader");
            }
        };
    }
}
