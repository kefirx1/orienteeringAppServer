package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.data.repository.MobileUserRepository
import pl.dev.bkwiatkowski.domain.model.MobileUser
import java.time.LocalDateTime

interface AddNewMobileUserUC : UseCase<AddNewMobileUserUC.Params, Unit> {
  data class Params(
    val username: String,
    val email: String,
    val password: String,
    val phoneNumber: String? = null,
    val dateOfBirth: LocalDateTime? = null,
  ) : UseCase.Params
}

class AddNewMobileUserUCImpl(
  private val mobileUserRepository: MobileUserRepository,
  private val generateAdminPanelUserPasswordHashUC: GenerateAdminPanelUserPasswordHashUC,
) : AddNewMobileUserUC {
  override suspend fun invoke(params: AddNewMobileUserUC.Params): Either<DomainError, Unit> = either {
    val hashedPassword = generateAdminPanelUserPasswordHashUC(
      params = GenerateAdminPanelUserPasswordHashUC.Params(userPassword = params.password),
    ).getRight()

    val newUser = MobileUser(
      username = params.username,
      email = params.email,
      password = hashedPassword.hash,
      salt = hashedPassword.salt,
      phoneNumber = params.phoneNumber,
      dateOfBirth = params.dateOfBirth,
      joinedAt = LocalDateTime.now(),
    )

    mobileUserRepository.insertUser(user = newUser)
  }
}
