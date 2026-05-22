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

public class MiniShopClientJsonWorker implements Runnable, IMiniShopObserver {

    private final IMiniShopServices server;
    private final Socket connection;
    private BufferedReader input;
    private PrintWriter output;
    private final ObjectMapper mapper = new ObjectMapper();
    private volatile boolean connected;

    public MiniShopClientJsonWorker(IMiniShopServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            output = new PrintWriter(connection.getOutputStream(), true);
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (connected) {
            try {
                String requestJson = input.readLine();
                if (requestJson == null || requestJson.isEmpty()) continue;

                Request request = mapper.readValue(requestJson, Request.class);
                Response response = handleRequest(request);
                if (response != null) {
                    sendResponse(response);
                }
            } catch (IOException e) {
                connected = false;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Response handleRequest(Request request) {
        try {
            switch (request.getType()) {
                case LOGIN -> {
                    System.out.println("Login request ...");
                    User user;
                    synchronized (server) {
                        user = server.login(request.getUsername(), request.getPassword(), this);
                    }
                    return JsonProtocolUtils.createLoginResponse(user);
                }
                case LOGOUT -> {
                    System.out.println("Logout request ...");
                    User user = DTOUtils.getFromDTO(request.getUser());
                    synchronized (server) {
                        server.logout(user);
                    }
                    connected = false;
                    return JsonProtocolUtils.createOkResponse();
                }
                case GET_ALL_PRODUCTS -> {
                    System.out.println("GetAllProducts request ...");
                    List<Product> products;
                    synchronized (server) {
                        products = server.getAllProducts();
                    }
                    return JsonProtocolUtils.createGetAllProductsResponse(products);
                }
                case SEARCH_PRODUCTS -> {
                    System.out.println("SearchProducts request ...");
                    List<Product> products;
                    synchronized (server) {
                        products = server.searchProducts(request.getSearchQuery());
                    }
                    return JsonProtocolUtils.createSearchProductsResponse(products);
                }
                case PLACE_ORDER -> {
                    System.out.println("PlaceOrder request ...");
                    synchronized (server) {
                        server.placeOrder(request.getUserId(), request.getProductQuantities());
                    }
                    return JsonProtocolUtils.createOkResponse();
                }
                default -> {
                    return JsonProtocolUtils.createErrorResponse("Request necunoscut");
                }
            }
        } catch (Exception e) {
            connected = false;
            return JsonProtocolUtils.createErrorResponse(e.getMessage());
        }
    }

    private void sendResponse(Response response) {
        try {
            String jsonString = mapper.writeValueAsString(response);
            System.out.println("Sending response: " + jsonString);
            synchronized (output) {
                output.println(jsonString);
                output.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void productUpdated(Product product) {
        System.out.println("Product updated: " + product.getId());
        try {
            sendResponse(JsonProtocolUtils.createProductUpdatedResponse(product));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}