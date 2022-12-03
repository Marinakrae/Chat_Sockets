package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class GerenciadorDeClientes extends Thread{

    private Socket cliente;
    private String nomeCliente;
    //Lista de clientes
    private static final Map<String, GerenciadorDeClientes> clientes =
            new HashMap<String, GerenciadorDeClientes>();
    private BufferedReader leitor;
    private PrintWriter escritor;

//    public BufferedReader getLeitor() {
//        return leitor;
//    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public PrintWriter getEscritor() {
        return escritor;
    }

    public GerenciadorDeClientes(Socket cliente) {
        this.cliente = cliente;
        start();
    }

    @Override
    public void run() {
        try {
            //pegando a mensagem do cliente
            leitor = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            //Mandando uma mensagem para o cliente
            escritor = new PrintWriter(cliente.getOutputStream(), true); //o flush é o envio para o cliente
            escritor.println("Ola, por favor, digite o seu nome: ");
            String msg = leitor.readLine();
            this.nomeCliente = msg;
            escritor.println("Ola, "+this.nomeCliente);
            //Colocar o cliente na lista
            clientes.put(this.nomeCliente, this);

            while (true){
                msg = leitor.readLine();
                if(msg.equalsIgnoreCase("::SAIR")){
                    this.cliente.close();
                } else if (msg.toLowerCase().startsWith("::msg")) {
                    //pegar o cliente com base no nome informado
                    String nomeDestinatario = msg.substring(5, msg.length());
                    GerenciadorDeClientes destinatario = clientes.get(nomeDestinatario);
                    if (destinatario == null) {
                        escritor.println("O cliente informado nao existe");
                    } else {
                        escritor.println("Digite uma menssagem para " + destinatario.getNomeCliente());
                        destinatario.getEscritor().println(this.nomeCliente + " disse: " + leitor.readLine());
                    }
                }
                //Lista o nome de todos os clientes logados
                else if (msg.equals("::listar-clientes")){
                    StringBuffer str = new StringBuffer();
                    for(String c: clientes.keySet()){
                        str.append(c);
                        str.append(",");
                    }
                    str.delete(str.length()-1, str.length());
                    escritor.println(str.toString());
                }else {
                    escritor.println(this.nomeCliente + ", você disse: "+ msg);
                }
            }

        } catch (IOException e) {
            System.out.println("O cliente fechou a conexão");
            throw new RuntimeException(e);
        }
    }
}
