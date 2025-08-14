package softuni.bg.supplementsonlinestore.web;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import softuni.bg.supplementsonlinestore.exception.*;


@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(UsernameAlreadyInUseException.class)
    public String handleUsernameAlreadyExistException(HttpServletRequest request, RedirectAttributes redirectAttributes, UsernameAlreadyInUseException exception) {
        String message = exception.getMessage();
        redirectAttributes.addFlashAttribute("usernameAlreadyExist", message);

        return "redirect:/register";
    }

    @ExceptionHandler(EmailAlreadyInUserException.class)
    public String handleEmailAlreadyExistException(HttpServletRequest request, RedirectAttributes redirectAttributes, EmailAlreadyInUserException exception) {
        String message = exception.getMessage();
        redirectAttributes.addFlashAttribute("emailAlreadyExist", message);

        return "redirect:/register";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ProductNotFoundException.class)
    public ModelAndView handleProductNotFoundException(ProductNotFoundException exception) {
        ModelAndView modelAndView = new ModelAndView("not-found");
        modelAndView.addObject("errorMessage", exception.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(InsufficientFundsForPurchaseException.class)
    public String handleInsufficientFundsException(HttpServletRequest request, RedirectAttributes redirectAttributes, InsufficientFundsForPurchaseException exception) {
        String message = exception.getMessage();

        String referer = request.getHeader("Referer");
        redirectAttributes.addFlashAttribute("insufficientFunds", message);

        return "redirect:" + (referer != null ? referer : "/products");
    }

    @ExceptionHandler(UserNotFoundException.class)
    public Object handleUserNotFoundException(UserNotFoundException exception, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String referer = request.getHeader("Referer");

        if (referer != null && referer.contains("/wallet")) {
            redirectAttributes.addFlashAttribute("userNotFound", exception.getMessage());
            return "redirect:/wallet";
        }

        ModelAndView modelAndView = new ModelAndView("not-found");
        modelAndView.addObject("userNotFound", exception.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(InsufficientFundsSendMoneyException.class)
    public String handleInsufficientFundsSendMoneyException(InsufficientFundsSendMoneyException exception, RedirectAttributes redirectAttributes) {
        String message = exception.getMessage();
        redirectAttributes.addFlashAttribute("insufficientFunds", message);
        return "redirect:/wallet";
    }



    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            AccessDeniedException.class,
            NoResourceFoundException.class,
            MethodArgumentTypeMismatchException.class,
            MissingRequestValueException.class,
    })
    public ModelAndView handleNotFoundExceptions() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("not-found");
        return modelAndView;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAnyException(Exception exception) {
        ModelAndView modelAndView = new ModelAndView("internal-server-error");
        modelAndView.addObject("errorMessage", exception.getClass().getSimpleName());

        return modelAndView;
    }

}

