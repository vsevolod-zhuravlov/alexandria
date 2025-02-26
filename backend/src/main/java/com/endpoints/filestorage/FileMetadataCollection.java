package com.endpoints.filestorage;

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

/************************************************
 Unfortunately, Jersey saves all files under temp names,
 And I can't figure out how to make it work with original names,
 So currently Metadata collection is not in use.
 ************************************************/

public class FileMetadataCollection {
    private static final String KEY_FIELD = "fileId";
    private final MongoCollection<FileMetadata> metadataCollection;

    public FileMetadataCollection(MongoUtil mongoUtil) {
        MongoDatabase database = mongoUtil.getDatabase();
        if (database == null) {
            throw new IllegalStateException("Database connection is not initialized.");
        }
        this.metadataCollection = database.getCollection("file_metadata", FileMetadata.class);
    }

    public void save(FileMetadata metadata) {
        try {
            metadataCollection.insertOne(metadata);
        } catch (MongoWriteException e) {
            if (e.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
                throw new IllegalArgumentException("A metadata for this key already exists!");
            }
            throw e;
        }
    }

    public void update(FileMetadata metadata) {
        Bson findQuery = Filters.eq(KEY_FIELD, metadata.fileId);

        metadataCollection.findOneAndReplace(findQuery, metadata);
    }

    public void delete(String fileId) {
    }

    public FileMetadata get(String fileId) {
        Bson findQuery = Filters.eq("fileId", fileId);
        return metadataCollection.find(findQuery).first();
    }

    public List<FileMetadata> getAll(SortingOrder order) {

        Bson sortQuery;
        if (order == SortingOrder.ASCENDING) {
            sortQuery = Sorts.ascending("fileName");
        } else {
            sortQuery = Sorts.descending("fileName");
        }

        return metadataCollection
                .find()
                .sort(sortQuery)
                .into(new ArrayList<>());
    }

    public FileMetadata get(String projectAddress, String taskAddress, String studentAddress) {
        Bson findQuery = Filters.and(Filters.eq("projectAddress", projectAddress),
                Filters.eq("taskId", taskAddress),
                Filters.eq("studentAddress", studentAddress)
        );
        return metadataCollection.find(findQuery).first();
    }
}
