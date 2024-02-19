package org.fundraiser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailData {

    private String receiver;

    private String subject;

    private String messageBody;

}
