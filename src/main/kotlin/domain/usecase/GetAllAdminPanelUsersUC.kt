package pl.dev.bkwiatkowski.domain.usecase

import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.UseCase
import pl.dev.bkwiatkowski.data.repository.AdminPanelUserRepository
import pl.dev.bkwiatkowski.domain.model.AdminPanelUser

interface GetAllAdminPanelUsersUC : UseCase<GetAllAdminPanelUsersUC.Params, List<AdminPanelUser>> {
  object Params : UseCase.Params
}

class GetAllAdminPanelUsersUCImpl(
  private val adminPanelUserRepository: AdminPanelUserRepository,
) : GetAllAdminPanelUsersUC {
  override suspend fun invoke(params: GetAllAdminPanelUsersUC.Params): Either<DomainError, List<AdminPanelUser>> {
    return adminPanelUserRepository.getAllUsers()
  }
}
