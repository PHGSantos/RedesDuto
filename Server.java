/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidormari;

/**
 *
 * @author Pedro Gomes
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import servidormari.User;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mariane Sotero e Pedro Gomes
 */
public class Server implements Serializable {

    //private ArrayList<User> usuarios;
    private ConcurrentLinkedQueue<User> usuarios;
    private transient ConcurrentLinkedQueue<User> usuariosLogados;
    private ServerSocket serverSocket;
    //private transient static int porta;

    public Server() throws Exception {

        this.usuarios = new ConcurrentLinkedQueue<User>();
        this.usuariosLogados = new ConcurrentLinkedQueue<User>();
       // this.porta = 12345;
    
    }

    //a main só chama o inicio e o  fim do servidor
    public static void main(String[] args) throws Exception {
        
        Server servidor = null;
        
        try{
        servidor = (Server) Server.carregarListaUsuarios();
        }catch(FileNotFoundException ex){} 
         catch(IOException ex){}  
         catch(ClassNotFoundException ex){}
        
        if(servidor == null){
            servidor = new Server();
        }else{
            servidor.startServer();
        }
        
        System.out.println("Inicializando as operações do servidor");
            
        servidor.serverSocket = new ServerSocket(12345);
        Server.cadastrarUsuarioEspecial(servidor);
        //servidor = new Server();
        //servidor.startServer();
        
        while (true) {
            Socket socket = servidor.serverSocket.accept();
            System.out.println("conexao realizada");
            TrataCliente tratador = new TrataCliente(socket, servidor);
            Thread t = new Thread(tratador);
            t.start();
        }
    }

    private void startServer() throws Exception {
        //serverSocket = new ServerSocket(porta);
        
        System.out.println("Status do servidor: Online");
        this.usuariosLogados = new ConcurrentLinkedQueue<User>();
    }

    public ConcurrentLinkedQueue<User> getUsuarios() {
        return usuarios;
    }

    public ConcurrentLinkedQueue<User> getUsuariosLogados() {
        return usuariosLogados;
    }

    public void salvarListaUsuarios() {
        try { 
            FileOutputStream out = new FileOutputStream("Cliente.txt");
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(this/*.usuarios*/);
            out.close();
            objOut.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Object carregarListaUsuarios() throws FileNotFoundException, IOException, 
            ClassNotFoundException{
            FileInputStream fis = new FileInputStream("Cliente.txt");
            ObjectInputStream ois = new ObjectInputStream(fis);
            return ois.readObject();

    }

    public static void cadastrarUsuarioEspecial(Server servidor){
        User usuarioEspecial = new User("phgsantos", "admin", 1);
        servidor.usuarios.add(usuarioEspecial);
    }
    
}
