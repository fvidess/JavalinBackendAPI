package DAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Model.Account;
import Model.Message;
import Util.ConnectionUtil;

public class AccountDAO {
    public Account getAccount (Account act){
        try(Connection con = ConnectionUtil.getConnection();
            PreparedStatement st = con.prepareStatement("select account_id from account where username = ? and password= ?")){
            st.setString(1, act.getUsername());
            st.setString(2, act.getPassword());
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                return new Account(rs.getInt(1), act.getUsername(), act.getPassword());
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean validLogin (Account act) {
        try(Connection con = ConnectionUtil.getConnection();
            PreparedStatement st = con.prepareStatement("select account_id from account where username = ? and password= ?")){
            st.setString(1, act.getUsername());
            st.setString(2, act.getPassword());
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                return true;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean accountExists(Account act, boolean type) throws SQLException{
        ResultSet rs = null;
        try(Connection con = ConnectionUtil.getConnection();
            PreparedStatement st = con.prepareStatement("select count(*) from account where username = ?");
            PreparedStatement ps = con.prepareStatement("select count(*) from account where account_id = ?")){
            
            if(type){
                st.setString(1, act.getUsername());
                rs = st.executeQuery();
            }
            else{
                ps.setInt(1, act.getAccount_id());
                rs = ps.executeQuery();               
            }

            while(rs.next()){
                if(rs.getInt(1)>0) return true;
            }
        }
        return false;
    }

    public Account createAccount(String username, String password){
        Account act = null;
        try(Connection con = ConnectionUtil.getConnection();
            PreparedStatement st = con.prepareStatement("insert into account(username, password) values (?,?)", Statement.RETURN_GENERATED_KEYS)){
            st.setString(1, username);
            st.setString(2, password);
            st.executeUpdate();

            ResultSet rs = st.getGeneratedKeys();
            while(rs.next()){
                act = new Account(rs.getInt(1), username, password);
            }

            
        }catch(SQLException e){
            e.printStackTrace();
        }
        return act;
    }

    public Message createMessage(Message msg){
        Message message = null;
        try(Connection con = ConnectionUtil.getConnection();
            PreparedStatement st = con.prepareStatement("insert into message(posted_by, message_text, time_posted_epoch) values (?,?,?)",
             Statement.RETURN_GENERATED_KEYS)){
            st.setInt(1, msg.getPosted_by());
            st.setString(2, msg.getMessage_text());
            st.setLong(3, msg.getTime_posted_epoch());
            st.executeUpdate();

            ResultSet rs = st.getGeneratedKeys();
            if(rs.first()){
                return message = new Message(rs.getInt(1), msg.getPosted_by(), msg.getMessage_text(), msg.getTime_posted_epoch());
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return message;
    }

    public List<Message> getAllMessages(){
        List<Message> allMessages = new ArrayList<Message>();
        String sql = "select * from message";
        try(Connection con = ConnectionUtil.getConnection(); Statement st = con.createStatement() ){
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()){
                allMessages.add(new Message(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getLong(4)));
            }
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
        return allMessages;
    }

    public Message getMessageByID(int id) {
        try(Connection con = ConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement("select * from message where message_id = ?") ){
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                return new Message(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getLong(4));
            }
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
        return null;
    }

    public Message deleteMessage(int id){
        Message msg = getMessageByID(id);
        try(Connection con = ConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement("delete from message where message_id = ?") ){
            ps.setInt(1, id);
            int rs = ps.executeUpdate();
            if(rs>0){
                return msg;
            }
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
        return null;
    }

    public List<Message> getMessagesByUserID(int id){
        List<Message> allMessages = new ArrayList<Message>();
        try(Connection con = ConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement("select * from message where posted_by = ?") ){
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                allMessages.add(new Message(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getLong(4)));
            }
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
        return allMessages;
    }

    public Message updateMessage(int msg_id, String newText){
        Message message = null;
        try(Connection con = ConnectionUtil.getConnection();
            PreparedStatement ps = con.prepareStatement(("update message set message_text = ? where message_id = ?"),
             Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, newText);
            ps.setInt(2, msg_id);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if(rs.first()){
                return getMessageByID(msg_id);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return message;
    }
}
