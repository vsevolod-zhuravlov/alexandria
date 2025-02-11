package com.accounts;
import com.database.MongoUtil;
import com.mongodb.client.MongoCollection;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountsCollectionTest {
    private MongoCollection<Account> mockCollection;
    private AccountsCollection accountsCollection;

    @BeforeEach
    void setUp() {
        mockCollection = Mockito.mock(MongoCollection.class);
        MongoUtil mockMongoUtil = Mockito.mock(MongoUtil.class);
        when(mockMongoUtil.getDatabase()).thenReturn(Mockito.mock(com.mongodb.client.MongoDatabase.class));
        when(mockMongoUtil.getDatabase().getCollection(anyString(), eq(Account.class)))
                .thenReturn(mockCollection);
        accountsCollection = new AccountsCollection(mockMongoUtil);
    }

    @Test
    void save_ShouldInsertAccount() {
        Account testAccount = new Account("0000000000", "Ivan Synenko");

        accountsCollection.save(testAccount);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(mockCollection).insertOne(captor.capture());
        assertEquals(testAccount, captor.getValue());
    }

    @Test
    void update_ShouldReplaceAccount() {
        Account testAccount = new Account("0000000000", "Ivan Synenko");

        accountsCollection.update(testAccount);

        ArgumentCaptor<Bson> filterCaptor = ArgumentCaptor.forClass(Bson.class);
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(mockCollection).findOneAndReplace(filterCaptor.capture(), accountCaptor.capture());

        assertEquals(testAccount, accountCaptor.getValue());
    }
}