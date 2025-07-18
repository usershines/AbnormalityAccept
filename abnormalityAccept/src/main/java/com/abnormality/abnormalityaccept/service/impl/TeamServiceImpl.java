//package com.abnormality.abnormalityaccept.service.impl;
//
//import com.abnormality.abnormalityaccept.entity.Team;
//import com.abnormality.abnormalityaccept.entity.User;
//import org.springframework.stereotype.Service;
//
///**
// * @author shanh
// * @version 1.0
// * {@code @description:}
// * @since 2025-07-13
// */
//@Service
//public class TeamServiceImpl {
//
//}
package com.abnormality.abnormalityaccept.service.impl;

import com.abnormality.abnormalityaccept.annotation.Level;
import com.abnormality.abnormalityaccept.entity.Team;
import com.abnormality.abnormalityaccept.entity.User;
import com.abnormality.abnormalityaccept.exception.BaseException;
import com.abnormality.abnormalityaccept.mapper.TeamMapper;
import com.abnormality.abnormalityaccept.mapper.UserMapper;
import com.abnormality.abnormalityaccept.mapper.QuestMapper;
import com.abnormality.abnormalityaccept.service.TeamService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamMapper teamMapper;
    private final UserMapper userMapper;
    private final QuestMapper questMapper;


}