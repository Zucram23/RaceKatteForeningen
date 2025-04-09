package presentation;

import application.CatServiceImpl;
import application.RaceService;
import domain.Cat;
import domain.Race;
import domain.User;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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


    @GetMapping("/{id}/add-cat")
    public String showAddCatForm(@PathVariable int id, HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null || user.getId() != id) {
            return "redirect:/RaceKatteKlubben/login";
        }
        List<Race> races = raceService.getAllRaces();
        model.addAttribute("user", user);
        model.addAttribute("races", races);
        model.addAttribute("cat", new Cat());
        return "add-cat";
    }

    @PostMapping("/{id}/add-race")
    public String addRace(@RequestParam String raceName, HttpSession session, @PathVariable int id) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/RaceKatteKlubben/login";
        }

        Integer raceId = null;
        try {
            raceId = raceService.getRaceId(raceName);
        } catch (EmptyResultDataAccessException e) {
            // Race doesn't exist, so we create it
            Race race = new Race();
            race.setName(raceName);
            raceService.addRace(race);
            // Now fetch the ID again
            raceId = raceService.getRaceId(raceName);
        }

        return "redirect:/RaceKatteKlubben/profile/" + id + "/add-cat?raceId=" + raceId;
    }

 /*   @PostMapping("/{id}/add-race")
    public String addRace(@RequestParam String raceName, HttpSession session, @PathVariable int id) {
        User user = (User) session.getAttribute("user");
        if(user == null) {
            return "redirect:/RaceKatteKlubben/login";
        }


        Integer raceId = raceService.getRaceId(raceName);


        if (raceId == null) {
            Race race = new Race();
            race.setName(raceName);
            raceService.addRace(race);

        }


        return "redirect:/RaceKatteKlubben/profile/" + id + "/add-cat?raceId=" + raceId;
    } */

    // Handle adding a new cat
    @PostMapping("/{id}/add-cat")
    public String addCat(@PathVariable int id, @RequestParam("race_id") int raceId, @ModelAttribute @Valid
        Cat cat, HttpSession session, BindingResult result) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getId() != id) {
            return "redirect:/login"; // Redirect if no user session
        }

        Race race = raceService.getRaceByid(raceId);
        if (race == null) {
            // Handle case when race is not found, e.g., by returning an error page or setting a default
            return "redirect:/RaceKatteKlubben/profile/" + id + "/add-cat?error=RaceNotFound";
        }

        if (result.hasErrors()){
            return "add-cat";
        }

        cat.setRace(race);
        cat.setOwner(user);
        catServiceImpl.save(cat);
        return "redirect:/RaceKatteKlubben/profile/" + user.getId(); // Redirect back to the profile page

    }
   /* @PostMapping("/{id}/add-cat")
    public String addCat(@PathVariable int id, @ModelAttribute @Valid Cat cat, HttpSession session, BindingResult result) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getId() != id) {
            return "redirect:/login";
        }
        if (result.hasErrors()){
            return "add-cat";
        }

        if (cat.getRace() == null) {

            return "redirect:/RaceKatteKlubben/profile/" + id + "/add-cat?error=RaceNotFound";
        }

        cat.setOwner(user);
        catServiceImpl.save(cat);
        return "redirect:/RaceKatteKlubben/profile/" + user.getId();

    } */
}