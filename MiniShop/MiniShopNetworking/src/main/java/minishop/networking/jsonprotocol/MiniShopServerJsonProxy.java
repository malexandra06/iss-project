package minishop.networking.jsonprotocol;

import com.fasterxml.jackson.databind.ObjectMapper;
import minishop.model.Product;
import minishop.model.User;
import minishop.networking.Response;
import minishop.networking.dto.DTOUtils;
import minishop.services.IMiniShopObserver;
import minishop.services.IMiniShopServices;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MiniShopServerJsonProxy implements IMiniShopServices {

    private final String host;
    private final int port;
    private final ObjectMapper mapper = new ObjectMapper();

    private Socket connection;
    private BufferedReader input;
    private PrintWriter output;
    private IMiniShopObserver client;
    private final BlockingQueue<Response> responses = new LinkedBlockingQueue<>();
    private volatile boolean finished;

    public MiniShopServerJsonProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public User login(String username, String password, IMiniShopObserver observer) throws Exception {
        initializeConnection();
        sendRequest(JsonProtocolUtils.createLoginRequest(username, password));
        Response response = readResponse();
        if (response.getType() == ResponseType.LOGIN) {
            this.client = observer;
            return DTOUtils.getFromDTO(response.getUser());
        }
        if (response.getType() == ResponseType.ERROR) {
            closeConnection();
            throw new Exception(response.getErrorMessage());
        }
        return null;
    }

    @Override
    public void logout(User user) throws Exception {
        sendRequest(JsonProtocolUtils.createLogoutRequest(user));
        Response response = readResponse();
        closeConnection();
        if (response.getType() == ResponseType.ERROR) {
            throw new Exception(response.getErrorMessage());
        }
    }

    private void sendRequest(Request request) throws Exception {
        String jsonRequest = mapper.writeValueAsString(request);
        System.out.println("Sending request: " + jsonRequest);
        synchronized (output) {
            output.println(jsonRequest);
            output.flush();
        }
    }

    private Response readResponse() throws Exception {
        return responses.take();
    }

    private void initializeConnection() throws IOException {
        connection = new Socket(host, port);
        input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        output = new PrintWriter(connection.getOutputStream(), true);
        finished = false;
        startReaderThread();
    }

    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            client = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startReaderThread() {
        Thread readerThread = new Thread(this::readerRun);
        readerThread.setDaemon(true);
        readerThread.start();
    }

    private boolean isUpdate(Response response) {
        return response.getType() == ResponseType.PRODUCT_UPDATED;
    }

    private void handleUpdate(Response response) {
        if (response.getType() == ResponseType.PRODUCT_UPDATED && response.getProduct() != null) {
            System.out.println("Product updated notification");
            Product product = DTOUtils.getFromDTO(response.getProduct());
            if (client != null) {
                client.productUpdated(product);
            }
        }
    }

    private void readerRun() {
        while (!finished) {
            try {
                String responseJson = input.readLine();
                if (responseJson == null || responseJson.isEmpty()) continue;

                Response response = mapper.readValue(responseJson, Response.class);
                System.out.println("Response received: " + response.getType());

                if (isUpdate(response)) {
                    handleUpdate(response);
                } else {
                    responses.put(response);
                }
            } catch (Exception e) {
                System.out.println("Reading error: " + e.getMessage());
                finished = true;
            }
        }
    }

    @Override
    public List<Product> getAllProducts() throws Exception {
        if (connection == null || connection.isClosed()) {
            initializeConnection();
        }
        sendRequest(JsonProtocolUtils.createGetAllProductsRequest());
        Response response = readResponse();
        if (response.getType() == ResponseType.ERROR) {
            throw new Exception(response.getErrorMessage());
        }
        return DTOUtils.getFromDTOList(response.getProducts());
    }
}