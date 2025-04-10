package application;

import domain.Cat;
import domain.InvalidCredentialsException;
import domain.User;
import infrastructure.UserRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements ServiceInterface<User> {

    private final UserRepositoryImpl userRepository;
    private final CatServiceImpl catServiceImpl;

    @Autowired
    public UserServiceImpl(UserRepositoryImpl userRepository, CatServiceImpl catServiceImpl) {
        this.userRepository = userRepository;
        this.catServiceImpl = catServiceImpl;
    }

    @Override
    public User save(User user) {
        if (userRepository.emailExists(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists! Please use another email.");
        }
        userRepository.save(user);
        user.setId(userRepository.findIdByEmail(user.getEmail()));
        return user;
    }
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void update(User user) {
        userRepository.update(user);
    }

    @Override
    public void delete(int id) {
        userRepository.delete(id);
    }

    public User getUserById(int id) {
        return userRepository.findUserById(id);
    }


    public User authenticateUser(String email, String password){

        User user = userRepository.authenticateUser(email, password);

        if(user == null){
            throw new InvalidCredentialsException("Invalid email or password");
        }
        return user;

    }

    public boolean emailExist(String email) {
        return userRepository.emailExists(email);
    }
    public List<User> getAllUsersWithCats() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            List<Cat> cats = catServiceImpl.findCatsByOwner(user.getId());
            user.setCats(cats);
        }
        return users;
    }

}
