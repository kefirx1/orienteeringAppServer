package pl.dev.bkwiatkowski.core

import io.ktor.utils.io.CancellationException

sealed class Either<out L, out R> {
  data class Left<out L>(val value: L) : Either<L, Nothing>()
  data class Right<out R>(val value: R) : Either<Nothing, R>()

  inline fun onLeft(action: (L) -> Unit): Either<L, R> {
    if (this is Left) {
      action(value)
    }
    return this
  }

  inline fun onRight(action: (R) -> Unit): Either<L, R> {
    if (this is Right) {
      action(value)
    }
    return this
  }

  inline fun <T> fold(onLeft: (L) -> T, onRight: (R) -> T): T {
    return when (this) {
      is Left -> onLeft(value)
      is Right -> onRight(value)
    }
  }

  inline fun <T> mapRight(transform: (R) -> T): Either<L, T> {
    return when (this) {
      is Left -> Left(value)
      is Right -> Right(transform(value))
    }
  }

  inline fun <T> mapLeft(transform: (L) -> T): Either<T, R> {
    return when (this) {
      is Left -> Left(transform(value))
      is Right -> Right(value)
    }
  }

  fun getRightOr(default: @UnsafeVariance R): R {
    return when (this) {
      is Left -> default
      is Right -> value
    }
  }

  inline fun getRightOrElse(default: (L) -> @UnsafeVariance R): R {
    return when (this) {
      is Left -> default(value)
      is Right -> value
    }
  }

  fun getRightOrNull(): R? {
    return when (this) {
      is Left -> null
      is Right -> value
    }
  }
}

@PublishedApi
internal class DefaultEitherException(val error: DomainError) : RuntimeException()

interface EitherScope {
  fun raise(error: DomainError): Nothing
  fun <R> Either<DomainError, R>.getRight(): R
}

@PublishedApi
internal class EitherScopeImpl : EitherScope {
  override fun raise(error: DomainError): Nothing {
    throw DefaultEitherException(error = error)
  }

  override fun <R> Either<DomainError, R>.getRight(): R {
    return when (this) {
      is Either.Right -> value
      is Either.Left -> raise(error = value)
    }
  }
}

inline fun <R> either(block: EitherScope.() -> R): Either<DomainError, R> {
  return try {
    val scope = EitherScopeImpl()
    Either.Right(value = scope.block())
  } catch (e: DefaultEitherException) {
    Either.Left(value = e.error)
  } catch (e: CancellationException) {
    throw e
  } catch (e: Exception) {
    Either.Left(value = DomainError.Custom(e = e))
  }
}
