package com.abnormality.abnormalityaccept.permission;

import com.abnormality.abnormalityaccept.entity.Abnormality;
import com.abnormality.abnormalityaccept.entity.User;
import com.abnormality.abnormalityaccept.mapper.AbnormalityMapper;
import com.abnormality.abnormalityaccept.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class AbnormalityPermissionService {

    private final UserMapper userMapper;

    private final AbnormalityMapper abnormalityMapper;

    //获取用户等级
     int getUserLevel(Long id) {
        User user = userMapper.findUserById(id);
        return user.getLevel();
    }

    boolean compareLevel(Integer level1 , Integer level2){
         return level1 > level2;
    }


}