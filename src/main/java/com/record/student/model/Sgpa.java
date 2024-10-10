package com.record.student.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sgpa {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private double sgpa1;

    private double sgpa2;

    private double sgpa3;

    private double sgpa4;

    private double sgpa5;

    private double sgpa6;

    private double sgpa7;

    private double sgpa8;

    @OneToOne
    private Student student;

}
