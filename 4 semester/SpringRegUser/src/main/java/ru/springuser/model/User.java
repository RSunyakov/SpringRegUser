package ru.springuser.model;

import ru.springuser.annotation.DateConstraint;
import ru.springuser.annotation.EmailConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @EmailConstraint
    private String email;

    private String activationCode;

    @NotNull
    @Size(min = 5)
    private String password;

    @NotNull
    @NotEmpty
    private String country;

    @NotNull
    @NotEmpty
    private String gender;

    @DateConstraint
    private String birthday;
}
