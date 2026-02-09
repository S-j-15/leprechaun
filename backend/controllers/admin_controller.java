package controllers;
import funk.admin_funk;
import funk.user_funk;
import db.db_connector;
import models.user;
import models.loan;
import java.sql.*;
import java.util.*;

public class admin_controller
{
    private db_connector db = null;
    private admin_funk af = null;
    private user admin = null;
    public admin_controller(db_connector db, user admin) throws SQLException
    {
        this.db = db;
        this.af = new admin_funk(db);
        this.admin = admin;
    }
    public boolean addNewAdmin(String username, String password) throws SQLException
    {
        return af.add_new_admin(username, password);
    }

    public boolean makeAdmin(user u) throws SQLException
    {
        return af.make_admin(u);
    }
    public boolean removeAdmin(user u) throws SQLException
    {
        return af.remove_admin(u);
    }
    public boolean terminateAccount(user u) throws SQLException
    {
        return af.terminate_account(u);
    }
    public List<user> getAllUsers() throws SQLException
    {
        return af.get_all_users();
    }
    public user getUser(String username) throws SQLException
    {
        return af.get_user(username);
    }
    public List<loan> getLoansOfUser(user u) throws SQLException
    {
        return af.get_loans_of_user(u);
    }
    public List<loan> getLoansOfUserWithStatus(user u, String status) throws SQLException
    {
        return af.get_loans_of_user_with_status(u, status);
    }
    public boolean approveLoan(loan ln) throws SQLException
    {
        return af.approve_loan(ln);
    }
    public boolean rejectLoan(loan ln) throws SQLException
    {
        return af.reject_loan(ln);
    }
    public String getAISuggestion(loan ln)
    {
        return af.get_ai_suggestion(ln);
    }
    public loan get_loan_from_loanId(String lid)throws SQLException
    {
        return af.get_loan_from_loanId(lid);
    }
}
