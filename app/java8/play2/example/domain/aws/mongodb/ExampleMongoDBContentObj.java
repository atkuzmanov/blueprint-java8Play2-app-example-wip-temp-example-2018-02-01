package java8.play2.example.domain.aws.mongodb;

public class ExampleMongoDBContentObj {
    private Long mongoId;
    private String someString1;
    private String someString2;

    public Long getMongoId() {
        return mongoId;
    }

    public void setMongoId(Long mongoId) {
        this.mongoId = mongoId;
    }

    public String getSomeString1() {
        return someString1;
    }

    public void setSomeString1(String someString1) {
        this.someString1 = someString1;
    }

    public String getSomeString2() {
        return someString2;
    }

    public void setSomeString2(String someString2) {
        this.someString2 = someString2;
    }
}
