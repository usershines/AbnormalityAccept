import request from "@/utils/requests.ts";

//用户类型
interface User {

    id: number,
    username: string,
    password: string,
    email: string,
    inviterId: number,
    leaderId: number,
    facilityId: number,
    introduction: string,
    level: number,

}

export function getUserList(pageNum: number, pageSize: number){
    return request.get('/api/user/getUserList', {
        params:{
            pageNum,
            pageSize,
        }
    })
}