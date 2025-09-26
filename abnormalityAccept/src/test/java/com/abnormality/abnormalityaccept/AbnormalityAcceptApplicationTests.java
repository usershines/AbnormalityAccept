package com.abnormality.abnormalityaccept;

import com.abnormality.abnormalityaccept.dto.request.InviteRequest;
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
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbnormalityAcceptApplicationTests {

    @Mock
    private UserMapper userMapper;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private UserServiceImpl userService;

    @Value("${jwt.keybase}")
    private String keyBase;

    private User mockUser;
    private User mockInviter;

    @BeforeEach
    void setUp() {
        // 创建模拟用户对象
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testUser");
        mockUser.setPassword("testPassword");
        mockUser.setLevel(1);

        // 创建模拟邀请者对象
        mockInviter = new User();
        mockInviter.setId(2L);
        mockInviter.setUsername("inviter");
        mockInviter.setPassword("inviterPassword");
        mockInviter.setLevel(3);
    }

    @Test
    void contextLoads() {
        // 基本的上下文加载测试
    }

    /**
     * 测试复杂调用关系：用户邀请新用户
     */
    @Test
    void testAddUserWithComplexDependencies() {
        // Given - 设置复杂的模拟场景
        InviteRequest inviteRequest = new InviteRequest();
        inviteRequest.setUsername("newUser");
        inviteRequest.setLevel(2);

        // 模拟数据库查询返回
        when(userMapper.selectById(2L)).thenReturn(mockInviter);
        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(null); // 确保用户名不存在
        when(userMapper.insert(any(User.class))).thenReturn(1); // 模拟插入成功

        // When - 执行测试方法
        boolean result = userService.addUser(inviteRequest, 2L);

        // Then - 验证结果和调用
        assertTrue(result);
        verify(userMapper, times(1)).selectById(2L);
        verify(userMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(userMapper, times(1)).insert(any(User.class));
    }

    /**
     * 测试复杂调用关系：用户更新个人信息及关联数据
     */
    @Test
    void testUpdateUserWithMultipleDependencies() {
        // Given - 设置复杂的模拟场景
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("oldUsername");
        existingUser.setEmail("old@example.com");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("newUsername");
        updatedUser.setEmail("new@example.com");

        List<User> subordinates = new ArrayList<>();
        User subordinate = new User();
        subordinate.setId(3L);
        subordinate.setLeaderName("oldUsername");
        subordinates.add(subordinate);

        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(null); // 用户名未被占用
        when(userMapper.selectById(1L)).thenReturn(existingUser);
        when(userMapper.updateUser(existingUser)).thenReturn(1);
        when(userMapper.findUserByLeaderId(1L)).thenReturn(subordinates);

        // When - 执行测试方法
        boolean result = userService.updateUser(createUpdateRequest(), 1L);

        // Then - 验证结果和调用
        assertTrue(result);
        verify(userMapper, times(1)).selectOne(any(QueryWrapper.class));
        verify(userMapper, times(1)).selectById(1L);
        verify(userMapper, times(1)).updateUser(existingUser);
        verify(userMapper, times(1)).findUserByLeaderId(1L);
        verify(userMapper, times(1)).updateUserAll(subordinate);
    }

    /**
     * 测试权限控制的复杂调用关系
     */
    @Test
    void testPermissionCheckInDeleteUser() {
        // Given
        User editor = new User();
        editor.setId(2L);
        editor.setLevel(4); // 高级用户

        User targetUser = new User();
        targetUser.setId(3L);
        targetUser.setLevel(2); // 被删除用户等级较低

        // 注意：deleteUserById方法中会多次调用selectById，需要为每个调用提供模拟数据
        when(userMapper.selectById(2L)).thenReturn(editor);
        when(userMapper.selectById(3L)).thenReturn(targetUser).thenReturn(targetUser).thenReturn(targetUser);
        when(userMapper.deleteUserById(3L, 0)).thenReturn(1);

        // When
        boolean result = userService.deleteUserById(3L, 2L, 0);

        // Then
        assertTrue(result);
        // 验证selectById被调用了正确的次数
        verify(userMapper, times(1)).selectById(2L);
        verify(userMapper, times(3)).selectById(3L);
        verify(userMapper, times(1)).deleteUserById(3L, 0);
    }

    /**
     * 测试异常情况的复杂调用关系
     */
    @Test
    void testAddUserWithInsufficientPermissions() {
        // Given
        InviteRequest inviteRequest = new InviteRequest();
        inviteRequest.setUsername("newUser");
        inviteRequest.setLevel(2);

        User lowLevelUser = new User();
        lowLevelUser.setId(3L);
        lowLevelUser.setLevel(2); // 权限不足

        when(userMapper.selectById(3L)).thenReturn(lowLevelUser);

        // When & Then
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.addUser(inviteRequest, 3L);
        });

        assertEquals("用户级别不足以邀请新用户", exception.getMessage());
        verify(userMapper, times(1)).selectById(3L);
        verify(userMapper, never()).insert(any(User.class));
    }

    private com.abnormality.abnormalityaccept.dto.request.UpdateUserOneSelfRequest createUpdateRequest() {
        com.abnormality.abnormalityaccept.dto.request.UpdateUserOneSelfRequest request = 
            new com.abnormality.abnormalityaccept.dto.request.UpdateUserOneSelfRequest();
        request.setUsername("newUsername");
        request.setEmail("new@example.com");
        request.setIntroduction("New intro");
        return request;
    }
}