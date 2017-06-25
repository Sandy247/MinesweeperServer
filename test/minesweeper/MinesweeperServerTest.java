package minesweeper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Random;

import minesweeper.server.MinesweeperServer;
import org.junit.Test;
public class MinesweeperServerTest {


    private static final String LOCALHOST = "127.0.0.1";

    private static final int MAX_CONNECTION_ATTEMPTS = 10;

    private static final String BOARDS_PKG = "/home/sandy/Advanced Software Construction in Java/ps2/boards/";

    /**
     * Start a MinesweeperServer in debug mode with a board file from BOARDS_PKG.
     *
     * @param boardFile board to load
     * @return thread running the server
     * @throws IOException if the board file cannot be found
     */
    private static Thread startMinesweeperServer(String boardFile,int PORT) throws IOException {

        final String boardPath = new File(BOARDS_PKG+boardFile).getAbsolutePath();
        final String[] args = new String[] {
                "--debug",
                "--port", Integer.toString(PORT),
                "--file", boardPath
        };
        Thread serverThread = new Thread(() -> MinesweeperServer.main(args));
        serverThread.start();
        return serverThread;
    }

    /**
     * Connect to a MinesweeperServer and return the connected socket.
     *
     * @param server abort connection attempts if the server thread dies
     * @return socket connected to the server
     * @throws IOException if the connection fails
     */
    private static Socket connectToMinesweeperServer(Thread server,int PORT) throws IOException {
        int attempts = 0;
        while (true) {
            try {
                Socket socket = new Socket(LOCALHOST, PORT);
                socket.setSoTimeout(3000);
                return socket;
            } catch (ConnectException ce) {
                if ( ! server.isAlive()) {
                    throw new IOException("Server thread not running");
                }
                if (++attempts > MAX_CONNECTION_ATTEMPTS) {
                    throw new IOException("Exceeded max connection attempts", ce);
                }
                try { Thread.sleep(attempts * 10); } catch (InterruptedException ie) { }
            }
        }
    }

    @Test(timeout = 10000)
    public void MyTest1() throws IOException {

        int PORT = 4000 + new Random().nextInt(1 << 15);

        Thread thread = startMinesweeperServer("board_file_5",PORT);

        Socket socket = connectToMinesweeperServer(thread,PORT);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        assertTrue("expected HELLO message", in.readLine().startsWith("Welcome"));

        out.println("look");
        assertEquals("- - - - - - -", in.readLine());
        assertEquals("- - - - - - -", in.readLine());
        assertEquals("- - - - - - -", in.readLine());
        assertEquals("- - - - - - -", in.readLine());
        assertEquals("- - - - - - -", in.readLine());
        assertEquals("- - - - - - -", in.readLine());
        assertEquals("- - - - - - -", in.readLine());

        out.println("dig 3 1");
        assertEquals("- - - - - - -", in.readLine());
        assertEquals("- - - 1 - - -", in.readLine());
        assertEquals("- - - - - - -", in.readLine());
        assertEquals("- - - - - - -", in.readLine());
        assertEquals("- - - - - - -", in.readLine());
        assertEquals("- - - - - - -", in.readLine());
        assertEquals("- - - - - - -", in.readLine());

        out.println("dig 4 1");
        assertEquals("BOOM!", in.readLine());

        out.println("look"); // debug mode is on
        assertEquals("             ", in.readLine());
        assertEquals("             ", in.readLine());
        assertEquals("             ", in.readLine());
        assertEquals("             ", in.readLine());
        assertEquals("             ", in.readLine());
        assertEquals("1 1          ", in.readLine());
        assertEquals("- 1          ", in.readLine());

        out.println("bye");
        socket.close();
    }
    @Test(timeout = 10000)
    public void myTest2()throws IOException
    {

        int PORT = 4000 + new Random().nextInt(1 << 15);
        Thread thread = startMinesweeperServer("board_file_3",PORT);
        Socket socket = connectToMinesweeperServer(thread,PORT);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        assertTrue("expected HELLO message", in.readLine().startsWith("Welcome"));

        out.println("look");
        assertEquals("- - - - - -", in.readLine());
        assertEquals("- - - - - -", in.readLine());
        assertEquals("- - - - - -", in.readLine());
        assertEquals("- - - - - -", in.readLine());
        assertEquals("- - - - - -", in.readLine());
        assertEquals("- - - - - -", in.readLine());

        out.println("dig 3 1");
        assertEquals("- - - - - -", in.readLine());
        assertEquals("- - - 3 - -", in.readLine());
        assertEquals("- - - - - -", in.readLine());
        assertEquals("- - - - - -", in.readLine());
        assertEquals("- - - - - -", in.readLine());
        assertEquals("- - - - - -", in.readLine());

        out.println("dig 0 5");
        assertEquals("BOOM!", in.readLine());

        out.println("bye");
        socket.close();

    }
    @Test(timeout = 10000)
    public void myTest3()throws IOException
    {

        int PORT = 4000 + new Random().nextInt(1 << 15);
        Thread thread = startMinesweeperServer("board_file_2",PORT);
        Socket socket = connectToMinesweeperServer(thread,PORT);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        assertTrue("expected HELLO message", in.readLine().startsWith("Welcome"));

        out.println("look");
        assertEquals("- - - - - -", in.readLine());
        assertEquals("- - - - - -", in.readLine());
        assertEquals("- - - - - -", in.readLine());
        assertEquals("- - - - - -", in.readLine());
        assertEquals("- - - - - -", in.readLine());
        assertEquals("- - - - - -", in.readLine());
        assertEquals("- - - - - -", in.readLine());

        out.println("dig 1 0");
        assertEquals("BOOM!", in.readLine());

        out.println("bye");
        socket.close();
    }
    @Test(timeout =10000)
    public void myTest4()throws IOException
    {

        int PORT = 4000 + new Random().nextInt(1 << 15);
        Thread thread = startMinesweeperServer("board_file_4",PORT);
        Socket socket = connectToMinesweeperServer(thread,PORT);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        assertTrue("expected HELLO message", in.readLine().startsWith("Welcome"));

        out.println("look");
        assertEquals("- - - - - -", in.readLine());
        assertEquals("- - - - - -", in.readLine());
        assertEquals("- - - - - -", in.readLine());
        assertEquals("- - - - - -", in.readLine());
        assertEquals("- - - - - -", in.readLine());
        assertEquals("- - - - - -", in.readLine());

        out.println("dig 1 1");
        assertEquals("BOOM!", in.readLine());

        out.println("bye");
        socket.close();
    }

}
