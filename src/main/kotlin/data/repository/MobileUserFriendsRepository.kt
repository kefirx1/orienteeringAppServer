package pl.dev.bkwiatkowski.data.repository

import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.or
import pl.dev.bkwiatkowski.core.DomainError
import pl.dev.bkwiatkowski.core.Either
import pl.dev.bkwiatkowski.core.database.DatabaseProvider
import pl.dev.bkwiatkowski.core.database.dbQuery
import pl.dev.bkwiatkowski.core.database.initTable
import pl.dev.bkwiatkowski.core.either
import pl.dev.bkwiatkowski.data.dao.MobileUserFriendDAO
import pl.dev.bkwiatkowski.data.entity.MobileUserFriendTable
import pl.dev.bkwiatkowski.data.mapper.toDomain
import pl.dev.bkwiatkowski.domain.model.FriendshipStatus
import pl.dev.bkwiatkowski.domain.model.MobileUserFriend
import java.time.LocalDateTime

interface MobileUserFriendsRepository {
  suspend fun getFriendsList(userId: Int): Either<DomainError, List<MobileUserFriend>>
  suspend fun sendFriendRequest(userId: Int, friendId: Int): Either<DomainError, Unit>
  suspend fun acceptFriendRequest(userId: Int, friendId: Int): Either<DomainError, Unit>
  suspend fun removeFriend(userId: Int, friendId: Int): Either<DomainError, Unit>
  suspend fun getFriendshipStatus(userId: Int, friendId: Int): Either<DomainError, MobileUserFriend?>
}

class MobileUserFriendsRepositoryImpl(
  databaseProvider: DatabaseProvider,
) : MobileUserFriendsRepository {

  private val database = databaseProvider.get()

  init {
    either {
      database.getRight().initTable(table = MobileUserFriendTable)
    }
  }

  override suspend fun getFriendsList(userId: Int): Either<DomainError, List<MobileUserFriend>> = either {
    database.getRight().dbQuery {
      MobileUserFriendDAO.find { MobileUserFriendTable.userId eq userId }.map { it.toDomain() }
    }.getRight()
  }

  override suspend fun sendFriendRequest(userId: Int, friendId: Int): Either<DomainError, Unit> = either {
    database.getRight().dbQuery {
      val existingFriendship = MobileUserFriendDAO.find {
        (MobileUserFriendTable.userId eq userId) and (MobileUserFriendTable.friendId eq friendId)
      }.firstOrNull()

      if (existingFriendship != null) {
        raise(error = DomainError.Custom(e = IllegalStateException("User is already in your friends list")))
      }

      val now = LocalDateTime.now()

      MobileUserFriendDAO.new {
        this.userId = userId
        this.friendId = friendId
        this.createdAt = now
        this.status = FriendshipStatus.ACCEPTED.name
      }

      MobileUserFriendDAO.new {
        this.userId = friendId
        this.friendId = userId
        this.createdAt = now
        this.status = FriendshipStatus.NOT_ACCEPTED.name
      }
    }.getRight()
  }

  override suspend fun acceptFriendRequest(userId: Int, friendId: Int): Either<DomainError, Unit> = either {
    database.getRight().dbQuery {
      val friendship = MobileUserFriendDAO.find {
        (MobileUserFriendTable.userId eq userId) and (MobileUserFriendTable.friendId eq friendId)
      }.firstOrNull()
        ?: raise(error = DomainError.Custom(e = NoSuchElementException("Friendship not found")))

      friendship.status = FriendshipStatus.ACCEPTED.name
    }.getRight()
  }

  override suspend fun removeFriend(userId: Int, friendId: Int): Either<DomainError, Unit> = either {
    database.getRight().dbQuery {
      MobileUserFriendDAO.find {
        ((MobileUserFriendTable.userId eq userId) and (MobileUserFriendTable.friendId eq friendId)) or
            ((MobileUserFriendTable.userId eq friendId) and (MobileUserFriendTable.friendId eq userId))
      }.forEach { it.delete() }
    }.getRight()
  }

  override suspend fun getFriendshipStatus(userId: Int, friendId: Int): Either<DomainError, MobileUserFriend?> = either {
    database.getRight().dbQuery {
      MobileUserFriendDAO.find {
        (MobileUserFriendTable.userId eq userId) and (MobileUserFriendTable.friendId eq friendId)
      }.firstOrNull()?.toDomain()
    }.getRight()
  }
}


