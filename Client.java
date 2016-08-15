/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientemari;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import javax.swing.JFileChooser;

/**
 *
 * @author Pedro Gomes 
 */
public class Client {

    public static void main(String[] args) throws Exception {

        Socket socket;
        Scanner input;
        PrintStream output;

        System.out.println("Iniciando o cliente...");
        System.out.println("Iniciando conexão com o servidor...");

        Socket cliente = new Socket("127.0.0.1", 12345);

        System.out.println("Conexão estabelecida com sucesso!");

        //Leitura do teclado
        Scanner scanner = new Scanner(System.in);

        //Chega do servidor
        input = new Scanner(cliente.getInputStream());

        //Vai para o servidor
        output = new PrintStream(cliente.getOutputStream());

        String serverString;
        while (true) {
            
            try{            
                serverString = input.nextLine();
                System.out.println(serverString);
                                    

                //Continua lendo até encontrar o EOT 
                while (!serverString.equals("EOT")) {
                    serverString = input.nextLine();

                    if (!serverString.equals("EOT")) {
                        System.out.println(serverString);
                    }if(serverString.equals("dw")){
                        Scanner scan = new Scanner(System.in);
                        String nome = scan.nextLine();
                        baixar(nome, cliente);
                    }

                }
            output.println(scanner.nextLine());

            }catch(NoSuchElementException ex){
                break;
            }
        }
        System.out.println("Encerrando...");
                
        cliente.close();
    }

    private static void baixar(String nome, Socket cliente) throws IOException {
        //Chega do servidor
        InputStream is = cliente.getInputStream();
        Scanner input = new Scanner(is);
        
        //Vai para o servidor
        OutputStream os = cliente.getOutputStream();
        PrintStream output = new PrintStream(os);
        
        output.println("pronto");
        String diretorio = input.nextLine();
 
        JFileChooser jfChooser = new JFileChooser();
        jfChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfChooser.setDialogTitle("Escolha o diretório");
        int op = jfChooser.showOpenDialog(null);
        if(op == JFileChooser.APPROVE_OPTION){
            diretorio = jfChooser.getSelectedFile().getAbsolutePath();
            File file = new File (diretorio+"\\"+nome);          
            byte[] cbuffer = new byte[4029];
            int bytesRead;	
            try {
            FileOutputStream fos = new FileOutputStream(file);                        
            DataInputStream dis = new DataInputStream(is);
            long tamanho = dis.readLong();
            long count = 0;
            System.out.println("Recebendo...");
            do {
                bytesRead = is.read(cbuffer);
		count = count + bytesRead;
		fos.write(cbuffer, 0, bytesRead);
		fos.flush();		
            } while (bytesRead != -1 && count < tamanho);
            fos.close();
            System.out.println("Arquivo recebido!");
            } catch (Exception ex) {
                System.out.println("Não foi posível baixar o arquivo");
            }
        }else{
            System.out.println("Você não selecionou nenhum diretório");
        }        	

        
    }
}
