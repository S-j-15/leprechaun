package models;
public class user
{
   public String username;
   public String type;
   public String id;
    public user(String id,String username,String type)
    {
        this.id=id;
        this.username=username;
        this.type=type;
    }
    public user(String username,String type)
    {
        this.username=username;
        this.type=type;
    }
}