import controllers.auth_controller;
import controllers.user_controller;
import controllers.admin_controller;
import db.db_connector;
import java.sql.*;
import models.user;
import models.loan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CheckerGUI {

    private JFrame frame;
    private CardLayout cards;
    private JPanel rootPanel;

    private db_connector db;
    private auth_controller au;
    private user currentUser;

    private Color bg = new Color(18,18,22);
    private Color sidebar = new Color(28,28,34);
    private Color card = new Color(36,36,42);
    private Color accent = new Color(0,120,215);
    private Color text = new Color(230,230,230);

    public CheckerGUI() throws SQLException {

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}

        db = new db_connector("jdbc:mysql://localhost:3306/LMS","root","1507Jash#");
        au = new auth_controller(db);

        initUI();
    }

    private void initUI() {
        frame = new JFrame("LMS – Loan Management System");
        frame.setSize(1100,700);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1000,600));

        cards = new CardLayout();
        rootPanel = new JPanel(cards);

        rootPanel.add(buildStartPanel(), "start");
        rootPanel.add(buildUserDashboard(), "user");
        rootPanel.add(buildAdminDashboard(), "admin");

        frame.setContentPane(rootPanel);
        frame.setVisible(true);

        cards.show(rootPanel,"start");
    }

    /* ====================== START SCREEN ====================== */

    private JPanel buildStartPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(bg);

        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(card);
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(40,60,40,60));

        JLabel title = new JLabel("Loan Management System");
        title.setFont(new Font("Segoe UI",Font.BOLD,26));
        title.setForeground(text);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton login = styledButton("Login");
        JButton signup = styledButton("Sign Up");
        JButton exit = styledButton("Exit");

        login.addActionListener(e -> {try{onLogin();} catch(Exception ignored){}});
        signup.addActionListener(e -> {try{onSignup();}catch(Exception ignored){}});
        exit.addActionListener(e -> System.exit(0));

        cardPanel.add(title);
        cardPanel.add(Box.createRigidArea(new Dimension(0,30)));
        cardPanel.add(login);
        cardPanel.add(Box.createRigidArea(new Dimension(0,15)));
        cardPanel.add(signup);
        cardPanel.add(Box.createRigidArea(new Dimension(0,15)));
        cardPanel.add(exit);

        panel.add(cardPanel);
        return panel;
    }

    /* ====================== USER DASHBOARD ====================== */

    private JPanel buildUserDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bg);

        JPanel side = sidebarPanel("USER");

        JButton apply = styledButton("Apply Loan");
        JButton view = styledButton("View Loans");
        JButton pay = styledButton("Pay Loan");
        JButton logout = styledButton("Logout");

        side.add(apply);
        side.add(view);
        side.add(pay);
        side.add(logout);

        JTextArea content = styledTextArea();

        apply.addActionListener(e -> {
            try {
                String a = JOptionPane.showInputDialog("Amount:");
                if(a==null) return;
                String m = JOptionPane.showInputDialog("Months:");
                if(m==null) return;

                double amount = Double.parseDouble(a);
                int months = Integer.parseInt(m);
                double rate = (months<6)?0.12:0.10;

                new user_controller(db,currentUser).applyLoan(amount,rate,months);
                content.append("Applied loan: "+amount+" | "+months+" months\n");
            } catch(Exception ignored){}
        });

        view.addActionListener(e -> {
            try {
                user_controller uc = new user_controller(db,currentUser);
                String[] stats = {"PENDING","APPROVED","REJECTED","CLOSED","DEFAULTED"};
                content.setText("");
                for(String s:stats){
                    java.util.List<loan> list = uc.getMyLoansWithStatus(s);
                    content.append("\n=== "+s+" ===\n");
                    for(loan l:list){
                        content.append(l.loan_id+" | "+l.principle+" | "+l.remaining+"\n");
                    }
                }
            } catch(Exception ignored){}
        });

        pay.addActionListener(e -> {
            try{
                String id = JOptionPane.showInputDialog("Loan ID:");
                if(id==null) return;
                String amt = JOptionPane.showInputDialog("Amount:");
                if(amt==null) return;
                loan l = new user_controller(db,currentUser).getUserLoan(id);
                new user_controller(db,currentUser).payLoan(l,Double.parseDouble(amt));
                content.append("Paid "+amt+" for "+id+"\n");
            }catch(Exception ignored){}
        });

        logout.addActionListener(e -> {
            currentUser=null;
            cards.show(rootPanel,"start");
        });

        panel.add(side,BorderLayout.WEST);
        panel.add(new JScrollPane(content),BorderLayout.CENTER);

        return panel;
    }

    /* ====================== ADMIN DASHBOARD ====================== */

    private JPanel buildAdminDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bg);

        JPanel side = sidebarPanel("ADMIN");

        JButton viewUsers = styledButton("View All Users");
        JButton checkLoans = styledButton("Check User Loans");
        JButton approve = styledButton("Approve/Reject Loan");
        JButton logout = styledButton("Logout");

        side.add(viewUsers);
        side.add(checkLoans);
        side.add(approve);
        side.add(logout);

        JTextArea content = styledTextArea();

        viewUsers.addActionListener(e -> {
            try{
                java.util.List<user> users = new admin_controller(db,currentUser).getAllUsers();
                content.setText("ID | USERNAME | TYPE\n\n");
                for(user u:users){
                    content.append(u.id+" | "+u.username+" | "+u.type+"\n");
                }
            }catch(Exception ignored){}
        });

        checkLoans.addActionListener(e -> {
            try{
                String un = JOptionPane.showInputDialog("Username:");
                if(un==null) return;
                admin_controller adc = new admin_controller(db,currentUser);
                java.util.List<loan> list = adc.getLoansOfUser(adc.getUser(un));
                content.setText("Loans of "+un+"\n\n");
                for(loan l:list){
                    content.append(l.loan_id+" | "+l.status+" | "+l.remaining+"\n");
                }
            }catch(Exception ignored){}
        });

        approve.addActionListener(e -> {
            try{
                String id = JOptionPane.showInputDialog("Loan ID:");
                if(id==null) return;
                String[] opt = {"Approve","Reject"};
                int sel = JOptionPane.showOptionDialog(frame,"Action?","Approve/Reject",
                        JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE,
                        null,opt,opt[0]);
                admin_controller adc = new admin_controller(db,currentUser);
                loan l = adc.get_loan_from_loanId(id);
                if(sel==0) adc.approveLoan(l);
                else adc.rejectLoan(l);
                content.append("Updated loan "+id+"\n");
            }catch(Exception ignored){}
        });

        logout.addActionListener(e -> {
            currentUser=null;
            cards.show(rootPanel,"start");
        });

        panel.add(side,BorderLayout.WEST);
        panel.add(new JScrollPane(content),BorderLayout.CENTER);

        return panel;
    }

    /* ====================== LOGIN & SIGNUP ====================== */

    private void onLogin() throws SQLException {
        JTextField u = new JTextField();
        JPasswordField p = new JPasswordField();
        Object[] fields = {"Username:",u,"Password:",p};

        int ok = JOptionPane.showConfirmDialog(frame,fields,"Login",JOptionPane.OK_CANCEL_OPTION);
        if(ok!=JOptionPane.OK_OPTION) return;

        user you = au.login(u.getText(),new String(p.getPassword()));
        if(you==null){
            JOptionPane.showMessageDialog(frame,"Invalid credentials");
            return;
        }

        currentUser = you;
        if("USER".equals(you.type)) cards.show(rootPanel,"user");
        else cards.show(rootPanel,"admin");
    }

    private void onSignup() throws SQLException {
        JTextField u = new JTextField();
        JPasswordField p = new JPasswordField();
        Object[] fields = {"Username:",u,"Password:",p};

        int ok = JOptionPane.showConfirmDialog(frame,fields,"Sign Up",JOptionPane.OK_CANCEL_OPTION);
        if(ok!=JOptionPane.OK_OPTION) return;

        if(au.signup(u.getText(),new String(p.getPassword())))
            JOptionPane.showMessageDialog(frame,"Account Created");
        else
            JOptionPane.showMessageDialog(frame,"Username already exists");
    }

    /* ====================== UI HELPERS ====================== */

    private JButton styledButton(String textVal){
        JButton b = new JButton(textVal);
        b.setFocusPainted(false);
        b.setBackground(accent);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI",Font.BOLD,14));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));

        b.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){ b.setBackground(accent.darker()); }
            public void mouseExited(MouseEvent e){ b.setBackground(accent); }
        });

        return b;
    }

    private JTextArea styledTextArea(){
        JTextArea t = new JTextArea();
        t.setBackground(card);
        t.setForeground(text);
        t.setFont(new Font("Consolas",Font.PLAIN,14));
        t.setCaretColor(text);
        t.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        return t;
    }

    private JPanel sidebarPanel(String role){
        JPanel p = new JPanel();
        p.setBackground(sidebar);
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        p.setPreferredSize(new Dimension(250,0));

        JLabel r = new JLabel(role+" DASHBOARD");
        r.setForeground(text);
        r.setFont(new Font("Segoe UI",Font.BOLD,18));
        r.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(r);
        p.add(Box.createRigidArea(new Dimension(0,25)));

        return p;
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            try { new CheckerGUI(); }
            catch(Exception ignored){}
        });
    }
}
