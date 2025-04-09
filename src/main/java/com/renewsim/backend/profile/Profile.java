package com.renewsim.backend.profile;

import jakarta.persistence.*;
import lombok.*;

import com.renewsim.backend.user.User; 

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String profileImageUrl;

    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

