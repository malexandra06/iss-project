package minishop.networking.jsonprotocol;

import minishop.networking.dto.UserDTO;

public class Request {
    private RequestType type;

    private String username;
    private String password;

    private UserDTO user;

    public Request() {}

    public RequestType getType() { return type; }
    public void setType(RequestType type) { this.type = type; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }
}