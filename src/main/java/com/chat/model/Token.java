package com.chat.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String token;

}
