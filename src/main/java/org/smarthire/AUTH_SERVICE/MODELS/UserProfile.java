package org.smarthire.AUTH_SERVICE.MODELS;

import jakarta.persistence.*;
        import lombok.*;
        import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private String fullName;
    private LocalDate dateOfBirth;

    private String gender;
    private String city;
    private String state;
    private String country;
    private String address;

    private String headline;           // "Java Developer | Spring Boot | AWS"
    private String bio;                // Brief about the user
    private String profilePictureUrl;

    private String resumeUrl;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;

    private String highestQualification;
    private String university;
    private Integer graduationYear;

    private Integer experienceInYears;     // Optional: for experienced users
    private String currentCompany;
    private String currentJobTitle;

    private Boolean isJobSeeker;           // true = seeker, false = recruiter (optional)

    private LocalDateTime lastProfileUpdate;
}
