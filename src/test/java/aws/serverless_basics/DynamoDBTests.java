package aws.serverless_basics;

import org.junit.Test;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;

public class DynamoDBTests extends AbstractTest{

    @Test
    public void testDynamoDBTableRequirements() {
        TableDescription tableDescription = getTableDescription(TABLE_NAME);
        System.out.println("Test test test");
        // Verify Table Name
        logger.info("Table name is: " + tableDescription.tableName());
        assertEquals(TABLE_NAME, tableDescription.tableName());

        // Verify Global Secondary Indexes
        logger.info("Global Secondary Indexes is empty: " + tableDescription.globalSecondaryIndexes().isEmpty());
        assertTrue(tableDescription.globalSecondaryIndexes().isEmpty());

        // Verify Provisioned Read Capacity Units
        logger.info("Provisioned Read Capacity Units: " + Optional.ofNullable(tableDescription.provisionedThroughput().readCapacityUnits()));
        assertEquals(Optional.of(5L), Optional.ofNullable(tableDescription.provisionedThroughput().readCapacityUnits()));

        // Verify Provisioned Write Capacity Units
        logger.info("Provisioned Write Capacity Units: " + Optional.ofNullable(tableDescription.provisionedThroughput().writeCapacityUnits()));
        assertEquals(Optional.of(1L), Optional.ofNullable(tableDescription.provisionedThroughput().writeCapacityUnits()));

        // Verify Tags
        List<Tag> tags = listTableTags(tableDescription.tableArn());
        logger.info("Tags: " + tags.stream().filter(tag -> tag.key().equals("cloudx") && tag.value().equals("qa")).collect(Collectors.toList()) + "\n");
        assertTrue(tags.stream().anyMatch(tag -> "cloudx".equals(tag.key()) && "qa".equals(tag.value())));
    }

    @Test
    public void testDynamoDBTableStoresMetadata() {
        // Define the item to be inserted (image metadata)
/*        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().s("11112222").build());
        item.put("object_key", AttributeValue.builder().s("images/sample-image.png").build());
        item.put("created_at", AttributeValue.builder().s("2024-07-24T10:00:00Z").build());
        item.put("last_modified", AttributeValue.builder().s("2024-07-24T10:05:00Z").build());
        item.put("object_size", AttributeValue.builder().n("125119").build());
        item.put("object_type", AttributeValue.builder().s("image/png").build());

        // Put the item into the DynamoDB table
        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(item)
                .build();
        PutItemResponse putItemResponse = dynamoDbClient.putItem(putItemRequest);

        // Verify that the item was inserted successfully
        assertNotNull(putItemResponse);

        // Retrieve the item from the DynamoDB table
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("object_key", AttributeValue.builder().s("images/sample-image.png").build());
*/
        Map<String, AttributeValue> itemKey = new HashMap<>();
        itemKey.put("id", AttributeValue.builder().s("c569b1eb-7251-48d2-b9de-909d176a48fd").build());

        GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(itemKey)
                .build();
        GetItemResponse response = dynamoDbClient.getItem(getItemRequest);

        // Verify that the returned item matches the inserted item
        assertNotNull(response.item());
        Map<String, AttributeValue> returnedItem = response.item();
        logger.info("----------------------- Dynamo DB Item -------------------------");
        logger.info("id: " + returnedItem.get("id").s());
        assertEquals("c569b1eb-7251-48d2-b9de-909d176a48fd", returnedItem.get("id").s());
        logger.info("created_at: " + returnedItem.get("created_at").n());
        assertEquals("1723455976", returnedItem.get("created_at").n());
        logger.info("last_modified: " + returnedItem.get("last_modified").n());
        assertEquals("1723455977", returnedItem.get("last_modified").n());
        logger.info("object_size: " + returnedItem.get("object_size").n());
        assertEquals("54820", returnedItem.get("object_size").n());
        logger.info("object_type: " + returnedItem.get("object_type").s());
        assertEquals("binary/octet-stream", returnedItem.get("object_type").s());
        logger.info("object_key: " + returnedItem.get("object_key").s());
        assertEquals("images/8b41d76e-e436-49ad-9abc-1f537ef70e6e-running_container.png", returnedItem.get("object_key").s());
        logger.info("----------------------------------------------------------------");
    }
    public TableDescription getTableDescription(String tableName) {
        DescribeTableRequest describeTableRequest = DescribeTableRequest.builder()
                .tableName(tableName)
                .build();
        DescribeTableResponse describeTableResponse = dynamoDbClient.describeTable(describeTableRequest);
        return describeTableResponse.table();
    }
    public List<Tag> listTableTags(String tableArn) {
        ListTagsOfResourceRequest request = ListTagsOfResourceRequest.builder()
                .resourceArn(tableArn)
                .build();
        return dynamoDbClient.listTagsOfResource(request).tags();
    }
}

