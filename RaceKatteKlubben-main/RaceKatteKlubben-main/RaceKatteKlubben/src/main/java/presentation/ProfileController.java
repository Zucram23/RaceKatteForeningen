package presentation;


import application.CatServiceImpl;
import application.UserServiceImpl;
import domain.Cat;
import domain.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/RaceKatteKlubben/profile")
public class ProfileController {

    private final UserServiceImpl userService;
    private final CatServiceImpl catServiceImpl;

    public ProfileController(UserServiceImpl userService, CatServiceImpl catServiceImpl) {
        this.userService = userService;
        this.catServiceImpl = catServiceImpl;
    }

    @GetMapping("/{id}")
    public String showUserProfile(@PathVariable int id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getId() != id) {
            return "redirect:/login";
        }

        List<Cat> cats = catServiceImpl.findCatsByOwner(user.getId());
        user.setCats(cats);

        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/{id}/edit")
    public String showEditUserProfile(@PathVariable int id, Model model, HttpSession session){
        User user = (User) session.getAttribute("user");

        if (user == null || user.getId() != id) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "edit-profile";
    }

    @PostMapping("/{id}/update")
    public String updateUserProfile(@PathVariable int id, @ModelAttribute User updatedUser, HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null || user.getId() != id) {
            return "redirect:/login";
        }

        userService.update(updatedUser);

        session.setAttribute("user", updatedUser);
        return "redirect:/RaceKatteKlubben/profile/" + updatedUser.getId();
    }

    @GetMapping("")
    public String redirectToProfile(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        return "redirect:/RaceKatteKlubben/profile/" + user.getId();
    }
}