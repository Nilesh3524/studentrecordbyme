package com.record.student.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Attendence {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String pday1;
    private String tday1;
    private double a1;

    private String pday2;
    private String tday2;
    private double a2;

    private String pday3;
    private String tday3;
    private double a3;

    private String pday4;
    private String tday4;
    private double a4;

    private String pday5;
    private String tday5;
    private double a5;

    private String pday6;
    private String tday6;
    private double a6;

    private String pday7;
    private String tday7;
    private double a7;

    private String pday8;
    private String tday8;
    private double a8;

    @OneToOne
    private Student student;

}
