package net.artux.pda.map.utils;

import net.artux.pda.map.di.scope.PerGameMap;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.UUID;

import kotlin.ExperimentalMultiplatform;
import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf;
import multiplayer.Multiplayer;

@PerGameMap
public class UDPClient {

    private final DatagramSocket clientSocket;

    private final InetAddress IPAddress;
    private UUID id;
    private int mapId;

    public UDPClient() throws SocketException, UnknownHostException {
        clientSocket = new DatagramSocket();
        IPAddress = InetAddress.getByName("95.181.230.245");
    }

    void join(int mapId, String login) throws IOException {
        Multiplayer.Request.JOIN join = Multiplayer.Request.JOIN.newBuilder()
                .setLogin(login)
                .setMapId(0)
                .build();

        sendPacket(join.toByteArray());

        Multiplayer.Response.JOIN responseJoin = Multiplayer.Response.JOIN.parseFrom(receivePacket());
        id = UUID.fromString(responseJoin.getUuid());
        this.mapId = mapId;
    }

    void sendPacket(byte[] arr) throws IOException {
        DatagramPacket sendingPacket = new DatagramPacket(arr, arr.length, IPAddress, 8080);
        clientSocket.send(sendingPacket);
    }

    byte[] receivePacket() throws IOException {
        byte[] arr = new byte[1024];
        DatagramPacket receivingPacket = new DatagramPacket(arr, arr.length);
        clientSocket.receive(receivingPacket);
        return receivingPacket.getData();
    }

    void update(double dx, double dy) throws IOException {
        Multiplayer.Request.GET get = Multiplayer.Request.GET.newBuilder()
                .setUuid(id.toString())
                .setDx(dx)
                .setDy(dy)
                .build();
        sendPacket(get.toByteArray());

        Multiplayer.Response.GET response = Multiplayer.Response.GET.parseFrom(receivePacket());
    }

    void close(){
        // Закройте соединение с сервером через сокет
        clientSocket.close();
    }
}
