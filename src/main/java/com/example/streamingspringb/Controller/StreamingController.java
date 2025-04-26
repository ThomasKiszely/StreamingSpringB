package com.example.streamingspringb.Controller;

import com.example.streamingspringb.Model.Movie;
import com.example.streamingspringb.Model.Review;
import com.example.streamingspringb.Model.User;
import com.example.streamingspringb.Service.MovieService;
import com.example.streamingspringb.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@Controller
public class StreamingController {

    @Autowired
    private UserService userService = new UserService();
    @Autowired
    private MovieService movieservice = new MovieService();

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute User user, Model model, HttpSession session) {
        Integer loginAttempts = session.getAttribute("loginAttempts") == null ? 0 : (Integer) session.getAttribute("loginAttempts");
        if (loginAttempts >= 3) {
            model.addAttribute("exception", "Du har brugt for mange loginforsøg. Vent 30 min og prøv igen");
            return "error";
        }
        try {
            User currentUser = userService.login(user.getEmail(), user.getPassword());
            if (currentUser != null) {
                session.setAttribute("currentUser", currentUser);
                session.setAttribute("loginAttempts", loginAttempts = 0);
                return "redirect:/welcome";
            } else {
                model.addAttribute("error", "Invalid email or password");
                return "redirect:/login";
            }
        }
        catch (EmptyResultDataAccessException e) {
            session.setAttribute("loginAttempts", loginAttempts + 1);
            model.addAttribute("error", "Email eller kodeord forkert.<br>Du har " + (3 - loginAttempts) + " forsøg tilbage");
        }
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model, HttpSession session) {
        User currentUser = userService.registerUser(user);
        session.setAttribute("currentUser", currentUser);
        return "redirect:/welcome";
    }

    @GetMapping("/welcome")
    public String welcome(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        List<Movie> movies = movieservice.getMovies();
        List<Movie> favorites = movieservice.favorites(currentUser.getId());
        model.addAttribute("user", currentUser);
        model.addAttribute("movies", movies);
        model.addAttribute("favorites", favorites);
        return "welcome";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }


    @GetMapping("/watchmovie/{fileName}")
    public String watchMovie(Model model, @PathVariable("fileName") String fileName, HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            model.addAttribute("moviePath", fileName);
            return "watchmovie";
        }
        model.addAttribute("exception", "Du er ikke logget ind");
        return "error";
    }

    @GetMapping("/movie/{id}")
    public String showMovieInfo(Model model, @PathVariable int id, HttpSession session) {
        if (session.getAttribute("currentUser") == null) {
            model.addAttribute("exception", "Du er ikke logget ind");
            return "error";
        }
        User user = (User) session.getAttribute("currentUser");
        Movie movie = movieservice.getMovie(id); // Get the movie once
        List<Movie> favorites = movieservice.favorites(user.getId());
        model.addAttribute("movie", movie);
        model.addAttribute("imgsrc", movie.getImgsrc());
        model.addAttribute("reviews", movieservice.getReviews(id));
        model.addAttribute("isFavorite", favorites.contains(movie));
        return "movie";
    }

    @PostMapping("/movie")
    public String writeReview(@ModelAttribute Movie movie, HttpSession session, Model model) {
        model.addAttribute("movie", movie);
        return "review";
    }

    @GetMapping("/edituser")
    public String editUser(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        model.addAttribute("user", currentUser);
        return "edituser";
    }

    @PostMapping("/edituser")
    public String editUser(@ModelAttribute User user, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        boolean updated = userService.updateUser(user);
        if (updated) {
            session.setAttribute("currentUser", user);
            model.addAttribute("user", user);
            redirectAttributes.addFlashAttribute("succes", "Dine oplysninger er gemt");
            return "redirect:/edituser";
        } else {
            redirectAttributes.addFlashAttribute("fejl", "Der er sket en fejl - prøv igen");
            return "redirect:/edituser";
        }
    }

    @GetMapping("/review/{id}")
    public String review(Model model, @PathVariable int id, HttpSession session, @ModelAttribute Review review, @ModelAttribute User user) {
        User currentUser = (User) session.getAttribute("currentUser");
        review = new Review();
        review.setAuthor(currentUser.getName());
        review.setAuthorId(currentUser.getId());
        review.setDate(LocalDate.now());
        review.setMovieId(id);
        if (session.getAttribute("currentUser") != null) {
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("movie", movieservice.getMovie(id));
            model.addAttribute("review", review);
            return "review";
        }
        model.addAttribute("exception", "Du er ikke logget ind");
        return "error";
    }

    @PostMapping("/review")
    public String createReview(@ModelAttribute Review review, RedirectAttributes redirectAttributes, Model model) {
        boolean ok = movieservice.createReview(review);
        int id = review.getMovieId();
        if (ok) {
            redirectAttributes.addFlashAttribute("succes", "Anmeldelsen er gemt!");
            return "redirect:/movie/" + id;
        } else {
            redirectAttributes.addFlashAttribute("fejl", "Noget gik galt - prøv igen.");
            return "redirect:/movie" + id;
        }
    }

    @GetMapping("/movie/addToFavorites/{id}")
    public String addToFavorites(@PathVariable int id, HttpSession session, User user, RedirectAttributes redirectAttributes) {
        user = (User) session.getAttribute("currentUser");
        Movie movie = movieservice.getMovie(id);
        boolean added = movieservice.addFavorite(movie.getMovieId(), user.getId());
        if (added) {
            redirectAttributes.addFlashAttribute("succes", "Filmen er tilføjet som favorit!");
            return "redirect:/movie/" + movie.getMovieId();
        } else {
            redirectAttributes.addFlashAttribute("fejl", "Noget gik galt - prøv igen.");
            return "redirect:/movie/" + movie.getMovieId();
        }
    }

    @GetMapping("/movie/removeFromFavorites/{id}")
    public String removeFromFavorites(@PathVariable int id, HttpSession session, User user, RedirectAttributes redirectAttributes) {
        user = (User) session.getAttribute("currentUser");
        Movie movie = movieservice.getMovie(id);
        boolean removed = movieservice.removeFavorite(movie.getMovieId(), user.getId());
        if (removed) {
            redirectAttributes.addFlashAttribute("succes", "Filmen er nu ikke favorit mere!");
            return "redirect:/movie/" + movie.getMovieId();
        } else {
            redirectAttributes.addFlashAttribute("fejl", "Noget gik galt - prøv igen.");
            return "redirect:/movie/" + movie.getMovieId();
        }
    }

    @GetMapping("/allmovies")
    public String allMovies(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        List<Movie> movies = movieservice.getMovies();
        List<Movie> favorites = movieservice.favorites(currentUser.getId());
        model.addAttribute("user", currentUser);
        model.addAttribute("movies", movies);
        model.addAttribute("favorites", favorites);
        return "allmovies";
    }

    @GetMapping("/search")
    public String searchMovies(@RequestParam("query") String query, @RequestParam("genre") String genre, Model model) {
        List<Movie> searchResults = movieservice.searchMovies(query, genre);
        model.addAttribute("movies", searchResults);
        return "allmovies";
    }

    //Tak til AI
    @GetMapping("/files/{filename}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get("C:/filmtilst/" + filename);
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("Kan ikke læse filen: " + filename);
        }

        // Dynamically determine the content type
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream"; // Default for unknown types
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
    }

    @GetMapping("/adminpage")
    public String adminPage(User user, HttpSession session, Model model, Review rewiew) {
        user = (User) session.getAttribute("currentUser");
        model.addAttribute("user", user);
        if (user.getRole().equals("ROLE_ADMIN")) {
            return "adminpage";
        }
        model.addAttribute("exception", "Du skal være logget ind som admin");
        return "error";
    }
    @GetMapping("/adminpage/giveadminrights/{id}")
    public String adminRights(@PathVariable int id, Model model, HttpSession session, User user, RedirectAttributes redirectAttributes) {
        user = (User) session.getAttribute("currentUser");
        if (user.getRole().equals("ROLE_ADMIN")) {
            boolean adminrights = userService.giveAdminRights(id);
            if (adminrights) {
                redirectAttributes.addFlashAttribute("succes", "adminrettigheder givet");
                return "redirect:/adminusers";
            }
            redirectAttributes.addFlashAttribute("fejl", "adminrettigheder ikke givet");
            return "redirect:/adminusers";
        }
        model.addAttribute("exception", "Kan ikke give bruger adminrettigheder.");
        return "error";
    }
    @GetMapping("/adminpage/removeadminrights/{id}")
    public String removeAdminRights(@PathVariable int id, Model model, HttpSession session, User user, RedirectAttributes redirectAttributes) {
        user = (User) session.getAttribute("currentUser");
        if (user.getRole().equals("ROLE_ADMIN") && user.getId() != id) {
            boolean adminrights = userService.removeAdminRights(id);
            if (adminrights) {
                redirectAttributes.addFlashAttribute("succes", "adminrettigheder fjernet");
                return "redirect:/adminusers";
            }
            redirectAttributes.addFlashAttribute("fejl", "adminrettigheder ikke fjernet");
            return "redirect:/adminusers";
        }
        redirectAttributes.addFlashAttribute("fejl", "Man kan ikke fjerne rettigheder fra sig selv");
        return "redirect:/adminpage";
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.status(HttpStatus.OK).body("Deleted");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("/deleteReview/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable int id) {
        boolean deleted = movieservice.deleteReview(id);
        if (deleted) {
            return ResponseEntity.status(HttpStatus.OK).body("Deleted");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/edituserAdmin/{id}")
    public String editUserAdmin(@PathVariable int id, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        User user = userService.getUser(id);
        if (user != null && currentUser.getRole().equals("ROLE_ADMIN")) {
            model.addAttribute("user", user);
            return "/edituserAdmin";
        }
        model.addAttribute("exception", "Bruger kan ikke findes");
        return "error";
    }

    @PostMapping("/edituserAdmin")
    public String editUserAdmin(@ModelAttribute User user, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        boolean updated = userService.updateUser(user);
        if (updated) {
            redirectAttributes.addFlashAttribute("succes", "Dine oplysninger er gemt");
            return "redirect:/adminusers";
        } else {
            redirectAttributes.addFlashAttribute("fejl", "Der er sket en fejl - prøv igen");
            return "redirect:/adminusers";
        }
    }
    @GetMapping("/adminusers")
    public String usersPage(@RequestParam(defaultValue = "1") int page, Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (!user.getRole().equals("ROLE_ADMIN")) {
            model.addAttribute("exception", "Du skal være admin for at bruge denne funktion");
            return "error";
        }
        int pageSize = 10;
        List<User> users = userService.getUsersPage(page, pageSize);
        int totalPages = (users.size() + pageSize) / pageSize;
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", pageSize);
        return "adminusers";
    }
    @GetMapping("/searchForUser")
    public String searchForUser(@RequestParam("query") String query, Model model, HttpSession session) {
        List<User> filteredUsers = userService.searchForUser(query);
        model.addAttribute("users", filteredUsers);
        model.addAttribute("currentPage", 1);
        return "adminusers";
    }
    @GetMapping("/adminreviews")
    public String reviewsPage(@RequestParam(defaultValue = "1") int page, Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (!user.getRole().equals("ROLE_ADMIN")) {
            model.addAttribute("exception", "Du skal være admin for at bruge denne funktion");
        }
        int pageSize = 10;
        List<Movie> movies = movieservice.getMovies();
        List<Review> reviewsPaged = movieservice.getReviewsPaged(page, pageSize);
        int totalPages = (reviewsPaged.size() + pageSize) / pageSize;
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("movies", movies);
        model.addAttribute("reviewsPaged", reviewsPaged);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", pageSize);
        return "adminreviews";
    }
    @GetMapping("/searchreviews")
    public String searchReviews(@RequestParam("query") String query, Model model, HttpSession session) {
        List<Review> filteredReviews = movieservice.searchReviews(query);
        model.addAttribute("reviewsPaged", filteredReviews);
        model.addAttribute("currentPage", 1);
        return "adminreviews";
    }
}
