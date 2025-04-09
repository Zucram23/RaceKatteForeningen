package presentation;

import application.UserServiceImpl;
import domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/RaceKatteKlubben")
public class MemberListController {

    private final UserServiceImpl userService;

    public MemberListController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/members")
    public String showMembers(Model model) {
        List<User> users = userService.getAllUsersWithCats();
        model.addAttribute("users", users);
        return "member-list";
    }
}