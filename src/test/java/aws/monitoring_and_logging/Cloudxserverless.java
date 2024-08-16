package aws.monitoring_and_logging;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Cloudxserverless extends AbstractObject{
    @JsonProperty("TopicArn")
    String TopicArn;
    @JsonProperty("AppInstancePrivateDns")
    String AppInstancePrivateDns;
    @JsonProperty("TopicPublishPolicyArn")
    String TopicPublishPolicyArn;
    @JsonProperty("QueueUrl")
    String QueueUrl;
    @JsonProperty("ImageBucketName")
    String ImageBucketName;
    @JsonProperty("ImageBucketArn")
    String ImageBucketArn;
    @JsonProperty("AppInstancePublicDns")
    String AppInstancePublicDns;
    @JsonProperty("QueueArn")
    String QueueArn;
    @JsonProperty("PublicSubnetIds")
    String PublicSubnetIds;
    @JsonProperty("TrailBucketArn")
    String TrailBucketArn;
    @JsonProperty("EventHandlerLambdaRoleARN")
    String EventHandlerLambdaRoleARN;
    @JsonProperty("KeyId")
    String KeyId;
    @JsonProperty("PrivateSubnetIds")
    String PrivateSubnetIds;
    @JsonProperty("ImageBucketPolicyARN")
    String ImageBucketPolicyARN;
    @JsonProperty("TableName")
    String TableName;
    @JsonProperty("AppInstancePublicIp")
    String AppInstancePublicIp;
    @JsonProperty("TopicSubscriptionPolicyArn")
    String TopicSubscriptionPolicyArn;
    @JsonProperty("VpcId")
    String VpcId;
    @JsonProperty("AppInstanceInstanceId")
    String AppInstanceInstanceId;
    @JsonProperty("AppInstanceSecurityGroupId")
    String AppInstanceSecurityGroupId;
    @JsonProperty("AppInstanceInstanceRoleARN")
    String AppInstanceInstanceRoleARN;
    @JsonProperty("AppInstancePrivateIp")
    String AppInstancePrivateIp;
    @JsonProperty("TrailBucketName")
    String TrailBucketName;
    @JsonProperty("EventHandlerLambdaName")
    String EventHandlerLambdaName;
    @JsonProperty("TrailArn")
    String TrailArn;

    @Override
    public String toString() {
        return "Cloudxserverless{" +
                "TopicArn='" + TopicArn + '\'' +
                ", AppInstancePrivateDns='" + AppInstancePrivateDns + '\'' +
                ", TopicPublishPolicyArn='" + TopicPublishPolicyArn + '\'' +
                ", QueueUrl='" + QueueUrl + '\'' +
                ", mageBucketName='" + ImageBucketName + '\'' +
                ", ImageBucketArn='" + ImageBucketArn + '\'' +
                ", AppInstancePublicDns='" + AppInstancePublicDns + '\'' +
                ", QueueArn='" + QueueArn + '\'' +
                ", PublicSubnetIds='" + PublicSubnetIds + '\'' +
                ", TrailBucketArn='" + TrailBucketArn + '\'' +
                ", EventHandlerLambdaRoleARN='" + EventHandlerLambdaRoleARN + '\'' +
                ", KeyId='" + KeyId + '\'' +
                ", PrivateSubnetIds='" + PrivateSubnetIds + '\'' +
                ", ImageBucketPolicyARN='" + ImageBucketPolicyARN + '\'' +
                ", TableName='" + TableName + '\'' +
                ", AppInstancePublicIp='" + AppInstancePublicIp + '\'' +
                ", TopicSubscriptionPolicyArn='" + TopicSubscriptionPolicyArn + '\'' +
                ", VpcId='" + VpcId + '\'' +
                ", AppInstanceInstanceId='" + AppInstanceInstanceId + '\'' +
                ", AppInstanceSecurityGroupId='" + AppInstanceSecurityGroupId + '\'' +
                ", AppInstanceInstanceRoleARN='" + AppInstanceInstanceRoleARN + '\'' +
                ", AppInstancePrivateIp='" + AppInstancePrivateIp + '\'' +
                ", TrailBucketName='" + TrailBucketName + '\'' +
                ", EventHandlerLambdaName='" + EventHandlerLambdaName + '\'' +
                ", TrailArn='" + TrailArn + '\'' +
                '}';
    }

    public String getTopicArn() {
        return TopicArn;
    }

    public void setTopicArn(String topicArn) {
        TopicArn = topicArn;
    }

    public String getAppInstancePrivateDns() {
        return AppInstancePrivateDns;
    }

    public void setAppInstancePrivateDns(String appInstancePrivateDns) {
        AppInstancePrivateDns = appInstancePrivateDns;
    }

    public String getTopicPublishPolicyArn() {
        return TopicPublishPolicyArn;
    }

    public void setTopicPublishPolicyArn(String topicPublishPolicyArn) {
        TopicPublishPolicyArn = topicPublishPolicyArn;
    }

    public String getQueueUrl() {
        return QueueUrl;
    }

    public void setQueueUrl(String queueUrl) {
        QueueUrl = queueUrl;
    }

    public String getImageBucketName() {
        return ImageBucketName;
    }

    public void setImageBucketName(String imageBucketName) {
        ImageBucketName = imageBucketName;
    }

    public String getImageBucketArn() {
        return ImageBucketArn;
    }

    public void setImageBucketArn(String imageBucketArn) {
        ImageBucketArn = imageBucketArn;
    }

    public String getAppInstancePublicDns() {
        return AppInstancePublicDns;
    }

    public void setAppInstancePublicDns(String appInstancePublicDns) {
        AppInstancePublicDns = appInstancePublicDns;
    }

    public String getQueueArn() {
        return QueueArn;
    }

    public void setQueueArn(String queueArn) {
        QueueArn = queueArn;
    }

    public String getPublicSubnetIds() {
        return PublicSubnetIds;
    }

    public void setPublicSubnetIds(String publicSubnetIds) {
        PublicSubnetIds = publicSubnetIds;
    }

    public String getTrailBucketArn() {
        return TrailBucketArn;
    }

    public void setTrailBucketArn(String trailBucketArn) {
        TrailBucketArn = trailBucketArn;
    }

    public String getEventHandlerLambdaRoleARN() {
        return EventHandlerLambdaRoleARN;
    }

    public void setEventHandlerLambdaRoleARN(String eventHandlerLambdaRoleARN) {
        EventHandlerLambdaRoleARN = eventHandlerLambdaRoleARN;
    }

    public String getKeyId() {
        return KeyId;
    }

    public void setKeyId(String keyId) {
        KeyId = keyId;
    }

    public String getPrivateSubnetIds() {
        return PrivateSubnetIds;
    }

    public void setPrivateSubnetIds(String privateSubnetIds) {
        PrivateSubnetIds = privateSubnetIds;
    }

    public String getImageBucketPolicyARN() {
        return ImageBucketPolicyARN;
    }

    public void setImageBucketPolicyARN(String imageBucketPolicyARN) {
        ImageBucketPolicyARN = imageBucketPolicyARN;
    }

    public String getTableName() {
        return TableName;
    }

    public void setTableName(String tableName) {
        TableName = tableName;
    }

    public String getAppInstancePublicIp() {
        return AppInstancePublicIp;
    }

    public void setAppInstancePublicIp(String appInstancePublicIp) {
        AppInstancePublicIp = appInstancePublicIp;
    }

    public String getTopicSubscriptionPolicyArn() {
        return TopicSubscriptionPolicyArn;
    }

    public void setTopicSubscriptionPolicyArn(String topicSubscriptionPolicyArn) {
        TopicSubscriptionPolicyArn = topicSubscriptionPolicyArn;
    }

    public String getVpcId() {
        return VpcId;
    }

    public void setVpcId(String vpcId) {
        VpcId = vpcId;
    }

    public String getAppInstanceInstanceId() {
        return AppInstanceInstanceId;
    }

    public void setAppInstanceInstanceId(String appInstanceInstanceId) {
        AppInstanceInstanceId = appInstanceInstanceId;
    }

    public String getAppInstanceSecurityGroupId() {
        return AppInstanceSecurityGroupId;
    }

    public void setAppInstanceSecurityGroupId(String appInstanceSecurityGroupId) {
        AppInstanceSecurityGroupId = appInstanceSecurityGroupId;
    }

    public String getAppInstanceInstanceRoleARN() {
        return AppInstanceInstanceRoleARN;
    }

    public void setAppInstanceInstanceRoleARN(String appInstanceInstanceRoleARN) {
        AppInstanceInstanceRoleARN = appInstanceInstanceRoleARN;
    }

    public String getAppInstancePrivateIp() {
        return AppInstancePrivateIp;
    }

    public void setAppInstancePrivateIp(String appInstancePrivateIp) {
        AppInstancePrivateIp = appInstancePrivateIp;
    }

    public String getTrailBucketName() {
        return TrailBucketName;
    }

    public void setTrailBucketName(String trailBucketName) {
        TrailBucketName = trailBucketName;
    }

    public String getEventHandlerLambdaName() {
        return EventHandlerLambdaName;
    }

    public void setEventHandlerLambdaName(String eventHandlerLambdaName) {
        EventHandlerLambdaName = eventHandlerLambdaName;
    }

    public String getTrailArn() {
        return TrailArn;
    }

    public void setTrailArn(String trailArn) {
        TrailArn = trailArn;
    }
}
