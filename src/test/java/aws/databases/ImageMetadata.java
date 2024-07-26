package aws.databases;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.util.Date;

public class ImageMetadata {
    @JsonProperty("id")
    private String id;
    @JsonProperty("object_key")
    private String object_key;
    @JsonProperty("object_size")
    private long object_size;
    @JsonProperty("object_type")
    private String object_type;
    @JsonProperty("last_modified")
    private Timestamp last_modified;

    public ImageMetadata(String id, String object_key, long object_size, String object_type, Timestamp last_modified){
        this.id = id;
        this.object_key = object_key;
        this.object_size = object_size;
        this.object_type = object_type;
        this.last_modified = last_modified;
    }
    public ImageMetadata(String id, Timestamp last_modified, String object_key, long object_size, String object_type){
        this.id = id;
        this.object_key = object_key;
        this.object_size = object_size;
        this.object_type = object_type;
        this.last_modified = last_modified;
    }
    public ImageMetadata(String id, long object_size, String object_type, Timestamp last_modified) {
        this.id = id;
        this.object_size = object_size;
        this.object_type = object_type;
        this.last_modified = last_modified;
    }
    public ImageMetadata(){}

    public String getImageKey() {
        return object_key;
    }

    public void setImageKey(String object_key) {
        this.object_key = object_key;
    }

    public long getObjectSize() {
        return object_size;
    }

    public void setObjectSize(long object_size) {
        this.object_size = object_size;
    }

    public String getObjectType() {
        return object_type;
    }

    public void setObjectType(String object_type) {
        this.object_type = object_type;
    }

    public Date getLastModified() {
        return last_modified;
    }

    public void setLastModified(Timestamp last_modified) {
        this.last_modified = last_modified;
    }

    @Override
    public String toString() {
        return "ImageMetadata:\n{" +
                "\nid='" + id + '\'' +
                ", \nobject_key='" + object_key + '\'' +
                ", \nobject_size=" + object_size +
                ", \nobject_type='" + object_type + '\'' +
                ", \nlast_modified=" + last_modified + "\n"+
                '}';
    }
}
