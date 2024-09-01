package org.example.entry;

import lombok.Data;

@Data
public class Message {

    private String id;
    private String content;
    private Integer approvalNum;
    private Integer reviewNum;
    private Double score;
}
