package petshop_test.mappers;

import petshop_test.UserGroup;

import java.util.List;

public interface UserGroupMapper
{
    UserGroup findById(short id);
    List<UserGroup> findAll();
}
