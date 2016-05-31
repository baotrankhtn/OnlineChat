/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import message.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ttb
 */
public class ClientThread extends Thread{
    private Server server = null;
    
    // Socket to connect with each client socket
    private Socket socket = null;
    
    // id to distinguish among sockets
    private int id = -1;
    private String username = "";
    private ObjectInputStream streamIn  =  null;
    private ObjectOutputStream streamOut = null;

    public ClientThread(Server _server, Socket _socket){  
    	super();
        server = _server;
        socket = _socket;
        id     = socket.getPort();
        try {
            //ui = _server.ui;
            streamOut = new ObjectOutputStream(socket.getOutputStream());
            streamOut.flush();
            streamIn = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    /**
     * @return the id of thread
     */
    public int getID()
    {
        return id;
    }
    
    public void run()
    {  
        while (true){  
    	    try{  
                Message msg = (Message) streamIn.readObject();
    	    	server.checkMessage(id, msg);
            }
            catch(Exception ioe){  
            	System.out.println(" ERROR reading: " + ioe.getMessage());
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ioe);
                //server.remove(ID);
                //stop();
            }
        }
    }
    
    // Send to user
    public void send(Message msg){
        try {
            streamOut.writeObject(msg);
            streamOut.flush();
        } 
        catch (IOException ex) {
            System.out.println("Exception SocketClient: send()");
        }
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
