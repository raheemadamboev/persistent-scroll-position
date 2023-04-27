package xyz.teamgravity.persistentscrollposition

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import xyz.teamgravity.persistentscrollposition.ui.theme.PersistentScrollPositionTheme

class MainActivity : ComponentActivity() {

    private val preferences: SharedPreferences by lazy { applicationContext.getSharedPreferences("Prefs", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val position = preferences.getInt("position", 0)
        setContent {
            PersistentScrollPositionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val state = rememberLazyListState(
                        initialFirstVisibleItemIndex = position
                    )

                    LaunchedEffect(key1 = state) {
                        snapshotFlow { state.firstVisibleItemIndex }
                            .debounce(500L)
                            .collectLatest { position ->
                                preferences.edit()
                                    .putInt("position", position)
                                    .apply()
                            }
                    }

                    LazyColumn(
                        state = state,
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(100) { index ->
                            Text(
                                text = "Item: $index",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}