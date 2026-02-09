package backend;
import models.user;
import db.db_connector;
import java.util.*;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class auth 
{
    db_connector db=null;
    private String salt="I_Hate_Fascists";
    public auth(db_connector db)throws SQLException
    {
        this.db=db;
    }
    private static String sha256(String input)
    {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x",b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean signup(user uobj,String password)throws SQLException
    {
       String arr[]=new String[3];
       arr[0]=uobj.username;
       arr[1]=uobj.type;
       arr[2]=sha256(password+sha256(salt));
        String params[]={arr[0]};
        Map<String,Object> hm=this.db.fetchOne_db("select id,username,usertype from users where username=?",params);
        if (hm!=null)
        {
            return false;
        }
       this.db.update_db("insert into users (username,usertype,passw) values (?,?,?)",arr);
       hm=this.db.fetchOne_db("select id,username,usertype from users where username=?",params);
       uobj.id=hm.get("id").toString();
       return true;
    }
    public user login(String username,String password)throws SQLException
    {
        String params[]={sha256(password+sha256(salt)),username};
        Map<String,Object> hm=this.db.fetchOne_db("select id,username,usertype from users where passw=? and username=?",params);
        if (hm==null)
        {
            return null;
        }
            user u=new user(hm.get("id").toString(),hm.get("username").toString(),hm.get("usertype").toString());
            return u;
    }

}