package com.example.streamingspringb.Model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jdk.jfr.Name;
import org.springframework.format.annotation.DateTimeFormat;



import java.time.LocalDate;


public class User {
    private int id;

    @Name("Skriv dit fulde navn")
    @NotBlank(message = "Må ikke være tom")
    private String name;
    @Email(message = "Skal indeholde @")
    @NotBlank(message = "Må ikke være tom")
    private String email;
    @Size(min = 3, message = "Skal være mindst 3 tegn")
    @NotBlank(message = "Må ikke være tom")
    private String password;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthdate;
    private String role;

    public User(){
    }

    public User(int id, String name, String email, String password, LocalDate birthdate, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.birthdate = birthdate;
        this.role = role;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public LocalDate getBirthdate() {
        return birthdate;
    }
    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    @Override
    public String toString() {
        return name + " " + email + " ******* " + birthdate + " " + role;
    }
}
