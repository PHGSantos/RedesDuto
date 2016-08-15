/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidormari;

import java.io.Serializable;

/**
 *
 * @author Pedro Gomes e Mariane Sotero
 */
public class User implements Serializable{
    
    private String login;
    private String senha;
    private transient boolean online;
    private int permissao;
    
    public User(String login, String senha, int permissao){
        
        this.login = login;
        this.senha = senha;
        this.permissao = permissao;
        this.online = false;
    
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    public boolean isOnline(){
        return this.online;
    }
    
    public void setOnline(boolean online){
        this.online = online;
    }
    
    public int getPermissao(){
        return this.permissao;
    }
    
    @Override
    public boolean equals(Object o){
        if(o instanceof User){
            User auxiliar = (User) o;
            return (this.login.equalsIgnoreCase(auxiliar.getLogin()) && this.senha.equalsIgnoreCase(auxiliar.getSenha()));
        }
        return false;
    }
}

