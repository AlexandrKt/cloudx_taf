package aws.monitoring_and_logging;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AbstractObject {
    @JsonProperty("cloudxserverless")
    Cloudxserverless cloudxserverless;
    public Cloudxserverless getCloudxserverless() {
        return cloudxserverless;
    }

    public void setCloudxserverless(Cloudxserverless cloudxserverless) {
        this.cloudxserverless = cloudxserverless;
    }

    @Override
    public String toString() {
        return "AbstractObject{" +
                "cloudxserverless=" + cloudxserverless +
                '}';
    }
}
