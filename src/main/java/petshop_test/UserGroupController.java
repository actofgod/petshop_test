package petshop_test;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import petshop_test.mappers.UserGroupMapper;

import java.util.List;

@Controller
@RequestMapping("/usergroup")
public class UserGroupController extends AbstractController
{
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public @ResponseBody AbstractResponse userGroups()
    {
        List<UserGroup> groups = null;
        SqlSession sqlSession = null;
        AbstractResponse response;

        try {
            sqlSession = getSqlSessionFactory().openSession();
            UserGroupMapper mapper = sqlSession.getMapper(UserGroupMapper.class);
            groups = mapper.findAll();
            response = new MultiUserGroupResponse(groups, groups.size());
        } catch (Exception e) {
            System.err.print(e);
            response = new FailureResponse(e.getMessage());
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
        return response;
    }
}
