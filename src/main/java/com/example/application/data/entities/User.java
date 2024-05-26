package com.example.application.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

import java.util.Set;

@Entity
@Table(name = "application_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "idgenerator", initialValue = 1000)
    private Long id;

    @Email
    @Column(unique = true)
    private String username;

    private String lastName;
    private String firstName;

    @JsonIgnore
    private String hashedPassword;
    private boolean locked = true;


    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;

//    @Lob
//    @Column(length = 1000000)
//    private byte[] profilePicture;

    private Fachbereich fachbereich;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getHashedPassword() {
        return hashedPassword;
    }
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
    public Set<Role> getRoles() {
        return roles;
    }
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    //approval
    public boolean isLocked() {return locked;}
    public void setLocked(boolean locked) {this.locked = locked;}





//    public byte[] getProfilePicture() {
//        return profilePicture;
//    }
//    public void setProfilePicture(byte[] profilePicture) {
//        this.profilePicture = profilePicture;
//    }
    public Fachbereich getFachbereich() {
        return fachbereich;
    }
    public void setFachbereich(Fachbereich fachbereich) {
        this.fachbereich = fachbereich;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

}
