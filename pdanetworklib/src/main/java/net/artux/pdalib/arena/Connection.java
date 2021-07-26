package net.artux.pdalib.arena;

public class Connection {

    public String ip;
    public String token;
    public String session;

    public Connection(String ip, String token, String session) {
        this.ip = ip;
        this.token = token;
        this.session = session;
    }
}
