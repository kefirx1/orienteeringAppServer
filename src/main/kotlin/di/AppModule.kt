package pl.dev.bkwiatkowski.di

import io.ktor.server.config.*
import org.koin.dsl.bind
import org.koin.dsl.module
import pl.dev.bkwiatkowski.controller.adminusers.AdminUsersController
import pl.dev.bkwiatkowski.controller.adminusers.handler.AddAdminUserHandler
import pl.dev.bkwiatkowski.controller.adminusers.handler.DeleteAdminUserHandler
import pl.dev.bkwiatkowski.controller.adminusers.handler.GetAllAdminUsersHandler
import pl.dev.bkwiatkowski.controller.auth.AuthController
import pl.dev.bkwiatkowski.controller.auth.handler.*
import pl.dev.bkwiatkowski.controller.events.EventController
import pl.dev.bkwiatkowski.controller.events.handler.*
import pl.dev.bkwiatkowski.controller.maps.MapController
import pl.dev.bkwiatkowski.controller.maps.handler.AddMapHandler
import pl.dev.bkwiatkowski.controller.maps.handler.DeleteMapHandler
import pl.dev.bkwiatkowski.controller.maps.handler.MapDetailHandler
import pl.dev.bkwiatkowski.controller.maps.handler.MapListHandler
import pl.dev.bkwiatkowski.controller.mobile.auth.MobileAuthController
import pl.dev.bkwiatkowski.controller.mobile.auth.handler.MobileRefreshTokenHandler
import pl.dev.bkwiatkowski.controller.mobile.auth.handler.MobileSignInHandler
import pl.dev.bkwiatkowski.controller.mobile.auth.handler.MobileSignUpHandler
import pl.dev.bkwiatkowski.controller.mobile.events.MobileEventController
import pl.dev.bkwiatkowski.controller.mobile.events.handler.*
import pl.dev.bkwiatkowski.controller.mobile.settings.MobileSettingsController
import pl.dev.bkwiatkowski.controller.mobile.settings.handler.MobileChangePasswordHandler
import pl.dev.bkwiatkowski.controller.mobile.settings.handler.MobileSettingsHandler
import pl.dev.bkwiatkowski.controller.mobile.user.MobileUserController
import pl.dev.bkwiatkowski.controller.mobile.user.handler.MobileGetUserSessionsSummaryHandler
import pl.dev.bkwiatkowski.controller.mobile.user.handler.MobileCheckUserHandler
import pl.dev.bkwiatkowski.controller.mobile.user.handler.MobileGetFriendsListHandler
import pl.dev.bkwiatkowski.controller.mobile.user.handler.MobileSendFriendRequestHandler
import pl.dev.bkwiatkowski.controller.mobile.user.handler.MobileAcceptFriendRequestHandler
import pl.dev.bkwiatkowski.controller.mobile.user.handler.MobileRemoveFriendHandler
import pl.dev.bkwiatkowski.controller.settings.SettingsController
import pl.dev.bkwiatkowski.controller.settings.handler.SettingsHandler
import pl.dev.bkwiatkowski.core.EnvironmentConfig
import pl.dev.bkwiatkowski.core.EnvironmentConfigImpl
import pl.dev.bkwiatkowski.core.database.DatabaseProvider
import pl.dev.bkwiatkowski.core.database.PostgresProvider
import pl.dev.bkwiatkowski.core.routing.Controller
import pl.dev.bkwiatkowski.core.security.coder.Base64ByteCoder
import pl.dev.bkwiatkowski.core.security.coder.ByteCoder
import pl.dev.bkwiatkowski.core.security.hashing.HashGenerator
import pl.dev.bkwiatkowski.core.security.hashing.HashGeneratorImpl
import pl.dev.bkwiatkowski.core.security.hashing.SaltGenerator
import pl.dev.bkwiatkowski.core.security.hashing.SaltGeneratorImpl
import pl.dev.bkwiatkowski.core.security.token.JwtTokenProvider
import pl.dev.bkwiatkowski.core.security.token.TokenProvider
import pl.dev.bkwiatkowski.core.serialization.JsonSerializer
import pl.dev.bkwiatkowski.core.serialization.KotlinxJsonSerializer
import pl.dev.bkwiatkowski.core.storage.LocalStorageService
import pl.dev.bkwiatkowski.core.storage.StorageService
import pl.dev.bkwiatkowski.core.validation.DefaultTextValidator
import pl.dev.bkwiatkowski.core.validation.TextValidator
import pl.dev.bkwiatkowski.data.repository.*
import pl.dev.bkwiatkowski.domain.usecase.*
import pl.dev.bkwiatkowski.plugins.HTTPPlugin
import pl.dev.bkwiatkowski.plugins.MonitoringPlugin
import pl.dev.bkwiatkowski.plugins.RoutingPlugin
import pl.dev.bkwiatkowski.plugins.SecurityPlugin

