import request from "@/utils/requests.ts";

export interface Quest{
    id: number;
    questCode: String;
    questName: string;
    questDescription: string;
    state: number;
    pageNum: number;
    pageSize: number;
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
    return request.get('')
}


//新建任务
export function createQuest(quest:Quest){
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