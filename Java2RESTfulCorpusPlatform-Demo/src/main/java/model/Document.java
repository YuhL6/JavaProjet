package model;

import com.alibaba.fastjson.annotation.JSONField;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Document is used to record information about the file, and it is the entry of <code>Tableview</code> in frontend. <br>
 * There are many fields in this class, but not every field is required, e.g. <code>selected</code> and <code>full</code>
 * is used in frontend to record the complete and selected information.<br>
 */
public class Document {
    @JSONField(name = "md5")
    private String hash;
    @JSONField(name = "preview")
    private String content;
    @JSONField
    private String name;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @JSONField
    private int length = 0;
    @JSONField(serialize = false)
    private boolean selected = false;
    @JSONField(serialize = false)
    private boolean isComplete = false;
    public Document(){}

    public boolean getIsComplete(){
        return isComplete;
    }

    public Document(String hash, String content, String name, int length){
        this.hash = hash;
        this.name = name;
        this.content = content;
        this.length = length;
        if (length == content.length())
            isComplete = true;
    }

    public String getHash() {
        return hash;
    }

    public String getContent() {
        return content;
    }

    /**
     *
     * @param bytes turn the bytes into UTF-8 encoding
     * @throws IOException
     */
    public void setContent(byte[] bytes) throws IOException {
        CharsetDetector charsetDetector = new CharsetDetector();
        charsetDetector.setText(bytes);
        CharsetMatch charsetMatch = charsetDetector.detect();
        String encoding = charsetMatch.getName();
        if (!encoding.equalsIgnoreCase("UTF-8")){
            bytes = charsetMatch.getString().getBytes(StandardCharsets.UTF_8);
        }
        if (bytes[0] == -17 && bytes[1] == -69 && bytes[2] == -65){
            // handle the UTF with BOM
            byte[] bytes1 = new byte[bytes.length - 3];
            System.arraycopy(bytes, 3, bytes1, 0, bytes1.length);
            bytes = bytes1;
        }
        this.content = new String(bytes, StandardCharsets.UTF_8);
        if (hash == null || hash.equalsIgnoreCase(""))
            setHash();
        if (length == 0)
            setLength(this.content.length());
        if (length == content.length())
            isComplete = true;
    }

    /**
     *
     * @param content if the content is not UTF-8 encoding, turns it into UTF-8
     * @throws IOException
     */
    public void setContent(String content) throws IOException {
        if (!new String(content.getBytes(), StandardCharsets.UTF_8).equals(content)){
            setContent(content.getBytes());
            return;
        }
        this.content = content;
        if (hash == null || hash.equalsIgnoreCase(""))
            setHash();
        if (length == 0)
            setLength(this.content.length());
        if (length == content.length())
            isComplete = true;
    }

    public StringProperty nameProperty() {
        return new SimpleStringProperty(name);
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
        if (content == null || content.equalsIgnoreCase("")){
            return;
        }
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(content.getBytes(StandardCharsets.UTF_8), 0, content.getBytes().length);
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
