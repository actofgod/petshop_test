package petshop_test;

public class UserGroup
{
    private short id;
    private short parentId;
    private int usersCount;
    private String name;
    private String description;

    private UserGroup()
    {}

    /**
     *
     * @param id
     * @param parentId
     * @param usersCount
     */
    public UserGroup(short id, short parentId, int usersCount)
    {
        this.id = id;
        this.parentId = parentId;
        this.usersCount = usersCount;
    }

    public short getId()
    {
        return id;
    }

    public short getParentId()
    {
        return parentId;
    }

    public int getUsersCount()
    {
        return usersCount;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
