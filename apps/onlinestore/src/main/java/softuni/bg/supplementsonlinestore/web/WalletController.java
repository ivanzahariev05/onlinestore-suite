package softuni.bg.supplementsonlinestore.web;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import softuni.bg.supplementsonlinestore.security.MetaDataAuthentication;
import softuni.bg.supplementsonlinestore.transaction.model.Transaction;
import softuni.bg.supplementsonlinestore.transaction.model.TransactionType;
import softuni.bg.supplementsonlinestore.transaction.service.TransactionService;
import softuni.bg.supplementsonlinestore.user.model.User;
import softuni.bg.supplementsonlinestore.user.service.UserService;
import softuni.bg.supplementsonlinestore.wallet.service.WalletService;
import softuni.bg.supplementsonlinestore.web.dto.AddFundsRequest;
import softuni.bg.supplementsonlinestore.web.dto.SendToAFriendRequest;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/wallet")
public class WalletController {

    private final UserService userService;
    private final WalletService walletService;
    private final TransactionService transactionService;

    public WalletController(UserService userService, WalletService walletService, TransactionService transactionService) {
        this.userService = userService;
        this.walletService = walletService;
        this.transactionService = transactionService;
    }

    @GetMapping
    public ModelAndView getWallet(@AuthenticationPrincipal MetaDataAuthentication metaDataAuthentication) {
        ModelAndView modelAndView = new ModelAndView("wallet");
        User user = userService.findById(metaDataAuthentication.getId());

        List<Transaction> transactions = transactionService.findByUser(user.getUsername())
                .stream()
                .filter(t -> (t.getType() == TransactionType.DEPOSIT && t.getOwner().equals(user.getUsername())) ||
                        (t.getType() != TransactionType.DEPOSIT && t.getSender().equals(user.getUsername())))
                .sorted(Comparator.comparing(Transaction::getTransactionDate).reversed())
                .limit(8)
                .collect(Collectors.toList());

        modelAndView.addObject("transactions", transactions);
        modelAndView.addObject("user", user);
        return modelAndView;
    }


    @PostMapping("/{id}/add-funds")
    public String addFunds(@PathVariable UUID id, @Valid AddFundsRequest addFundsRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "wallet";
        }

        walletService.addFunds(id, addFundsRequest.getAmount());
        return "redirect:/wallet";
    }

    @PostMapping("/send")
    public String sendToAFriend(@Valid SendToAFriendRequest sendToAFriendRequest, BindingResult bindingResult) {
        log.info("Send request received: {}", sendToAFriendRequest);

        if (bindingResult.hasErrors()) {
            log.error("Validation errors: {}", bindingResult.getAllErrors());
            return "wallet";
        }

        walletService.sendToAFriend(sendToAFriendRequest);
        return "redirect:/wallet";
    }

}
