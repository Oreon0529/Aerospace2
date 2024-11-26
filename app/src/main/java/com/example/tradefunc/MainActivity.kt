package com.example.tradefunc

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.tradefunc.data.Item
import com.example.tradefunc.repository.ItemRepository
import com.example.tradefunc.repository.RentalRepository
import com.example.tradefunc.ui.theme.TradeFuncTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase 익명 인증
        auth = FirebaseAuth.getInstance()
        auth.signInAnonymously()

        setContent {
            TradeFuncTheme {
                Scaffold { padding ->
                    ItemListScreen(modifier = Modifier.padding(padding))
                }
            }
        }
    }
}

@Composable
fun ItemListScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val itemRepository = ItemRepository()
    val rentalRepository = RentalRepository(context)
    var itemList by remember { mutableStateOf<List<Item>>(emptyList()) }

    LaunchedEffect(Unit) {
        itemRepository.getItems(
            onSuccess = { items ->
                itemList = items
            },
            onFailure = { exception ->
                Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        )
    }

    LazyColumn(modifier = modifier.fillMaxSize().padding(16.dp)) {
        items(itemList) { item ->
            ItemRow(item, rentalRepository, context) {
                // 목록 새로고침
                itemRepository.getItems(
                    onSuccess = { updatedItems ->
                        itemList = updatedItems
                    },
                    onFailure = {}
                )
            }
        }
    }
}

@Composable
fun ItemRow(
    item: Item,
    rentalRepository: RentalRepository,
    context: android.content.Context,
    onRefresh: () -> Unit
) {
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Name: ${item.name}")
            Text("Description: ${item.description}")
            Text("Available: ${if (item.available) "Yes" else "No"}")

            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Button(
                    onClick = {
                        val userId = "testUser"
                        rentalRepository.addRentalHistory(
                            userId = userId,
                            itemId = item.id,
                            itemName = item.name,
                            onSuccess = {
                                rentalRepository.updateItemAvailability(item.id, false,
                                    onSuccess = {
                                        onRefresh()
                                    },
                                    onFailure = {
                                        Toast.makeText(context, "Failed to update availability.", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            onFailure = { Toast.makeText(context, "Failed to rent.", Toast.LENGTH_SHORT).show() }
                        )
                    },
                    enabled = item.available,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text("Rent")
                }

                Button(
                    onClick = {
                        val userId = "testUser"
                        rentalRepository.returnItem(item.id, userId,
                            onSuccess = {
                                rentalRepository.updateItemAvailability(item.id, true,
                                    onSuccess = {
                                        onRefresh()
                                    },
                                    onFailure = {
                                        Toast.makeText(context, "Failed to update availability.", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            onFailure = {
                                Toast.makeText(context, "Failed to return.", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    enabled = !item.available,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text("Return")
                }
            }
        }
    }
}
