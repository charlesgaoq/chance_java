package com;

import com.mongodb.BasicDBObject;

import org.junit.Test;

public class BasicDBObjectTest {

    @Test
    public void testBson() {
        BasicDBObject.parse("{task:'c9a40db934de41408b73934d62a1438b'}");
    }
}
