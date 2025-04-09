package presentation;


import application.CatServiceImpl;
import application.UserServiceImpl;
import domain.Cat;
import domain.User;
import infrastructure.UserRepositoryImpl;
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
    @GetMapping("/members")
    public String showAllUsersWithCats(Model model) {
        List<User> users = userService.getAllUsersWithCats();
        model.addAttribute("users", users);
        return "member-list";
    }

    @GetMapping("/{id}/update-cat/{catId}")
    public String showUpdateCatForm(@PathVariable int id, @PathVariable int catId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getId() != id) {
            return "redirect:/login";
        }
            model.addAttribute("cat", catServiceImpl.getCatById(catId));
            model.addAttribute("user", user);
            return "update-cat";



    }

    @PostMapping("/{id}/update-cat/{catId}")
    public String updateCat(@PathVariable int id, @PathVariable int catId, @ModelAttribute Cat updatedCat, HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null || user.getId() != id) {
            return "redirect:/login";
        }

        // Fetch the cat object by catId
        Cat existingCat = catServiceImpl.getCatById(catId);
        if (existingCat != null && existingCat.getOwner().getId() == id) {
            // Update the cat's details
            existingCat.setName(updatedCat.getName());
            existingCat.setAge(updatedCat.getAge());
            existingCat.setOwner(updatedCat.getOwner());
            catServiceImpl.update(existingCat);

            return "redirect:/RaceKatteKlubben/profile/" + user.getId();
        }

        return "redirect:/RaceKatteKlubben/profile/" + user.getId();
    }


  /*  @PostMapping("/{id}/update-cat")
    public String updateCat(@PathVariable int id, @ModelAttribute Cat updatedCat, HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null || user.getId() != id) {
            return "redirect:/login";
        }

            catServiceImpl.update(updatedCat);

        return "redirect:/RaceKatteKlubben/profile/" + user.getId();
    } */

}