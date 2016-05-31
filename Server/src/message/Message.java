/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package message;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author shop
 */
public class Message implements Serializable{
    
    private static long serialVersionUID = 1L;
    private String type, sender, recipient, content;
    private ArrayList<String> listContent;
    
    public Message(String type, String sender, String content, String recipient){
        this.type = type; this.sender = sender; this.content = content; this.recipient = recipient;
    }
    
    public Message(String type, String sender, ArrayList<String> content, String recipient){
        this.type = type; this.sender = sender; this.listContent = content; this.recipient = recipient;
    }
    
    @Override
    public String toString(){
        return "{type='"+getType()+"', sender='"+getSender()+"', content='"+getContent()+"', recipient='"+getRecipient()+"'}";
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the sender
     */
    public String getSender() {
        return sender;
    }

    /**
     * @param sender the sender to set
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the recipient
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * @param recipient the recipient to set
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    /**
     * @return the listContent
     */
    public ArrayList<String> getListContent() {
        return listContent;
    }

    /**
     * @param listContent the listContent to set
     */
    public void setListContent(ArrayList<String> listContent) {
        this.listContent = listContent;
    }
}
