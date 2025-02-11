package com.accounts;

import com.database.SortingOrder;
import com.database.MongoUtil;
import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class AccountsCollection {
    private static final String KEY_FIELD = "walletAddress";
    private final MongoCollection<Account> accountCollection;

    public AccountsCollection(MongoUtil mongoUtil) {
        MongoDatabase database = mongoUtil.getDatabase();
        if (database == null) {
            throw new IllegalStateException("Database connection is not initialized.");
        }
        this.accountCollection = database.getCollection("accounts", Account.class);
    }

    public void save(Account account) {
        try {
            accountCollection.insertOne(account);
        } catch (MongoWriteException e) {
            if (e.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
                throw new IllegalArgumentException("An account for this key already exists!");
            }
            throw e;
        }
    }

    public void update(Account account) {
        Bson findQuery = Filters.eq(KEY_FIELD, account.getWalletAddress());

        accountCollection.findOneAndReplace(findQuery, account);
    }

    public void edit(Account account) {}

    public void delete(Account account) {}

    public Account get(String key) {
        return null;
    }

    public List<Account> getAll(SortingOrder order) {

        Bson sortQuery;
        if (order == SortingOrder.ASCENDING) {
            sortQuery = Sorts.ascending("name");
        } else {
            sortQuery = Sorts.descending("name");
        }

        return accountCollection
                .find()
                .sort(sortQuery)
                .into(new ArrayList<>());
    }


}
