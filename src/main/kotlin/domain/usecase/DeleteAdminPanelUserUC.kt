package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.data.repository.AdminPanelUserRepository

interface DeleteAdminPanelUserUC : UseCase<DeleteAdminPanelUserUC.Params, Unit> {
  data class Params(
    val userId: Int,
  ) : UseCase.Params
}

class DeleteAdminPanelUserUCImpl(
  private val adminPanelUserRepository: AdminPanelUserRepository,
) : DeleteAdminPanelUserUC {
  override suspend fun invoke(params: DeleteAdminPanelUserUC.Params): Either<DomainError, Unit> {
    return adminPanelUserRepository.deleteUserById(id = params.userId)
  }
}
