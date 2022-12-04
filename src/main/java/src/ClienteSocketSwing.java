package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClienteSocketSwing extends JFrame {

    @Serial
    private static final long serialVersionUID = 0;
    private JTextArea taEditor = new JTextArea("Digite aqui sua mensagem");
    private JTextArea taVisor= new JTextArea();
    private JList liUsuarios = new JList();
    private PrintWriter escritor;
    private BufferedReader leitor;

    public ClienteSocketSwing() {
        setTitle("Chat com sockets");
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        liUsuarios.setBackground(Color.PINK);
        taEditor.setBackground(Color.cyan);

        taEditor.setPreferredSize(new Dimension(400, 40));
        taVisor.setEditable(false);
        liUsuarios.setPreferredSize(new Dimension(100, 140));

        add(taEditor, BorderLayout.SOUTH);
        add(new JScrollPane(taVisor), BorderLayout.CENTER);
        add(new JScrollPane(liUsuarios), BorderLayout.WEST);

        pack();
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

//        String[] usuarios = new String[]{"Marina", "Arisa"};
//        preencherListaUsuarios(usuarios);
    }

    private void iniciarEscritor(){
      taEditor.addKeyListener(new KeyListener() {
          @Override
          public void keyTyped(KeyEvent e) {
          }

          @Override
          public void keyPressed(KeyEvent e) {
              if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                  //escrevendo para o servidor
                  if(taVisor.getText().isEmpty()){
                      return;
                  }

                  Object usuario = liUsuarios.getSelectedValue();
                  if(usuario != null){
                      //jogando no visor
                      taVisor.append("Eu: ");
                      taVisor.append(taEditor.getText());
                      taVisor.append("\n");

                      escritor.println(Comandos.MENSAGEM+usuario);
                      escritor.println(taEditor.getText());

                      //limpando o editor
                      taEditor.setText("");
                      e.consume();
                  } else {
                      if(taVisor.getText().equalsIgnoreCase(Comandos.SAIR)) {
                          System.exit(0);
                      }
                      JOptionPane.showMessageDialog(ClienteSocketSwing.this, "Selecione um usuario");
                      return;
                  }

              }
          }

          @Override
          public void keyReleased(KeyEvent e) {

          }
      });
    }

    private void preencherListaUsuarios(String[] usuarios){
        DefaultListModel modelo = new DefaultListModel();
        liUsuarios.setModel(modelo);
        for (String usuario: usuarios){
            modelo.addElement(usuario);
        }
    }

    public void iniciarChat() {
        try {
            //conectar ao servidor
            final Socket cliente = new Socket("LOCALHOST", 9999);
            escritor = new PrintWriter(cliente.getOutputStream(), true);
            leitor = new BufferedReader(new InputStreamReader(cliente.getInputStream()));

        } catch (UnknownHostException e) {
            System.out.println("O endereco passado é invalido");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("O servidor pode estar fora do ar");
            throw new RuntimeException(e);
        }

    }

    private void iniciarLeitor() {
        //Lendo mensagens do servidor
        try {
            while(true){
                String mensagem = leitor.readLine();
                if (mensagem == null || mensagem.isEmpty())
                    continue;

                //recebe o texto
                if (mensagem.startsWith(Comandos.LISTA_USUARIOS)){
                    String[] usuarios =
                            leitor.readLine().split(",");
                    preencherListaUsuarios(usuarios);
                } else if (mensagem.equals(Comandos.LOGIN)){
                    String login = JOptionPane.showInputDialog("Qual o seu login?");
                    escritor.println(login);
                } else if (mensagem.equals(Comandos.LOGIN_NEGADO)){
                    JOptionPane.showMessageDialog(
                            ClienteSocketSwing.this, "o login é inválido");
                } else if (mensagem.equals(Comandos.LOGIN_ACEITO)){
                    atualizarListaUsuarios();
                }else {
                    taVisor.append(mensagem);
                    taVisor.append("\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Impossivel ler a mensagem recebida");
            throw new RuntimeException(e);
        }
    }

    private void atualizarListaUsuarios() {
        escritor.println(Comandos.LISTA_USUARIOS);
    }

    public static void main(String[] args) {
        ClienteSocketSwing cliente = new ClienteSocketSwing();
        cliente.iniciarChat();
        cliente.iniciarEscritor();
        cliente.iniciarLeitor();
    }
}
