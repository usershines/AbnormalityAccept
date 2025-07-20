import request from "@/utils/requests.ts";

// 队伍类型
export interface Team {
    id: number;
    name: string;
    members: number[];
    status: number;
    resolvingQuestId: number;
    level:  number;
    description: number;
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
    id: number;
    name: string;
    status: 0;
    resolvingQuestId: number;
    level:  number;
    description: string;
    leaderId: number;

    //范围查询参数
    minLevel: number;
    maxLevel: number;

    //状态
    statusList: number[];

    //分页
    pageNum: number;
    pageSize: number;

}

export function getTeamList(pageNum: number,pageSize: number){
    return request.get('/team/list',{
        params:{
            pageNum:pageNum,
            pageSize:pageSize
        }
    })
}

export function findTeamById(teamId: number) {
    return request.get(`/team/${teamId}`);
}

export function findUserBelongNoTeam(pageNum:number,pageSize:number){
    return request.get('/team/usersBelongNotTeam',{
        params:{
            pageNum:pageNum,
            pageSize:pageSize
        }
    })
}

export function addTeam(team:Team) {
    return request.post('/team/add',team)
}

export function updateTeam(team: TeamUpdateRequest){
    return request.put('/team/update',team)
}

export function addMember(teamId: number, memberId: number) {
    return request.post('/team/addMember',{
        params:{
            teamId: teamId,
            userId: memberId
        }
    })
}

export function removeMember(teamId: number, memberId: number) {
    return request.post('/team/removeMember',{
        params:{
            teamId: teamId,
            userId: memberId
        }
    })
}

export function findTeamByCondition(team: TeamParam){
    if(team.minLevel === null) team.minLevel = 1;
    if(team.maxLevel === null) team.maxLevel = 5;
    if(team.statusList === null) team.statusList = [team.status]
    return request.get('/team/conditions',{
        params: team
    })
}