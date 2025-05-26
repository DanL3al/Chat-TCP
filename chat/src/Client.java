import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client  {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String nickname;
    private Scanner scanner;
    private volatile boolean running = true;

    public Client(){
        initializeClient();
        setNickname();
        new Thread(
                new Runnable() {
                    public void run() {
                        sendMessage();
                    }
                }
        ).start();

        new Thread(
                new Runnable() {
                    public void run() {
                        receiveMessage();
                    }
                }
        ).start();
    }

    private void initializeClient(){
        try{
            socket = new Socket("localhost",1234);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            scanner = new Scanner(System.in);
        }catch (IOException e){
            System.out.println("Error initializing client");
            e.printStackTrace();
        }
    }

    private void setNickname(){
        try{
            System.out.print(in.readLine() + " ");
            nickname = scanner.nextLine();
            out.write(nickname + "\n");
            out.flush();
        }catch (IOException e){
            System.out.println("Error setting nickname");
            closeEverything();
        }
    }

    private void sendMessage(){
        try{
            while(running && socket.isConnected()){
                String messageToSend = scanner.nextLine();
                if(messageToSend.equalsIgnoreCase("/exit")){
                    running = false;
                    out.write("User left the chat" + "\n");
                    out.flush();
                    break;
                }
                out.write(messageToSend + "\n");
                out.flush();
            }
        }catch (IOException e){
            System.out.println("Error sending message");
        }finally {
            closeEverything();
        }
    }

    private void receiveMessage(){
        try{
            String messageFromServer;
            while ((messageFromServer = in.readLine()) != null && running){
                System.out.println(messageFromServer);
            }
        }catch (IOException e){
            System.out.println("Error receiving message");
        }finally {
            closeEverything();
        }
    }


    private void closeEverything(){
        try{
            running = false;
            if(socket != null){
                socket.close();
            }if(in != null){
                in.close();
            }if(out != null){
                out.close();
            }if(scanner != null){
                scanner.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
    }
}
