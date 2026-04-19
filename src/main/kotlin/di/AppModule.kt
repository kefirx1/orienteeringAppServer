package pl.dev.bkwiatkowski.di

import io.ktor.server.config.*
import org.koin.dsl.bind
import org.koin.dsl.module
import pl.dev.bkwiatkowski.plugins.RoutingPlugin
import pl.dev.bkwiatkowski.controller.auth.AuthController
import pl.dev.bkwiatkowski.controller.auth.handler.AuthenticateHandler
import pl.dev.bkwiatkowski.controller.auth.handler.RefreshTokenHandler
import pl.dev.bkwiatkowski.controller.auth.handler.SignInHandler
import pl.dev.bkwiatkowski.controller.auth.handler.SignUpHandler
import pl.dev.bkwiatkowski.core.routing.Controller
import pl.dev.bkwiatkowski.core.EnvironmentConfig
import pl.dev.bkwiatkowski.core.EnvironmentConfigImpl
import pl.dev.bkwiatkowski.core.database.DatabaseProvider
import pl.dev.bkwiatkowski.core.database.PostgresProvider
import pl.dev.bkwiatkowski.core.security.coder.Base64ByteCoder
import pl.dev.bkwiatkowski.core.security.coder.ByteCoder
import pl.dev.bkwiatkowski.core.security.hashing.HashGenerator
import pl.dev.bkwiatkowski.core.security.hashing.HashGeneratorImpl
import pl.dev.bkwiatkowski.core.security.hashing.SaltGenerator
import pl.dev.bkwiatkowski.core.security.hashing.SaltGeneratorImpl
import pl.dev.bkwiatkowski.core.security.token.JwtTokenProvider
import pl.dev.bkwiatkowski.core.security.token.TokenProvider
import pl.dev.bkwiatkowski.core.validation.DefaultTextValidator
import pl.dev.bkwiatkowski.core.validation.TextValidator
import pl.dev.bkwiatkowski.data.repository.AdminPanelUserRepository
import pl.dev.bkwiatkowski.data.repository.AdminPanelUserRepositoryImpl
import pl.dev.bkwiatkowski.data.repository.RefreshTokenRepository
import pl.dev.bkwiatkowski.data.repository.RefreshTokenRepositoryImpl
import pl.dev.bkwiatkowski.domain.usecase.GenerateAdminPanelUserPasswordHashUC
import pl.dev.bkwiatkowski.domain.usecase.GenerateAdminPanelUserPasswordHashUCImpl
import pl.dev.bkwiatkowski.domain.usecase.AddNewAdminPanelUserUC
import pl.dev.bkwiatkowski.domain.usecase.AddNewAdminPanelUserUCImpl
import pl.dev.bkwiatkowski.domain.usecase.GetAdminPanelUserUC
import pl.dev.bkwiatkowski.domain.usecase.GetAdminPanelUserUCImpl
import pl.dev.bkwiatkowski.domain.usecase.GetAdminPanelUserByIdUC
import pl.dev.bkwiatkowski.domain.usecase.GetAdminPanelUserByIdUCImpl
import pl.dev.bkwiatkowski.domain.usecase.VerifyAdminPanelUserAuthenticationUC
import pl.dev.bkwiatkowski.domain.usecase.VerifyAdminPanelUserAuthenticationUCImpl
import pl.dev.bkwiatkowski.domain.usecase.ValidateAdminPanelUserRequestUC
import pl.dev.bkwiatkowski.domain.usecase.ValidateAdminPanelUserRequestUCImpl
import pl.dev.bkwiatkowski.domain.usecase.SaveRefreshTokenUC
import pl.dev.bkwiatkowski.domain.usecase.SaveRefreshTokenUCImpl
import pl.dev.bkwiatkowski.domain.usecase.VerifyAndRevokeRefreshTokenUC
import pl.dev.bkwiatkowski.domain.usecase.VerifyAndRevokeRefreshTokenUCImpl
import pl.dev.bkwiatkowski.domain.usecase.RevokeAllUserRefreshTokensUC
import pl.dev.bkwiatkowski.domain.usecase.RevokeAllUserRefreshTokensUCImpl
import pl.dev.bkwiatkowski.plugins.MonitoringPlugin
import pl.dev.bkwiatkowski.plugins.SecurityPlugin
import pl.dev.bkwiatkowski.controller.auth.handler.LogoutHandler
import pl.dev.bkwiatkowski.plugins.HTTPPlugin

