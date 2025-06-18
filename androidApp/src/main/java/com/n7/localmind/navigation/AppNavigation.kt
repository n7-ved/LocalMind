package com.n7.localmind.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.n7.localmind.BuildConfig
import com.n7.localmind.di.appDI
import kotlinx.serialization.Serializable

@Serializable
object NavOnboarding



@Composable
fun AppNavigationComposable() {

    val navController = rememberNavController()

    NavHost(navController, startDestination = NavOnboarding) {

        composable<NavOnboarding> {

            if (BuildConfig.DEBUG) {
                SideEffect {
                    Log.d("Nick-RecomposeCheck", "AppNavigationComposable recomposed")
                }
            }

            appDI.featureMainDI.MainScreenDI(
                remoteGptScreen = { appDI.featureRemoteGptDI.RemoteGptScreenDI() },
                documentLocalRagScreen = { appDI.featureDocumentLocalRagDI.DocumentLocalRagScreenDI() },
                performanceLocalRagScreen = { appDI.featurePerformanceLocalRagDI.PerformanceLocalRagScreenDI() }
            )

/*            if (appDI.isAtleastOneDocumentUploaded()) {

                appDI.featureMainDI.MainScreenDI(
                    documentLocalRagScreen = { appDI.featureDocumentLocalRagDI.DocumentLocalRagScreenDI() },
                    remoteGptScreen = { appDI.featureRemoteGptDI.RemoteGptScreenDI() }
                )
            } else {

                appDI.featureDocumentLocalRagDI.DocumentLocalRagScreenDI()
            }*/

        }

    }
}
