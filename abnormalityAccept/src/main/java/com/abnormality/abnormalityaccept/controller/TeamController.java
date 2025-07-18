//package com.abnormality.abnormalityaccept.controller;
//
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * @author shanh
// * @version 1.0
// * {@code @description:}
// * @since 2025-07-13
// */
//@RestController
//@CrossOrigin
//@Tag(name = "小队管理")
//public class TeamController {
//
//
//}
package com.abnormality.abnormalityaccept.controller;

import com.abnormality.abnormalityaccept.dto.Result;
import com.abnormality.abnormalityaccept.entity.Team;
import com.abnormality.abnormalityaccept.entity.User;
import com.abnormality.abnormalityaccept.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/teams")
@Tag(name = "机动小队管理", description = "包含小队创建、更新、任务指派等接口")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;


}
