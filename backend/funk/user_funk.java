package funk;
import models.loan;
import db.db_connector;
import models.user;
import java.sql.*;
import java.util.*;

public class user_funk
{
    private db_connector db;

    public user_funk(db_connector db) 
    {
        this.db = db;
    }
    public double calculateEMI(double principal, int months, double rate) 
    {
        return (principal*rate*Math.pow(1 + rate,months))/(Math.pow(1+rate,months)-1);
    }
    public void applyLoan(user u, double principle,double rate,int months) throws SQLException
    {
        double rem=calculateEMI(principle,months,rate)*months;
        String params[]={String.valueOf(u.id),String.valueOf(principle),String.valueOf(rate),String.valueOf(months),"PENDING",String.valueOf(rem)};
        this.db.update_db("insert into loan (user_id,principal,interest_rate,months,status,remaining) values (?,?,?,?,?,?)",params);
    }

    public List<loan> getMyLoans(user u) throws SQLException
    {  String params[]={u.id.toString()};
       List<loan> lns=new ArrayList<loan>();
       List<Map<String,Object>> ls=this.db.fetch_db("select * from loan where user_id=?",params);
       for(Map<String,Object> curln : ls)
       {
         loan ll=new loan(curln.get("loan_id").toString(),((Number) curln.get("months")).intValue(),((Number) curln.get("principal")).doubleValue(),((Number) curln.get("interest_rate")).doubleValue(),curln.get("status").toString(),((Number) curln.get("remaining")).doubleValue());
         lns.add(ll);
       } 
       return lns;
    }
    public List<loan> getMyLoansWithStatus(user u,String status) throws SQLException
    {
        String params[]={u.id.toString(),status};
       List<loan> lns=new ArrayList<loan>();
       List<Map<String,Object>> ls=this.db.fetch_db("select * from loan where user_id=? and status=?",params);
       for(Map<String,Object> curln : ls)
       {
         loan ll=new loan(curln.get("loan_id").toString(),((Number) curln.get("months")).intValue(),((Number) curln.get("principal")).doubleValue(),((Number) curln.get("interest_rate")).doubleValue(),curln.get("status").toString(),((Number) curln.get("remaining")).doubleValue());
         lns.add(ll);
       } 
       return lns;
    }
    public loan getUserLoan(user u,String lid) throws SQLException
    {
        String params[]={u.id.toString(),lid};
       Map<String,Object> curln=this.db.fetchOne_db("select * from loan where user_id=? and loan_id=?",params);
       if(curln==null)return null;
       loan ll=new loan(curln.get("loan_id").toString(),((Number) curln.get("months")).intValue(),((Number) curln.get("principal")).doubleValue(),((Number) curln.get("interest_rate")).doubleValue(),curln.get("status").toString(),((Number) curln.get("remaining")).doubleValue());
       return ll;
    }
    public boolean payLoan(user u,loan ln,double amount)throws SQLException 
    {
          String params[]={u.id.toString(),ln.loan_id.toString(),"APPROVED"};
          Map<String,Object> lm=this.db.fetchOne_db("select * from loan where user_id=? and loan_id=? and status=?",params);
          if(lm==null)
          {
            return false;
          }
          double rem=((Number) lm.get("remaining")).doubleValue();
          rem-=amount;
          if(rem<=0)
          {
            String np[]={String.valueOf(0),"CLOSED",ln.loan_id.toString()};
            this.db.update_db("update loan set remaining=?, status=?, closed_at=CURRENT_TIMESTAMP where loan_id=?",np);
            return true;
          }
          String np[]={String.valueOf(rem),ln.loan_id.toString()};
          this.db.update_db("update loan set remaining=? where loan_id=?",np);
          return true;
    }
  
}
