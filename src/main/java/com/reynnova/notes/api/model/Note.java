package com.reynnova.notes.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

@Entity
@Table(name = "\"Note\"")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String value;

    @Column(name = "\"projectId\"")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer projectId;

    @ManyToOne(cascade = CascadeType.MERGE)
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

    public Integer getProjectId() {
        return this.projectId;
    }

    public void setProjectId(Object projectId) {
        this.projectId = (Integer) projectId;
    }
}
