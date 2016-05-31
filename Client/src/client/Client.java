/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import message.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import ui.Frame;

/**
 *
 * @author ttb
 */
public class Client extends Thread{
    private int port = 12345;
    //public String serverAddr;
    private Socket socket;
    private Frame frame;
    private ObjectInputStream In;
    private ObjectOutputStream Out;
    
    // Friend list
    private ArrayList<String> friendList = new ArrayList<>();
    
    public Client(Frame f){
        
        frame = f; 
        //this.serverAddr = ui.serverAddr; 
        //this.port = ui.port;
        try
        {
            socket = new Socket(InetAddress.getByName("localhost"), port);

            Out = new ObjectOutputStream(socket.getOutputStream());
            Out.flush();
            In = new ObjectInputStream(socket.getInputStream());
        }
        catch (Exception e)
        {
            
        }
        //hist = ui.hist;
    }
    
    public void send(Message msg){
        
        try {
            Out.writeObject(msg);
            Out.flush();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        System.out.println("Outgoing : "+msg.toString());  
        
    }

    @Override
    public void run() {
        while(true)
        {
            // Read message
            Message msg = null;
            try {
                msg = (Message) In.readObject();

                if(msg.getType().equals("login")){
                    if(msg.getContent().equals("TRUE"))
                    {
                        frame.jTextAreaShow.append("[SERVER > Me] : Login Successfully\n");
                        frame.jTextFieldMessage.setEditable(true);
                    }
                    else
                    {
                        frame.jTextAreaShow.append("[SERVER > Me] : Login failed\n");
                    }
                }
                else if(msg.getType().equals("signup"))
                {
                    if(msg.getContent().equals("TRUE")){

                        frame.jTextAreaShow.append("[SERVER > Me] : Singup Successful\n");
                    }
                    else
                    {
                        frame.jTextAreaShow.append("[SERVER > Me] : Signup Failed\n");
                    }
                }
                else if(msg.getType().equals("message"))
                {
                    if(msg.getRecipient().equals(frame.username)){
                        frame.jTextAreaShow.append("["+msg.getSender() +" > Me] : " + msg.getContent() + "\n");
                    }
                }
//                else if (msg.getType().equals("newuser"))
//                {
//                    if (!msg.getContent().equals(frame.username))
//                    {
//                        System.out.print("New user: " + msg.getContent());
//                        // JList model
//                        frame.lm.addElement(msg.getContent());
//                    }
//                }
                else if (msg.getType().equals("friendlist"))
                {
                    //System.out.print(msg.getContent());
                    friendList = msg.getListContent();
                    for (int i = 0; i < friendList.size(); i++)
                    {
                        frame.lm.addElement(friendList.get(i));
                    }
                }
                else if (msg.getType().equals("search"))
                {
                    if (msg.getContent().equals("TRUE"))
                    {
                        frame.jTextAreaShow.append("["+msg.getSender() +" > Me] : " + "Add new friend?" + "\n");
                        int kq = JOptionPane.showConfirmDialog(null, "Add this new friend?",
    			        "Confirm", JOptionPane.OK_CANCEL_OPTION);
                        
                        // Update database
    			if (kq == JOptionPane.OK_OPTION)
    			{
                            if (!friendList.contains(frame.jTextFieldSearchNewFriend.getText()))
                            {
                                this.send(new Message("addnewfriend", frame.jTextFieldUsername.getText(), 
                                        frame.jTextFieldSearchNewFriend.getText(), "SERVER"));

                                // Update UI list
                                friendList.add(frame.jTextFieldSearchNewFriend.getText());
                                frame.lm.addElement(frame.jTextFieldSearchNewFriend.getText());
                            }
                        }
                        
                        
                    }
                    else if (msg.getContent().equals("FALSE"))
                    {
                        frame.jTextAreaShow.append("["+msg.getSender() +" > Me] : " + "Username does not exist" + "\n");
                    }
                }


            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    
}
