package com.projects.tasks.submits;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Confirmation {
    private String confirmerAddress;
    private Status status;

    public static enum Status {
        CONFIRMED,
        DECLINED
    }
}
