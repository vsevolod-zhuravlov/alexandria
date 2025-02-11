package com.projects.tasks;

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

public class TasksCollection {

    private static final String KEY_FIELD = "id";
    private final MongoCollection<Task> tasksCollection;

    public TasksCollection(MongoUtil mongoUtil) {
        MongoDatabase database = mongoUtil.getDatabase();
        if (database == null) {
            throw new IllegalStateException("Database connection is not initialized.");
        }
        this.tasksCollection = database.getCollection("tasks", Task.class);
    }

    public void save(Task task) {
        try {
            tasksCollection.insertOne(task);
        } catch (MongoWriteException e) {
            if (e.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
                throw new IllegalArgumentException("An account for this key already exists!");
            }
            throw e;
        }
    }

    public void update(Task task) {
        Bson findQuery = Filters.eq(KEY_FIELD, task.getId());

        tasksCollection.findOneAndReplace(findQuery, task);
    }

    public void edit(Task task) {}

    public void delete(Task task) {}

    public Task get(String key) {
        Bson findQuery = Filters.eq(KEY_FIELD, key);
        return tasksCollection.find(findQuery).first();
    }

    public List<Task> getAll(SortingOrder order) {

        Bson sortQuery;
        if (order == SortingOrder.ASCENDING) {
            sortQuery = Sorts.ascending();
        } else {
            sortQuery = Sorts.descending();
        }

        return tasksCollection
                .find()
                .sort(sortQuery)
                .into(new ArrayList<>());
    }

    public List<Task> getAll(String[] taskAddresses) {
        Bson filterQuery = Filters.in("id", taskAddresses);
        return tasksCollection.find(filterQuery).into(new ArrayList<>());
    }
}
