package model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;
import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Document {
    @JSONField
    private String hash;
    @JSONField
    private byte[] bytes;
    @JSONField
    private String name;
    @JSONField
    private String comment;
    @JSONField(serialize = false)
    private boolean selected = false;
    // private StringProperty content;
    // private StringProperty version;
    // private StringProperty classify;
    public Document(){}

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

    public void setBytes(byte[] bytes) throws UnsupportedEncodingException {
        // check the encoding and transform into UTF-8
        CharsetDetector charsetDetector = new CharsetDetector();
        charsetDetector.setText(bytes);
        CharsetMatch charsetMatch = charsetDetector.detect();
        String encoding = charsetMatch.getName();
        if (!encoding.equalsIgnoreCase("UTF-8")){
            String content = new String(bytes, encoding);
            bytes = content.getBytes(StandardCharsets.UTF_8);
        }
        this.bytes = bytes;
        setHash();
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

    public void setHash(String hash){
        this.hash = hash;
    }

    public void setHash(){
        if (bytes == null || bytes.length == 0){
            return;
        }
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(bytes, 0, bytes.length);
            hash = new BigInteger(1, messageDigest.digest()).toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void setSelected(boolean selected){
        this.selected = selected;
    }

    public boolean getSelected(){
        return selected;
    }

}
