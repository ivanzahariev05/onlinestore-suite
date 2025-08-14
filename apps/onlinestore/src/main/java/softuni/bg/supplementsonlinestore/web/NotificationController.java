package softuni.bg.supplementsonlinestore.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import softuni.bg.supplementsonlinestore.notification.dto.NotificationPreference;
import softuni.bg.supplementsonlinestore.notification.service.NotificationService;
import softuni.bg.supplementsonlinestore.security.MetaDataAuthentication;
import softuni.bg.supplementsonlinestore.user.model.User;
import softuni.bg.supplementsonlinestore.user.service.UserService;

import java.util.UUID;
@Slf4j
@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final UserService userService;
    private final NotificationService notificationService;

    public NotificationController(UserService userService, NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public ModelAndView getNotifications(@AuthenticationPrincipal MetaDataAuthentication metaDataAuthentication) {
        ModelAndView mav = new ModelAndView("notifications");
        User user = userService.findById(metaDataAuthentication.getId());

        NotificationPreference preference = notificationService.getNotificationPreferences(user.getId());

        mav.addObject("user", user);
        mav.addObject("notificationPreference", preference);
        return mav;
    }

    @PostMapping("/toggle")
    public String toggleNotifications(@AuthenticationPrincipal MetaDataAuthentication metaDataAuthentication) {
        notificationService.toggleNotification(metaDataAuthentication.getId());
        return "redirect:/notifications";
    }

    @PostMapping("/send-notification")
    public ResponseEntity<Void> sendNotification(@RequestParam UUID userId) {
        log.info("Received request to send notification for userId: {}", userId);

        if (userId == null) {
            log.error("UserId is NULL before calling Notification Service!");
            return ResponseEntity.badRequest().build();
        }

        return sendNotification(userId);
    }
}
