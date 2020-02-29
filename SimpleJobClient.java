/** Client
 *  EE5400
 *  Java Socket Programming
 *  Simple Job Client
 **/

import java.io.*;
import java.net.*;
import java.util.*;

public class SimpleJobClient {
    public static void main(String[] args) {
        // Obtain host name & port number
        String hostName = "127.0.0.1";
        int portNumber = 9090;
        if (args.length >= 1)
            hostName = args[0];
        if (args.length >= 2)
            portNumber = Integer.parseInt(args[1]);

        // Declare socket and thread
        Socket socket = null;
        SimpleJobClientThread thread = null;

        // Open socket and start thread
        try {
            socket = new Socket(hostName, portNumber);
            thread = new SimpleJobClientThread(socket);
            thread.start();
            thread.join();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host.");
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

final class SimpleJobClientThread extends Thread {
    private Socket socket;

    public SimpleJobClientThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            process();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private void process() throws Exception {
        // Display connection
        String serverInfo = socket.getInetAddress() + ":" + socket.getPort();
        String clientInfo = socket.getLocalAddress() + ":" + socket.getLocalPort();
        System.out.println("Client " + clientInfo + " connected to Server " + serverInfo);
        // Open input/output streams
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        // Process job
        
        double errorMargin = 0.4;
        // Random rand = new Random();
        boolean done = false;
        while (!done) {
            String inputLine;
            String outputLine;
            // Request job
            // Task #1: fill next line
            outputLine = "requestJob";
            System.out.println("client ->  " + outputLine);
            out.println(outputLine);
            sleep(100);
            // Process feedback
            inputLine = in.readLine();
            System.out.println("client <-  " + inputLine);
            String token[] = inputLine.split(" ");
            // interpret response and take proper action

            // if job is assigned 
            if (token[0].equals("assignJob") || token[0].equals("rejectJob")) {
                // random working time
                sleep(100);
                outputLine = "submitJob " + token[1] + " " + (1 + errorMargin * (double)Math.round(Math.random()));
                System.out.println("client ->  " + outputLine);
                out.println(outputLine);
            }
            else if(token[0].equals("wait"))
            {
                sleep(100);
            }
            else if (token[0].equals("acceptJob")) {
                outputLine = "requestJob";
                System.out.println("client ->  " + outputLine);
                out.println(outputLine);
            }
            else{
                done = true;
            }
            
        }
        // Close streams and socket
        out.close();
        in.close();
        socket.close();
    }
}
