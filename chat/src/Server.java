import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private ServerSocket server;
    private List<ClientHandler> clients = new ArrayList<>();
    private static final int PORT = 1234;

    public Server() {
        startServer();
    }

    private void startServer(){
        try{
            server = new ServerSocket(PORT);
            System.out.println("Server started");
        }catch(IOException e){
            shutdown();
        }
    }

    private void run(){
        try {
            while(!server.isClosed()){
                Socket socket = server.accept();
                ClientHandler ch = new ClientHandler(this,socket);
                clients.add(ch);
                new Thread(ch).start();
            }
        } catch (IOException e) {
            shutdown();
        }
    }

    public synchronized void broadcast(String msg, String senderNickname){
        for(ClientHandler ch : clients){
            if (ch.nickname != null && !senderNickname.equals(ch.nickname)) {
                try {
                    ch.out.write(senderNickname + ": " + msg + "\n");
                    ch.out.flush();
                }catch(IOException e){
                    ch.shutdown();
                    clients.remove(ch);
                }
            }
        }
    }

    public void broadcastJoined(String msg,String senderNickname){
        for(ClientHandler ch : clients){
            if (ch.nickname != null && !ch.nickname.equals(senderNickname)) {
                try {
                    ch.out.write(msg + "\n");
                    ch.out.flush();
                } catch (IOException e) {
                    ch.shutdown();
                    clients.remove(ch);
                }
            }
        }
    }

    public synchronized void sendPrivateMessage(String msg, String senderNickname, String nicknameReceiver){
        boolean found = false;
        for(ClientHandler ch : clients){
            if(ch.nickname != null && ch.nickname.equals(nicknameReceiver)){
                try {
                    ch.out.write("[private] " + senderNickname + ": " + msg + "\n");
                    ch.out.flush();
                    found = true;
                }catch(IOException e){
                    ch.shutdown();
                    clients.remove(ch);
                }
                break;
            }
        }
        if(!found){
            for(ClientHandler ch : clients){
                if(ch.nickname != null && ch.nickname.equals(senderNickname)){
                    try {
                        ch.out.write("User " + nicknameReceiver + " not found!\n");
                        ch.out.flush();
                    } catch (IOException e) {
                        ch.shutdown();
                        clients.remove(ch);
                        broadcast(senderNickname + " left the chat.", senderNickname);
                    }
                    break;
                }
            }
        }
    }

    public synchronized void removeClient(ClientHandler ch){
        List<ClientHandler> toRemove = new ArrayList<>();
        for(ClientHandler client : clients){
            if(client.equals(ch)){
                toRemove.add(client);
                break;
            }
        }
        clients.removeAll(toRemove);
    }

    public synchronized boolean nicknameExists(String nickname){
        for(ClientHandler ch : clients){
            if(ch.nickname != null && ch.nickname.equalsIgnoreCase(nickname)){
                return true;
            }
        }
        return false;
    }

    private void shutdown(){
        try{
            for(ClientHandler ch : clients){
                ch.shutdown();
            }
            if(server != null){
                server.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}
