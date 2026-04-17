package com.tenco.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(exclude = "password")
public class User {
    private int id; // 고유아이디
    private String password; //패스워드
    private String name; //이름

}