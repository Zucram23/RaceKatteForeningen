package presentation;


import application.UserServiceImpl;
import domain.InvalidCredentialsException;
import domain.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
@Controller
@RequestMapping("/RaceKatteKlubben")
public class LoginController {
    private final UserServiceImpl userService;

    public LoginController(UserServiceImpl userService){this.userService =  userService;}

    @GetMapping("/login")
    public String showUserForm(Model model){
        model.addAttribute("user", new User());
        return "login";
    }
    @PostMapping("/login")
    public String authenticateUser(@RequestParam String email, @RequestParam String password, Model model, HttpSession session) {

        try {
            User user = userService.authenticateUser(email, password);

            if (user != null) {
                session.setAttribute("user", user);
                return "redirect:/RaceKatteKlubben/profile/" + user.getId();
            }
        } catch (InvalidCredentialsException e){
            model.addAttribute("errorMessage", "Invalid email or password");
            return "login";
        }
        return "redirect:/RaceKatteKlubben/login";
    }

    @PostMapping("/register")
    public String saveUser(@Validated @ModelAttribute User user, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "register";
        }
        if (userService.emailExist(user.getEmail())) {
            model.addAttribute("errorMessage", "Email already exists!");
            return "register";
        }
        try {
            userService.save(user);
        } catch (DataIntegrityViolationException e){
            model.addAttribute("errorMessage", "Error happened while saving user");
            return "register";
        }
        return "redirect:/RaceKatteKlubben/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/RaceKatteKlubben/login";
    }
}
