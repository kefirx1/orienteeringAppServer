package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.data.repository.MobileUserRepository

interface ChangeMobileUserPasswordUC : UseCase<ChangeMobileUserPasswordUC.Params, Unit> {
  data class Params(
    val userId: Int,
    val newPassword: String,
  ) : UseCase.Params
}

class ChangeMobileUserPasswordUCImpl(
  private val mobileUserRepository: MobileUserRepository,
  private val generateAdminPanelUserPasswordHashUC: GenerateAdminPanelUserPasswordHashUC,
) : ChangeMobileUserPasswordUC {
  override suspend fun invoke(params: ChangeMobileUserPasswordUC.Params): Either<DomainError, Unit> =
    either {
      val saltedHash = generateAdminPanelUserPasswordHashUC(
        params = GenerateAdminPanelUserPasswordHashUC.Params(userPassword = params.newPassword)
      ).getRight()


      mobileUserRepository.updateUserPassword(
        id = params.userId,
        saltedHash = saltedHash,
      ).getRight()
    }
}
