package com.n7.localmind.feature.main.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.n7.localmind.design.system.theme.bottomNavElevation
import com.n7.localmind.feature.main.R
import com.n7.localmind.feature.main.ui.viewmodel.MainViewModelImpl
import kotlinx.serialization.Serializable

@Serializable
internal object BottomNavRemoteGpt

@Serializable
internal object BottomNavDocumentLocalRag

@Serializable
internal object BottomNavPerformanceLocalRag

internal data class TopLevelRoute<out T : Any>(
    val name: String,
    val route: T,
    val icon: ImageVector
)


@Composable
private fun topLevelRoutes() = listOf(
    TopLevelRoute(stringResource(R.string.nav_remote_gpt), BottomNavRemoteGpt, Icons.Filled.Face),
    TopLevelRoute(stringResource(R.string.nav_document_local_rag), BottomNavDocumentLocalRag, Icons.Outlined.Add),
    TopLevelRoute(stringResource(R.string.nav_performance_local_rag), BottomNavPerformanceLocalRag, Icons.Outlined.Warning),
)



@Composable
fun MainScreen(
    mainViewModel: MainViewModelImpl,
    remoteGptScreen: @Composable () -> Unit,
    documentLocalRagScreen: @Composable () -> Unit,
    performanceLocalRagScreen: @Composable () -> Unit,
) {

    val navController = rememberNavController()
    val topLevelRoutes = topLevelRoutes()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { Box(Modifier.size(0.dp)) },
        bottomBar = {
            BottomNavigationBar(topLevelRoutes, navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = topLevelRoutes.first().route,
            modifier = Modifier.padding(paddingValues = paddingValues)
        ) {
            composable<BottomNavRemoteGpt> { remoteGptScreen() }
            composable<BottomNavDocumentLocalRag> { documentLocalRagScreen() }
            composable<BottomNavPerformanceLocalRag> { performanceLocalRagScreen() }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    topLevelRoutes: List<TopLevelRoute<Any>>,
    navController: NavHostController
) {
    val navigationSelectedItem = rememberSaveable {
        mutableIntStateOf(0)
    }
    NavigationBar(
        modifier = Modifier.shadow(elevation = bottomNavElevation)
    ) {
        topLevelRoutes.forEachIndexed { index, navigationItem ->
            NavigationItem(
                index,
                navigationItem,
                navigationSelectedItem,
                navController
            )
        }
    }
}

@Composable
private fun RowScope.NavigationItem(
    index: Int,
    navigationItem: TopLevelRoute<Any>,
    navigationSelectedItem: MutableState<Int>,
    navController: NavHostController
) {
    NavigationBarItem(
        icon = {
            Icon(navigationItem.icon, contentDescription = navigationItem.name)
        },
        label = {
            Text(
                text = navigationItem.name.uppercase(),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                )
            )
        },
        selected = index == navigationSelectedItem.value,
        onClick = {
            navigationSelectedItem.value = index
            navController.navigate(navigationItem.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    )
}