package com.example.daily_expense_tracker_app.view

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daily_expense_tracker_app.R
import com.example.daily_expense_tracker_app.database.Expense
import com.example.daily_expense_tracker_app.databinding.ActivityMainBinding
import com.example.daily_expense_tracker_app.view.adapter.ExpenseAdapter
import com.example.daily_expense_tracker_app.viewmodel.ExpenseViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var expenseAmount_str: String = ""
    private var expenseCategory_str: String = ""
    private var expenseDate_str: String = ""
    private var expenseDescription_str: String = ""
    private lateinit var expenseAdapter: ExpenseAdapter
    private var mainlist: ArrayList<Expense>? = null
    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private var count :Int = 0
    private var id :Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainlist = ArrayList()
        //  mainlist!!.add(1, Expense(1,"450","Rent","17/09/2023","Rent",""))
        recyclerViewList()

        // Get the BottomSheetBehavior
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetMain)

        // Set initial state
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        // initializing our view modal.
        expenseViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(ExpenseViewModel::class.java)

        expenseViewModel.allExpenses?.observe(this, Observer { expenses ->
            mainlist!!.clear()
            expenseAdapter.setExpenses(expenses)
            mainlist = expenses as ArrayList<Expense>
            var totalAmount = 0.0
            for (expense in expenses) {
                val amount = expense.amount.toDoubleOrNull() ?: 0.0
                totalAmount += amount
            }
            binding.monthlyAmountTxt.text = "Total monthly expense: ₹$totalAmount"
        })


        binding.addExpenseButton.setOnClickListener {
            count=0
            clearData()
            toggleBottomSheet()
        }

        binding.AddExpenseDetails.setOnClickListener {
            if (validateInputs()) {
                // Proceed with saving the expense
                if (count ==0) {
                    addNewExpense()
                    clearData()
                    Toast.makeText(this, "Expense added successfully", Toast.LENGTH_SHORT).show()
                    toggleBottomSheet()
                }else {
                    updateExpense()
                    clearData()
                    Toast.makeText(this, "Expense Update successfully", Toast.LENGTH_SHORT).show()
                    toggleBottomSheet()
                }
            }
        }
        binding

        binding.expenseDateEt.setOnClickListener {
            showDatePickerDialog()
        }

        // Set up ActionBarDrawerToggle
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.open_drawer, R.string.close_drawer
        )
        binding.drawerLayout.addDrawerListener(toggle)
        // Set up click listener for Menu Button
        binding.MenuBTN.setOnClickListener {
            // Open the navigation drawer
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        // Handle navigation item clicks
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_profile -> {
                    // Launch ProfileActivity
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                }
            }
            // Close the drawer after selection
            drawerLayout.closeDrawers()
            true
        }

        //spinner adapter
        val adapter = ArrayAdapter.createFromResource(
            this, R.array.categories, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter
        binding.spinnerFilter.adapter = adapter
        expenseCategory_str = binding.spinnerFilter.setSelection(0).toString()
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                expenseCategory_str=selectedItem
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optional: Handle cases where no item is selected
            }
        }

        //for filter by category
        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCategory = parent.getItemAtPosition(position).toString()
                var totalAmount = 0.0

                val filteredExpenses = if (selectedCategory == "Select Category") {
                    expenseAdapter.setExpenses(mainlist!!)
                    for (expense in mainlist!!) {
                        val amount = expense.amount.toDoubleOrNull() ?: 0.0
                        totalAmount += amount
                    }
                } else {
                 var filterList =  filterByCategory(mainlist!!, selectedCategory)
                    this@MainActivity.expenseAdapter.setExpenses(filterList!!)
                    for (expense in filterList) {
                        val amount = expense.amount.toDoubleOrNull() ?: 0.0
                        totalAmount += amount
                    }
                }
                binding.monthlyAmountTxt.text = "Total monthly expense: ₹$totalAmount"

                // Update the RecyclerView
                expenseAdapter.notifyDataSetChanged()
                //Toast.makeText(this@MainActivity, "Selected Category: $selectedCategory", Toast.LENGTH_SHORT).show()


            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optional: Handle cases where no item is selected
            }
        }
    }

    fun filterByCategory(expenses: List<Expense>, category: String): List<Expense> {
        return expenses.filter { it.category == category }
    }

    private fun clearData(){
        binding.expenseAmountEt.text?.clear()
        binding.expenseCategoryEt.text?.clear()
        binding.expenseDateEt.text = "Expense Date"
        binding.expenseDescriptionEt.text?.clear()
        expenseCategory_str = binding.spinnerFilter.setSelection(0).toString()

    }
    // Handle menu icon click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun addNewExpense() {
        // Example: Add a new expense
        val newExpense = Expense(
            id = 0, // Let Room handle the auto-generated ID
            amount = expenseAmount_str, category = expenseCategory_str,
            // date = System.currentTimeMillis().toString(),
            date = expenseDate_str, description = expenseDescription_str, imagePath = ""
        )
        expenseViewModel.insert(newExpense)
    }

    // update the expense
    private fun updateExpense() {
        val updateExpense = Expense(
            id = id,
            amount = expenseAmount_str, category = expenseCategory_str,
            date = expenseDate_str, description = expenseDescription_str, imagePath = ""
        )
        expenseViewModel.update(updateExpense)
    }

    //delete the expense
    private fun showExpenseDetails(expense: Expense) {
        expenseViewModel.delete(expense)
        Toast.makeText(this, "Expense deleted successfully", Toast.LENGTH_SHORT).show()
    }

    private fun toggleBottomSheet() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

    }

    //For Recyclerview List
    private fun recyclerViewList() {
        // Set LayoutManager
        binding.expenseRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter
        expenseAdapter = ExpenseAdapter(
            onItemClick = { expense,position ->
                //call bottom sheet function
                toggleBottomSheet()
                // set all value in textview
                binding.expenseAmountEt.setText(expense.amount)
               // binding.expenseCategoryEt.setText(expense.category)
                binding.expenseDateEt.setText(expense.date)
                binding.expenseDescriptionEt.setText(expense.description)
                //setvalue in spinner
                val adapter = binding.spinner.adapter as ArrayAdapter<String>
                val position = adapter.getPosition(expense.category)
                binding.spinner.setSelection(position)
                id = expense.id
                count=1
            },
            onItemClickDelete = { expense ->
                showExpenseDetails(expense)
            }
        )


        // Set Adapter to RecyclerView
        binding.expenseRecyclerView.adapter = expenseAdapter
        expenseAdapter.setExpenses(mainlist!!)
    }

    // Function to validate input fields
    private fun validateInputs(): Boolean {
        var isValid = true

        // Clear previous error messages
        binding.expenseAmountEt.error = null
       // binding.expenseCategoryEt.error = null
        binding.expenseDateEt.error = null
        binding.expenseDescriptionEt.error = null

        // Validate Expense Amount
        val expenseAmount = binding.expenseAmountEt.text.toString()
        expenseAmount_str = expenseAmount
        if (expenseAmount.isEmpty()) {
            binding.expenseAmountEt.error = "Expense Amount cannot be empty"
            isValid = false
        }

        // Validate Expense Category
       // val expenseCategory = binding.expenseCategoryEt.text.toString()
      //  expenseCategory_str = expenseCategory
        if (expenseCategory_str.isEmpty() && expenseCategory_str.equals("Select Category")) {
            binding.expenseCategoryEt.error = "Expense Category cannot be select"
            isValid = false
        }

        // Validate Expense Date
        val expenseDate = binding.expenseDateEt.text.toString()
        expenseDate_str = expenseDate
        if (expenseDate.isEmpty()) {
            binding.expenseDateEt.error = "Expense Date cannot be empty"
            isValid = false
        }

        // Validate Expense Description
        val expenseDescription = binding.expenseDescriptionEt.text.toString()
        expenseDescription_str = expenseDescription
        if (expenseDescription.isEmpty()) {
            binding.expenseDescriptionEt.error = "Expense Description cannot be empty"
            isValid = false
        }

        return isValid
    }

    private fun showDatePickerDialog() {
        val calendar: Calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // Set the selected date in the calendar
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Format the date
                val formattedDate =
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
                Log.d("MainActivity", "Selected Date: $formattedDate")
                binding.expenseDateEt.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

}