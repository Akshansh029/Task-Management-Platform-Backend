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
    @Size(min = 2, max = 25, message = "Name must be between 2 and 25 characters")
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "color")
    private String color;

    @ToString.Exclude
    @ManyToMany(mappedBy = "labels")
    private Set<Task> tasks = new HashSet<>();
}
