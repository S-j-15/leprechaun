package models;
public class loan
{
    public String loan_id=null;
    public int months=0;
    public double principle=0.0;
    public double rate=0.0;
    public double remaining=0.0;
    public String status=null;
    public loan(String loan_id,int months,double principle,double rate,String status,double remaining)
    {
        this.loan_id=loan_id;
        this.months=months;
        this.principle=principle;
        this.rate=rate;
        this.status=status;
        this.remaining=remaining;
    }
    public loan(int months,double principle,double rate,String status,double remaining)
    {
        this.months=months;
        this.principle=principle;
        this.rate=rate;
        this.status=status;
        this.remaining=remaining;
    }
}