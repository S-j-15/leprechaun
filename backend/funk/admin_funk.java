package funk;
import models.loan;
import db.db_connector;
import backend.auth;
import models.user;
import java.sql.*;
import java.util.*;
public class admin_funk
{
  private db_connector db;

    public admin_funk(db_connector db) throws SQLException
    {
        this.db = db;
    }
    public boolean add_new_admin(String username,String passw)throws SQLException
    {
       auth au=new auth(this.db);
       String typ="ADMIN";
       user u=new user(username,typ);
       return au.signup(u,passw);
    }
    public boolean make_admin(user u)throws SQLException
    {
        String params[]={u.id.toString()};
        Map<String,Object> uu=this.db.fetchOne_db("select username from users where id=?",params);
        if(uu==null)return false;
        String par[]={"ADMIN",u.id.toString()};
        this.db.update_db("update users set usertype=? where id=?",par);
        return true;
    }
    public boolean remove_admin(user u)throws SQLException
    {
        String params[]={u.id.toString(),"ADMIN"};
        Map<String,Object> uu=this.db.fetchOne_db("select username from users where id=? and usertype=?",params);
        if(uu==null)return false;
        String par[]={"USER",u.id.toString()};
        this.db.update_db("update users set usertype=? where id=?",par);
        return true;
    }
    public boolean terminate_account(user u)throws SQLException
    {
        String params[]={u.id.toString()};
        Map<String, Object> uu=this.db.fetchOne_db("select username from users where id=?",params);
        if (uu==null)return false;
        this.db.update_db("delete from users where id=?",params);
        return true;
    }
    public List<user> get_all_users()throws SQLException
    {
       List<user> users = new ArrayList<>();
       String params[]={};
       List<Map<String,Object>> res=db.fetch_db("select * from users",params);
        for(Map<String,Object> row : res) 
        {
            user u=new user(row.get("id").toString(),row.get("username").toString(),row.get("usertype").toString());
            users.add(u);
        }
       return users;   
    }
    public user get_user(String username)throws SQLException
    {
        String params[]={username};
        Map<String,Object>row=db.fetchOne_db("select * from users where username=?",params);
        if(row==null)return null;
        return new user(row.get("id").toString(),row.get("username").toString(),row.get("usertype").toString());
    }
    public List<loan> get_loans_of_user(user u)throws SQLException
    {
      user_funk uf=new user_funk(this.db);
      return uf.getMyLoans(u);
    }
    public loan get_loan_from_loanId(String lid)throws SQLException
    {
        String params[]={lid};
        Map<String,Object> row=db.fetchOne_db("select * from loan where loan_id=?", params);
        if(row == null) return null;
        return new loan(row.get("loan_id").toString(),((Number)row.get("months")).intValue(),((Number)row.get("principal")).doubleValue(),((Number)row.get("interest_rate")).doubleValue(),row.get("status").toString(),((Number)row.get("remaining")).doubleValue());
    }
    public List<loan> get_loans_of_user_with_status(user u,String status)throws SQLException
    {
       user_funk uf=new user_funk(this.db);
       return uf.getMyLoansWithStatus(u,status);
    }
    public String get_ai_suggestion(loan lnz)
    {
        return "to be implemented";
    }
    public boolean approve_loan(loan lnz)throws SQLException
    {
       String params[]={lnz.loan_id.toString()};
       Map<String,Object> skidrow=db.fetchOne_db("select * from loan where loan_id=?",params);
       if(skidrow==null || skidrow.get("status").toString().equals("REJECTED")|| skidrow.get("status").toString().equals("CLOSED")|| skidrow.get("status").toString().equals("DEFAULTED"))return false;
       if(skidrow.get("status").toString().equals("APPROVED"))return true;
       String params2[]={"APPROVED",lnz.loan_id.toString()};
       db.update_db("update loan set status=? , approved_at=CURRENT_TIMESTAMP where loan_id=?",params2);
       return true;
    }
    public boolean reject_loan(loan lnz)throws SQLException
    {
       String params[]={lnz.loan_id.toString()};
       Map<String,Object> skidrow=db.fetchOne_db("select * from loan where loan_id=?",params);
       if(skidrow==null || skidrow.get("status").toString().equals("APPROVED")|| skidrow.get("status").toString().equals("CLOSED")|| skidrow.get("status").toString().equals("DEFAULTED"))return false;
       if(skidrow.get("status").toString().equals("REJECTED"))return true;
       String params2[]={"REJECTED",lnz.loan_id.toString()};
       db.update_db("update loan set status=? where loan_id=?",params2);
       return true;
    }
}