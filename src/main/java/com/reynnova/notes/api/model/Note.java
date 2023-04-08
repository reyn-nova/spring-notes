package com.reynnova.notes.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "\"Note\"")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String value;

    @Column(name = "\"projectId\"")
    private int projectId;

    @ManyToOne
    @JoinColumn(name="\"projectId\"", nullable=false, insertable = false, updatable = false)
    private Project project;

    public Note() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getProjectId() {
        return this.projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
}
