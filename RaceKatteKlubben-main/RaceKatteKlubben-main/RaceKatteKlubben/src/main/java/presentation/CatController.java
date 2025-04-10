package presentation;

import application.CatServiceImpl;
import application.RaceServiceImpl;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/RaceKatteKlubben/profile")
public class CatController {

    private final CatServiceImpl catServiceImpl;
    private final RaceServiceImpl raceServiceImpl;

    public CatController(CatServiceImpl catServiceImpl, RaceServiceImpl raceServiceImpl) {
        this.catServiceImpl = catServiceImpl;
        this.raceServiceImpl = raceServiceImpl;
    }


    @GetMapping("/{id}/add-cat")
    public String showAddCatForm(@PathVariable int id, HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null || user.getId() != id) {
            return "redirect:/RaceKatteKlubben/login";
        }
        List<Race> races = raceServiceImpl.findAll();
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
            raceId = raceServiceImpl.getRaceId(raceName);
        } catch (EmptyResultDataAccessException e) {

            Race race = new Race();
            race.setName(raceName);
            raceServiceImpl.save(race);

            raceId = raceServiceImpl.getRaceId(raceName);
        }

        return "redirect:/RaceKatteKlubben/profile/" + id + "/add-cat?raceId=" + raceId;
    }



    @PostMapping("/{id}/add-cat")
    public String addCat(@PathVariable int id, @ModelAttribute @Valid
    Cat cat, HttpSession session, BindingResult result, RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("user");
        if (user == null || user.getId() != id) {
            return "redirect:/login"; // Redirect if no user session
        }

        if (cat.getRace() == null || raceServiceImpl.getRaceByid(cat.getRace().getId()) == null) {
            redirectAttributes.addFlashAttribute("error", "Something went wrong while fetching the race");
            return "redirect:/RaceKatteKlubben/profile/" + id + "/add-cat?error=RaceNotFound";

        }

        if (result.hasErrors()){
            return "add-cat";
        }
        Race race = raceServiceImpl.getRaceByid(cat.getRace().getId());
        cat.setRace(race);
        cat.setOwner(user);
        catServiceImpl.save(cat);
        redirectAttributes.addFlashAttribute("success", "Cat has been successfully added");
        return "redirect:/RaceKatteKlubben/profile/" + user.getId(); // Redirect back to the profile page

    }


}