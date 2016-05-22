package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlRepository {

    protected final Connection connection;
    private static final String CREATE_MEMBERS = "CREATE TABLE MEMBERS(ID INTEGER, NAME VARCHAR(64))";
    private static final String SELECT_MEMBERS = "SELECT * FROM MEMBERS";
    private static final String INSERT_MEMBERS = "INSERT INTO MEMBERS(ID, NAME) VALUES(?, ?)";

    public SqlRepository(String connectionString) throws SQLException {
        connection = DriverManager.getConnection(connectionString);
    }

    public boolean createTable() throws SQLException {
        Statement createStatement = connection.createStatement();
        return createStatement.execute(CREATE_MEMBERS);
    }

    public List<Member> allMembers() throws SQLException {
        List<Member> allMembers = new ArrayList<>();
        Statement selectStatement = connection.createStatement();
        ResultSet membersResultSet = selectStatement.executeQuery(SELECT_MEMBERS);
        while (membersResultSet.next()) {
            allMembers.add(Member.aMember(membersResultSet.getInt(1), membersResultSet.getString(2)));
        }
        return allMembers;
    }

    public void insertMembers(List<Member> members) throws SQLException {
        final PreparedStatement insertMembers = connection.prepareStatement(INSERT_MEMBERS);
        members.forEach(member -> insertMember(member, insertMembers));
        insertMembers.executeBatch();
    }

    private void insertMember(Member member, PreparedStatement insertMembers) {
        try {
            insertMembers.setInt(1, member.id());
            insertMembers.setString(2, member.name());
            insertMembers.addBatch();
        } catch (SQLException e) {
            throw new UnsupportedOperationException(e.getMessage());
        }
    }

    public void close() throws SQLException {
        connection.close();
    }

}
