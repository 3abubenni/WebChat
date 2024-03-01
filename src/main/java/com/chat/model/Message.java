package com.chat.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Message {

    @Id
    private Long id;
    @ManyToOne
    private User sender;
    private String text;
    private Date sentAt;

}
