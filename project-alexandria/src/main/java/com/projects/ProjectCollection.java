package com.projects;

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

public class ProjectCollection {
    private static final String KEY_FIELD = "projectAddress";
    private final MongoCollection<Project> projectCollection;

    public ProjectCollection(MongoUtil mongoUtil) {
        MongoDatabase database = mongoUtil.getDatabase();
        if (database == null) {
            throw new IllegalStateException("Database connection is not initialized.");
        }
        this.projectCollection = database.getCollection("projects", Project.class);
    }

    public void save(Project project) {
        try {
            projectCollection.insertOne(project);
        } catch (MongoWriteException e) {
            if (e.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
                throw new IllegalArgumentException("An account for this key already exists!");
            }
            throw e;
        }
    }

    public void update(Project project) {
        Bson findQuery = Filters.eq(KEY_FIELD, project.getProjectAddress());

        projectCollection.findOneAndReplace(findQuery, project);
    }

    public void edit(Project project) {}

    public void delete(Project project) {}

    Project get(String key) {
        return null;
    }

    public List<Project> getAll(SortingOrder order) {
        Bson sortQuery;
        if (order == SortingOrder.ASCENDING) {
            sortQuery = Sorts.ascending();
        } else {
            sortQuery = Sorts.descending();
        }

        return projectCollection
                .find()
                .sort(sortQuery)
                .into(new ArrayList<>());
    }

    public List<Project> getAllProjectsOfAddress(String address) {
        Bson filterQuery = Filters.eq("creatorAddress", address);
        return projectCollection
                .find(filterQuery)
                .into(new ArrayList<>());
    }

}
