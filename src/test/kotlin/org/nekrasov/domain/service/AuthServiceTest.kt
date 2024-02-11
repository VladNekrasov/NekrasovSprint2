package org.nekrasov.domain.service

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.nekrasov.data.repository.UserRepository
import org.nekrasov.domain.dto.request.CreateUserDto
import org.nekrasov.domain.models.User
import org.nekrasov.utils.ErrorCode

class AuthServiceTest {

    @MockK
    lateinit var userRepository: UserRepository
    @InjectMockKs
    lateinit var authService: AuthService

    @BeforeEach
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `createUser - should return Unit when user is created successfully`() = runTest {
        //given
        val createUserDto = CreateUserDto(
            username = "vlama",
            firstName = "Vlad",
            lastName = "Nekrasov",
            password = "12345678"
        )
        val userSave = User(
            id = 10,
            username = createUserDto.username,
            firstName = createUserDto.firstName,
            lastName = createUserDto.lastName,
            password = createUserDto.password,
            token = null,
            registrationTime = Clock.System.now(),
            deleted = false
        )
        coEvery { userRepository.readByUsername(createUserDto.username) } returns null
        coEvery { userRepository.create(any()) } returns userSave
        //when
        val result = authService.createUser(createUserDto)
        //then
        assertAll(
            { assertNotNull(result) },
            { assertEquals(Unit, result.get())}
        )
        coVerify { userRepository.readByUsername(createUserDto.username) }
        coVerify { userRepository.create(any()) }
    }

    @Test
    fun `createUser - should return ErrorCode when username is duplicate`() = runTest {
        //given
        val createUserDto = CreateUserDto(
            username = "vlama",
            firstName = "Vlad",
            lastName = "Nekrasov",
            password = "12345678"
        )
        val userExists = User(
            id = 10,
            username = createUserDto.username,
            firstName = createUserDto.firstName,
            lastName = createUserDto.lastName,
            password = "8UPx1dxGZo8qqTyDx7.XdOJehmk2YQ1ikpT3WzYR6rveXicNCzExO",
            token = null,
            registrationTime = Clock.System.now(),
            deleted = false
        )
        coEvery { userRepository.readByUsername(createUserDto.username) } returns userExists
        //when
        val result = authService.createUser(createUserDto)
        //then
        assertAll(
            { assertNotNull(result) },
            { assertEquals(ErrorCode.DUPLICATE_USERNAME, result.getErrorCode())}
        )
        coVerify { userRepository.readByUsername(createUserDto.username) }
    }
    @Test
    fun `checkUser - should return True when id is equivalent to token`() = runTest {
        //given
        val idConsumer: Long = 10
        val token = "6kN0qiBXjruJVdh08d6aBumfkx6olmViP35uNtwtrCrb6F9CeXDgq"
        val user = User(
            id = idConsumer,
            username = "vlama",
            firstName = "Vlad",
            lastName = "Nekrasov",
            password = "8UPx1dxGZo8qqTyDx7.XdOJehmk2YQ1ikpT3WzYR6rveXicNCzExO",
            token = token,
            registrationTime = Clock.System.now(),
            deleted = false
        )
        val produce = user.copy()
        coEvery { userRepository.read(idConsumer) } returns user
        coEvery { userRepository.readByToken(token) } returns produce
        //when
        val result = authService.checkUser(idConsumer, token)
        //then
        assertAll(
            { assertNotNull(result) },
            { assertEquals(true, result)}
        )
        coVerify { userRepository.read(idConsumer) }
        coVerify { userRepository.readByToken(token) }
    }

    @Test
    fun `checkUser - should return False when id isn't equivalent to token`() = runTest {
        //given
        val idConsumer: Long = 10
        val token = "6kN0qiBXjruJVdh08d6aBumfkx6olmViP35uNtwtrCrb6F9CeXDgq"
        val user = User(
            id = idConsumer,
            username = "vlama",
            firstName = "Vlad",
            lastName = "Nekrasov",
            password = "8UPx1dxGZo8qqTyDx7.XdOJehmk2YQ1ikpT3WzYR6rveXicNCzExO",
            token = token,
            registrationTime = Clock.System.now(),
            deleted = false
        )
        val produce = User(
            id = idConsumer+1,
            username = "vlama2",
            firstName = "Vlad2",
            lastName = "Nekrasov2",
            password = "8UPx1dxGZo8qqTyDx7.XdOJehmk2YQ1ikpT3WzYR6rveXicNCzExOx",
            token = token,
            registrationTime = Clock.System.now(),
            deleted = false
        )
        coEvery { userRepository.read(idConsumer) } returns user
        coEvery { userRepository.readByToken(token) } returns produce
        //when
        val result = authService.checkUser(idConsumer, token)
        //then
        assertAll(
            { assertNotNull(result) },
            { assertEquals(false, result)}
        )
        coVerify { userRepository.read(idConsumer) }
        coVerify { userRepository.readByToken(token) }
    }
    @Test
    fun `checkUser - should return False when user is null`() = runTest {
        //given
        val idConsumer: Long = 10
        val token = "6kN0qiBXjruJVdh08d6aBumfkx6olmViP35uNtwtrCrb6F9CeXDgq"
        val produce = User(
            id = idConsumer+1,
            username = "vlama2",
            firstName = "Vlad2",
            lastName = "Nekrasov2",
            password = token,
            token = token,
            registrationTime = Clock.System.now(),
            deleted = false
        )
        coEvery { userRepository.read(idConsumer) } returns null
        coEvery { userRepository.readByToken(token) } returns produce
        //when
        val result = authService.checkUser(idConsumer, token)
        //then
        assertAll(
            { assertNotNull(result) },
            { assertEquals(false, result)}
        )
        coVerify { userRepository.read(idConsumer) }
        coVerify { userRepository.readByToken(token) }
    }

    @Test
    fun `checkUser - should return False when produce is null`() = runTest {
        //given
        val idConsumer: Long = 10
        val token = "6kN0qiBXjruJVdh08d6aBumfkx6olmViP35uNtwtrCrb6F9CeXDgq"
        val user = User(
            id = idConsumer,
            username = "vlama",
            firstName = "Vlad",
            lastName = "Nekrasov",
            password = "8UPx1dxGZo8qqTyDx7.XdOJehmk2YQ1ikpT3WzYR6rveXicNCzExO",
            token = token,
            registrationTime = Clock.System.now(),
            deleted = false
        )
        coEvery { userRepository.read(idConsumer) } returns user
        coEvery { userRepository.readByToken(token) } returns null
        //when
        val result = authService.checkUser(idConsumer, token)
        //then
        assertAll(
            { assertNotNull(result) },
            { assertEquals(false, result)}
        )
        coVerify { userRepository.read(idConsumer) }
        coVerify { userRepository.readByToken(token) }
    }

    @Test
    fun `checkUser - should return False when produce and user aren't exists`() = runTest {
        //given
        val idConsumer: Long = 10
        val token = "6kN0qiBXjruJVdh08d6aBumfkx6olmViP35uNtwtrCrb6F9CeXDgq"
        coEvery { userRepository.read(idConsumer) } returns null
        coEvery { userRepository.readByToken(token) } returns null
        //when
        val result = authService.checkUser(idConsumer, token)
        //then
        assertAll(
            { assertNotNull(result) },
            { assertEquals(false, result)}
        )
        coVerify { userRepository.read(idConsumer) }
        coVerify { userRepository.readByToken(token) }
    }
}