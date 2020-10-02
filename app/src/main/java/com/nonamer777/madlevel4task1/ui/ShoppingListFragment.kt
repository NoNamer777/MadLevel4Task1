package com.nonamer777.madlevel4task1.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nonamer777.madlevel4task1.R
import com.nonamer777.madlevel4task1.model.Product
import com.nonamer777.madlevel4task1.repository.ProductRepository
import kotlinx.android.synthetic.main.add_product_dialog.*
import kotlinx.android.synthetic.main.fragment_shopping_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass that shows the shopping list with the product types and amount of items.
 */
class ShoppingListFragment : Fragment() {

    private val products = arrayListOf<Product>()

    private val productAdapter = ProductAdapter(products)

    private lateinit var productRepository: ProductRepository

    private val mainScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shopping_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productRepository = ProductRepository(requireContext())

        getProductsFromDatabase()
        initializeRecyclerView()

        fabAddItem.setOnClickListener { showProductDialog() }
        fabClearList.setOnClickListener { removeAllProducts() }
    }

    /** Initializes the Recycler View */
    private fun initializeRecyclerView() {
        // Sets up the recycler view.
        rvShoppingList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvShoppingList.adapter = productAdapter
        rvShoppingList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        createItemTouchHelper().attachToRecyclerView(rvShoppingList)
    }

    /** Creates a touch helper to recognize when a User swipes an product in the recycler view. */
    private fun createItemTouchHelper(): ItemTouchHelper {
        val callback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            // Disable moving a product up or down.
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            // Removes a product when it gets swiped LEFT.
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val product = products[viewHolder.adapterPosition]

                mainScope.launch {
                    withContext(Dispatchers.IO) { productRepository.deleteProduct(product) }

                    getProductsFromDatabase()
                }
            }
        }
        return ItemTouchHelper(callback)
    }

    /** Requests all Products from the database. */
    private fun getProductsFromDatabase() {
        mainScope.launch {
            val products = withContext(Dispatchers.IO) { productRepository.getAllProducts() }

            this@ShoppingListFragment.products.clear()
            this@ShoppingListFragment.products.addAll(products)
            
            this@ShoppingListFragment.productAdapter.notifyDataSetChanged()
        }
    }

    /** Shows an Dialog to fill in the name and amount of the to be added Product. */
    @SuppressLint("InflateParams")
    private fun showProductDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogLayout = layoutInflater.inflate(R.layout.add_product_dialog, null)
        val productName = dialogLayout.findViewById<EditText>(R.id.inputProductName)
        val amount = dialogLayout.findViewById<EditText>(R.id.inputProductAmount)

        builder.setTitle(getString(R.string.add_product_dialog_title))
        builder.setView(dialogLayout)

        builder.setPositiveButton(R.string.btn_add_product) { _: DialogInterface, _: Int ->
            addProduct(productName, amount)
        }
        builder.show()
    }

    /** Requests a new product of a certain amount to be added to the database. */
    private fun addProduct(inputProductName: EditText, inputProductAmount: EditText) {

        // Only add the products if both fields are filled in.
        if (validateFields(inputProductName, inputProductAmount)) {
            mainScope.launch {
                val product = Product(
                    null,
                    inputProductName.text.toString(),
                    inputProductAmount.text.toString().toInt()
                )

                withContext(Dispatchers.IO) { productRepository.saveProduct(product) }

                getProductsFromDatabase()
            }
        } else {
            Toast.makeText(
                activity,
                "Please fill in the input fields",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /** Checks if the provided input fields are not empty. */
    private fun validateFields(inputProductName: EditText, inputProductAmount: EditText): Boolean =
        inputProductName.text.toString().isNotBlank() && inputProductAmount.text.toString().isNotBlank()

    /** Requests all products to be removed from the database. */
    private fun removeAllProducts() {
        mainScope.launch {
            withContext(Dispatchers.IO) { productRepository.deleteAllProducts() }

            getProductsFromDatabase()
        }
    }
}
