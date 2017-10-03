package lerrain.service.printer;

/**
 * Created by lerrain on 2017/9/17.
 */
public class Sign
{
    Long id;

    String keystore;
    String password;
    String scope;
    String reason;
    String location;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getKeystore()
    {
        return keystore;
    }

    public void setKeystore(String keystore)
    {
        this.keystore = keystore;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public String getScope()
    {
        return scope;
    }

    public void setScope(String scope)
    {
        this.scope = scope;
    }
}
