package com.akshansh.taskmanagementplatform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "labels")
@Getter @Setter @NoArgsConstructor @ToString
public class Label {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 30, message = "Name must be between 2 and 30 characters")
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "color")
    @Size(min = 7, max = 7, message = "Must be valid hex color code")
    private String color;

    @ToString.Exclude
    @ManyToMany(mappedBy = "labels")
    private Set<Task> tasks = new HashSet<>();

    public Label(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addLabelToTask(Task task){
        this.tasks.add(task);
        task.getLabels().add(this);
    }

    public void removeLabelFromTask(Task task){
        this.tasks.remove(task);
        task.getLabels().remove(this);
    }
}
