package server;

import com.google.gson.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static server.ResponseUtil.responseOk;

/**
 * The type Main.
 *
 * @author namvdo
 */
public class Main {
    public static void main(String[] args) {
        (new Server()).executeServer();
    }
}

