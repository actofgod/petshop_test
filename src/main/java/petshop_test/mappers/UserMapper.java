package petshop_test.mappers;

import org.apache.ibatis.session.RowBounds;
import petshop_test.User;

import java.util.List;

public interface UserMapper
{
    User findById(int id);
    List<User> findAll(RowBounds rowbounds);

    int count();
    int countUserByLogin(String login);
    int countUserByEmail(String email);

    int insertUser(User user);
    int updateUser(User user);
    int removeUser(User user);
}
