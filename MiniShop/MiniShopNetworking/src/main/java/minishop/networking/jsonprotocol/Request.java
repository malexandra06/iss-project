package minishop.networking.jsonprotocol;

import minishop.networking.dto.UserDTO;

import java.util.Map;

public class Request {
    private RequestType type;
    private String username;
    private String password;
    private UserDTO user;
    private String searchQuery;
    private String userId;
    private Map<String,Integer> productQuantities;

    public Request() {}

    public RequestType getType() { return type; }
    public void setType(RequestType type) { this.type = type; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }
    public String getSearchQuery() { return searchQuery; }
    public void setSearchQuery(String searchQuery) { this.searchQuery = searchQuery; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Map<String, Integer> getProductQuantities() { return productQuantities; }
    public void setProductQuantities(Map<String, Integer> productQuantities) { this.productQuantities = productQuantities; }
}