import request from "@/utils/requests.ts";

// 队伍类型
interface Team {
    id: number;
    name: string;
    members: number[];
    status: number;
    resolvingQuestId: number;
    level:  number;
    description: number;
    leaderId: number;
}

export function getTeamList(pageNum: number,pageSize: number){
    return request.get('/team/list',{
        params:{
            pageNum:pageNum,
            pageSize:pageSize
        }
    })
}