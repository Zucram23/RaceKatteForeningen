package presentation;

import application.CatServiceImpl;
import application.RaceService;
import domain.Cat;
import domain.Race;
import domain.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/RaceKatteKlubben/profile")
public class CatController {

    private final CatServiceImpl catServiceImpl;
    private final RaceService raceService;

    public CatController(CatServiceImpl catServiceImpl, RaceService raceService) {
        this.catServiceImpl = catServiceImpl;
        this.raceService = raceService;
    }

    // Show form to add a new cat
    @GetMapping("/{id}/add-cat")
    public String showAddCatForm(@PathVariable int id, HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null || user.getId() != id) {
            return "redirect:/RaceKatteKlubben/login"; // Redirect if no user session
        }
        List<Race> races = raceService.getAllRaces();
        model.addAttribute("user", user);
        model.addAttribute("races", races);
        model.addAttribute("Cat", new Cat());
        return "add-cat"; // Show the add-cat form
    }

    @PostMapping("/{id}/add-race")
    public String addRace(@RequestParam String raceName, HttpSession session, @PathVariable int id) {
        User user = (User) session.getAttribute("user");
        if(user == null) {
            return "redirect:/RaceKatteKlubben/login"; // Redirect to login if user is not authenticated
        }

        // Check if the race already exists
        Integer raceId = raceService.getRaceId(raceName);

        // If the race doesn't exist, add it and get the raceId
        if (raceId == null) {
            Race race = new Race();
            race.setName(raceName);
            raceService.addRace(race);  // Save the new race

        }

        // Redirect to the add cat form with the raceId included
        return "redirect:/RaceKatteKlubben/profile/" + id + "/add-cat?raceId=" + raceId;
    }

    // Handle adding a new cat
    @PostMapping("/{id}/add-cat")
    public String addCat(@PathVariable int id, @RequestParam("race_id") int raceId, @ModelAttribute Cat cat, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getId() != id) {
            return "redirect:/login"; // Redirect if no user session
        }

        Race race = raceService.getRaceByid(raceId);
        if (race == null) {
            // Handle case when race is not found, e.g., by returning an error page or setting a default
            return "redirect:/RaceKatteKlubben/profile/" + id + "/add-cat?error=RaceNotFound";
        }

        cat.setRace(race);
        cat.setOwner(user);
        catServiceImpl.save(cat);
        return "redirect:/RaceKatteKlubben/profile/" + user.getId(); // Redirect back to the profile page

    }
}