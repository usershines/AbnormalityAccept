import request from "@/utils/requests.ts";

interface Quest{
    id: number;
    questName: string;
    questDescription: string;
    state: number;
    pageNum: number;
    pageSize: number;
}

// 分页查询
export function getQuestList(pageNum: number,pageSize: number){
    return request.get('')
}

// 由id查询任务