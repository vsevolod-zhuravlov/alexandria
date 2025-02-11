package com.accounts;

import com.database.MongoUtil;

public class AccountsService {
    private static AccountsService instance; // Singleton instance
    private MongoUtil mongoUtil;
    private AccountsCollection accountsCollection;

    // Private constructor to prevent direct instantiation
    private AccountsService() {
        mongoUtil = new MongoUtil();
        mongoUtil.connect();
        accountsCollection = new AccountsCollection(mongoUtil);
    }

    // Public method to provide access to the singleton instance
    public static synchronized AccountsService getInstance() {
        if (instance == null) {
            instance = new AccountsService();
        }
        return instance;
    }

    public AccountsCollection getAccountsCollection() {
        return accountsCollection;
    }


}
