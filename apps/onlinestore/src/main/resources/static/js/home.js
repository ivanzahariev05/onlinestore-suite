document.addEventListener("DOMContentLoaded", function () {
    const productWrapper = document.querySelector(".product-wrapper");
    const products = Array.from(document.querySelectorAll(".product-card"));
    const prevBtn = document.getElementById("prev-btn");
    const nextBtn = document.getElementById("next-btn");

    let currentIndex = 0;

    function updateProductDisplay() {
        productWrapper.innerHTML = "";
        let clonedProduct = products[currentIndex].cloneNode(true);
        productWrapper.appendChild(clonedProduct);
    }

    prevBtn.addEventListener("click", function () {
        currentIndex = (currentIndex - 1 + products.length) % products.length;
        updateProductDisplay();
    });

    nextBtn.addEventListener("click", function () {
        currentIndex = (currentIndex + 1) % products.length;
        updateProductDisplay();
    });

    updateProductDisplay();
});
