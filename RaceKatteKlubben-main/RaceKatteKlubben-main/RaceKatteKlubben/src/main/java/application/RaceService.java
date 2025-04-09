package application;

import domain.Race;
import infrastructure.RaceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RaceService {

    private final RaceRepository raceRepository;

    public RaceService(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    public List<Race> getAllRaces() {
        return raceRepository.findAll();
    }

    public Race addRace(Race race) {
        return raceRepository.save(race);
    }

    public Integer getRaceId(String name){
        return raceRepository.getRaceId(name);
    }
    public Race getRaceByid(int id){
        return raceRepository.findById(id);
    }
}