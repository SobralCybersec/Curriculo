package com.dev.model;

import java.util.ArrayList;
import java.util.List;

public class ResumeData {
    private String firstName;
    private String lastName;
    private List<String> positions;
    private String mobile;
    private String email;
    private String github;
    private String linkedin;
    private String summary;
    private List<Education> educations;
    private List<Experience> experiences;
    private List<Skill> skills;
    
    public ResumeData() {
        this.positions = new ArrayList<>();
        this.educations = new ArrayList<>();
        this.experiences = new ArrayList<>();
        this.skills = new ArrayList<>();
    }
    
    public void setName(String fullName) {
        setFullName(fullName);
    }
    
    public void setPhone(String phone) {
        this.mobile = phone;
    }
    
    public void addPosition(String position) {
        this.positions.add(position);
    }
    
    public void addEducation(Education education) {
        this.educations.add(education);
    }
    
    public void addExperience(Experience experience) {
        this.experiences.add(experience);
    }
    
    public void addSkill(Skill skill) {
        this.skills.add(skill);
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public void setFullName(String fullName) {
        String[] parts = fullName.trim().split(" ", 2);
        this.firstName = parts.length > 0 ? parts[0] : "";
        this.lastName = parts.length > 1 ? parts[1] : "";
    }
    
    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public List<String> getPositions() { return positions; }
    public void setPositions(List<String> positions) { this.positions = positions; }
    
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getGithub() { return github; }
    public void setGithub(String github) { this.github = github; }
    
    public String getLinkedin() { return linkedin; }
    public void setLinkedin(String linkedin) { this.linkedin = linkedin; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    
    public List<Education> getEducations() { return educations; }
    public void setEducations(List<Education> educations) { this.educations = educations; }
    
    public List<Experience> getExperiences() { return experiences; }
    public void setExperiences(List<Experience> experiences) { this.experiences = experiences; }
    
    public List<Skill> getSkills() { return skills; }
    public void setSkills(List<Skill> skills) { this.skills = skills; }
    
    public static class Education {
        public String degree;
        public String institution;
        public String location;
        public String period;
        public String description;
        
        public Education() {}
        
        public Education(String degree, String institution, String location, String period, String description) {
            this.degree = degree;
            this.institution = institution;
            this.location = location;
            this.period = period;
            this.description = description;
        }
    }
    
    public static class Experience {
        public String position;
        public String company;
        public String location;
        public String period;
        public String description;
        
        public Experience() {}
        
        public Experience(String position, String company, String location, String period, String description) {
            this.position = position;
            this.company = company;
            this.location = location;
            this.period = period;
            this.description = description;
        }
    }
    
    public static class Skill {
        public String category;
        public String items;
        
        public Skill() {}
        
        public Skill(String category, String items) {
            this.category = category;
            this.items = items;
        }
    }
}
