package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClienteSocket {
    public static void main(String[] args) {
        try {
            //conectar ao servidor
            final Socket cliente = new Socket("LOCALHOST", 9999);

            //É possivel escrever e ler ao mesmo tempo

            //Lendo mensagens do servidor
            new Thread(){
                @Override
                public void run() {
                    try {
                        BufferedReader leitor =
                                new BufferedReader(new InputStreamReader(cliente.getInputStream()));

                        while(true){
                            String mensagem = leitor.readLine();
                            System.out.println("O servidor disse - "+mensagem);
                        }

                    } catch (IOException e) {
                        System.out.println("Impossivel ler a mensagem recebida");
                        throw new RuntimeException(e);
                    }
                }
            }.start();

            //Escrevendo para o servidor
            PrintWriter escritor = new PrintWriter(cliente.getOutputStream(), true);
            BufferedReader leitorTerminal =
                    new BufferedReader(new InputStreamReader(System.in));

            while(true){
                String mensagemTerminal = leitorTerminal.readLine();
                escritor.println(mensagemTerminal);
            }

        } catch (UnknownHostException e) {
            System.out.println("O endereco passado é invalido");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("O servidor pode estar fora do ar");
            throw new RuntimeException(e);
        }

    }
}
