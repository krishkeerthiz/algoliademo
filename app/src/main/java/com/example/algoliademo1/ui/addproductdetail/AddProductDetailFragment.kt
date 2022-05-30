package com.example.algoliademo1.ui.addproductdetail

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.algoliademo1.R
import com.example.algoliademo1.databinding.FragmentAddProductDetailBinding
import com.example.algoliademo1.model.ProductModel
import com.example.algoliademo1.util.brandTypeCheck
import com.example.algoliademo1.util.categoryCheck
import com.example.algoliademo1.util.nameDescriptionCheck
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AddProductDetailFragment : Fragment() {

    private lateinit var binding: FragmentAddProductDetailBinding

    private lateinit var name: TextInputEditText
    private lateinit var price: TextInputEditText
    private lateinit var brand: TextInputEditText
    private lateinit var category: TextInputEditText
    private lateinit var type: TextInputEditText
    private lateinit var description: TextInputEditText
    private lateinit var rating: TextInputEditText

    private lateinit var nameLayout: TextInputLayout
    private lateinit var priceLayout: TextInputLayout
    private lateinit var brandLayout: TextInputLayout
    private lateinit var categoryLayout: TextInputLayout
    private lateinit var typeLayout: TextInputLayout
    private lateinit var descriptionLayout: TextInputLayout
    private lateinit var ratingLayout: TextInputLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_product_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddProductDetailBinding.bind(view)

        name = binding.productNameText
        price = binding.productPriceText
        brand = binding.productBrandText
        category = binding.productCategoryText
        type = binding.productTypeText
        description = binding.productDescriptionText
        rating = binding.productRatingText

        nameLayout = binding.outlinedProductNameTextField
        priceLayout = binding.outlinedProductPriceTextField
        brandLayout = binding.outlinedProductBrandTextField
        categoryLayout = binding.outlinedProductCategoryTextField
        typeLayout = binding.outlinedProductTypeTextField
        descriptionLayout = binding.outlinedProductDescriptionTextField
        ratingLayout = binding.outlinedProductRatingTextField

        name.addTextChangedListener(ValidationTextWatcher(name))
        price.addTextChangedListener(ValidationTextWatcher(price))
        brand.addTextChangedListener(ValidationTextWatcher(brand))
        category.addTextChangedListener(ValidationTextWatcher(category))
        type.addTextChangedListener(ValidationTextWatcher(type))
        description.addTextChangedListener(ValidationTextWatcher(description))
        rating.addTextChangedListener(ValidationTextWatcher(rating))

        binding.nextButton.setOnClickListener {
            if (validateProductBrand() && validateProductCategory() && validateProductDescription()
                && validateProductName() && validateProductPrice() && validateProductRating()
                && validateProductType()
            ) {

                val productModel = ProductModel(
                    brand.text.toString(),
                    listOf(category.text.toString()),
                    description.text.toString(),
                    binding.freeShippingCheckBox.isChecked,
                    "",
                    name.text.toString(),
                    "1010",
                    100,
                    price.text.toString().toFloat(),
                    calculatePriceRange(price.text.toString().toInt()),
                    rating.text.toString().toInt(),
                    type.text.toString(),
                    "https://onlineshopping.com"
                )

                gotoAddProductImageFragment(productModel)
            } else {
                checkEmptyFields()
                Toast.makeText(
                    requireContext(),
                    "Make sure all fields are entered",
                    Toast.LENGTH_SHORT
                ).show()

            }


        }
    }

    private fun checkEmptyFields() {
        validateProductBrand()
        validateProductCategory()
        validateProductDescription()
        validateProductName()
        validateProductPrice()
        validateProductRating()
        validateProductType()
    }

    private fun calculatePriceRange(price: Int): String {
        return when (price) {
            in 1..50 -> "1 - 50" // 1 to 50
            in 51..100 -> "50 - 100" // 51 to 100
            in 101..200 -> "100 - 200" // 101 to 200
            in 201..500 -> "200 - 500" // 201 to 500
            in 501..2000 -> "500 - 2000" // 501 to 2000
            else -> "> 2000" // >2000
        }
    }

    private fun gotoAddProductImageFragment(productModel: ProductModel) {
        val action =
            AddProductDetailFragmentDirections.actionAddProductDetailFragmentToAddProductImageFragment(
                productModel
            )
        view?.findNavController()?.navigate(action)
    }

    private fun validateProductRating(): Boolean {
        if (rating.text.toString().isNotEmpty()) {

            return when (rating.text.toString().toInt()) {
                0 -> {
                    ratingLayout.error = "Rating should not be Zero"
                    false
                }
                !in 1..10 -> {
                    ratingLayout.error = "Rating should not be greater than 10"
                    false
                }
                else -> {
                    ratingLayout.error = null
                    ratingLayout.isErrorEnabled = false
                    true
                }
            }
        } else {
            ratingLayout.error = "Rating should not be empty"
            return false
        }
    }

    private fun validateProductDescription(): Boolean {
        return if (!description.text.isNullOrBlank()) {
            val productDescription = description.text!!.trim().toString()

            if (nameDescriptionCheck(productDescription)) {
                descriptionLayout.error = null
                descriptionLayout.isErrorEnabled = false
                true
            } else {
                descriptionLayout.error = "Enter valid product description"
                false
            }

        } else {
            descriptionLayout.error = "Description should not be empty"
            false
        }
    }

    private fun validateProductType(): Boolean {
        return if (!type.text.isNullOrBlank()) {
            val productType = type.text!!.trim().toString()

            if (brandTypeCheck(productType)) {
                typeLayout.error = null
                typeLayout.isErrorEnabled = false
                true
            } else {
                typeLayout.error = "Enter valid product type"
                false
            }

        } else {
            typeLayout.error = "Type should not be empty"
            false
        }
    }

    private fun validateProductCategory(): Boolean {
        return if (!category.text.isNullOrBlank()) {
            val productCategory = category.text!!.trim().toString()

            if (categoryCheck(productCategory)) {
                categoryLayout.error = null
                categoryLayout.isErrorEnabled = false
                true
            } else {
                categoryLayout.error = "Enter valid product category"
                false
            }

        } else {
            categoryLayout.error = "Category should not be empty"
            false
        }
    }

    private fun validateProductBrand(): Boolean {
        return if (!brand.text.isNullOrBlank()) {
            val productBrand = brand.text!!.trim().toString()

            if (brandTypeCheck(productBrand)) {
                brandLayout.error = null
                brandLayout.isErrorEnabled = false
                true
            } else {
                brandLayout.error = "Enter valid product brand"
                false
            }

        } else {
            brandLayout.error = "Brand should not be empty"
            false
        }
    }

    private fun validateProductPrice(): Boolean {
        if (price.text.toString().isNotEmpty()) {

            return when (price.text.toString().toInt()) {
                0 -> {
                    priceLayout.error = "Price should not be Zero"
                    false
                }
                !in 1..5000 -> {
                    priceLayout.error = "Price should not be greater than 5000"
                    false
                }
                else -> {
                    priceLayout.error = null
                    priceLayout.isErrorEnabled = false
                    true
                }
            }
        } else {
            priceLayout.error = "Price should not be empty"
            return false
        }
    }

    private fun validateProductName(): Boolean {
        return if (!name.text.isNullOrBlank()) {
            val productName = name.text!!.trim().toString()

            if (nameDescriptionCheck(productName)) {
                nameLayout.error = null
                nameLayout.isErrorEnabled = false
                true
            } else {
                nameLayout.error = "Enter valid product name"
                false
            }

        } else {
            nameLayout.error = "Name should not be empty"
            false
        }
    }


    inner class ValidationTextWatcher(var view: View) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
        override fun afterTextChanged(s: Editable?) {
            when (view.id) {
                R.id.productNameText -> validateProductName()
                R.id.productPriceText -> validateProductPrice()
                R.id.productBrandText -> validateProductBrand()
                R.id.productCategoryText -> validateProductCategory()
                R.id.productTypeText -> validateProductType()
                R.id.productDescriptionText -> validateProductDescription()
                R.id.productRatingText -> validateProductRating()
            }
        }
    }
}