package application;

import domain.Race;
import infrastructure.RaceRepositoryImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RaceServiceImpl implements ServiceInterface<Race> {

    private final RaceRepositoryImpl raceRepository;


    public RaceServiceImpl(RaceRepositoryImpl raceRepository) {
        this.raceRepository = raceRepository;
    }

    @Override
    public List<Race> findAll() {
        return raceRepository.findAll();
    }

    @Override
    public Race save(Race race) {
        return raceRepository.save(race);
    }

    public Integer getRaceId(String name){
        return raceRepository.getRaceId(name);
    }
    public Race getRaceByid(int id){
        return raceRepository.findById(id);
    }

    @Override
    public void update(Race race){
        raceRepository.save(race);
    }

    @Override
    public void delete(int id){
        raceRepository.delete(id);
    }
}