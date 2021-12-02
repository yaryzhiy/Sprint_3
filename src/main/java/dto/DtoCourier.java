package dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DtoCourier {

    @JsonProperty("login")
    public String login;

    @JsonProperty("password")
    public String password;

    @JsonProperty("firstName")
    public String firstName;

    public DtoCourier() {
    }

    public DtoCourier(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }
}
