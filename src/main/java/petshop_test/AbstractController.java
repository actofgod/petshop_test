package petshop_test;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;

abstract public class AbstractController
{
    final public static String DATABASE_CONFIG = "petshop_test/db-config.xml";

    private SqlSessionFactory sqlSessionFactory;

    protected SqlSessionFactory getSqlSessionFactory() throws Exception
    {
        if (sqlSessionFactory == null) {
            try {
                InputStream inputStream = Resources.getResourceAsStream(DATABASE_CONFIG);
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            } catch (Exception e) {
                System.err.println(e);
                throw e;
            }
        }
        return sqlSessionFactory;
    }
}
