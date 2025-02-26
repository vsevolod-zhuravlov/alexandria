package com.database;

import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.ClientSession;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;


public class MongoUtil {
    private static String URI;
    private static String DATABASE_NAME;

    private final MongoClient mongoClient;
    private MongoDatabase database;


    public MongoUtil() {
        URI = DatabaseConfig.URI;
        DATABASE_NAME = DatabaseConfig.DATABASE_NAME;
        mongoClient = MongoClients.create(URI);
    }

    public void connect() {
        try {
            database = mongoClient
                    .getDatabase(DATABASE_NAME)
                    .withCodecRegistry(getCodecRegistry());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public ClientSession getClientSession() {
        return mongoClient.startSession();
    }

    public void disconnect() {
        database = null;
    }

    private CodecRegistry getCodecRegistry() {
        return fromRegistries(
                MongoClientSettings
                        .getDefaultCodecRegistry(),
                CodecRegistries
                        .fromProviders(
                                PojoCodecProvider
                                        .builder()
                                        .automatic(true)
                                        .build()));

    }
}