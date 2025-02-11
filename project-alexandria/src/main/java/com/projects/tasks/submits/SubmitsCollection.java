package com.projects.tasks.submits;

import com.database.MongoUtil;
import com.database.SortingOrder;
import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class SubmitsCollection {

    private static final String KEY_FIELD = "id";
    private final MongoCollection<Submit> submissionCollection;

    public SubmitsCollection(MongoUtil mongoUtil) {
        MongoDatabase database = mongoUtil.getDatabase();
        if (database == null) {
            throw new IllegalStateException("Database connection is not initialized.");
        }
        this.submissionCollection = database.getCollection("submits", Submit.class);
    }

    public void save(Submit submission) {
        try {
            submissionCollection.insertOne(submission);
        } catch (MongoWriteException e) {
            if (e.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
                throw new IllegalArgumentException("An account for this key already exists!");
            }
            throw e;
        }
    }

    public void update(Submit submission) {
        Bson findQuery = Filters.eq(KEY_FIELD, submission.getId());

        submissionCollection.findOneAndReplace(findQuery, submission);
    }

    public void edit(Submit submission) {}

    public void delete(Submit submission) {}

    public Submit get(String key) {
        Bson findQuery = Filters.eq(KEY_FIELD, key);
        return submissionCollection.find(findQuery).first();
    }

    public List<Submit> get(List<String> keys) {
        Bson findQuery = Filters.in(KEY_FIELD, keys);
        return submissionCollection.find(findQuery).into(new ArrayList<>());
    }

    public List<Submit> getAll(SortingOrder order) {

        Bson sortQuery;
        if (order == SortingOrder.ASCENDING) {
            sortQuery = Sorts.ascending();
        } else {
            sortQuery = Sorts.descending();
        }

        return submissionCollection
                .find()
                .sort(sortQuery)
                .into(new ArrayList<>());
    }
}
