package com.record.student.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class EmailRequest {

    private String to;

    private String subject;

    private String message;
}
