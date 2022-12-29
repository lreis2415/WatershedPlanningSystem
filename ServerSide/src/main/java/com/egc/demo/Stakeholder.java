package com.egc.demo;

import javax.persistence.*;

@Entity
@Table(name = "stakeholder")
public class Stakeholder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer id;

    public Stakeholder() {

    }

    private int groupId;
    private String name;
    private String password;
    private String email;
    private String role;

    public Stakeholder(int groupId, String name, String password, String email, String role) {
        this.groupId = groupId;
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public int getGroupId() {
        return this.groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
