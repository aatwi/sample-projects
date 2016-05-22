package dal;

import java.sql.SQLException;

public final class H2Repository extends SqlRepository {
    public H2Repository(String connectionString) throws SQLException {
        super(connectionString);
    }

    @Override
    public void close() throws SQLException {
        connection.createStatement().execute("DROP ALL OBJECTS");
    }
}
