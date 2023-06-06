package com.app.demo.email;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailDto {
    String recipient;
    String subject;
    String text;
}
