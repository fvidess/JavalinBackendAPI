package Controller;

import static org.mockito.ArgumentMatchers.notNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.h2.expression.function.SoundexFunction;

import DAO.AccountDAO;
import Exception.BlankUsernameException;
import Exception.InvalidPasswordException;
import Exception.LoginErrorException;
import Model.Account;
import Model.Message;
import Service.AccountService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    private AccountService accountService = new AccountService();
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        
        app.post("/register", this::createAccount);
        app.post("/login", this::loginToAccount);
        app.post("/messages", this::createMessage);
        app.get("/messages", this::getAllMessages);
        app.get("/messages/{message_id}", this::getMessageByID);
        app.delete("/messages/{message_id}", this::deleteMessage);
        app.get("/accounts/{account_id}/messages", this::getMessagesByUserID);
        app.patch("/messages/{message_id}", this::updateMessage);

        return app;
    }

    private void createAccount(Context ctx) {
        Account account = ctx.bodyAsClass(Account.class);
        try{
            ctx.status(200).json(accountService.registerAccount(account));
        }catch(InvalidPasswordException ipe){
            ctx.status(400).result(ipe.getMessage());
        }catch(BlankUsernameException bue){
            ctx.status(400).result(bue.getMessage());
        }catch(SQLException sqle){
            ctx.status(400);
        }
    }

    private void loginToAccount(Context ctx){
        Account account = ctx.bodyAsClass(Account.class);
        try{
            ctx.status(200).json(accountService.loginToAccount(account));
        }catch(LoginErrorException lex){
            ctx.status(401);
        }
    }

    private void createMessage(Context ctx){
        Message msg = ctx.bodyAsClass(Message.class);

        try{
            ctx.status(200).json(accountService.createMessage(msg));
        }catch(BlankUsernameException bue){
            ctx.status(400).result(bue.getMessage());
        }catch(SQLException sqle){
            ctx.status(400);
        }
    }

    private void getAllMessages(Context ctx){
        ctx.status(200).json(accountService.getAllMessages());
    }

    private void getMessageByID(Context ctx){
        Message resp = accountService.getMessageByID(Integer.parseInt(ctx.pathParam("message_id")));

        if(resp == null){
            ctx.status(200);
        }
        else{
            ctx.status(200).json(resp);
        }
    } 

    private void deleteMessage(Context ctx){
        Message resp = accountService.deleteMessage(Integer.parseInt(ctx.pathParam("message_id")));

        if(resp == null){
            ctx.status(200);
        }
        else{
            ctx.status(200).json(resp);
        }
    } 

    private void getMessagesByUserID(Context ctx){
        List<Message> messagesByUserID = accountService.getMessagesByUserID(Integer.parseInt(ctx.pathParam("account_id")));
        ctx.status(200).json(messagesByUserID);
    }

    private void updateMessage(Context ctx){
        try{
            ctx.status(200).json(accountService.updateMessage(Integer.parseInt(ctx.pathParam("message_id")), ctx.bodyAsClass(Message.class).getMessage_text()));
        }catch(BlankUsernameException bue){
            ctx.status(400).result(bue.getMessage());
        }catch(SQLException sqle){
            ctx.status(400);
        }
    }
}
