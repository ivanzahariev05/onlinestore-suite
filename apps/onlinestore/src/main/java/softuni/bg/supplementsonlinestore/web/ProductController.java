package softuni.bg.supplementsonlinestore.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import softuni.bg.supplementsonlinestore.product.model.Product;
import softuni.bg.supplementsonlinestore.product.model.ProductType;
import softuni.bg.supplementsonlinestore.product.service.ProductService;
import softuni.bg.supplementsonlinestore.security.MetaDataAuthentication;
import softuni.bg.supplementsonlinestore.user.model.User;
import softuni.bg.supplementsonlinestore.user.service.UserService;
import softuni.bg.supplementsonlinestore.wallet.model.Wallet;
import softuni.bg.supplementsonlinestore.web.dto.ProductRequest;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    public ProductController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping()
    public ModelAndView productsPage(@RequestParam ProductType productType, @AuthenticationPrincipal MetaDataAuthentication metaDataAuthentication) {
        ModelAndView modelAndView = new ModelAndView("products");

        User user = userService.findById(metaDataAuthentication.getId());
        List<Product> products = productService.findByProductType(productType);
        List<ProductType> productTypes = List.of(ProductType.values());

        modelAndView.addObject("products", products);
        modelAndView.addObject("productType", productType);
        modelAndView.addObject("user", user);
        modelAndView.addObject("productTypes", productTypes);

        return modelAndView;
    }


    @GetMapping("/{id}")
    public ModelAndView getProductPage(@PathVariable UUID id, @AuthenticationPrincipal MetaDataAuthentication metaDataAuthentication) {
        User user = userService.findById(metaDataAuthentication.getId());
        ModelAndView modelAndView = new ModelAndView("product-details");
        modelAndView.addObject("product", productService.findProductById(id));
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @PostMapping("/buy/{id}")
    public String buyProduct(@PathVariable UUID id,
                             @AuthenticationPrincipal MetaDataAuthentication metaDataAuthentication,
                             @RequestParam("quantity") int quantity) {
        ProductRequest productRequest = new ProductRequest(quantity);
        User user = userService.findById(metaDataAuthentication.getId());
        Wallet wallet = user.getWallet();
        productService.buyProduct(id, productRequest, wallet);

        return "redirect:/products/purchase-success";
    }
    @GetMapping("/purchase-success")
    public ModelAndView purchaseSuccess(@AuthenticationPrincipal MetaDataAuthentication metaDataAuthentication) {
        ModelAndView modelAndView = new ModelAndView("purchase-success");
        User user = userService.findById(metaDataAuthentication.getId());
        modelAndView.addObject("user", user);
        return modelAndView;
    }

}
