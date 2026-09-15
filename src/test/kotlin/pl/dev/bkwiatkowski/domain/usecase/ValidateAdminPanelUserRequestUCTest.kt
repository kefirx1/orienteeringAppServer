package pl.dev.bkwiatkowski.domain.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.validation.DefaultTextValidator
import pl.dev.bkwiatkowski.core.validation.ValidationState
import pl.dev.bkwiatkowski.domain.model.AdminPanelUser
import pl.dev.bkwiatkowski.domain.repository.AdminPanelUserRepository
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.fail

class ValidateAdminPanelUserRequestUCTest {
  private val repository = mockk<AdminPanelUserRepository>()
  private val useCase = ValidateAdminPanelUserRequestUCImpl(
    usernameValidator = DefaultTextValidator(),
    passwordValidator = DefaultTextValidator(),
    emailValidator = DefaultTextValidator(),
    adminPanelUserRepository = repository,
  )

  private val eitherLeft = Either.Left(value = DomainError.Custom())

  @Test
  fun `valid request returns Valid`() = runTest {
    coEvery { repository.getUserByUsername(any()) } returns eitherLeft
    coEvery { repository.getUserByEmail(any()) } returns eitherLeft

    val params = ValidateAdminPanelUserRequestUC.Params(
      username = "john",
      email = "john@example.com",
      password = "verysecure",
    )

    when (val result = useCase(params)) {
      is Either.Right -> assertTrue(result.value is ValidationState.Valid)
      is Either.Left -> fail("Expected Right but got Left: ${result.value}")
    }
  }

  @Test
  fun `username already exists returns Invalid`() = runTest {
    coEvery { repository.getUserByUsername(any()) } returns
        Either.Right(
          value = AdminPanelUser(
            id = 1,
            username = "john",
            email = "e@e.com",
            password = "p",
            salt = "s",
            role = AdminPanelUser.Role.USER
          ),
        )
    coEvery { repository.getUserByEmail(any()) } returns eitherLeft

    val params = ValidateAdminPanelUserRequestUC.Params(
      username = "john",
      email = "john@example.com",
      password = "verysecure",
    )

    when (val result = useCase(params)) {
      is Either.Right -> assertTrue(result.value is ValidationState.Invalid)
      is Either.Left -> fail("Expected Right but got Left: ${result.value}")
    }
  }

  @Test
  fun `email already exists returns Invalid`() = runTest {
    coEvery { repository.getUserByUsername(any()) } returns eitherLeft
    coEvery { repository.getUserByEmail(any()) } returns
        Either.Right(
          value = AdminPanelUser(
            id = 1,
            username = "u",
            email = "john@example.com",
            password = "p",
            salt = "s",
            role = AdminPanelUser.Role.USER
          ),
        )

    val params = ValidateAdminPanelUserRequestUC.Params(
      username = "john",
      email = "john@example.com",
      password = "verysecure",
    )

    when (val result = useCase(params)) {
      is Either.Right -> assertTrue(result.value is ValidationState.Invalid)
      is Either.Left -> fail("Expected Right but got Left: ${result.value}")
    }
  }

  @Test
  fun `wrong username returns Invalid early`() = runTest {
    coEvery { repository.getUserByUsername(any()) } returns eitherLeft
    coEvery { repository.getUserByEmail(any()) } returns eitherLeft

    val params = ValidateAdminPanelUserRequestUC.Params(
      username = "",
      email = "john@example.com",
      password = "verysecure",
    )

    when (val result = useCase(params)) {
      is Either.Right -> assertTrue(result.value is ValidationState.Invalid)
      is Either.Left -> fail("Expected Right but got Left: ${result.value}")
    }
    coVerify(exactly = 0) { repository.getUserByUsername(any()) }
    coVerify(exactly = 0) { repository.getUserByEmail(any()) }
  }

  @Test
  fun `wrong password returns Invalid and does not query repo`() = runTest {
    coEvery { repository.getUserByUsername(any()) } returns eitherLeft
    coEvery { repository.getUserByEmail(any()) } returns eitherLeft

    val params = ValidateAdminPanelUserRequestUC.Params(
      username = "john",
      email = "john@example.com",
      password = "short",
    )

    when (val result = useCase(params)) {
      is Either.Right -> assertTrue(result.value is ValidationState.Invalid)
      is Either.Left -> fail("Expected Right but got Left: ${result.value}")
    }

    coVerify(exactly = 0) { repository.getUserByUsername(any()) }
    coVerify(exactly = 0) { repository.getUserByEmail(any()) }
  }
}

