package softuni.bg.supplementsonlinestore.web;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import softuni.bg.supplementsonlinestore.security.MetaDataAuthentication;
import softuni.bg.supplementsonlinestore.user.model.User;
import softuni.bg.supplementsonlinestore.user.service.UserService;
import softuni.bg.supplementsonlinestore.web.dto.LoginRequest;
import softuni.bg.supplementsonlinestore.web.dto.RegisterRequest;

@Controller
@RequestMapping("/")
public class IndexController {

    private final UserService userService;

    public IndexController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView index() {

        return new ModelAndView("index");
    }


    @GetMapping("/login")
    public ModelAndView getLogin(@RequestParam(value = "error", required = false) String error) {
        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("loginRequest", new LoginRequest());

        if (error != null) {
            modelAndView.addObject("errorMessage", "Incorrect username or password.");
        }

        return modelAndView;
    }



    @GetMapping("/register")
    public ModelAndView register() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        modelAndView.addObject("registerRequest", new RegisterRequest());
        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView registerUser(@Valid RegisterRequest registerRequest, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        if (bindingResult.hasErrors()) {
            return new ModelAndView("register");
        }

        userService.registerUser(registerRequest);
        modelAndView.setViewName("redirect:/login");
        return modelAndView;
    }






}
