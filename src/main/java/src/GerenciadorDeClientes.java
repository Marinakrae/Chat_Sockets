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

            efetuarLogin();
            String msg;

            while (true){
                msg = leitor.readLine();
                if(msg.equalsIgnoreCase(Comandos.SAIR)){
                    this.cliente.close();
                } else if (msg.startsWith(Comandos.MENSAGEM)) {
                    //pegar o cliente com base no nome informado
                    String nomeDestinatario = msg.substring(Comandos.MENSAGEM.length(), msg.length());
                    GerenciadorDeClientes destinatario = clientes.get(nomeDestinatario);
                    if (destinatario == null) {
                        escritor.println("O cliente informado nao existe");
                    } else {
                        //escritor.println("Digite uma menssagem para " + destinatario.getNomeCliente());
                        destinatario.getEscritor().println(this.nomeCliente + " disse: " + leitor.readLine());
                    }
                }
                //Lista o nome de todos os clientes logados
                else if (msg.equals(Comandos.LISTA_USUARIOS)){
                    atualizarListaUsuarios(this);
                }else {
                    escritor.println(this.nomeCliente + ", você disse: "+ msg);
                }
            }

        } catch (IOException e) {
            System.out.println("O cliente fechou a conexão");
            clientes.remove(this.nomeCliente);
            throw new RuntimeException(e);
        }
    }

    private void efetuarLogin() throws IOException {

        while(true){
            escritor.println(Comandos.LOGIN);
            this.nomeCliente = leitor.readLine().toLowerCase().replaceAll(",","");

            //teste pra ver se n é null
            if(this.nomeCliente.equalsIgnoreCase("null") || this.nomeCliente.isEmpty()){
                escritor.println(Comandos.LOGIN_NEGADO);
            }
            //teste para n add o mesmo usuario
            else if(clientes.containsKey(this.nomeCliente)){

            } else {
                this.nomeCliente = this.nomeCliente;
                escritor.println(Comandos.LOGIN_ACEITO);
                escritor.println("Ola, "+this.nomeCliente);
                //Colocar o cliente na lista
                clientes.put(this.nomeCliente, this);

                for(String cliente: clientes.keySet()){
                    atualizarListaUsuarios(clientes.get(cliente));
                }
                break;
            }
        }
    }

    private void atualizarListaUsuarios(GerenciadorDeClientes cliente) {
        StringBuffer str = new StringBuffer();
        for(String c: clientes.keySet()){
            //testar para que o user n veja ele mesmo
            if(cliente.getNomeCliente().equals(c))
                continue;
            str.append(c);
            str.append(",");
        }
        if (str.length() > 0)
            str.delete(str.length()-1, str.length());
        cliente.getEscritor().println(Comandos.LISTA_USUARIOS);
        cliente.getEscritor().println(str.toString());
    }


}
