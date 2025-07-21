import request from "@/utils/requests.ts";

// 队伍类型
export interface Team {
    id: number;
    name: string;
    status: number;
    resolvingQuestId: number;
    level:  number;
    description: string;
    leaderId: number;
}

// 队伍更新类型
export interface TeamUpdateRequest {
    id: number;
    name: string;
    status: number;
    resolvingQuestId: number;
    level:  number;
    description: string;
}

// 队伍条件查询类型
export interface TeamParam {
    id: number | null;
    name: string | null;
    status: 0 | null;
    resolvingQuestId: number | null;
    level:  number | null;
    description: string | null;
    leaderId: number | null;

    //范围查询参数
    minLevel: number | null;
    maxLevel: number | null;

    //状态
    statusList: number[] | null;

    //分页
    pageNum: number | null;
    pageSize: number | null;

}

// 获取队伍
export function getTeamList(pageNum: number,pageSize: number){
    return request.get('/team/list',{
        params:{
            pageNum:pageNum,
            pageSize:pageSize
        }
    })
}

// id查询小队
export function findTeamById(teamId: number) {
    return request.get(`/team/${teamId}`);
}

// 寻找未加入小队的用户
export function findUserBelongNoTeam(pageNum:number,pageSize:number){
    return request.get('/team/usersBelongNotTeam',{
        params:{
            pageNum:pageNum,
            pageSize:pageSize
        }
    })
}

// 新建队伍
export function addTeam(team:Team) {
    return request.post('/team/add',team)
}

// 更新队伍信息
export function updateTeam(team: TeamUpdateRequest){
    return request.put('/team/update',team)
}

// 添加队员
export function addMember(teamId: number, memberId: number) {
    return request.post('/team/addMember',{},{
        params: {
            teamId: teamId,
            userId: memberId,
        }
    })
}

// 移除队员
export function removeMember(teamId: number, memberId: number) {
    return request.post('/team/removeMember',{
        params:{
            teamId: teamId,
            userId: memberId
        }
    })
}

// 小队条件查询
export function findTeamByCondition(team: TeamParam){
    if(team.minLevel === null) team.minLevel = 1;
    if(team.maxLevel === null) team.maxLevel = 5;
    if(team.statusList === null) {
        if(team.status !== null)         team.statusList = [team.status];
        else team.statusList = [0,1,2,3]
    }
    return request.get('/team/conditions',{
        params: team
    })
}

// 删除小队
export function deleteTeam(teamId: number) {
    return request.delete(`/team/delete`,{
        params:{
            id: teamId,
        }
    });
}

// 获取小队成员
export function getMemberList(teamId: number) {
    return request.get(`/${teamId}/members`)
}