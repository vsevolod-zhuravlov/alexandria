package com.accounts;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Account {
    private String walletAddress;
    private String fullName;

    private List<String> ownedCourses;
    private List<String> confirmerCourses;
    private List<String> studentCourses;


    public Account(String walletAddress, String fullName) {
        this.walletAddress = walletAddress;
        this.fullName = fullName;
    }

}
