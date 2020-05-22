package dao;

import com.alibaba.fastjson.JSON;
import model.Document;
import org.apache.commons.lang3.RandomStringUtils;
import org.sql2o.Sql2o;
import ucar.units.Base;

import javax.swing.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

public class TextDao {
    private Connection con;
    public void getConnection(){
        try {
            // CLASSPATH must be properly set, for instance on
            // a Linux system or a Mac:
            // $ export CLASSPATH=.:sqlite-jdbc-version-number.jar
            // Alternatively, run the program with
            // $ java -cp .:sqlite-jdbc-version-number.jar BasicJDBC
            Class.forName("org.sqlite.JDBC");
        } catch (Exception e) {
            System.err.println("Cannot find the driver.");
            System.exit(1);
        }
        try {
            con = DriverManager.getConnection("jdbc:sqlite:Documents.sqlite");
            System.err.println("Successfully connected to the database.");
            Statement statement = con.createStatement();
            statement.setQueryTimeout(30); // set timeout to 30 sec.

            // create table
            // statement.executeUpdate("drop table if exists  document");
            statement.executeUpdate("create table if not exists document (hash Char(100) primary key, fileName Char(50) not null, content Text);");

        } catch (Exception e) {
            System.err.println("openDB" + e.getMessage());
            System.exit(1);
        }
    }

    public void insert(Document d){
        // insert the information of document into databases
        String sql = "insert into document (hash, fileName, content) values (?, ?, ?);";
        try{
            PreparedStatement statement = con. prepareStatement(sql);
            statement.setString(1, d.getHash());
            statement.setString(2, d.getName());
            statement.setString(3, d.getContent());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return the list of all Document stored in the database
     */

    public List<Document> getAll(){
        // get information of all documents from databases
        String sql = "select hash, content, fileName from document;";
        List<Document> list = new ArrayList<>();
        try{
            PreparedStatement statement = con.prepareStatement(sql);
            ResultSet set = statement.executeQuery();
            while (set.next()){
                String preview = new String(set.getBytes(2), StandardCharsets.UTF_8);
                if (preview.length() > 100)
                    list.add(new Document(set.getString(1), preview.substring(0, 100), set.getString(3), preview.length()));
                else
                    list.add(new Document(set.getString(1), preview, set.getString(3), preview.length()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * This is used to check whether the file exists or not
     * @param hash the hash value of the file
     * @return the file name in the database
     */

    public String getDocument(String hash){
        /* return the name of the file */
        String sql = "select fileName from document where hash = ?";
        String name = "";
        try{
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, hash);
            ResultSet set = statement.executeQuery();
            if (set.next()){
                name = set.getString(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        if (name.equalsIgnoreCase(""))
            return "";
        return name;
    }

    /**
     *
     * @param s is hash value of file
     * @return the content of the file in UTF-8
     */

    public String getDetail(String s){
        // get a detailed content, used when download
        String sql = "select content from document where hash = ?";
        String res = "";
        try{
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, s);
            ResultSet set = statement.executeQuery();
            if (set.next())
                res = new String(set.getBytes(1), StandardCharsets.UTF_8);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public boolean hasHash(String hash){
        String str = getDocument(hash);
        if (str.equalsIgnoreCase(""))
            return false;
        else
            return true;
    }


}
