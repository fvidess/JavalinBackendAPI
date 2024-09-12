package Service;

import java.sql.SQLException;
import java.util.List;

import DAO.AccountDAO;
import Exception.BlankUsernameException;
import Exception.InvalidPasswordException;
import Exception.LoginErrorException;
import Model.Account;
import Model.Message;

public class AccountService {
    private AccountDAO actDAO = new AccountDAO();

    public Account registerAccount(Account account) throws InvalidPasswordException, BlankUsernameException, SQLException{
        if(account.getPassword().length()<4){
            throw new InvalidPasswordException("");
        }
        if(account.getUsername().isBlank()){
            throw new BlankUsernameException("");
        }
        if(actDAO.accountExists(account, true)){
            throw new SQLException();
        }
        return actDAO.createAccount(account.getUsername(), account.getPassword());
    }

    public Account loginToAccount(Account account) throws LoginErrorException{
        if(!actDAO.validLogin(account)){
            throw new LoginErrorException("");
        }
        return actDAO.getAccount(account);
    }

    public Message createMessage(Message msg) throws BlankUsernameException, SQLException{
        if(msg.getMessage_text().isBlank() || msg.getMessage_text().length()>255){
            throw new BlankUsernameException("");
        }
        Account act = new Account();
        act.setAccount_id(msg.getPosted_by());
        if(!actDAO.accountExists(act, false)){
            throw new SQLException();
        }
        return actDAO.createMessage(msg);
    }
        
    public List<Message> getAllMessages(){
        return actDAO.getAllMessages();
    }

    public Message getMessageByID(int id){
        Message msg = actDAO.getMessageByID(id);
        return msg == null? null: msg;
    }

    public Message deleteMessage(int id){
        Message msg = actDAO.deleteMessage(id);
        return msg == null? null: msg;
    }

    public List<Message> getMessagesByUserID(int id){
        List<Message> messagesByUserID = actDAO.getMessagesByUserID(id);
        return messagesByUserID;
    }

    public Message updateMessage(int msg_id, String newText) throws BlankUsernameException, SQLException{
        if(newText.isBlank() || newText.length()>255){
            throw new BlankUsernameException("");
        }
        if(actDAO.getMessageByID(msg_id)==null){
            throw new SQLException();
        }
        return actDAO.updateMessage(msg_id, newText);
    }
}
