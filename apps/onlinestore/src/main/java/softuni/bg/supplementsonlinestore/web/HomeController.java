package softuni.bg.supplementsonlinestore.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import softuni.bg.supplementsonlinestore.product.model.Product;
import softuni.bg.supplementsonlinestore.product.model.ProductType;
import softuni.bg.supplementsonlinestore.product.service.ProductService;
import softuni.bg.supplementsonlinestore.security.MetaDataAuthentication;
import softuni.bg.supplementsonlinestore.user.model.User;
import softuni.bg.supplementsonlinestore.user.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {

    private final UserService userService;
    private final ProductService productService;

    public HomeController(UserService userService, ProductService productService) {
        this.userService = userService;
        this.productService = productService;
    }

    @GetMapping()
    public ModelAndView home(@AuthenticationPrincipal MetaDataAuthentication metaDataAuthority,
                             @RequestParam(required = false) ProductType productType) {
        ModelAndView modelAndView = new ModelAndView("home");

        User user = userService.findById(metaDataAuthority.getId());
        List<Product> products;

        if (productType != null) {
            products = productService.findByProductType(productType);
        } else {
            products = productService.findAllProducts();
        }

        List<ProductType> productTypes = List.of(ProductType.values());

        modelAndView.addObject("user", user);
        modelAndView.addObject("products", products);
        modelAndView.addObject("productTypes", productTypes);

        return modelAndView;
    }
}

