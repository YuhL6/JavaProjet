package dao;

import com.alibaba.fastjson.JSON;
import model.Document;
import org.apache.commons.lang3.RandomStringUtils;
import org.sql2o.Sql2o;
import ucar.units.Base;

import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

public class TextDao {
    private Connection con;
    private Base64.Decoder decoder = Base64.getDecoder();
    private Base64.Encoder encoder = Base64.getEncoder();
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
            statement.executeUpdate("create table if not exists document (hash Char(100) primary key, name Char(50) not null, " +
                    "comment Char(100), bytes Text);");

        } catch (Exception e) {
            System.err.println("openDB" + e.getMessage());
            System.exit(1);
        }
    }

    public void insert(Document d){
        // insert the information of document into databases
        String sql = "insert into document (hash, name, comment, bytes) values (?, ?, ?, ?);";
        try{
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, d.getHash());
            statement.setString(2, d.getName());
            statement.setString(3, d.getComment());
            statement.setBytes(4, d.getBytes());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getAll(){
        // get information of all documents from databases
        String sql = "select hash, name, comment from document;";
        List<Document> list = new ArrayList<>();
        try{
            PreparedStatement statement = con.prepareStatement(sql);
            ResultSet set = statement.executeQuery();
            while (set.next()){
                list.add(new Document(set.getString(1), new byte[0], set.getString(2), set.getString(3)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return JSON.toJSONString(list);
    }

    public String getRough(String s){
        // get a roughly content
        String sql = "select bytes from document where hash = ?;";
        String res = "";
        try{
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, s);
            ResultSet set = statement.executeQuery();
            if (set.next())
                res = String.valueOf(set.getString(1));
            else
                return "";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public String getDetail(String s){
        // get a detailed content, used when download
        String sql = "select bytes from document where hash = ?";
        String res = "";
        try{
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, s);
            ResultSet set = statement.executeQuery();
            if (set.next())
                res = String.valueOf(set.getString(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }


}
