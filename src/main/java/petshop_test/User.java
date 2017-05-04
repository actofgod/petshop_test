package petshop_test;

import java.util.Date;

public class User
{
    private int id;
    private short groupId;
    private String login;
    private String email;
    private String passwordHash;
    private Date dateRegister;
    private Date lastActivity;
    private Date lastLogin;

    private User()
    {}

    public User(int id, String login, Date dateRegister)
    {
        this.id = id;
        this.login = login;
        this.dateRegister = dateRegister;
    }

    public int getId()
    {
        return id;
    }

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public Date getDateRegister()
    {
        return dateRegister;
    }

    public short getGroupId()
    {
        return groupId;
    }

    public void setGroupId(short value)
    {
        groupId = value;
    }

    public String getPasswordHash()
    {
        return passwordHash;
    }

    public void setPasswordHash(String value)
    {
        passwordHash = value;
    }

    public void setPassword(String value)
    {
        passwordHash = generateSalt();
        passwordHash += getHashedPassword(value);
    }

    public boolean isPasswordValid(String plainPassword)
    {
        String hashed = getHashedPassword(plainPassword);
        return passwordHash.equals(hashed);
    }

    public Date getLastActivity()
    {
        return lastActivity;
    }

    public void setLastActivity(Date value)
    {
        lastActivity = value;
    }

    public Date getLastLogin()
    {
        return lastLogin;
    }

    public void setLastLogin(Date value)
    {
        lastLogin = value;
    }

    private String getHashedPassword(String plainPassword)
    {
        return plainPassword;
    }

    private String generateSalt()
    {
        return "";
    }
}
