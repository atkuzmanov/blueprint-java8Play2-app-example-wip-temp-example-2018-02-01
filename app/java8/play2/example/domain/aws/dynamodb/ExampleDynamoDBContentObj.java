package java8.play2.example.domain.aws.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

//Overriding table name in application.conf file.
@DynamoDBTable(tableName = "")
public class ExampleDynamoDBContentObj {
    private Integer dynamoId;

    @DynamoDBHashKey(attributeName = "dynamoId")
    public Integer getDynamoId() {
        return dynamoId;
    }

    public void setDynamoId(Integer dynamoId) {
        this.dynamoId = dynamoId;
    }
}
