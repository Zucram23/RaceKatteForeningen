package infrastructure;

import domain.Race;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class RaceRepository {

    private final JdbcTemplate jdbcTemplate;

    public RaceRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public Race save(Race race){
        String sql = "Insert into races (name) VALUES (?)";

        jdbcTemplate.update(sql, race.getName());
        return race;
    }
    public List<Race> findAll(){
        String sql = "Select * from races";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Race.class));
}

    public Integer getRaceId(String name){
        String sql = "Select id from races where name = ?";
            return jdbcTemplate.queryForObject(sql, Integer.class, name);

    }
    public Race findById(int id) {
        String sql = "SELECT * FROM races WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Race.class), id);
        } catch (EmptyResultDataAccessException e) {
            // If no race is found, return null or handle as needed
            return null;
        }
    }

}
