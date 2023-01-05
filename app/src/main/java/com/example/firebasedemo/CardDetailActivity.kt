package com.example.firebasedemo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasedemo.databinding.ActivityCardDetailBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class CardDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCardDetailBinding
    private lateinit var adapter: ItemsAdapter
    private var customerId = ""
    private val collectionRef =
        Firebase.firestore.collection("cartdata")
    private val list = arrayListOf<Sku>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        customerId = intent.getStringExtra("customer_id").toString()
        getSkus()
        intiClickListeners()
    }

    private fun intiClickListeners() {
        binding.submitButton.setOnClickListener {
            val sku = binding.tilAddSku.editText?.text.toString()
            val quantityMap = mutableMapOf<String, Any>()
            quantityMap["quantity"] = 1
            quantityMap["sku"] = sku
            if (list.find { it.name == sku } != null) {
                val newQuantityMap = mutableMapOf<String, Any>()
                newQuantityMap["quantity"] = list.find { it.name == sku }!!.quantity.plus(1)
                collectionRef.document(customerId).collection("skuDetails").document(sku)
                    .update(newQuantityMap)
                getSkus()
            } else {
                try {
                    collectionRef.document(customerId).collection("skuDetails").document(sku)
                        .set(quantityMap)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "Successfully saved data.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                } catch (e: Exception) {
                    Log.d("Firebase", "${e.message}")
                } finally {
                    getSkus()
                }
            }
        }

    }


    private fun getSkus() {
        list.clear()
        try {
            collectionRef.document(customerId).collection("skuDetails").get()
                .addOnSuccessListener { it ->
                    for (doc in it.documents) {
                        list.add(
                            Sku(
                                doc.data?.get("sku").toString(),
                                doc.data?.get("quantity").toString().toInt()
                            )
                        )
                    }
                    adapter = ItemsAdapter() { newQuantity, sku ->
                        val quantityMap = mutableMapOf<String, Any>()
                        quantityMap["quantity"] = newQuantity
                        collectionRef.document(customerId).collection("skuDetails")
                            .document(sku.name).update(quantityMap).addOnCompleteListener {
                                getSkus()
                            }
                    }
                    binding.rvCartItems.adapter = adapter
                    Log.d("list: ", "${list.size}")
                    adapter.submitList(list)
                }
        } catch (e: Exception) {
            Log.d("Firebase 2", "${e.message}")
        }
    }
}
