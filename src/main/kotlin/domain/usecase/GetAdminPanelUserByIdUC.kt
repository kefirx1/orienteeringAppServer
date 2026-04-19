package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.data.repository.AdminPanelUserRepository
import pl.dev.bkwiatkowski.domain.model.AdminPanelUser

interface GetAdminPanelUserByIdUC : UseCase<GetAdminPanelUserByIdUC.Params, AdminPanelUser> {
  data class Params(
    val id: Int,
  ): UseCase.Params
}

class GetAdminPanelUserByIdUCImpl(
  private val adminPanelUserRepository: AdminPanelUserRepository,
): GetAdminPanelUserByIdUC {
  override suspend fun invoke(params: GetAdminPanelUserByIdUC.Params): Either<DomainError, AdminPanelUser> {
    return adminPanelUserRepository.getUserById(id = params.id)
  }
}
