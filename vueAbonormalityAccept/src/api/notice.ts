import request from "@/utils/requests.ts";

export interface Notice {
    id: number
    theme: string
    content: string
    time: Date
    state: number
}

// 获取所有公告
export function findAllNotice(){
    return request.get('notice/list',{
        params:{
            pageNum:1,
            pageSize:10,
        }
    })
}