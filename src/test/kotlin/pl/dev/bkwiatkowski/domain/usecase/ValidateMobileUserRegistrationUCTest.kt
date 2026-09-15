package pl.dev.bkwiatkowski.domain.usecase

import domain.repository.MobileUserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.validation.DefaultTextValidator
import pl.dev.bkwiatkowski.core.validation.ValidationState
import pl.dev.bkwiatkowski.domain.model.MobileUser
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.fail

class ValidateMobileUserRegistrationUCTest {
  private val repository = mockk<MobileUserRepository>()
  private val useCase = ValidateMobileUserRegistrationUCImpl(
    usernameValidator = DefaultTextValidator(),
    passwordValidator = DefaultTextValidator(),
    emailValidator = DefaultTextValidator(),
    mobileUserRepository = repository,
  )
  private val eitherLeft = Either.Left(value = DomainError.Custom())

  @Test
  fun `valid request returns Valid`() = runBlocking {
    coEvery { repository.getUserByUsername(any()) } returns eitherLeft
    coEvery { repository.getUserByEmail(any()) } returns eitherLeft

    val params = ValidateMobileUserRegistrationUC.Params(
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
  fun `username already exists returns Invalid`() = runBlocking {
    coEvery { repository.getUserByUsername(any()) } returns
        Either.Right(
          value = MobileUser(
            id = 1,
            username = "john",
            email = "e@e.com",
            password = "p",
            salt = "s",
            phoneNumber = null,
            dateOfBirth = null
          ),
        )
    coEvery { repository.getUserByEmail(any()) } returns eitherLeft

    val params = ValidateMobileUserRegistrationUC.Params(
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
  fun `email already exists returns Invalid`() = runBlocking {
    coEvery { repository.getUserByUsername(any()) } returns eitherLeft
    coEvery { repository.getUserByEmail(any()) } returns Either.Right(
      value = MobileUser(
        id = 1,
        username = "u",
        email = "john@example.com",
        password = "p",
        salt = "s",
        phoneNumber = null,
        dateOfBirth = null
      ),
    )

    val params = ValidateMobileUserRegistrationUC.Params(
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
  fun `password too short returns Invalid`() = runBlocking {
    coEvery { repository.getUserByUsername(any()) } returns eitherLeft
    coEvery { repository.getUserByEmail(any()) } returns eitherLeft

    val params = ValidateMobileUserRegistrationUC.Params(
      username = "john",
      email = "john@example.com",
      password = "short",
    )

    when (val result = useCase.invoke(params)) {
      is Either.Right -> assertTrue(result.value is ValidationState.Invalid)
      is Either.Left -> fail("Expected Right but got Left: ${result.value}")
    }
    coVerify(exactly = 0) { repository.getUserByUsername(any()) }
    coVerify(exactly = 0) { repository.getUserByEmail(any()) }
  }
}
