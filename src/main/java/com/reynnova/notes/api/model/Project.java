package com.reynnova.notes.api.model;

import jakarta.persistence.*;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
@Table(name = "\"Project\"")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @Column(name = "\"ownerId\"")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer ownerId;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="\"ownerId\"", nullable=false, insertable = false, updatable = false)
    private User user;

    @OneToMany(mappedBy="project")
    private Set<Note> notes;

    public Project() {}

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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Set<Note> getNotes() {
        return  notes;
    }

    public void setNotes(Set<Note> notes) {
        this.notes = notes;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }
}
