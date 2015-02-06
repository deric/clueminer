package org.clueminer.meta.h2;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.clueminer.meta.api.MetaResult;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 *
 * @author Tomas Barton
 */
public class MetaResultMapper implements ResultSetMapper<MetaResult> {

    @Override
    public MetaResult map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new MetaResult(r.getInt("k"), r.getString("template"), r.getDouble("score"));
    }

}
