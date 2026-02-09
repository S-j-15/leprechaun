package controllers;
import backend.auth;
import db.db_connector;
import models.user;
import java.sql.*;
public class auth_controller
{
    private db_connector db=null;   
    private auth au=null;
    public auth_controller(db_connector db) throws SQLException
    {
        this.db=db;
        this.au=new auth(this.db);
    }
    public boolean signup(String name,String passw)throws SQLException
    {
        String typ="USER";
        user u=new user(name,typ);
        return this.au.signup(u,passw);
    }
    public user login(String name,String passw)throws SQLException
    {
        return this.au.login(name,passw);
    }
}