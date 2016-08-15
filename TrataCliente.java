/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidormari;

import servidormari.Server;
import servidormari.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Scanner;
import static java.util.Spliterators.iterator;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pedro Gomes
 */
class TrataCliente implements Runnable, Serializable {

    //private ArrayList<User> usuarios;
    private Socket socket;
    private Scanner input;
    private PrintStream output;
    private Thread thread;
    private Server servidor;
    private int permissao;
    private /*transient*/ String auxiliar = "não alterado";
    private String pasta;
    private static final String diretorio = "C:\\Users\\vaio\\Desktop";
    private String[] list;
    private User usuarioLogado;
    private String diretório2;
    
    public TrataCliente(Socket socket, Server servidor) throws Exception {
        
        this.servidor = servidor;
        this.socket = socket;
        this.permissao = -1;
        //this.usuarios = servidor.getUsuarios();
        this.output = new PrintStream(socket.getOutputStream());
        this.input = new Scanner(socket.getInputStream());

    }

    //libera os recursos alocados pelo atendente, cuidado em testar se o atributo foi inicializado corretamente em open()
    void closeTratador() {
        //fecha o input, output e o socket
        if (input != null) {
            try {
                input.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        if (output != null) {
            try {
                output.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        try {
            socket.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        //reinicia os atributos
        input = null;
        output = null;
        socket = null;

        thread = null;
    }

    @Override
    public void run() {
        try {
            output = new PrintStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(TrataCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (true) {

            output.println("---------------------------Bem-Vindo ao Lava Duto-----------------------------\n");
            output.println("1 - Efetuar Cadastro de Usuário");
            output.println("2 - Efetuar Login de Usuário");
            output.println("3 - Sair do Sistema");
            output.println("Digite sua opção aqui ");
            output.println("EOT");

            try{
            String option = input.nextLine();
            switch (option) {

                case "1":{
                try {
                    //Opção de cadastro
                    this.cadastrarCliente();
                } catch (IOException ex) {
                    Logger.getLogger(TrataCliente.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
                    break;

                case "2"://Opção de login
                    try{
                        
                        boolean b = this.logarCliente();
                        if(b == true){ //pode ser também if(this.permissao != -1 pois o valor só é diferente de -1 se houver usuario logado 
                            menuIntermediario();
                            
                        }
                        break;
                        
                    }catch(NoSuchElementException ex){//n sei se esse try catch é mesmo necessario
                        this.servidor.getUsuariosLogados().remove(this.usuarioLogado);
                        break;
                    } catch (IOException ex) {
                        Logger.getLogger(TrataCliente.class.getName()).log(Level.SEVERE, null, ex);
                    }
                
                case "3":
                    sair();
                    break;
       
                default:
                    //System.out.println("Esta entrada não é válida");
                    output.println("Esta entrada não é valida, tente novamente.");
                    break;
                    
            }
            }catch(NoSuchElementException ex){
                this.servidor.getUsuariosLogados().remove(this.usuarioLogado);
                break;
            }

        }
    }

    public void sair(){
                      try {
                            this.permissao = -1;
                            this.servidor.getUsuariosLogados().remove(this.usuarioLogado);
                            socket.close();
                        } catch (IOException ex) {
                            Logger.getLogger(TrataCliente.class.getName()).log(Level.SEVERE, null, ex);
                        }
        
    }
    
    public void menuIntermediario() throws IOException{
        
        while(true){
        
            output.println("-------------------------------Lava Duto---------------------------------\n");
            output.println("1 - Navegar nas Pastas de Arquivos");
            output.println("2 - Sair do Sistema");
            output.println("Digite sua opção aqui: ");
            output.println("EOT");
    
            String option = input.nextLine();
            switch(option){
                case "1":
                    //menuNavegacao(this.permissao);
                    menuNavegacao();
                    break;
                case "2":
                    sair();
                    break;
                default:
                    //System.out.println("Esta entrada não é válida");
                    output.println("Esta entrada não é valida, tente novamente.");
                    break;
            }
        
        }
    }
    
    
    public int menuNavegacao(){
        /*File[] list = File.listRoots();
        String s;
        String caminhoAtual = null;
         */
        File f = new File("C:\\Users\\vaio\\Desktop");
        
        list = f.list();
        String s;
        String caminhoAtual = null;
        
        while(true){
            
            for(String nome : list){
                output.println(nome);
            }
                   
        output.println("------------------------------------MENU DE NAVEGAÇÃO----------------------------------");
        output.println("1 -- Entrar na pasta");
        output.println("2 -- Voltar pasta");
        output.println("3 -- Download");
        output.println("4 -- Menu anterior");
        if (this.permissao > 0) {
            output.println("5 -- Upload");
            output.println("6 -- Excluir Arquivo");
        }
        output.println("Digite sua opção aqui:");
        output.println("EOT");
        
        s = input.nextLine();
        
        
           //cliente
            switch (s) {
                case "1"://Entrar
                    output.println("Digite o nome da pasta:");
                    output.println("EOT");
                    
                    String msg = input.nextLine();
                    
                    this.pasta = msg;
                    preparaServidor("1", f);
                    
                    if(caminhoAtual != null){
                        output.println(caminhoAtual + s + File.separator);
                    }else{
                        output.println(this.pasta);
                    }
                    
                    if(this.auxiliar.equals("Inválido")){
                        output.println("Caminho inválido!\n");
                    }else{
                        caminhoAtual = this.auxiliar;
                        output.println("Você está em: " + caminhoAtual);
                    }
                    
                    break;
                    
                case "2"://Voltar
                    preparaServidor("2", f);
                    if(this.auxiliar.equals("raiz")){
                        output.println("Voce está no diretório raiz");
                        caminhoAtual = null;
                        break;
                    }
                    caminhoAtual = this.auxiliar;
                    output.println("Você está em: " + caminhoAtual);
                    break;
                    
                case "3"://Download
                    output.println("dw");
                    output.println("EOT");
                    if(input.nextLine().equals("pronto")){
                        output.println(this.auxiliar);
                    }
                    break;
                    
                case "4"://Menu Anterior
                    
                    return 0;
                    
                case "5"://Upload
                    if(this.permissao > 0){
                        
                    }else{
                        output.println("Opção inválida, tente novamente");
                    }
                    break;
                    
                case "6"://Remover
                    if(this.permissao > 0){
                        output.println("Nome do Arquivo a deletar: ");
                        output.println("EOT");
                        String nomeDoArquivo = input.nextLine();
                        File file = new File(this.diretório2 + "\\" + nomeDoArquivo);
                        
                        if(file.exists()){
                            if(file.isDirectory()){
                                output.println("Não é possível deletar diretórios, navegue até encontrar um arquivo");
                            }else{
                                if(file.delete()){
                                    output.println("Arquivo deletado com sucesso!");
                                    int i = this.diretório2.lastIndexOf("\\");
                                    this.diretório2 = this.diretório2.substring(0, i);
                                    File f2 = new File(this.diretório2);
                                    list = f2.list();
                                    //this.auxiliar = this.diretório2;
                                }else{
                                    output.println("Falha ao deletar arquivo");
                                }
                            }
                        }else{
                            output.println("Caminho inválido");
                        }
                    }else{
                        output.println("Opção inválida, tente novamente");
                    }
                    
                    break;
                    
                default: 
                    
                    output.println("Opção inválida, tente novamente");
                    break;
                    
            }
        
    }
         
    }
   
    public void preparaServidor(String opcao, /*File[]String[] list,*/ File f){
        String[] lista = new String[50];
        String navega;
        File current;
        //File[] list = File.listRoots(); //aqui
        List<String> caminhos = new Stack();
        
        if(opcao.equals("1")){
            System.out.println("achou a opcao");
            navega = this.diretorio+"\\"+this.pasta;
            System.out.println(navega);
            current = new File(navega);
            if(current.exists()){
                System.out.println("Arquivo Certo");
                //list = current.listFiles();
                list = current.list();
                this.auxiliar = navega;
                this.diretório2 = navega; //para deletar
                //output.println(navega); //aqui
                caminhos.add(navega);
                System.out.println("Passou tudo");
            }else{
                System.out.println("Arquivo errado");
                this.auxiliar = "Inválido";
            }
        }else if(opcao.equals("2")){
            if(!caminhos.isEmpty()){
                caminhos.remove(caminhos.size() - 1);
                if(!caminhos.isEmpty()){
                    current = new File(caminhos.get(caminhos.size() - 1));
                    //list = current.listFiles();
                    list = current.list();
                    this.auxiliar = caminhos.get(caminhos.size() - 1);
                    this.diretório2 = caminhos.get(caminhos.size() - 1);
                }else{
                    //list = File.listRoots();
                    current = f;
                    list = current.list();
                    this.auxiliar = "raiz";
                    this.diretório2 = "raiz";
                }
            }else{
                current = f;
                //list = File.listRoots();
                list = current.list();
                this.auxiliar = "raiz";
                this.diretório2 = "raiz";
            }
        }
    }
    
    public void cadastrarCliente() throws IOException {

        if(servidor.getUsuarios().isEmpty()){
            
                output.println("Defina o nome do usuário:");
                output.println("EOT");
                String login = input.nextLine();
                
                output.println("Digite uma senha:");
                output.println("EOT");
                String senha = input.nextLine();

                User novo = new User(login, senha, 0);
                servidor.getUsuarios().add(novo);
                
                output.println("Usuario cadastrado com sucesso");
                
                this.servidor.salvarListaUsuarios();
                return;
                
        }
        output.println("Defina o nome do usuário:");
        output.println("EOT");

        String login = input.nextLine();
        Iterator it = servidor.getUsuarios().iterator();
        boolean existe = false;
        
        while (it.hasNext()) {

            User u = (User) it.next();
            if (u.getLogin().equals(login)) {
                output.println("Ja existe usuario com esse login.");
                existe = true;
            }
            if (!existe) {
                output.println("Digite uma senha:");
                output.println("EOT");
                String senha = input.nextLine();
                User novo = new User(login, senha, 0);
                servidor.getUsuarios().add(novo);
                output.println("Usuario cadastrado com sucesso");
            }
        }

        this.servidor.salvarListaUsuarios();
    }

    public boolean logarCliente() {
        
        if(this.servidor.getUsuarios().isEmpty()){
            output.println("Ainda não há usuários cadastrados para fazer login. Cadastre-se!");
            return false;
        }
        
        output.println("Digite o login do usuário:");
        output.println("EOT");
        String login = input.nextLine();
        
        Iterator it = servidor.getUsuarios().iterator();
       
        while (it.hasNext()) {

            User u = (User) it.next();
            if(!this.servidor.getUsuariosLogados().contains(u)){
                if (u.getLogin().equals(login)) {
                    output.println("Digite a senha:");
                    output.println("EOT");
                    String senha = input.nextLine();
                    if(u.getSenha().equals(senha)){
                        output.println("Usuario logado com sucesso");
                        this.permissao = u.getPermissao();
                        servidor.getUsuariosLogados().add(u);
                        this.usuarioLogado = u;
                        return true; 
                    }
                    else{
                        output.println("Senha incorreta");
                    //  return false;
                    }
                }
                else{
                    output.println("Login inexistente");
                    //return false;
                }
            }else{
                output.println("o usuário já está logado");
            }
        }
    return false;    
    }
    
    

}
