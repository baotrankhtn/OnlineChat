/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;
import message.Message;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import ui.Frame;

/**
 *
 * @author shop
 */
public class Server extends Thread{
    // Thread for each client
    private ArrayList<ClientThread> clients;
  
    // Server Socket
    private ServerSocket serversocket = null;
    
    // UI
    private Frame frame;
    
    // Accounts database
    private Account accounts;
    
    private int port = 12345;
    
    public Server(Frame f)
    {
        clients = new ArrayList<>();
        frame = f;
        accounts = new Account();
        
        try {
            // Start ServetSocket
            serversocket = new ServerSocket(port);
            
            // Notification
            frame.jTextAreaServer.append("Server started: port " + serversocket.getLocalPort());
            
        } catch (IOException ex) {
            System.out.println("Fail to start server socket");
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() { 
        while (true)  
        {
            try{  
		frame.jTextAreaServer.append("\nWaiting for a client ..."); 
	        addThread(serversocket.accept()); 
	    }
	    catch(Exception ioe){ 
                System.out.println("Can not accept client error");
	    }
        }
    }
    
    private void addThread(Socket s)
    {
        frame.jTextAreaServer.append("\nClient accepted: " + s);
        clients.add(new ClientThread(this, s));
 
        //clients.get(clients.size() - 1).open(); 
        clients.get(clients.size() - 1).start();  
        //clientCount++; 
	
    }
    
    // Find client thread
    private ClientThread findClientThread(String usr){
        for(int i = 0; i < clients.size(); i++){
            if(clients.get(i).getUsername().equals(usr)){
                return clients.get(i);
            }
        }
        return null;
    }
    
    // Find client thread position
    private int findClientThreadPosition(int id)
    {
        for (int i = 0; i < clients.size(); i++)
        {
            if (clients.get(i).getID() == id)
            {
                return i;
            }
        }
        return -1;
    }
    
    // New user anouncement
    private void notifyNewUser(String username)
    {
        for (int i = 0; i < clients.size(); i++)
        {
            clients.get(i).send(new Message("newuser", "SERVER", username, ""));
        }
    }
    
    // Check message 
    public synchronized void checkMessage(int id, Message msg)
    {
        if(msg.getType().equals("login"))
        {
            // No client with this username is already online
            if(findClientThread(msg.getSender()) == null){
                if(accounts.checkLogin(msg.getSender(), (String)msg.getContent())){
                   
                    // Now set user name for thread
                    clients.get(findClientThreadPosition(id)).setUsername(msg.getSender());
                    clients.get(findClientThreadPosition(id)).send(new Message("login", "SERVER", "TRUE", msg.getSender()));
                    
                    // New online user
                    //notifyNewUser(msg.getSender());

                    // Change status: online
                    accounts.setStatus(msg.getSender(), "online");
                    
                    // Send friend list
                    ArrayList<String> friendList = accounts.getFriendList(msg.getSender());
                    clients.get(findClientThreadPosition(id)).send(new Message("friendlist", "SERVER", friendList, msg.getSender()));
                }
                else{
                    clients.get(findClientThreadPosition(id)).send(new Message("login", "SERVER", "FALSE", msg.getSender()));
                } 
            }
            else{
                clients.get(findClientThreadPosition(id)).send(new Message("login", "SERVER", "FALSE", msg.getSender()));
            }   
        }
        else if(msg.getType().equals("signup"))
        {
            if(findClientThread(msg.getSender()) == null)
            {
                if(!accounts.userExists(msg.getSender()))
                {
                    accounts.addUser(msg.getSender(), (String)msg.getContent());
                    clients.get(findClientThreadPosition(id)).setUsername(msg.getSender());
                    clients.get(findClientThreadPosition(id)).send(new Message("signup", "SERVER", "TRUE", msg.getSender()));
                    clients.get(findClientThreadPosition(id)).send(new Message("login", "SERVER", "TRUE", msg.getSender()));
                    //Announce("newuser", "SERVER", msg.sender);
                    //SendUserList(msg.sender);
                }
                else
                {
                    clients.get(findClientThreadPosition(id)).send(new Message("signup", "SERVER", "FALSE", msg.getSender()));
                }
            }
            else
            {
                clients.get(findClientThreadPosition(id)).send(new Message("signup", "SERVER", "FALSE", msg.getSender()));
            }
       
        }
        else if(msg.getType().equals("message"))
        {
            // Write to recipient
            findClientThread(msg.getRecipient()).send(new Message(msg.getType(), msg.getSender(), msg.getContent(), msg.getRecipient()));
            
            // Write to sender
            clients.get(findClientThreadPosition(id)).send(new Message(msg.getType(), msg.getSender(), msg.getContent(), msg.getRecipient())); 
        }
        else if (msg.getType().equals("search"))
        {
            String username = msg.getContent();
            if (accounts.userExists(username))
            {
                clients.get(findClientThreadPosition(id)).send(new Message(msg.getType(), msg.getSender(), "TRUE", msg.getRecipient()));
            }
            else
            {
                clients.get(findClientThreadPosition(id)).send(new Message(msg.getType(), msg.getSender(), "FALSE", msg.getRecipient()));
            }
        }
        else if (msg.getType().equals("addnewfriend"))
        {
           accounts.addNewFriend(msg.getSender(), msg.getContent());
        }
        
    }
}
