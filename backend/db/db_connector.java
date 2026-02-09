package db;
import java.sql.*;
import java.util.*;
public class db_connector 
{
    String db,user,password;
    public Connection conn=null;
    public db_connector(String db, String user, String password) throws SQLException
    {
        this.db=db;
        this.user=user;
        this.password=password;
        this.conn=DriverManager.getConnection(this.db,this.user,this.password);
    }
    public List<Map<String,Object>> fetch_db(String cmds,String params[]) throws SQLException
    {
        if(this.conn==null)return null;
        PreparedStatement ps=this.conn.prepareStatement(cmds);
        for(int i=0;i<params.length;i++)ps.setString(i+1,params[i]);
        ResultSet res=ps.executeQuery();
        List<Map<String,Object>> rows = new ArrayList<>();
        ResultSetMetaData md = res.getMetaData();
        int n=md.getColumnCount();
        while(res.next())
        {
            Map<String,Object> row=new HashMap<>();
            for(int i=1;i<n+1;i++)
            {
                row.put(md.getColumnLabel(i),res.getObject(i));
            }
            rows.add(row);
        }
        res.close();
        ps.close();
        return rows;
    }
    public Map<String,Object> fetchOne_db(String cmds,String params[])throws SQLException
    {
        if(this.conn==null)return null;
        PreparedStatement ps=this.conn.prepareStatement(cmds);
        for(int i=0;i<params.length;i++)ps.setString(i+1,params[i]);
        ResultSet res=ps.executeQuery();
        List<Map<String,Object>> rows = new ArrayList<>();
        ResultSetMetaData md = res.getMetaData();
        int n=md.getColumnCount();
        Map<String,Object> row=new HashMap<>();
        boolean b=res.next();
        if(!b)
        {
            res.close();
            ps.close();
            return null;
        }
        for(int i=1; i<=n;i++)row.put(md.getColumnLabel(i),res.getObject(i));
        res.close();
        ps.close();
        return row;
    }
    public void update_db(String cmds,String params[])throws SQLException
    {
        PreparedStatement ps=this.conn.prepareStatement(cmds);
        for(int i=0;i<params.length;i++)ps.setString(i+1,params[i]);
        ps.executeUpdate();
        ps.close();
    }
    public void close_db() throws SQLException
    {
        if(this.conn!=null)
        this.conn.close();
        this.conn=null;
    }

}