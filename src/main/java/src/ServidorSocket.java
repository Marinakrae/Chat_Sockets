package src;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorSocket {
    public static void main(String[] args) {
        ServerSocket servidor = null;
        try {
            System.out.println("Startando o servidor");
            servidor = new ServerSocket(9999);
            System.out.println("Servidor startado");

            while(true){
                Socket cliente = servidor.accept();
                new GerenciadorDeClientes(cliente);
            }
        } catch (IOException e) {
            System.err.println("A porta está ocupada ou o servidor foi fechado");
            try {
                servidor.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
        }
    }
}
