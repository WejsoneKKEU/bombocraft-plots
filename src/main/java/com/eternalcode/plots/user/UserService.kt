package com.eternalcode.plots.user

import java.util.*

class UserService(private val userRepository: UserRepository) {
    private val usersByUUID: MutableMap<UUID, User> = HashMap()

    fun findOrCreate(uuid: UUID): User {
        return usersByUUID.computeIfAbsent(uuid) { k: UUID? -> User(uuid) }
    }

    fun find(uuid: UUID): Optional<User> {
        return Optional.ofNullable(usersByUUID[uuid])
    }

    fun users(): Collection<User> {
        return Collections.unmodifiableCollection(usersByUUID.values)
    }
}
