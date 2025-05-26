import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    Socket socket;
    BufferedReader in;
    BufferedWriter out;
    String nickname;
    Server server;

    public ClientHandler(Server server, Socket socket){
        this.server = server;
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            shutdown();
        }
    }

    @Override
    public void run() {
        try{
            out.write("Enter your nickname: " + "\n");
            out.flush();

            String tempNickname = in.readLine();
            // if nickname is not null, verify if it contains only spaces
            tempNickname = tempNickname != null ? tempNickname.trim() : null;
            while (tempNickname == null || server.nicknameExists(tempNickname) || tempNickname.length() <= 2){
                out.write("Enter a valid nickname: " + "\n");
                out.flush();
                tempNickname = in.readLine();
                tempNickname = tempNickname != null ? tempNickname.trim() : null;
            }

            this.nickname = tempNickname;

            System.out.println(nickname + " joined the server");
            server.broadcastJoined(nickname + " joined the chat!" ,this.nickname);

            String message;
            while((message = in.readLine()) != null){
                if(message.startsWith("/private")){

                    int first = message.indexOf(' ');
                    int second = message.indexOf(' ', first+1);
                    if(second != -1){
                        String receiver = message.substring(first+1, second);
                        String privateMessage = message.substring(second+1);
                        server.sendPrivateMessage(privateMessage, nickname, receiver);
                    }
                }else{
                    server.broadcast(message, nickname);
                }
            }
        }catch (IOException e){
            System.err.println("Error in the communication with " + (nickname != null ? nickname : "unknown user"));
        }finally {
            if(nickname != null){
                server.broadcast(nickname + " left the chat!" ,nickname);
                System.out.println(nickname + " left the chat!");
            }
            server.removeClient(this);
            shutdown();
        }
    }

    public void shutdown(){
        try{
            if(socket != null){
                socket.close();
            }if(in != null){
                in.close();
            }if(out != null){
                out.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
