package minishop.networking;

import minishop.networking.jsonprotocol.MiniShopClientJsonWorker;
import minishop.services.IMiniShopServices;

import java.net.Socket;

public class MiniShopConcurrentServer extends AbsConcurrentServer {

    private final IMiniShopServices services;

    public MiniShopConcurrentServer(int port, IMiniShopServices services) {
        super(port);
        this.services = services;
        System.out.println("MiniShopConcurrentServer created");
    }

    @Override
    protected Thread createWorker(Socket client) {
        MiniShopClientJsonWorker worker = new MiniShopClientJsonWorker(services, client);
        return new Thread(worker);
    }
}