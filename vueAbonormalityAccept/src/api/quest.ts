import request from "@/utils/requests.ts";

export interface Quest {
    id: number;
    questCode: string;
    questName: string;
    questLevel: number; // 添加任务等级字段
    questDescription: string;
    state: number;
    resolvingByTeamId: number;
    resolvingByTeamName: string;
    deadline?: string; // 可选字段
    assignee?: string; // 可选字段
    avatar?: string; // 可选字段
    progress?: number; // 可选字段
    history?: any[]; // 可选字段
}

//添加任务专用接口
export interface QuestRequest {
    questCode: string;
    questName : string;
    questLevel: number;
    resolvingByTeamId: number;
    resolvingByTeamName: string;
    questDescription: string;
    state: number;

}

// 搜索参数接口
export interface QuestParam {
     id: number | null,
     questCode : string | null,
     questName: string | null,
     questLevel: number | null,
     resolvingByTeamId: number | null,
     resolvingByTeamName: string | null,
     questDescription: string | null,
     state: number | null,

    //多值查询
     stateList: number[] | null,

    //范围查询参数
     minQuestLevel: number | null;
     maxQuestLevel: number | null;

     pageNum : number | null;
     pageSize: number | null;
}

// 分页查询
export function getQuestList(pageNum: number,pageSize: number){
    return request.get('/quest/list',{
        params: {
            pageNum,
            pageSize
        }
    })
}

// 由id查询任务
export function getQuest(id:number){
    return request.get(`/quest/${id}`)
}


//新建任务
export function createQuest(quest:QuestRequest){
    return request.post('/quest/new',quest);
}

export function updateQuest(quest: Quest) {
    return request.put('/quest/update', quest);
}

export function deleteQuest(id: number) {
    return request.delete(`/quest/${id}`);
}

export function searchQuests(questParam: any) {
    return request.get('/quest/conditions', {
        params: questParam
    });
}