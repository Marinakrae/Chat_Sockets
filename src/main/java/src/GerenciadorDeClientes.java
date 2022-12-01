package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GerenciadorDeClientes extends Thread{

    private Socket cliente;
    private String nomeCliente;

    public GerenciadorDeClientes(Socket cliente) {
        this.cliente = cliente;
        start();
    }

    @Override
    public void run() {
        try {
            //pegando a mensagem do cliente
            BufferedReader leitor = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            //Mandando uma mensagem para o cliente
            PrintWriter escritor = new PrintWriter(cliente.getOutputStream(), true); //o flush é o envio para o cliente
            escritor.println("Ola, por favor, digite o seu nome: ");
            String msg = leitor.readLine();
            this.nomeCliente = msg;
            escritor.println("Ola, "+this.nomeCliente);

            while (true){
                msg = leitor.readLine();
                escritor.println(this.nomeCliente + ", você disse: "+ msg);
            }
        } catch (IOException e) {
            System.out.println("O cliente fechou a conexão");
            throw new RuntimeException(e);
        }
    }
}
