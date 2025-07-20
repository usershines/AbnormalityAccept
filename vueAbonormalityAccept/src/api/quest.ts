import request from "@/utils/requests.ts";

interface Quest{
    id: number;
    questCode: String;
    questName: string;
    level: number;
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
//更新任务
export function updateQuest(quest: Quest) {
    return request.put('/quest/update', quest);
}
//删除任务
export function deleteQuest(id: number) {
    return request.delete(`/quest/${id}`);
}

// export function searchQuests(questParam: any) {
//     return request.get('/quest/conditions', {
//         params: questParam
//     });
// }
// 条件搜索任务（核心新增接口）
export function searchQuests(questParam: {
    pageNum: number;
    pageSize: number;
    questname?: string;
    level?: string;
    status?: string;
}) {
    return request.get('/quest/conditions', { params: questParam });
}