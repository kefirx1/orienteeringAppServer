package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.data.repository.AdminPanelUserRepository
import pl.dev.bkwiatkowski.domain.model.AdminPanelUser

interface GetAdminPanelUserUC : UseCase<GetAdminPanelUserUC.Params, AdminPanelUser> {
  data class Params(
    val username: String,
  ): UseCase.Params
}

class GetAdminPanelUserUCImpl(
  private val adminPanelUserRepository: AdminPanelUserRepository,
): GetAdminPanelUserUC {
  override suspend fun invoke(params: GetAdminPanelUserUC.Params): Either<DomainError, AdminPanelUser> {
    return adminPanelUserRepository.getUserByUsername(username = params.username)
  }
}