fun appModule(config: ApplicationConfig) = module {
  single<ApplicationConfig> { config }

  single<EnvironmentConfig> { EnvironmentConfigImpl(config = get()) }

  single<ByteCoder> { Base64ByteCoder() }

  single<HashGenerator> { HashGeneratorImpl() }

  single<SaltGenerator> { SaltGeneratorImpl() }

  single<DatabaseProvider> { PostgresProvider(config = get()) }

  single<TokenProvider> { JwtTokenProvider(config = get()) }

  single<AdminPanelUserRepository> { AdminPanelUserRepositoryImpl(databaseProvider = get()) }
  
  single<RefreshTokenRepository> { RefreshTokenRepositoryImpl(databaseProvider = get()) }

  factory<TextValidator> { DefaultTextValidator() }

  factory<ValidateAdminPanelUserRequestUC> {
    ValidateAdminPanelUserRequestUCImpl(
      usernameValidator = get(),
      passwordValidator = get(),
      emailValidator = get(),
      adminPanelUserRepository = get()
    )
  }

  factory<AddNewAdminPanelUserUC> {
    AddNewAdminPanelUserUCImpl(
      adminPanelUserRepository = get(),
      generateAdminPanelUserPasswordHashUC = get(),
    )
  }

  factory<GetAdminPanelUserUC> {
    GetAdminPanelUserUCImpl(
      adminPanelUserRepository = get(),
    )
  }

  factory<GetAdminPanelUserByIdUC> {
    GetAdminPanelUserByIdUCImpl(
      adminPanelUserRepository = get(),
    )
  }

  factory<GenerateAdminPanelUserPasswordHashUC> {
    GenerateAdminPanelUserPasswordHashUCImpl(
      saltGenerator = get(),
      hashGenerator = get(),
      byteCoder = get(),
    )
  }

  factory<VerifyAdminPanelUserAuthenticationUC> {
    VerifyAdminPanelUserAuthenticationUCImpl(
      hashGenerator = get(),
      byteCoder = get(),
    )
  }
  
  factory<SaveRefreshTokenUC> {
    SaveRefreshTokenUCImpl(
      refreshTokenRepository = get(),
      environmentConfig = get(),
    )
  }
  
  factory<VerifyAndRevokeRefreshTokenUC> {
    VerifyAndRevokeRefreshTokenUCImpl(
      refreshTokenRepository = get(),
    )
  }
  
  factory<RevokeAllUserRefreshTokensUC> {
    RevokeAllUserRefreshTokensUCImpl(
      refreshTokenRepository = get(),
    )
  }

  single { HTTPPlugin(environmentConfig = get()) }

  single { SecurityPlugin(environmentConfig = get()) }

  single { MonitoringPlugin() }

  single { AuthenticateHandler() }

  single { 
    SignUpHandler(
      addNewAdminPanelUserUC = get(),
      validateAdminPanelUserRequestUC = get()
    ) 
  }

  single {
    SignInHandler(
      getAdminPanelUserUC = get(),
      verifyAdminPanelUserAuthenticationUC = get(),
      tokenProvider = get(),
      environmentConfig = get(),
      saveRefreshTokenUC = get(),
    )
  }
  
  single {
    RefreshTokenHandler(
      tokenProvider = get(),
      environmentConfig = get(),
      verifyAndRevokeRefreshTokenUC = get(),
      revokeAllUserRefreshTokensUC = get(),
      saveRefreshTokenUC = get(),
      getAdminPanelUserByIdUC = get(),
    )
  }
  
  single {
    LogoutHandler(
      revokeAllUserRefreshTokensUC = get(),
    )
  }

  single {
    AuthController(
      authenticateHandler = get(),
      signUpHandler = get(),
      signInHandler = get(),
      refreshTokenHandler = get(),
      logoutHandler = get(),
    )
  } bind Controller::class

  single { RoutingPlugin(controllers = getAll()) }
}
