package petshop_test;

import java.util.List;

public class MultiUserResponse extends AbstractResponse
{
    private List<User> data;
    private int total;

    public MultiUserResponse(List<User> userList, int totalCount)
    {
        data = userList;
        total = totalCount;
    }

    public List<User> getData()
    {
        return data;
    }

    public int getTotal()
    {
        return total;
    }
}
