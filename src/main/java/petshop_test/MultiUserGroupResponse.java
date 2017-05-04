package petshop_test;

import java.util.List;

public class MultiUserGroupResponse extends AbstractResponse
{
    private List<UserGroup> data;
    private int total;

    public MultiUserGroupResponse(List<UserGroup> groupList, int totalCount)
    {
        data = groupList;
        total = totalCount;
    }

    public List<UserGroup> getData()
    {
        return data;
    }

    public int getTotal()
    {
        return total;
    }
}
