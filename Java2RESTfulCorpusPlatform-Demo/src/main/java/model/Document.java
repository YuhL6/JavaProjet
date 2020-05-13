package model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Document {
    @JSONField
    private String hash;
    @JSONField
    private byte[] bytes;
    @JSONField
    private String name;
    @JSONField
    private String comment;
    // private StringProperty content;
    // private StringProperty version;
    // private StringProperty classify;

    public Document(String hash, byte[] bytes, String name, String comment){
        this.hash = hash;
        this.name = name;
        this.comment = comment;
        this.bytes = bytes;
        // this.version = new SimpleStringProperty(version);
        // this.classify = new SimpleStringProperty(classify);
    }

    @Override
    public String toString(){
        return "Document{name='" + String.valueOf(name) + "', comment='" + String.valueOf(comment) + "'}";
    }

    /*public String getVersion() {
        return String.valueOf(version);
    }

    public void setVersion(String version) {
        this.version = new SimpleStringProperty(version);
    }

    public String getContent() {
        return String.valueOf(content);
    }

    public void setContent(String content) {
        this.content = new SimpleStringProperty(content);
    }
    public StringProperty contentProperty() {
        return content;
    }

    public StringProperty versionProperty() {
        return version;
    }

    public String getClassify() {
        return String.valueOf(classify);
    }

    public void setClassify(String classify) {
        this.classify = new SimpleStringProperty(classify);
    }

    public StringProperty classifyProperty() {
        return classify;
    }*/

    public String getHash() {
        return hash;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getComment() {
        return String.valueOf(comment);
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public StringProperty nameProperty() {
        return new SimpleStringProperty(name);
    }

    public StringProperty commentProperty() {
        return new SimpleStringProperty(comment);
    }


    public String getName(){
        return String.valueOf(name);
    }

    public void setName(String name){
        this.name = name;
    }

}
