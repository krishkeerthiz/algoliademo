package com.example.algoliademo1.ui.productpreview

import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.algoliademo1.R
import com.example.algoliademo1.databinding.FragmentProductPreviewBinding
import com.example.algoliademo1.util.NetworkUtil

class ProductPreviewFragment : Fragment() {

    private lateinit var binding: FragmentProductPreviewBinding
    private val args: ProductPreviewFragmentArgs by navArgs()
    private val viewModel: ProductPreviewViewModel
            by viewModels {
                ProductPreviewViewModelFactory(
                    requireContext(),
                    args.productModel,
                    Uri.parse(args.imageUri)
                )
            }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_preview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentProductPreviewBinding.bind(view)

        val uri = Uri.parse(args.imageUri)
        binding.productImagePreview.setImageURI(uri)

        binding.productModelText.text = args.productModel.toString()

        binding.uploadButton.setOnClickListener {
            upload()
        }

    }

    private fun upload() {
        if (NetworkUtil.isNetworkAvailable(requireContext())) {
            Log.d(TAG, "upload: called")
            val urlLiveData = viewModel.uploadImage()

            turnOnLoading()

            urlLiveData.observe(viewLifecycleOwner) { url ->
                if (url != null) {
                    Log.d(TAG, "upload: entered inside")
                    uploadProduct(url)
                }
            }
        } else {
            Toast.makeText(requireContext(), "Check network connection", Toast.LENGTH_SHORT).show()
        }
    }


    private fun uploadProduct(url: String) {
        val resultLiveData = viewModel.uploadProduct(url)

        Log.d(TAG, "uploadProduct: started")
        resultLiveData.observe(viewLifecycleOwner) { result ->
            if (result != null) {

                turnOffLoading()

                if (result) {
                    Toast.makeText(requireContext(), "Product uploaded", Toast.LENGTH_SHORT).show()
                    navigateToProductFragment()
                } else
                    Toast.makeText(requireContext(), "Product upload failed", Toast.LENGTH_SHORT)
                        .show()
            }
        }

    }

    private fun turnOnLoading() {
        binding.productUploading.visibility = View.VISIBLE
        binding.uploadButton.isClickable = false
    }

    private fun turnOffLoading() {
        binding.productUploading.visibility = View.GONE
    }

    private fun navigateToProductFragment() {
        val action =
            ProductPreviewFragmentDirections.actionProductPreviewFragmentToProductFragment()
        view?.findNavController()?.navigate(action)
    }
}