package minishop.networking.jsonprotocol;

import minishop.model.Product;
import minishop.model.User;
import minishop.networking.Response;
import minishop.networking.dto.DTOUtils;

import java.util.List;

public class JsonProtocolUtils {


    public static Request createLoginRequest(String username, String password) {
        Request req = new Request();
        req.setType(RequestType.LOGIN);
        req.setUsername(username);
        req.setPassword(password);
        return req;
    }

    public static Request createLogoutRequest(User user) {
        Request req = new Request();
        req.setType(RequestType.LOGOUT);
        req.setUser(DTOUtils.getDTO(user));
        return req;
    }

    public static Request createGetAllProductsRequest() {
        Request req = new Request();
        req.setType(RequestType.GET_ALL_PRODUCTS);
        return req;
    }

    public static Response createOkResponse() {
        Response resp = new Response();
        resp.setType(ResponseType.OK);
        return resp;
    }

    public static Response createErrorResponse(String message) {
        Response resp = new Response();
        resp.setType(ResponseType.ERROR);
        resp.setErrorMessage(message);
        return resp;
    }

    public static Response createLoginResponse(User user) {
        Response resp = new Response();
        resp.setType(ResponseType.LOGIN);
        resp.setUser(DTOUtils.getDTO(user));
        return resp;
    }

    public static Response createGetAllProductsResponse(List<Product> products) {
        Response resp = new Response();
        resp.setType(ResponseType.GET_ALL_PRODUCTS);
        resp.setProducts(DTOUtils.getDTO(products));
        return resp;
    }

    public static Response createProductUpdatedResponse(Product product) {
        Response resp = new Response();
        resp.setType(ResponseType.PRODUCT_UPDATED);
        resp.setProduct(DTOUtils.getDTO(product));
        return resp;
    }
}