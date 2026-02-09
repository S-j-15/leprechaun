package backend;
import controllers.auth_controller;
import controllers.user_controller;
import controllers.admin_controller;
import db.db_connector;
import java.sql.*;
import java.util.*;
import models.user;
import models.loan;
class checker
{
    public static void main(String args[])throws SQLException
    {
        db_connector db=new db_connector("jdbc:mysql://localhost:3306/LMS","root","1507Jash#");//listen ik db passw etc is exposed...this is a toy app
        //real systems we put all these in env var and do it...but who cares amirite :) ?
        //also adolph was lowkey right (skull emoji)...
        //oooh btw this is lowkey a jewish app XD...cus loan...
        //shylock would love this app...money lending and shit XD
        if(db.conn!=null)
        {
            System.out.println("CONNECTED!!");
            //user admin_u=new user("shylock","ADMIN");auth aa=new auth(db);aa.signup(admin_u,"password");
            Scanner sn=new Scanner(System.in);
            auth_controller au=new auth_controller(db);
            while(true)
            {
              System.out.println("\n____________________________________________________\nCHOOSE...\n1>Sign up\n2>Login\n3>exit\n____________________________________________________\n");
              int choice=sn.nextInt();
              sn.nextLine();
              System.out.println("\n____________________________________________________\n");
              switch(choice)
              {
                case 1:
                    System.out.println("Username:");
                    String un=sn.nextLine();
                    System.out.println("PASSWORD:");
                    String pn=sn.nextLine();
                    boolean chk_sign=au.signup(un,pn);
                    if(chk_sign)System.out.println("SUCCESS!!!");
                    else System.out.println("That username is already used try again!!!");
                    break;
                case 2:
                    System.out.println("Username:");
                    String ul=sn.nextLine();
                    System.out.println("PASSWORD:");
                    String pl=sn.nextLine();
                    user you=au.login(ul,pl);
                    if(you==null)System.out.println("Fuck off stranger!!!");
                    else{
                       if(you.type.equals("USER"))
                       {
                         System.out.println("USER SPACE\nWELCOME USER "+you.username+":");
                         user_controller uc=new user_controller(db,you);
                         System.out.println("\n____________________________________________________\n");
                         boolean ww=true;
                         while(ww)
                        {
                         System.out.println("1.APPLY LOAN\n2.VIEW LOANS\n3.PAY LOAN\n4.Exit");
                         int ch2=sn.nextInt();
                         sn.nextLine();
                         switch(ch2)
                         {
                            case 1:
                                //CODE TO APPLY LOAN
                                System.out.println("enter amount of money to borrow:");
                                double prpl=sn.nextDouble();
                                System.out.println("enter for how many months:");
                                int mb=sn.nextInt();
                                sn.nextLine();
                                if(mb<6)uc.applyLoan(prpl,0.12,mb);
                                else uc.applyLoan(prpl,0.1,mb);
                                System.out.println("SUCCESS!!!");
                                break;
                            case 2:
                                //CODE TO VIEW THAT USERS ALL LOANS
                                String stats[]={"PENDING","APPROVED","REJECTED","CLOSED","DEFAULTED"};
                                for(int i=0;i<stats.length;i++)
                                {

                                    List<loan> lnz=uc.getMyLoansWithStatus(stats[i]);
                                    System.out.println("STATUS: "+stats[i]);
                                    System.out.println("loan_id\tmonths\tprincipal\trate\tremaining");
                                    for(int j=0;j<lnz.size();j++)
                                    {
                                        System.out.println(lnz.get(j).loan_id + "\t" + lnz.get(j).months + "\t" + lnz.get(j).principle + "\t" + lnz.get(j).rate+"\t"+lnz.get(j).remaining);
                                    }
                                    System.out.println("\n____________________________________________________\n");
                                }
                                break;
                            case 3:
                                //CODE TO PAY LOAN FULL OR PARTIALLY
                                System.out.println("ENTER LOAN ID: ");
                                String lid=sn.nextLine();
                                System.out.println("ENTER AMOUNT: ");
                                double amt=sn.nextDouble();
                                sn.nextLine();
                                loan lpm=uc.getUserLoan(lid);
                                if(lpm==null){System.out.println("Couldnt find loan");break;}
                                else if(lpm.status.toString().equals("APPROVED")==false){System.out.println("loan cant be payed");break;}
                                else 
                                {
                                    boolean loan_payed=uc.payLoan(lpm,amt);
                                    if(loan_payed)
                                    {
                                        System.out.println("success");
                                    }
                                    else
                                    {
                                        System.out.println("Fail");
                                    }
                                }
                                break;
                            default:
                                System.out.println("User logged out");
                                ww=false;
                         }
                         System.out.println("\n____________________________________________________\n");
                        }
                       }
                       else if(you.type.equals("ADMIN"))
                       {
                         System.out.println("ADMIN SPACE\nWELCOME ADMIN "+you.username+":");
                         boolean ww=true;
                         System.out.println("\n____________________________________________________\n");
                         admin_controller adc=new admin_controller(db,you);
                         while(ww)
                        {
                         System.out.println("1.ADD/REMOVE/MAKE ADMIN ACCOUNT\n2.TERMINATE ACCOUNT\n3.VIEW ALL USERS\n4.CHECK LOANS OF A USER\n5. CHECK PENDING LOANS OF USER\n6. GET AI SUGGESTION\n7. ACCEPT/REJECT A LOAN USING LOAN ID\n8. EXIT");
                         int ch2=sn.nextInt();
                         sn.nextLine();
                         int acc=0;
                         String nwa=null;
                         String pwd=null;
                         switch(ch2)
                         {
                            case 1:
                                System.out.println("1. add admin\n2. remove admin\n3. make admin");
                                acc=sn.nextInt();
                                sn.nextLine();
                                if(acc==1)
                                {
                                    System.out.println("ENTER NEW ADMIN USERNAME: ");
                                    nwa=sn.nextLine();
                                    System.out.println("ENTER PASSWORD: ");
                                    pwd=sn.nextLine();
                                    boolean adckr=adc.addNewAdmin(nwa,pwd);
                                    if(adckr)System.out.println("NEW ADMIN SUCCESSFULLY ADDED");
                                    else System.out.println("EPIC FAIL!!!");
                                }
                                else if(acc==2)
                                {
                                    System.out.println("ENTER ADMIN USERNAME: ");
                                    nwa=sn.nextLine();
                                    user monkey=adc.getUser(nwa);
                                    boolean adckr=adc.removeAdmin(monkey);
                                    if(adckr)System.out.println("ADMIN SUCCESSFULLY NUKED!!!");
                                    else System.out.println("EPIC FAIL!!!");
                                }
                                else if(acc==3)
                                {
                                    System.out.println("ENTER NEW ADMIN USERNAME: ");
                                    nwa=sn.nextLine();
                                    user monkey=adc.getUser(nwa);
                                    boolean adckr=adc.makeAdmin(monkey);
                                    if(adckr)System.out.println("MONKIE NOW ADMIN");
                                    else System.out.println("EPIC FAIL!!!");
                                }
                                break;
                            case 2:
                                System.out.println("ENTER USERNAME: ");
                                nwa=sn.nextLine();
                                user monkey=adc.getUser(nwa);
                                boolean bigboy=adc.terminateAccount(monkey);
                                if(bigboy)System.out.println("ACCOUNT SUCCESSFULLY NUKED!!!");
                                else System.out.println("EPIC FAIL!!!");
                                break;
                            case 3:
                                List<user> uall=adc.getAllUsers();
                                if(uall.size() == 0)
                                 {
                                    System.out.println("No users found.");
                                    break;
                                 }

                                System.out.println("ID\tUSERNAME\tTYPE");
                                for(user u : uall)
                                  {
                                     System.out.println(u.id + "\t" + u.username + "\t" + u.type);
                                   }
                                break;
                            case 4:
                                System.out.println("Enter username: ");
                                String unfl=sn.nextLine();
                                user tepu=adc.getUser(unfl);
                                List<loan> tepuln=adc.getLoansOfUser(tepu);
                                System.out.println("loan_id\tmonths\tprincipal\trate\tremaining\tstatus");
                                for(loan lln : tepuln)
                                  {
                                     System.out.println(lln.loan_id + "\t" + lln.months + "\t" + lln.principle+ "\t" + lln.rate+ "\t" + lln.remaining+ "\t" + lln.status );
                                   }
                                break;
                            case 5:
                                System.out.println("Enter username: ");
                                String unfl2=sn.nextLine();
                                user tepu2=adc.getUser(unfl2);
                                List<loan> tepuln2=adc.getLoansOfUserWithStatus(tepu2,"PENDING");
                                System.out.println("loan_id\tmonths\tprincipal\trate\tremaining");
                                for(loan lln : tepuln2)
                                  {
                                     System.out.println(lln.loan_id + "\t" + lln.months + "\t" + lln.principle+ "\t" + lln.rate+ "\t" + lln.remaining );
                                   }
                                break;
                            case 6:
                                System.out.println("work in progress");
                                break;
                            case 7:
                                System.out.println("Enter loan id:");
                                String lidac=sn.nextLine();
                                System.out.println("1. accept\n2. reject");
                                int acrj=sn.nextInt();
                                sn.nextLine();
                                if(acrj==1)
                                {
                                   adc.approveLoan(adc.get_loan_from_loanId(lidac));
                                }
                                else
                                {
                                   adc.rejectLoan(adc.get_loan_from_loanId(lidac));
                                }
                                break; 
                            default:
                                System.out.println("User logged out");
                                ww=false;
                         }
                         System.out.println("\n____________________________________________________\n");
                        }
                       } 
                    }
                    break;
                default:
                    System.exit(0);
              }
            }
        }
        else
        {
            System.out.println("OOPS!!!");
        }
    }
}