fun appModule(config: ApplicationConfig) = module {
  single<ApplicationConfig> { config }

  single<EnvironmentConfig> { EnvironmentConfigImpl(config = get()) }

  single<ByteCoder> { Base64ByteCoder() }

  single<HashGenerator> { HashGeneratorImpl() }

  single<SaltGenerator> { SaltGeneratorImpl() }

  single<DatabaseProvider> { PostgresProvider(config = get()) }

  single<TokenProvider> { JwtTokenProvider(config = get()) }

  single<JsonSerializer> { KotlinxJsonSerializer() }

  single<StorageService> { LocalStorageService(config = get()) }

  single<LocalFileRepository> { LocalFileRepositoryImpl(storageService = get()) }

  single<AdminPanelUserRepository> { AdminPanelUserRepositoryImpl(databaseProvider = get()) }

  single<RefreshTokenRepository> { RefreshTokenRepositoryImpl(databaseProvider = get()) }

  single<MobileUserRepository> { MobileUserRepositoryImpl(databaseProvider = get()) }

  single<MobileUserRefreshTokenRepository> { MobileUserRefreshTokenRepositoryImpl(databaseProvider = get()) }

  single<MobileUserFriendsRepository> { MobileUserFriendsRepositoryImpl(databaseProvider = get()) }

  single<MapRepository> { MapRepositoryImpl(databaseProvider = get()) }

  single<EventRepository> { EventRepositoryImpl(databaseProvider = get()) }

  single<SessionParticipantsRepository> { SessionParticipantsRepositoryImpl(databaseProvider = get()) }

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

  factory<GetEventBySessionUuidUC> {
    GetEventBySessionUuidUCImpl(
      eventRepository = get(),
    )
  }

  factory<AddEventUC> {
    AddEventUCImpl(
      eventRepository = get(),
      mapRepository = get(),
      createEventSessionUC = get(),
    )
  }

  factory<GetLastEventUC> {
    GetLastEventUCImpl(eventRepository = get())
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
      eventRepository = get(),
      sessionParticipantsRepository = get(),
      mobileUserRepository = get(),
    )
  }

  single { EventParticipantsProgressionHandler(getEventParticipantsProgressionUC = get(), getEventByIdUC = get(), getAdminPanelUserByIdUC = get()) }

  single { GetUserSessionWaypointDetailsHandler(getUserSessionWaypointDetailsUC = get(), getEventByIdUC = get()) }

  factory<CreateEventSessionUC> {
    CreateEventSessionUCImpl(eventRepository = get())
  }

  factory<SetEventSessionJoinableUC> {
    SetEventSessionJoinableUCImpl(eventRepository = get())
  }

  factory<CloseEventSessionUC> {
    CloseEventSessionUCImpl(eventRepository = get())
  }

  single { CreateEventSessionHandler(createEventSessionUC = get(), getEventByIdUC = get(), getAdminPanelUserByIdUC = get()) }

  single { SetEventSessionJoinableHandler(setEventSessionJoinableUC = get(), getEventByIdUC = get(), getAdminPanelUserByIdUC = get()) }

  single { CloseEventSessionHandler(closeEventSessionUC = get(), getEventByIdUC = get(), getAdminPanelUserByIdUC = get()) }

  single {
    EventController(
      eventListHandler = get(),
      eventDetailHandler = get(),
      addEventHandler = get(),
      deleteEventHandler = get(),
      eventParticipantsProgressionHandler = get(),
      eventGetUserSessionWaypointDetailsHandler = get(),
      createEventSessionHandler = get(),
      setEventSessionJoinableHandler = get(),
      closeEventSessionHandler = get(),
      imageHandler = get(),
    )
  } bind Controller::class

   single { MobileEventListHandler(getAllEventsUC = get(), getAdminPanelUserByIdUC = get()) }

   single { MobileEventDetailHandler(getEventByIdUC = get()) }

   factory<JoinSessionUC> {
     JoinSessionUCImpl(
       eventRepository = get(),
       sessionParticipantsRepository = get(),
     )
   }

   factory<IsUserInSessionUC> {
     IsUserInSessionUCImpl(
       sessionParticipantsRepository = get(),
     )
   }

  factory<GetSessionParticipantUC> {
    GetSessionParticipantUCImpl(
      sessionParticipantsRepository = get(),
    )
  }

  factory<GetFinishedSessionParticipantsUC> {
    GetFinishedSessionParticipantsUCImpl(
      sessionParticipantsRepository = get(),
    )
  }

  factory<RecordWaypointVisitUC> {
    RecordWaypointVisitUCImpl(
      sessionParticipantsRepository = get(),
    )
  }

  factory<FinishSessionUC> {
    FinishSessionUCImpl(
      sessionParticipantsRepository = get(),
    )
  }

  factory<GetUserSessionWaypointDetailsUC> {
    GetUserSessionWaypointDetailsUCImpl(
      sessionParticipantsRepository = get(),
    )
  }

  factory<GetUserSessionsSummaryUC> {
    GetUserSessionsSummaryUCImpl(
      sessionParticipantsRepository = get(),
    )
  }

  single { MobileJoinSessionHandler(joinSessionUC = get()) }

  single { MobileCheckSessionJoinHandler(isUserInSessionUC = get(), getSessionParticipantUC = get(), getEventBySessionUuidUC = get()) }

  single { MobileGetSessionWaypointDetailsHandler(getUserSessionWaypointDetailsUC = get()) }

  single { MobileGetSessionParticipantHandler(getFinishedSessionParticipantsUC = get()) }

  single { MobileGetUserSessionsSummaryHandler(getUserSessionsSummaryUC = get()) }

  single { MobileCheckUserHandler(getMobileUserUC = get(), getMobileUserByIdUC = get()) }

  single { MobileGetFriendsListHandler(getMobileUserFriendsUC = get(), getMobileUserByIdUC = get(), getFriendshipStatusUC = get(), getUserSessionsSummaryUC = get()) }

  single { MobileSendFriendRequestHandler(sendMobileUserFriendRequestUC = get()) }

  single { MobileAcceptFriendRequestHandler(acceptMobileUserFriendRequestUC = get()) }

  single { MobileRemoveFriendHandler(removeMobileUserFriendUC = get()) }

  single { MobileGetLastEventHandler(getLastEventUC = get()) }

  single { EventImageHandler(environmentConfig = get(), getAdminPanelUserByIdUC = get(), getEventByIdUC = get(), eventRepository = get()) }

  factory<StoreSessionImageUC> {
    StoreSessionImageUCImpl(
      localFileRepository = get(),
    )
  }

  single { MobileUploadImageHandler(storeSessionImageUC = get(), byteCoder = get()) }

  single { MobileFinishSessionHandler(finishSessionUC = get(), getUserSessionWaypointDetailsUC = get()) }


  single { MobileWaypointVisitListHandler(recordWaypointVisitUC = get()) }

  single { MobileRecordWaypointVisitHandler(recordWaypointVisitUC = get()) }

  single {
    MobileEventController(
      eventListHandler = get(),
      eventDetailHandler = get(),
      getLastEventHandler = get(),
      joinSessionHandler = get(),
      checkSessionJoinHandler = get(),
      getSessionWaypointDetailsHandler = get(),
      getSessionParticipantHandler = get(),
      uploadImageHandler = get(),
      waypointVisitListHandler = get(),
      recordWaypointVisitHandler = get(),
      finishSessionHandler = get(),
    )
  } bind Controller::class

  single { MobileUserController(getUserSessionsHandler = get(), checkUserExistsHandler = get(), getFriendsListHandler = get(), sendFriendRequestHandler = get(), acceptFriendRequestHandler = get(), removeFriendHandler = get()) } bind Controller::class

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

  factory<ChangeMobileUserPasswordUC> {
    ChangeMobileUserPasswordUCImpl(
      mobileUserRepository = get(),
      generateAdminPanelUserPasswordHashUC = get(),
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

  factory<GetMobileUserFriendsUC> {
    GetMobileUserFriendsUCImpl(
      mobileUserFriendsRepository = get(),
    )
  }

  factory<SendMobileUserFriendRequestUC> {
    SendMobileUserFriendRequestUCImpl(
      mobileUserFriendsRepository = get(),
      mobileUserRepository = get(),
    )
  }

  factory<AcceptMobileUserFriendRequestUC> {
    AcceptMobileUserFriendRequestUCImpl(
      mobileUserFriendsRepository = get(),
    )
  }

  factory<RemoveMobileUserFriendUC> {
    RemoveMobileUserFriendUCImpl(
      mobileUserFriendsRepository = get(),
    )
  }

  factory<GetFriendshipStatusUC> {
    GetFriendshipStatusUCImpl(
      mobileUserFriendsRepository = get(),
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
    MobileChangePasswordHandler(
      changeMobileUserPasswordUC = get(),
      getMobileUserByIdUC = get(),
      verifyMobileUserAuthenticationUC = get(),
    )
  }

  single {
    MobileSettingsController(
      mobileSettingsHandler = get(),
      mobileChangePasswordHandler = get(),
    )
  } bind Controller::class

  single { RoutingPlugin(controllers = getAll()) }
}
