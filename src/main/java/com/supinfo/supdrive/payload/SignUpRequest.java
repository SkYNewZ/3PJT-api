package com.supinfo.supdrive.payload;

import javax.validation.constraints.*;

public class SignUpRequest {
    @NotBlank
    @Size(min = 4, max = 40)
    private String firstName;

    @NotBlank
    @Size(min= 4, max = 40)
    private String lastName;

    @NotBlank
    @Size(min = 3, max = 15)
    private String username;

    @NotBlank
    @Size(max = 40)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 20)
    private String password;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstNameName(String name) {
        this.firstName = firstName;
    }

    public String getLastName(){ return lastName; }

    public void setLastName(){
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
}
