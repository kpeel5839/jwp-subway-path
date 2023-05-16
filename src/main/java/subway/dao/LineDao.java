package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.entity.LineEntity;
import subway.exception.LineNotFoundException;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static subway.dao.rowmapper.util.RowMapperUtil.lineEntityRowMapper;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;

    public LineDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");
    }

    public Long insert(final LineEntity line) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", line.getName());
        params.put("color", line.getColor());

        return insertAction.executeAndReturnKey(params).longValue();
    }

    public List<LineEntity> findAll() {
        final String sql = "select id, name, color from LINE";
        return jdbcTemplate.query(sql, lineEntityRowMapper);
    }

    public List<LineEntity> findByName(final String name) {
        final String sql = "SELECT * FROM line WHERE name = ?";
        return jdbcTemplate.query(sql, lineEntityRowMapper, name);
    }

    public List<LineEntity> findById(Long id) {
        String sql = "select id, name, color from LINE WHERE id = ?";
        List<LineEntity> lineEntities = jdbcTemplate.query(sql, lineEntityRowMapper, id);
        return lineEntities;
    }

    public void update(LineEntity newLine) {
        String sql = "update LINE set name = ?, color = ? where id = ?";
        jdbcTemplate.update(sql, new Object[]{newLine.getName(), newLine.getColor(), newLine.getId()});
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("delete from Line where id = ?", id);
    }
}
