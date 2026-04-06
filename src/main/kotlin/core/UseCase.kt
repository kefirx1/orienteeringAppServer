package pl.dev.bkwiatkowski.core

interface UseCase<PARAMS: UseCase.Params, RESULT> {
  suspend operator fun invoke(params: PARAMS): Either<DomainError, RESULT>

  interface Params
}