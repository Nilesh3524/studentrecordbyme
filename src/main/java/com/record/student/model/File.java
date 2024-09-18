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
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String type;

    @Lob
    @Column(length = 100000)
    private byte[] data;

    private String date;


    public File(String type, byte[] data) {
        this.type = type;
        this.data = data;
    }

    public File(String name, String type, byte[] data, String date) {
        this.name = name;
        this.type = type;
        this.data = data;
        this.date = date;
    }
}
