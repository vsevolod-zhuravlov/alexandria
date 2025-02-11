package com.projects.tasks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Task {
    private String id;
    private String shortDescription;
    private String description;
    private String certificate;

    private List<String> submits;
    private int minConfirmations;

    public void addSubmit(String submit) {
        this.submits.add(submit);
    }
}
