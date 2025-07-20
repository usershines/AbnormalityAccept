package com.abnormality.abnormalityaccept.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import com.abnormality.abnormalityaccept.annotation.Level;
import com.abnormality.abnormalityaccept.dto.request.EditSubordinateRequest;
import com.abnormality.abnormalityaccept.dto.request.InviteRequest;
import com.abnormality.abnormalityaccept.dto.request.UpdateUserOneSelfRequest;
import com.abnormality.abnormalityaccept.dto.request.UserParamRequest;
import com.abnormality.abnormalityaccept.dto.response.AuthResponse;
import com.abnormality.abnormalityaccept.entity.*;
import com.abnormality.abnormalityaccept.enums.Code;
import com.abnormality.abnormalityaccept.exception.BaseException;
import com.abnormality.abnormalityaccept.exception.ServiceException;
import com.abnormality.abnormalityaccept.mapper.EmailMapper;
import com.abnormality.abnormalityaccept.mapper.FacilityMapper;
import com.abnormality.abnormalityaccept.mapper.TeamMapper;
import com.abnormality.abnormalityaccept.mapper.UserMapper;
import com.abnormality.abnormalityaccept.service.RedisService;
import com.abnormality.abnormalityaccept.service.UserService;
import com.abnormality.abnormalityaccept.util.AopUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务实现类，提供用户管理、认证、权限验证等功能。
 *
 * <p>该类实现了 UserService 接口，封装了对数据库的操作，并处理 JWT 生成与校验、密码加密等安全相关逻辑。</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {




    /**
     * JWT 密钥基础字符串，用于生成和验证 JWT 的密钥。
     */
    @Value("${jwt.keybase}")
    private String keyBase;

    /**
     * Redis 服务实例，用于缓存 Token 并设置过期时间。
     */
    @Autowired
    private RedisService redisService;


    @Autowired
    private final UserMapper userMapper;

    @Autowired
    private final EmailMapper emailMapper;

    @Autowired
    private final FacilityMapper facilityMapper;

    /**
     * 分页查询所有用户信息。
     *
     * <p>使用 PageHelper 实现分页功能，将查询结果封装为 PageInfo 返回。</p>
     *
     * @param pageNum  当前页码
     * @param pageSize 每页显示条数
     * @return 包含用户列表的 PageInfo 对象
     */
    @Override
    public PageInfo<User> findAllUser(Integer pageNum, Integer pageSize, Long finderId) {

        int finderLevel = userMapper.selectById(finderId).getLevel();

        // 开启分页
        PageHelper.startPage(pageNum, pageSize);
        // 查询用户列表
        List<User> userList = userMapper.findAllUser();
        // 封装分页结果
        return PageInfo.of(userList);
    }

    /**
     * 根据用户 ID 查询用户详情。
     *
     * @param id 用户唯一标识
     * @return 查询到的用户对象
     */
    @Override
    public User findUserById(Long id , Long finderId) {
        User finder = userMapper.selectById(finderId);
        if(userMapper.selectById( id)== null) throw new ServiceException(Code.NOT_FOUND, "用户不存在");
        int finderLevel = finder.getLevel();
        if(finderLevel<userMapper.selectById(id).getLevel())throw new ServiceException(Code.FORBIDDEN,"无权查看当前用户");
        return userMapper.selectById(id);}

    @Override
    public User findUserByName(String name) {
        return userMapper.selectOne(new QueryWrapper<User>().eq("username", name));
    }

    /**
     * 根据用户 ID 删除用户。
     *
     * @param id 用户唯一标识
     * @return 删除成功返回 true，否则返回 false
     */
    @Override
    public boolean deleteUserById(Long id , Long editorId) {
        int editorLevel = userMapper.selectById(editorId).getLevel();
        if(editorLevel<=userMapper.selectById(id).getLevel())throw new ServiceException(Code.FORBIDDEN,"无权删除当前用户");
        if(userMapper.selectById(id).getLevel()==3 && editorLevel!=5) throw new ServiceException(Code.FORBIDDEN,"无权删除当前用户");
        int i = userMapper.deleteUserById(id);
        return i > 0;
    }

    /**
     * 添加新用户，并根据邀请者身份进行权限控制。
     *
     * <p>此方法包含以下逻辑：</p>
     * <ul>
     *   <li>邀请者等级不足 3 级时，不能邀请新用户。</li>
     *   <li>用户名已存在时抛出异常。</li>
     *   <li>邀请者为 3 级用户时，新用户级别必须低于 3 。</li>
     *   <li>邀请者为 4 级用户时，新用户级别必须低于 4。</li>
     * </ul>
     *
     * @param inviteRequest   待添加的新用户对象
     * @param inviterId 邀请者的用户 ID
     * @return 添加成功返回 true，否则返回 false
     * @throws ServiceException 如果违反权限规则或用户名重复时抛出业务异常
     */
    @Override
    public boolean addUser(InviteRequest inviteRequest, Long inviterId) {
        if(inviteRequest.getUsername()== null || inviteRequest.getUsername().isEmpty()) throw new ServiceException(Code.ERROR,"用户名不能为空");
        User newUser = new User();
        User inviter = userMapper.selectById(inviterId);

        int inviterLevel = inviter.getLevel();
        if (inviterLevel <= 2)
            throw new ServiceException(Code.FORBIDDEN, "用户级别不足以邀请新用户");
        if (userMapper.selectOne(new QueryWrapper<User>().eq("username", inviteRequest.getUsername())) != null)
            throw new ServiceException(Code.ERROR, "用户名已存在");
        if (inviterLevel == 3) {
            if (inviteRequest.getLevel() >= 3)
                throw new ServiceException(Code.FORBIDDEN, "只能设置比自己等级低的级别");
        }
        if (inviterLevel == 4) {
            if (inviteRequest.getLevel() >= 4)
                throw new ServiceException(Code.FORBIDDEN, "只能设置比自己等级低的级别");
        }
        newUser.setUsername(inviteRequest.getUsername());
        newUser.setFacilityId(null);
        newUser.setLevel(inviteRequest.getLevel());
        newUser.setPassword(encryptPassword(newUser.getUsername() + "888"));
        newUser.setInviterId(inviterId);
        newUser.setInviterName(userMapper.selectById(inviterId).getUsername());
        newUser.setLeaderId(inviterId);
        newUser.setLeaderName(userMapper.selectById(inviterId).getUsername());
        return userMapper.insert(newUser)>0;
    }


    @Override
    public boolean updateUser(UpdateUserOneSelfRequest updateUserOneSelfRequest, Long UserId) {

        if(updateUserOneSelfRequest.getUsername() == null) throw new ServiceException(Code.ERROR,"用户名不能为空");
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", updateUserOneSelfRequest.getUsername() ));
        if(ObjectUtil.isNotEmpty(user)) throw new ServiceException(Code.ERROR,"用户名已存在");

        User editor = userMapper.selectById(UserId);
        editor.setUsername(updateUserOneSelfRequest.getUsername());
        editor.setEmail(updateUserOneSelfRequest.getEmail());
        editor.setIntroduction(updateUserOneSelfRequest.getIntroduction());
        List<User> users = userMapper.findUserByLeaderId(editor.getId());
        for (User BelongUser : users) {
            BelongUser.setLeaderName(editor.getUsername());
            BelongUser.setInviterName(editor.getUsername());
            userMapper.updateUserAll(BelongUser);
        }

        List<Facility> facilities = facilityMapper.selectList(new QueryWrapper<Facility>().eq("manager_id", editor.getId()));
        for (Facility facility : facilities){
            facility.setManagerName(editor.getUsername());
            facilityMapper.updateFacility(facility);
        }

        List<Email> emails1 = emailMapper.selectList(new QueryWrapper<Email>().eq("sender_id", editor.getId()));
        for (Email email : emails1){
            email.setSenderName(editor.getUsername());
            emailMapper.updateById(email);
        }
        List<Email> emails2 = emailMapper.selectList(new QueryWrapper<Email>().eq("receiver_id", editor.getId()));
        for (Email email : emails2){
            email.setReceiverName(editor.getUsername());
            emailMapper.updateById(email);
        }

        return userMapper.updateUser(editor) > 0;
    }

    @Override
    @Level(allowLevel = {2,3,4,5})
    public boolean editSubordinate(EditSubordinateRequest editSubordinateRequest, Long editorId) {
        Integer subordinateLevel = editSubordinateRequest.getLevel();
        String leaderName = editSubordinateRequest.getLeaderName();
        if(subordinateLevel == null) throw new ServiceException(Code.ERROR,"等级不能为空");
        if(leaderName == null) throw new ServiceException(Code.ERROR,"上级领导不能为空");
        if(subordinateLevel<1 || subordinateLevel>5) throw new ServiceException(Code.BAD_REQUEST, "非法参数");
        User editedUser = userMapper.selectById(editSubordinateRequest.getSubordinateId());
        User editor = userMapper.selectById(editorId);
        if(editedUser == null) throw new ServiceException(Code.NOT_FOUND, "用户不存在");
        if(editedUser.getLevel()>=editor.getLevel()) throw new ServiceException(Code.BAD_REQUEST, "不能修改等级比自己高的用户信息");
        editedUser.setLevel(subordinateLevel);
        editedUser.setLeaderName(leaderName);
        User leader = userMapper.selectOne(new QueryWrapper<User>().eq("username",leaderName));
        editedUser.setLeaderId(leader.getId());
        return userMapper.editSubordinate(editedUser)>0;
    }

    /**
     * 根据条件分页查询用户信息。
     *
     * @param userParamRequest      查询条件封装的对象
     * @return 包含符合条件用户的 PageInfo 对象
     */
    @Override
    public PageInfo<User> findUserByConditions(UserParamRequest userParamRequest ) {
        PageHelper.startPage(userParamRequest.getPageNum(),userParamRequest.getPageSize());
        List<User> userList = userMapper.findUserByConditions(userParamRequest);
        return PageInfo.of(userList);
    }

    /**
     * 更新指定用户的密码。
     *
     * @param userId    用户 ID
     * @param newPassword 新密码
     * @return 更新成功返回 true，否则返回 false
     * @throws ServiceException 如果用户不存在时抛出异常
     */
    public boolean updatePassword(Long userId, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new ServiceException(Code.NOT_FOUND, "用户不存在");
        }

        user.setPassword(encryptPassword(newPassword));
        int result = userMapper.updateById( user);
        return result > 0;
    }

    /**
     * 用户登录方法，验证用户名和密码并生成 JWT Token。
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回 AuthResponse，包含用户名和 Token
     * @throws BaseException 如果用户不存在或密码错误时抛出异常
     */
    @Override
    public AuthResponse login(String username, String password) {
        AuthResponse authResponse = new AuthResponse();
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (ObjectUtil.isNull(user)) {
            throw new BaseException("用户不存在");
        }
        if (!verifyPassword(password, user.getPassword())) {
            throw new BaseException("密码错误");
        }
        authResponse.setName(user.getUsername());
        String token = generateJwt(user);
        redisService.setEx(getTokenKey(token), token, 12 * 60 * 60);
        authResponse.setToken(token);
        return authResponse;
    }

    /**
     * 用户注册方法，创建新用户并生成 JWT Token。
     *
     * @param username 用户名
     * @param password 密码
     * @return 注册成功返回 AuthResponse，包含用户名和 Token
     * @throws BaseException 如果用户名已存在时抛出异常
     */
    @Override
    public AuthResponse register(String username, String password) {
        AuthResponse authResponse = new AuthResponse();
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (ObjectUtil.isNotEmpty(user)) {
            throw new BaseException("用户已存在");
        }
        user = new User();
        user.setUsername(username);
        user.setPassword(encryptPassword(password));
        user.setLevel(1);
        userMapper. insert(user);
        String token = generateJwt(user);
        redisService.setEx(getTokenKey(token), token, 12 * 60 * 60);
        authResponse.setName(user.getUsername());
        authResponse.setToken(token);
        return authResponse;
    }
    /**
     * 优化后的注册，注册仅将新用户加入数据库，不需要生成Token以及加入Redis
     */
    @Override
    public boolean regist(String username, String password, String email) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (ObjectUtil.isNotEmpty(user)) {
            throw new BaseException("用户已存在");
        }
        user = new User();
        user.setUsername(username);
        user.setPassword(encryptPassword(password));
        user.setEmail(email);
        return userMapper.insert(user) > 0;
    }

    /**
     * 用户登出方法，从 Redis 中移除 Token。
     *
     * @param token 登录 Token
     * @return 登出成功返回 true
     */
    @Override
    public boolean logout(String token) {
        if (redisService.exists(getTokenKey(token))) {
            redisService.delete(getTokenKey(token));
        }
        return true;
    }

    /**
     * 验证 Token 是否有效。
     *
     * @param token 登录 Token
     * @return 验证通过返回 true，否则抛出异常
     * @throws BaseException 如果 Token 无效或已过期时抛出异常
     */
    @Override
    public boolean verify(String token) {
        if (!redisService.exists(getTokenKey(token))) {
            throw new BaseException(Code.ERROR, "用户未登录");
        }
        try {
            return validateJwt(token);
        } catch (JwtException e) {
            if (redisService.exists(getTokenKey(token))) {
                redisService.delete(getTokenKey(token));
            }
            throw new BaseException(Code.ERROR, "Token 无效，请重新登录", e);
        }catch (BaseException e) {
            throw new BaseException(Code.ERROR,"Token 验证过程中发生异常");
        }
    }

    /**
     * 生成 JWT Token。
     *
     * <p>使用用户名和密钥生成 MD5 作为签名密钥，并设置过期时间为当前日期加指定天数。</p>
     *
     * @param user 用户对象，用于提取用户名和权限等级
     * @return 生成的 JWT Token 字符串
     */
    private String generateJwt(User user) {
        String key = SecureUtil.md5(keyBase + user.getUsername());
        byte[] keytBytes = key.getBytes();
        log.info("生成JWT参数");
        log.info("key:{},username:{},", key, user.getUsername());

        JwtPayload jwtPayload = new JwtPayload();
        jwtPayload.setUsername(user.getUsername());
        jwtPayload.setLevel(user.getLevel());

        String token = JWT.create()
                .addPayloads(jwtPayload.toMap())
                .setKey(keytBytes)
                .setExpiresAt(DateUtil.date().offset(DateField.DAY_OF_YEAR, jwtPayload.getExpDays()))
                .sign();
        return token;
    }

    /**
     * 验证 JWT Token 的有效性。
     *
     * @param token 待验证的 JWT Token
     * @return 验证通过返回 true，否则抛出异常
     * @throws BaseException 如果 Token 无效、过期或用户不存在时抛出异常
     */
    private boolean validateJwt(String token) {
        JWT jwt = JWT.of(token);
        JwtPayload payload = jwt.getPayloads().toBean(JwtPayload.class);
        String username = payload.getUsername();
        String key = SecureUtil.md5(keyBase + username);
        byte[] keytBytes = key.getBytes();
        log.info("验证JWT参数");
        log.info("key:{},username:{}", key, username);
        JWTValidator validator = JWTValidator.of(token);
        try {
            validator.validateDate(DateUtil.date());
        } catch (ValidateException e) {
            throw new BaseException(Code.ERROR, "用户未登录", e);
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new BaseException(Code.ERROR, "用户不存在");
        }
        boolean result = JWTUtil.verify(token, keytBytes);
        log.info("用户名：{} 验证结果：{}", username, result);

        return result;
    }

    /**
     * 加密用户密码，使用盐值 + MD5 加密方式。
     *
     * @param password 原始密码
     * @return 加密后的字符串，格式为 salt$md5pwd
     */
    private String encryptPassword(String password) {
        String salt = RandomUtil.randomString(16);
        String md5pwd = SecureUtil.md5(salt + password);
        return salt + "$" + md5pwd;
    }

    /**
     * 验证输入密码是否与加密后的密码匹配。
     *
     * @param password 输入的明文密码
     * @param inputPwd 加密后的密码（格式为 salt$md5pwd）
     * @return 匹配返回 true，否则返回 false
     */
    private boolean verifyPassword(String password, String inputPwd) {
        String salt = inputPwd.substring(0, inputPwd.indexOf("$"));
        String md5pwd = SecureUtil.md5(salt + password);
        return inputPwd.substring(inputPwd.indexOf("$") + 1).equals(md5pwd);
    }

    /**
     * 构建 Redis 缓存 Token 的键。
     *
     * @param token 登录 Token
     * @return Redis 中存储的键名
     */
    private String getTokenKey(String token) {
        JWT jwt = JWT.of(token);
        return jwt.getPayload("username").toString();
    }

//    /**
//     * 解析 JWT Token 获取负载信息。
//     *
//     * @param token 待解析的 JWT Token
//     * @return 解析得到的 JwtPayload 对象
//     */
//    private JwtPayload getJwtPayload(String token) {
//        JWT jwt = JWT.of(token);
//        return jwt.getPayloads().toBean(JwtPayload.class);
//    }

    /**
     *
     * 封装从前端获取用户ID的方法。
     * @return 用户ID
     */
    public Long getUserIdByToken(){
        String token = AopUtil.getToken();
        String username = JwtPayload.fromToken(token).getUsername();
        return userMapper.findUserByName(username).getId();
    }

}




