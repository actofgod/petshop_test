package petshop_test;

import org.slf4j.LoggerFactory;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import petshop_test.mappers.UserGroupMapper;
import petshop_test.mappers.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController extends AbstractController
{
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(UserController.class);

    @RequestMapping(value="/", method = RequestMethod.GET)
    public @ResponseBody User user(@RequestParam(value="id") String id)
    {
        User user = null;
        SqlSession sqlSession = null;
        try {
            logger.debug("user -> [/user]");
            sqlSession = getSqlSessionFactory().openSession();
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            user = mapper.findById(Integer.parseInt(id));
        } catch (Exception e) {
            System.err.print(e);
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
        return user;
    }

    @RequestMapping(value="list")
    public @ResponseBody AbstractResponse getUserListAction(
            @RequestParam(value="start", defaultValue="0") String start,
            @RequestParam(value="limit", defaultValue="20") String limit
    )
    {
        List<User> users = null;
        SqlSession sqlSession = null;
        int total = 0;
        AbstractResponse response;

        try {
            logger.debug("getUserListAction -> [/user/list]");
            sqlSession = getSqlSessionFactory().openSession();
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            users = mapper.findAll(new RowBounds(Integer.parseInt(start), Integer.parseInt(limit)));
            total = mapper.count();
            response = new MultiUserResponse(users, total);
        } catch (Exception e) {
            response = new FailureResponse(e.getMessage());
            System.err.print(e);
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
        return response;
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse updateUserAction(@RequestBody Map<String, Object> requestBody)
    {
        SqlSession sqlSession = null;
        AbstractResponse response;

        Map<String, Object> request;

        try {
            logger.debug("updateUserAction -> [/user/update]");
            if (!requestBody.containsKey("data")) {
                throw new Exception("Required parameter \"data\" not specified");
            }
            request = (Map<String, Object>)requestBody.get("data");
            if (!request.containsKey("id")) {
                throw new Exception("Required parameter \"id\" not specified");
            }
            int userId = (Integer)request.get("id");

            sqlSession = getSqlSessionFactory().openSession();
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            User user = mapper.findById(userId);
            if (user == null) {
                throw new Exception("User#" + userId + " not exists");
            }
            updateUser(request, sqlSession, mapper, user);
            response = new SimpleSuccessResponse();
        } catch (Exception e) {
            response = new FailureResponse(e.getMessage());
            System.err.print(e);
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
        return response;
    }

    @RequestMapping(value = "destroy", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse removeUserAction(@RequestBody Map<String, Object> requestBody)
    {
        SqlSession sqlSession = null;
        AbstractResponse response;

        Map<String, Object> request;

        try {
            logger.debug("removeUserAction -> [/user/destroy]");
            if (!requestBody.containsKey("data")) {
                throw new Exception("Required parameter \"data\" not specified");
            }
            int userId = (Integer)requestBody.get("data");
            if (userId <= 0) {
                throw new Exception("User#" + userId + " removing not allowed");
            }

            sqlSession = getSqlSessionFactory().openSession();
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            User user = mapper.findById(userId);
            if (user == null) {
                throw new Exception("User#" + userId + " not exists");
            }
            mapper.removeUser(user);
            sqlSession.commit();
            response = new SimpleSuccessResponse();
        } catch (Exception e) {
            response = new FailureResponse(e.getMessage());
            System.err.print(e);
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
        return response;
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public @ResponseBody AbstractResponse createUserAction(@RequestBody Map<String, Object> request)
    {
        SqlSession sqlSession = null;
        AbstractResponse response;

        try {
            logger.debug("createUserAction -> [/user/create]");
            if (!request.containsKey("login")) {
                throw new Exception("Required parameter \"login\" not specified");
            }
            if (!request.containsKey("groupId")) {
                throw new Exception("Required parameter \"groupId\" not specified");
            }

            sqlSession = getSqlSessionFactory().openSession();
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);

            User user = new User(-200, ((String)request.get("login")).trim(), null);
            if (mapper.countUserByLogin(user.getLogin()) > 0) {
                throw new Exception("Player with login \"" + user.getLogin() + "\" already exists");
            }

            user.setGroupId(((Integer)request.get("groupId")).shortValue());

            UserGroupMapper groupMapper = sqlSession.getMapper(UserGroupMapper.class);
            UserGroup group = groupMapper.findById(user.getGroupId());
            if (group == null) {
                throw new Exception("UserGroup#" + user.getGroupId() + " not exists");
            }

            if (request.containsKey("email")) {
                String email = ((String)request.get("email")).trim();
                if (!email.isEmpty()) {
                    if (mapper.countUserByEmail(email) > 0) {
                        throw new Exception("Player with email \"" + email + "\" already exists");
                    }
                    user.setEmail(email);
                }
            }

            user.setPasswordHash("");
            if (request.containsKey("password")) {
                String password = ((String)request.get("password"));
                if (!password.isEmpty()) {
                    user.setPassword(password);
                }
            }

            mapper.insertUser(user);
            sqlSession.commit();

            List<User> list = new ArrayList<>();
            list.add(mapper.findById(user.getId()));
            response = new MultiUserResponse(list, 1);
        } catch (Exception e) {
            response = new FailureResponse(e.getMessage());
            System.err.print(e);
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
        return response;
    }

    private void updateUser(Map<String, Object> request, SqlSession sqlSession, UserMapper mapper, User user)
            throws Exception
    {
        boolean changed = false;

        if (request.containsKey("login")) {
            String login = (String)request.get("login");
            if (!login.isEmpty() && !login.equals(user.getLogin())) {
                if (mapper.countUserByLogin(login) > 0) {
                    throw new Exception("Player with login \"" + login + "\" already exists");
                }
                user.setLogin(login);
                changed = true;
            }
        }

        if (request.containsKey("email")) {
            String email = (String)request.get("email");
            if (!email.isEmpty() && !email.equals(user.getEmail())) {
                if (mapper.countUserByEmail(email) > 0) {
                    throw new Exception("Player with email \"" + email + "\" already exists");
                }
                user.setEmail(email);
                changed = true;
            }
        }

        if (request.containsKey("groupId")) {
            short groupId = ((Integer)request.get("groupId")).shortValue();
            UserGroupMapper groupMapper = sqlSession.getMapper(UserGroupMapper.class);
            UserGroup group = groupMapper.findById(groupId);
            if (group == null) {
                throw new Exception("UserGroup#" + groupId + " not exists");
            }
            user.setGroupId(group.getId());
            changed = true;
        }

        if (changed) {
            mapper.updateUser(user);
            sqlSession.commit();
        }
    }
}
