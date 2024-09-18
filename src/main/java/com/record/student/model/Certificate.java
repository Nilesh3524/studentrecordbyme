package com.record.student.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String type;

    private String name;

    @Lob
    @Column(length = 100000)
    private byte[] data;

    @OneToOne
    private Student student;


    public Certificate(String type, String name, byte[] data, Student student) {
        this.type = type;
        this.name = name;
        this.data = data;
        this.student = student;
    }
}
