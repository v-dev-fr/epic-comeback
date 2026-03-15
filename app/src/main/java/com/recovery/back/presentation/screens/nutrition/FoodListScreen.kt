package com.recovery.back.presentation.screens.nutrition

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.recovery.back.presentation.ui.theme.*

data class FoodItem(val name: String, val category: String, val isSafe: Boolean, val reason: String)

val foodDatabase = listOf(
    FoodItem("White Rice", "Grains", true, "Low FODMAP, easy to digest"),
    FoodItem("Oatmeal", "Grains", true, "Soluble fiber, soothing"),
    FoodItem("Banana", "Fruit", true, "Low FODMAP (firm), gentle fiber"),
    FoodItem("Blueberries", "Fruit", true, "Low FODMAP in moderate portions"),
    FoodItem("Chicken Breast", "Protein", true, "Lean protein, no triggers"),
    FoodItem("Eggs", "Protein", true, "High bioavailable protein"),
    FoodItem("Spinach", "Vegetables", true, "Low FODMAP (cooked is better)"),
    FoodItem("Carrots", "Vegetables", true, "Low FODMAP, non-gas forming"),
    
    FoodItem("Onions", "Vegetables", false, "High FODMAP (Fructans), major trigger"),
    FoodItem("Garlic", "Vegetables", false, "High FODMAP (Fructans), major trigger"),
    FoodItem("Broccoli", "Vegetables", false, "High FODMAP in large amounts, gas-forming"),
    FoodItem("Milk", "Dairy", false, "Lactose can trigger bloating/pain"),
    FoodItem("Apples", "Fruit", false, "High FODMAP (Fructose/Sorbitol)"),
    FoodItem("Beans", "Legumes", false, "High FODMAP (GOS), high gas production"),
    FoodItem("Wheat Bread", "Grains", false, "High FODMAP (Fructans) for many"),
    FoodItem("Coffee", "Drinks", false, "Caffeine can speed up motility")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodListScreen(onBackClick: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredFoods = foodDatabase.filter { it.name.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        containerColor = Obsidian,
        topBar = {
            TopAppBar(
                title = { Text("Safe Food Guide", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Obsidian, titleContentColor = TextPrimary)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                placeholder = { Text("Search foods...", color = TextSecondary) },
                shape = RoundedCornerShape(16.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricBlue,
                    unfocusedBorderColor = Color.White.copy(0.1f),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredFoods) { food ->
                    FoodCard(food)
                }
            }
        }
    }
}

@Composable
fun FoodCard(food: FoodItem) {
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(0.05f), RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (food.isSafe) Icons.Default.CheckCircle else Icons.Default.Cancel,
                contentDescription = null,
                tint = if (food.isSafe) NeonGreen else ErrorRed,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(food.name, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(food.reason, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    }
}
