package dal;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static dal.Member.aMember;

public class H2MembersRepositoryTest {

    private static final String H2_CONNECTION_STRING = "jdbc:h2:mem:test";
    private static final List<Member> MEMBERS = new ArrayList<>(10);
    private static H2Repository h2Repository;

    @BeforeClass
    public static void
    setup_database() throws SQLException {
        h2Repository = new H2Repository(H2_CONNECTION_STRING);
        initializeMembers();
    }

    @Test
    public void
    it_correctly_inserts_members_to_a_database() throws SQLException {
        h2Repository.createTable();
        h2Repository.insertMembers(MEMBERS);

        Assert.assertEquals(MEMBERS, h2Repository.allMembers());
    }

    private static void initializeMembers() {
        for (int index = 0; index < 10; index++) {
            MEMBERS.add(aMember(index, "Name_" + index));
        }
    }

    @AfterClass
    public static void
    tear_down_database() throws SQLException {
        h2Repository.close();
    }
}
