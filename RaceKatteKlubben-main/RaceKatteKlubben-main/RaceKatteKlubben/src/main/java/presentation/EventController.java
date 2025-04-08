package presentation;


import application.CatServiceImpl;
import application.EventParticipantsService;
import application.EventServiceImpl;
import application.UserServiceImpl;
import domain.Cat;
import domain.Event;
import domain.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/RaceKatteKlubben/Events")
public class EventController {

    private final EventServiceImpl eventService;
    @Autowired
    private CatServiceImpl catServiceImpl;
    @Autowired
    private UserServiceImpl userServiceImpl;

    public EventController(EventServiceImpl eventService) {this.eventService = eventService; }

    @Autowired
    private EventParticipantsService eventParticipantsService;


    @GetMapping
    public String showEvents(Model model, HttpSession httpSession){
        User user = (User) httpSession.getAttribute("user");
        if(user == null){
            return "redirect:/RaceKatteKlubben/login";
        }

        user.setCats(catServiceImpl.findCatsByOwner(user.getId()));
        List<Event> events = eventService.findAll();
        model.addAttribute("events", events);
        model.addAttribute("user", user);

        return "events";
    }

    @GetMapping("/{id}")
    public String showEventDetails(@PathVariable int id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/RaceKatteKlubben/login";
        }


        Event event = eventService.findById(id);

        List<String> participants = eventParticipantsService.getEventParticipantsWithCats((int) id);

        // Find eventopretteren
        User admin = userServiceImpl.getUserById(event.getAdmin_id());

        model.addAttribute("event", event);
        model.addAttribute("participants", participants);
        model.addAttribute("adminName", admin.getName());
        model.addAttribute("user", user); // Hvis du skal bruge noget fra brugeren i Thymeleaf

        return "event-details";
    }

    @GetMapping("/create")
    public String showCreateEventForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/RaceKatteKlubben/login";
        }

        model.addAttribute("event", new Event());
        model.addAttribute("user", user);
        return "create-event";
    }

    @PostMapping("/create")
    public String createEvent(@ModelAttribute Event event, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/RaceKatteKlubben/login";
        }

        event.setAdmin_id(user.getId());
        eventService.save(event);
        return "redirect:/RaceKatteKlubben/Events";
    }

    @GetMapping("/Events/{id}")
    public String showEventDetails(@PathVariable int id, Model model) {
        Event event = eventService.findById(id);
        model.addAttribute("event", event);
        return "event-details";
    }
    @PostMapping("/{id}/signup")
    public String signupToEvent(@PathVariable int id, @RequestParam Integer catId, HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null || catId == null) {
            // User is not logged in or has not selected a cat – redirect with error message
            return "redirect:/RaceKatteKlubben/Events?error=missingCat";
        }

        // Call the service to handle the event sign-up
        eventParticipantsService.enterEvent(user.getId(), id, List.of(catId));

        // Redirect back to events page with success message
        return "redirect:/RaceKatteKlubben/Events?success=joined";
    }

    @PostMapping("/delete/{id}")
    public String deleteEvent(@PathVariable int id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/RaceKatteKlubben/login";
        }

        eventService.delete(id);
        return "redirect:/RaceKatteKlubben/Events";
    }

}