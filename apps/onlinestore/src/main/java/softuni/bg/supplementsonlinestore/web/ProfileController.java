package softuni.bg.supplementsonlinestore.web;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import softuni.bg.supplementsonlinestore.security.MetaDataAuthentication;
import softuni.bg.supplementsonlinestore.user.model.User;
import softuni.bg.supplementsonlinestore.user.service.UserService;
import softuni.bg.supplementsonlinestore.web.dto.EditProfileRequest;

import java.util.List;
import java.util.UUID;

@Controller
public class ProfileController {
    private final UserService userService;

    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/edit/profile")
    public ModelAndView profile(@AuthenticationPrincipal MetaDataAuthentication metaDataAuthentication) {

        User user = userService.findById(metaDataAuthentication.getId());
        ModelAndView modelAndView = new ModelAndView("edit-profile");
        modelAndView.addObject("user", user);
        modelAndView.addObject("editProfileRequest", new EditProfileRequest());
        return modelAndView;
    }

    @PutMapping("/edit/{id}/profile")
    public ModelAndView editProfile(@PathVariable UUID id, @Valid EditProfileRequest editProfileRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            User user = userService.findById(id);
            ModelAndView modelAndView = new ModelAndView("edit-profile");
            modelAndView.addObject("user", user);
            modelAndView.addObject("editProfileRequest", editProfileRequest);
            return new ModelAndView("edit-profile");
        }

        userService.editProfile(id, editProfileRequest);
        return new ModelAndView("redirect:/home");
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView users(@AuthenticationPrincipal MetaDataAuthentication metaDataAuthentication) {
        ModelAndView modelAndView = new ModelAndView("users");
        List<User> strings = userService.getAllUsers();
        User user = userService.findById(metaDataAuthentication.getId());
        modelAndView.addObject("user", user);
        modelAndView.addObject("users", strings);
        return modelAndView;
    }

    @PostMapping("/users/{id}/role")
    public String switchRole(@PathVariable UUID id) {
        userService.switchRole(id);
        return "redirect:/users";
    }

    @PostMapping("/users/{id}/status")
    public String switchStatus(@PathVariable UUID id) {
        userService.switchStatus(id);
        return "redirect:/users";
    }

}