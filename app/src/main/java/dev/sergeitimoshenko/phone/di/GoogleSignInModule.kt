package dev.sergeitimoshenko.phone.di

import android.app.Application
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.sergeitimoshenko.phone.R
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GoogleSignInModule {
    @Singleton
    @Provides
    fun provideGoogleSignInOptions(app: Application) =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(app.getString(R.string.web_client_id)).requestProfile().build()

    @Singleton
    @Provides
    fun provideGoogleSignInClient(
        app: Application, options: GoogleSignInOptions
    ) = GoogleSignIn.getClient(app, options)

    @Singleton
    @Provides
    fun provideSignInIntent(signInClient: GoogleSignInClient) = signInClient.signInIntent
}

