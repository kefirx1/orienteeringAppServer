package pl.dev.bkwiatkowski.di

import io.ktor.server.config.*
import org.koin.dsl.bind
import org.koin.dsl.module
import pl.dev.bkwiatkowski.plugins.RoutingPlugin
import pl.dev.bkwiatkowski.controller.auth.AuthController
import pl.dev.bkwiatkowski.controller.auth.handler.AuthenticateHandler
import pl.dev.bkwiatkowski.controller.auth.handler.ChangePasswordHandler
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
import pl.dev.bkwiatkowski.domain.usecase.ChangePasswordUC
import pl.dev.bkwiatkowski.domain.usecase.ChangePasswordUCImpl
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
import pl.dev.bkwiatkowski.controller.settings.SettingsController
import pl.dev.bkwiatkowski.controller.settings.handler.SettingsHandler
import pl.dev.bkwiatkowski.data.repository.MapRepository
import pl.dev.bkwiatkowski.data.repository.MapRepositoryImpl
import pl.dev.bkwiatkowski.data.repository.EventRepository
import pl.dev.bkwiatkowski.data.repository.EventRepositoryImpl
import pl.dev.bkwiatkowski.domain.usecase.GetAllMapsUC
import pl.dev.bkwiatkowski.domain.usecase.GetAllMapsUCImpl
import pl.dev.bkwiatkowski.domain.usecase.GetMapByIdUC
import pl.dev.bkwiatkowski.domain.usecase.GetMapByIdUCImpl
import pl.dev.bkwiatkowski.domain.usecase.AddMapUC
import pl.dev.bkwiatkowski.domain.usecase.AddMapUCImpl
import pl.dev.bkwiatkowski.domain.usecase.DeleteMapUC
import pl.dev.bkwiatkowski.domain.usecase.DeleteMapUCImpl
import pl.dev.bkwiatkowski.domain.usecase.GetAllEventsUC
import pl.dev.bkwiatkowski.domain.usecase.GetAllEventsUCImpl
import pl.dev.bkwiatkowski.domain.usecase.GetEventByIdUC
import pl.dev.bkwiatkowski.domain.usecase.GetEventByIdUCImpl
import pl.dev.bkwiatkowski.domain.usecase.AddEventUC
import pl.dev.bkwiatkowski.domain.usecase.AddEventUCImpl
import pl.dev.bkwiatkowski.domain.usecase.DeleteEventUC
import pl.dev.bkwiatkowski.domain.usecase.DeleteEventUCImpl
import pl.dev.bkwiatkowski.domain.usecase.CompleteEventUC
import pl.dev.bkwiatkowski.domain.usecase.CompleteEventUCImpl
import pl.dev.bkwiatkowski.controller.maps.MapController
import pl.dev.bkwiatkowski.controller.maps.handler.MapListHandler
import pl.dev.bkwiatkowski.controller.maps.handler.MapDetailHandler
import pl.dev.bkwiatkowski.controller.maps.handler.AddMapHandler
import pl.dev.bkwiatkowski.controller.maps.handler.DeleteMapHandler
import pl.dev.bkwiatkowski.controller.events.EventController
import pl.dev.bkwiatkowski.controller.events.handler.EventListHandler
import pl.dev.bkwiatkowski.controller.events.handler.EventDetailHandler
import pl.dev.bkwiatkowski.controller.events.handler.AddEventHandler
import pl.dev.bkwiatkowski.controller.events.handler.DeleteEventHandler
import pl.dev.bkwiatkowski.controller.events.handler.CompleteEventHandler
import pl.dev.bkwiatkowski.controller.adminusers.AdminUsersController
import pl.dev.bkwiatkowski.controller.adminusers.handler.GetAllAdminUsersHandler
import pl.dev.bkwiatkowski.controller.adminusers.handler.DeleteAdminUserHandler
import pl.dev.bkwiatkowski.controller.adminusers.handler.AddAdminUserHandler
import pl.dev.bkwiatkowski.domain.usecase.GetAllAdminPanelUsersUC
import pl.dev.bkwiatkowski.domain.usecase.GetAllAdminPanelUsersUCImpl
import pl.dev.bkwiatkowski.domain.usecase.DeleteAdminPanelUserUC
import pl.dev.bkwiatkowski.domain.usecase.DeleteAdminPanelUserUCImpl
import pl.dev.bkwiatkowski.controller.mobile.auth.MobileAuthController
import pl.dev.bkwiatkowski.controller.mobile.auth.handler.MobileSignUpHandler
import pl.dev.bkwiatkowski.controller.mobile.auth.handler.MobileSignInHandler
import pl.dev.bkwiatkowski.controller.mobile.auth.handler.MobileRefreshTokenHandler
import pl.dev.bkwiatkowski.data.repository.MobileUserRepository
import pl.dev.bkwiatkowski.data.repository.MobileUserRepositoryImpl
import pl.dev.bkwiatkowski.data.repository.MobileUserRefreshTokenRepository
import pl.dev.bkwiatkowski.data.repository.MobileUserRefreshTokenRepositoryImpl
import pl.dev.bkwiatkowski.domain.usecase.AddNewMobileUserUC
import pl.dev.bkwiatkowski.domain.usecase.AddNewMobileUserUCImpl
import pl.dev.bkwiatkowski.domain.usecase.GetMobileUserUC
import pl.dev.bkwiatkowski.domain.usecase.GetMobileUserUCImpl
import pl.dev.bkwiatkowski.domain.usecase.GetMobileUserByIdUC
import pl.dev.bkwiatkowski.domain.usecase.GetMobileUserByIdUCImpl
import pl.dev.bkwiatkowski.domain.usecase.ValidateMobileUserRegistrationUC
import pl.dev.bkwiatkowski.domain.usecase.ValidateMobileUserRegistrationUCImpl
import pl.dev.bkwiatkowski.domain.usecase.VerifyMobileUserAuthenticationUC
import pl.dev.bkwiatkowski.domain.usecase.VerifyMobileUserAuthenticationUCImpl
import pl.dev.bkwiatkowski.domain.usecase.SaveMobileUserRefreshTokenUC
import pl.dev.bkwiatkowski.domain.usecase.SaveMobileUserRefreshTokenUCImpl
import pl.dev.bkwiatkowski.domain.usecase.VerifyAndRevokeMobileUserRefreshTokenUC
import pl.dev.bkwiatkowski.domain.usecase.VerifyAndRevokeMobileUserRefreshTokenUCImpl
import pl.dev.bkwiatkowski.domain.usecase.RevokeAllMobileUserRefreshTokensUC
import pl.dev.bkwiatkowski.domain.usecase.RevokeAllMobileUserRefreshTokensUCImpl
import pl.dev.bkwiatkowski.data.repository.MobileUserEventProgressionRepository
import pl.dev.bkwiatkowski.data.repository.MobileUserEventProgressionRepositoryImpl
import pl.dev.bkwiatkowski.domain.usecase.GetEventParticipantsProgressionUC
import pl.dev.bkwiatkowski.domain.usecase.GetEventParticipantsProgressionUCImpl
import pl.dev.bkwiatkowski.controller.events.handler.EventParticipantsProgressionHandler
import pl.dev.bkwiatkowski.controller.mobile.settings.MobileSettingsController
import pl.dev.bkwiatkowski.controller.mobile.settings.handler.MobileSettingsHandler

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

  single<MobileUserRepository> { MobileUserRepositoryImpl(databaseProvider = get()) }

  single<MobileUserRefreshTokenRepository> { MobileUserRefreshTokenRepositoryImpl(databaseProvider = get()) }

  single<MapRepository> { MapRepositoryImpl(databaseProvider = get()) }

  single<EventRepository> { EventRepositoryImpl(databaseProvider = get()) }

  single<MobileUserEventProgressionRepository> { MobileUserEventProgressionRepositoryImpl(databaseProvider = get()) }

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

  factory<ChangePasswordUC> {
    ChangePasswordUCImpl(
      adminPanelUserRepository = get(),
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

  factory<GetAllMapsUC> {
    GetAllMapsUCImpl(
      mapRepository = get(),
    )
  }

  factory<GetMapByIdUC> {
    GetMapByIdUCImpl(
      mapRepository = get(),
    )
  }

  factory<AddMapUC> {
    AddMapUCImpl(
      mapRepository = get(),
    )
  }

  factory<DeleteMapUC> {
    DeleteMapUCImpl(
      mapRepository = get(),
    )
  }

  factory<GetAllEventsUC> {
    GetAllEventsUCImpl(
      eventRepository = get(),
    )
  }

  factory<GetEventByIdUC> {
    GetEventByIdUCImpl(
      eventRepository = get(),
    )
  }

  factory<AddEventUC> {
    AddEventUCImpl(
      eventRepository = get(),
      mapRepository = get(),
    )
  }

  factory<DeleteEventUC> {
    DeleteEventUCImpl(
      eventRepository = get(),
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
     ChangePasswordHandler(
       changePasswordUC = get(),
       getAdminPanelUserByIdUC = get(),
       verifyAdminPanelUserAuthenticationUC = get(),
     )
   }

   single {
     AuthController(
       authenticateHandler = get(),
       signUpHandler = get(),
       signInHandler = get(),
       refreshTokenHandler = get(),
       logoutHandler = get(),
       changePasswordHandler = get(),
     )
   } bind Controller::class

  single { SettingsHandler(getAdminPanelUserByIdUC = get()) }

  single {
    SettingsController(settingsHandler = get())
  } bind Controller::class

  single { MapListHandler(getAllMapsUC = get()) }

  single { MapDetailHandler(getMapByIdUC = get()) }

  single { AddMapHandler(addMapUC = get()) }

  single { DeleteMapHandler(deleteMapUC = get()) }

  single { EventListHandler(getAllEventsUC = get(), getAdminPanelUserByIdUC = get()) }

  single { EventDetailHandler(getEventByIdUC = get(), getAdminPanelUserByIdUC = get()) }

  single { AddEventHandler(addEventUC = get()) }

  single { DeleteEventHandler(deleteEventUC = get(), getAdminPanelUserByIdUC = get()) }

  factory<GetAllAdminPanelUsersUC> {
    GetAllAdminPanelUsersUCImpl(adminPanelUserRepository = get())
  }

  factory<DeleteAdminPanelUserUC> {
    DeleteAdminPanelUserUCImpl(adminPanelUserRepository = get())
  }

  single { GetAllAdminUsersHandler(getAllAdminPanelUsersUC = get()) }

  single { DeleteAdminUserHandler(deleteAdminPanelUserUC = get()) }

  single {
    AddAdminUserHandler(
      addNewAdminPanelUserUC = get(),
      validateAdminPanelUserRequestUC = get(),
    )
  }

  single {
    AdminUsersController(
      getAllAdminUsersHandler = get(),
      deleteAdminUserHandler = get(),
      addAdminUserHandler = get(),
    )
  } bind Controller::class

  single {
    MapController(
      mapListHandler = get(),
      mapDetailHandler = get(),
      addMapHandler = get(),
      deleteMapHandler = get(),
    )
  } bind Controller::class

  single { DeleteEventHandler(deleteEventUC = get(), getAdminPanelUserByIdUC = get()) }

  factory<GetEventParticipantsProgressionUC> {
    GetEventParticipantsProgressionUCImpl(
      progressionRepository = get(),
      mobileUserRepository = get(),
    )
  }

  single { EventParticipantsProgressionHandler(getEventParticipantsProgressionUC = get(), getEventByIdUC = get(), getAdminPanelUserByIdUC = get()) }

  factory<CompleteEventUC> {
    CompleteEventUCImpl(eventRepository = get())
  }

  single { CompleteEventHandler(completeEventUC = get(), getEventByIdUC = get(), getAdminPanelUserByIdUC = get()) }

  single {
    EventController(
      eventListHandler = get(),
      eventDetailHandler = get(),
      addEventHandler = get(),
      deleteEventHandler = get(),
      completeEventHandler = get(),
      eventParticipantsProgressionHandler = get(),
    )
  } bind Controller::class

  factory<AddNewMobileUserUC> {
    AddNewMobileUserUCImpl(
      mobileUserRepository = get(),
      generateAdminPanelUserPasswordHashUC = get(),
    )
  }

  factory<GetMobileUserUC> {
    GetMobileUserUCImpl(mobileUserRepository = get())
  }

  factory<GetMobileUserByIdUC> {
    GetMobileUserByIdUCImpl(mobileUserRepository = get())
  }

  factory<ValidateMobileUserRegistrationUC> {
    ValidateMobileUserRegistrationUCImpl(
      usernameValidator = get(),
      passwordValidator = get(),
      emailValidator = get(),
      mobileUserRepository = get(),
    )
  }

  factory<VerifyMobileUserAuthenticationUC> {
    VerifyMobileUserAuthenticationUCImpl(
      hashGenerator = get(),
      byteCoder = get(),
    )
  }

  factory<SaveMobileUserRefreshTokenUC> {
    SaveMobileUserRefreshTokenUCImpl(
      mobileUserRefreshTokenRepository = get(),
      environmentConfig = get(),
    )
  }

  factory<VerifyAndRevokeMobileUserRefreshTokenUC> {
    VerifyAndRevokeMobileUserRefreshTokenUCImpl(
      mobileUserRefreshTokenRepository = get(),
    )
  }

  factory<RevokeAllMobileUserRefreshTokensUC> {
    RevokeAllMobileUserRefreshTokensUCImpl(
      mobileUserRefreshTokenRepository = get(),
    )
  }

  single {
    MobileSignUpHandler(
      addNewMobileUserUC = get(),
      validateMobileUserRegistrationUC = get(),
      getMobileUserUC = get(),
      tokenProvider = get(),
      environmentConfig = get(),
      saveMobileUserRefreshTokenUC = get(),
    )
  }

  single {
    MobileSignInHandler(
      getMobileUserUC = get(),
      verifyMobileUserAuthenticationUC = get(),
      tokenProvider = get(),
      environmentConfig = get(),
      saveMobileUserRefreshTokenUC = get(),
    )
  }

  single {
    MobileRefreshTokenHandler(
      tokenProvider = get(),
      environmentConfig = get(),
      verifyAndRevokeMobileUserRefreshTokenUC = get(),
      revokeAllMobileUserRefreshTokensUC = get(),
      saveMobileUserRefreshTokenUC = get(),
      getMobileUserByIdUC = get(),
    )
  }

  single {
    MobileAuthController(
      mobileSignUpHandler = get(),
      mobileSignInHandler = get(),
      mobileRefreshTokenHandler = get(),
    )
  } bind Controller::class

  single { MobileSettingsHandler() }

  single {
    MobileSettingsController(
      mobileSettingsHandler = get(),
    )
  } bind Controller::class

  single { RoutingPlugin(controllers = getAll()) }
}
