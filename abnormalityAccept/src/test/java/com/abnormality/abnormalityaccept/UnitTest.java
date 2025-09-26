package com.abnormality.abnormalityaccept;

import com.abnormality.abnormalityaccept.dto.request.InviteRequest;
import com.abnormality.abnormalityaccept.dto.request.UpdateUserOneSelfRequest;
import com.abnormality.abnormalityaccept.entity.User;
import com.abnormality.abnormalityaccept.exception.ServiceException;
import com.abnormality.abnormalityaccept.mapper.UserMapper;
import com.abnormality.abnormalityaccept.service.RedisService;
import com.abnormality.abnormalityaccept.service.impl.UserServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UnitTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockUser;
    private User mockAdmin;

    @BeforeEach
    void setUp() {
        // 创建模拟普通用户对象
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testUser");
        mockUser.setPassword("salt$md5password");
        mockUser.setLevel(5);
        mockUser.setIsActive(1);

        // 创建模拟管理员用户对象
        mockAdmin = new User();
        mockAdmin.setId(2L);
        mockAdmin.setUsername("admin");
        mockAdmin.setPassword("salt$md5password");
        mockAdmin.setLevel(5);
        mockAdmin.setIsActive(1);
    }

    /**
     * 测试查找所有用户功能 - 正常情况
     */
    @Test
    void testFindAllUser_Normal() {
        // Given
        List<User> userList = new ArrayList<>();
        userList.add(mockUser);
        userList.add(mockAdmin);

        when(userMapper.selectById(2L)).thenReturn(mockAdmin);
        when(userMapper.findAllUser()).thenReturn(userList);

        // When
        com.github.pagehelper.PageInfo<User> result = userService.findAllUser(1, 10, 2L);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getList().size());
        assertEquals("testUser", result.getList().get(0).getUsername());
        assertEquals("admin", result.getList().get(1).getUsername());
    }

    /**
     * 测试查找所有用户功能 - 用户不存在
     */
    @Test
    void testFindAllUser_UserNotFound() {
        // Given
        when(userMapper.selectById(2L)).thenReturn(null);

        // When & Then
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.findAllUser(1, 10, 2L);
        });

        assertEquals("用户不存在", exception.getMessage());
    }

    /**
     * 测试查找所有用户功能 - 权限不足
     */
    @Test
    void testFindAllUser_InsufficientPermission() {
        // Given
        User lowLevelUser = new User();
        lowLevelUser.setId(3L);
        lowLevelUser.setLevel(2);

        when(userMapper.selectById(3L)).thenReturn(lowLevelUser);

        // When & Then
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.findAllUser(1, 10, 3L);
        });

        assertEquals("权限不足", exception.getMessage());
    }

    /**
     * 测试根据ID查找用户功能 - 正常情况
     */
    @Test
    void testFindUserById_Normal() {
        // Given
        when(userMapper.selectById(2L)).thenReturn(mockAdmin);
        when(userMapper.selectById(1L)).thenReturn(mockUser);

        // When
        User result = userService.findUserById(1L, 2L);

        // Then
        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        assertEquals(1L, result.getId());
    }

    /**
     * 测试根据ID查找用户功能 - 当前用户不存在
     */
    @Test
    void testFindUserById_CurrentUserNotFound() {
        // Given
        when(userMapper.selectById(2L)).thenReturn(null);

        // When & Then
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.findUserById(1L, 2L);
        });

        assertEquals("用户不存在", exception.getMessage());
    }

    /**
     * 测试根据ID查找用户功能 - 目标用户不存在
     */
    @Test
    void testFindUserById_TargetUserNotFound() {
        // Given
        when(userMapper.selectById(2L)).thenReturn(mockAdmin);
        when(userMapper.selectById(1L)).thenReturn(null);

        // When & Then
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.findUserById(1L, 2L);
        });

        assertEquals("用户不存在", exception.getMessage());
    }

    /**
     * 测试根据ID查找用户时权限不足的情况
     */
    @Test
    void testFindUserByIdWithInsufficientPermission() {
        // Given
        User lowLevelUser = new User();
        lowLevelUser.setId(3L);
        lowLevelUser.setLevel(2);

        when(userMapper.selectById(1L)).thenReturn(mockUser);
        when(userMapper.selectById(3L)).thenReturn(lowLevelUser);

        // When & Then
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.findUserById(1L, 3L);
        });

        assertEquals("无权查看当前用户", exception.getMessage());
    }

    /**
     * 测试添加用户功能 - 正常情况
     */
    @Test
    void testAddUser_Normal() {
        // Given
        InviteRequest inviteRequest = new InviteRequest();
        inviteRequest.setUsername("newUser");
        inviteRequest.setLevel(2);

        when(userMapper.selectById(2L)).thenReturn(mockAdmin);
        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        // When
        boolean result = userService.addUser(inviteRequest, 2L);

        // Then
        assertTrue(result);
        verify(userMapper).insert(any(User.class));
    }

    /**
     * 测试添加用户功能 - 当前用户不存在
     */
    @Test
    void testAddUser_CurrentUserNotFound() {
        // Given
        InviteRequest inviteRequest = new InviteRequest();
        inviteRequest.setUsername("newUser");
        inviteRequest.setLevel(2);

        when(userMapper.selectById(2L)).thenReturn(null);

        // When & Then
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.addUser(inviteRequest, 2L);
        });

        assertEquals("用户不存在", exception.getMessage());
    }

    /**
     * 测试添加用户功能 - 权限不足
     */
    @Test
    void testAddUser_InsufficientPermission() {
        // Given
        InviteRequest inviteRequest = new InviteRequest();
        inviteRequest.setUsername("newUser");
        inviteRequest.setLevel(5); // 尝试创建权限级别高于自己的用户

        User lowLevelUser = new User();
        lowLevelUser.setId(3L);
        lowLevelUser.setLevel(2);

        when(userMapper.selectById(3L)).thenReturn(lowLevelUser);

        // When & Then
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.addUser(inviteRequest, 3L);
        });

        assertEquals("用户级别不足以邀请新用户", exception.getMessage());
    }

    /**
     * 测试添加用户时用户名已存在的情况
     */
    @Test
    void testAddUserWithExistingUsername() {
        // Given
        InviteRequest inviteRequest = new InviteRequest();
        inviteRequest.setUsername("existingUser");
        inviteRequest.setLevel(2);

        when(userMapper.selectById(2L)).thenReturn(mockAdmin);
        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(mockUser);

        // When & Then
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.addUser(inviteRequest, 2L);
        });

        assertEquals("用户名已存在", exception.getMessage());
        verify(userMapper, never()).insert(any(User.class));
    }

    /**
     * 测试更新用户信息功能 - 正常情况
     */
    @Test
    void testUpdateUser_Normal() {
        // Given
        UpdateUserOneSelfRequest updateRequest = new UpdateUserOneSelfRequest();
        updateRequest.setUsername("updatedUser");
        updateRequest.setEmail("updated@example.com");
        updateRequest.setIntroduction("Updated introduction");

        List<User> subordinates = new ArrayList<>();
        User subordinate = new User();
        subordinate.setId(3L);
        subordinate.setLeaderName("testUser");
        subordinates.add(subordinate);

        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);
        when(userMapper.selectById(1L)).thenReturn(mockUser);
        when(userMapper.updateUser(mockUser)).thenReturn(1);
        when(userMapper.findUserByLeaderId(1L)).thenReturn(subordinates);
        when(userMapper.updateUserAll(subordinate)).thenReturn(1);

        // When
        boolean result = userService.updateUser(updateRequest, 1L);

        // Then
        assertTrue(result);
        assertEquals("updatedUser", mockUser.getUsername());
        assertEquals("updated@example.com", mockUser.getEmail());
        assertEquals("Updated introduction", mockUser.getIntroduction());
    }

    /**
     * 测试更新用户信息功能 - 用户名已存在
     */
    @Test
    void testUpdateUser_UsernameExists() {
        // Given
        UpdateUserOneSelfRequest updateRequest = new UpdateUserOneSelfRequest();
        updateRequest.setUsername("existingUser");

        User existingUser = new User();
        existingUser.setId(3L);
        existingUser.setUsername("existingUser");

        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(existingUser);

        // When & Then
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.updateUser(updateRequest, 1L);
        });

        assertEquals("用户名已存在", exception.getMessage());
    }

    /**
     * 测试更新用户信息功能 - 用户不存在
     */
    @Test
    void testUpdateUser_UserNotFound() {
        // Given
        UpdateUserOneSelfRequest updateRequest = new UpdateUserOneSelfRequest();
        updateRequest.setUsername("updatedUser");

        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);
        when(userMapper.selectById(1L)).thenReturn(null);

        // When & Then
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.updateUser(updateRequest, 1L);
        });

        assertEquals("用户不存在", exception.getMessage());
    }

    /**
     * 测试更新用户信息功能 - 有下属用户
     */
    @Test
    void testUpdateUser_WithSubordinates() {
        // Given
        UpdateUserOneSelfRequest updateRequest = new UpdateUserOneSelfRequest();
        updateRequest.setUsername("updatedUser");

        List<User> subordinates = new ArrayList<>();
        User subordinate1 = new User();
        subordinate1.setId(3L);
        subordinate1.setLeaderName("testUser");
        User subordinate2 = new User();
        subordinate2.setId(4L);
        subordinate2.setLeaderName("testUser");
        subordinates.add(subordinate1);
        subordinates.add(subordinate2);

        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);
        when(userMapper.selectById(1L)).thenReturn(mockUser);
        when(userMapper.updateUser(mockUser)).thenReturn(1);
        when(userMapper.findUserByLeaderId(1L)).thenReturn(subordinates);
        when(userMapper.updateUserAll(subordinate1)).thenReturn(1);
        when(userMapper.updateUserAll(subordinate2)).thenReturn(1);

        // When
        boolean result = userService.updateUser(updateRequest, 1L);

        // Then
        assertTrue(result);
        assertEquals("updatedUser", mockUser.getUsername());
        verify(userMapper).updateUserAll(subordinate1);
        verify(userMapper).updateUserAll(subordinate2);
    }

    /**
     * 测试删除用户功能 - 正常情况
     */
    @Test
    void testDeleteUserById_Normal() {
        // Given
        when(userMapper.selectById(2L)).thenReturn(mockAdmin);
        when(userMapper.selectById(1L)).thenReturn(mockUser);
        when(userMapper.deleteUserById(1L, 0)).thenReturn(1);

        // When
        boolean result = userService.deleteUserById(1L, 2L, 0);

        // Then
        assertTrue(result);
    }

    /**
     * 测试删除用户功能 - 当前用户不存在
     */
    @Test
    void testDeleteUserById_CurrentUserNotFound() {
        // Given
        when(userMapper.selectById(2L)).thenReturn(null);

        // When & Then
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.deleteUserById(1L, 2L, 0);
        });

        assertEquals("用户不存在", exception.getMessage());
    }

    /**
     * 测试删除用户功能 - 目标用户不存在
     */
    @Test
    void testDeleteUserById_TargetUserNotFound() {
        // Given
        when(userMapper.selectById(2L)).thenReturn(mockAdmin);
        when(userMapper.selectById(1L)).thenReturn(null);

        // When & Then
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.deleteUserById(1L, 2L, 0);
        });

        assertEquals("用户不存在", exception.getMessage());
    }

    /**
     * 测试删除用户功能 - 权限不足
     */
    @Test
    void testDeleteUserById_InsufficientPermission() {
        // Given
        User lowLevelUser = new User();
        lowLevelUser.setId(3L);
        lowLevelUser.setLevel(2);

        when(userMapper.selectById(3L)).thenReturn(lowLevelUser);
        when(userMapper.selectById(1L)).thenReturn(mockUser);

        // When & Then
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.deleteUserById(1L, 3L, 0);
        });

        assertEquals("权限不足", exception.getMessage());
    }

    /**
     * 测试删除用户功能 - 不能删除自己
     */
    @Test
    void testDeleteUserById_CannotDeleteSelf() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(mockUser);

        // When & Then
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.deleteUserById(1L, 1L, 0);
        });

        assertEquals("不能删除自己", exception.getMessage());
    }

    /**
     * 测试密码更新功能 - 正常情况
     */
    @Test
    void testUpdatePassword_Normal() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(mockUser);
        when(userMapper.updateById(mockUser)).thenReturn(1);

        // When
        boolean result = userService.updatePassword(1L, "newPassword");

        // Then
        assertTrue(result);
        assertNotEquals("salt$md5password", mockUser.getPassword());
    }

    /**
     * 测试密码更新功能 - 用户不存在
     */
    @Test
    void testUpdatePassword_UserNotFound() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(null);

        // When & Then
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.updatePassword(1L, "newPassword");
        });

        assertEquals("用户不存在", exception.getMessage());
    }

    /**
     * 测试更新用户激活状态功能 - 正常情况
     */
    @Test
    void testUpdateUserIsActive_Normal() {
        // Given
        mockUser.setIsActive(1);
        when(userMapper.selectById(1L)).thenReturn(mockUser);
        when(userMapper.updateUserAll(mockUser)).thenReturn(1);

        // When
        boolean result = userService.updateUserIsActive(1L, 0);

        // Then
        assertTrue(result);
        assertEquals(0, mockUser.getIsActive());
    }

    /**
     * 测试更新用户激活状态功能 - 用户不存在
     */
    @Test
    void testUpdateUserIsActive_UserNotFound() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(null);

        // When & Then
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.updateUserIsActive(1L, 0);
        });

        assertEquals("用户不存在", exception.getMessage());
    }
}
