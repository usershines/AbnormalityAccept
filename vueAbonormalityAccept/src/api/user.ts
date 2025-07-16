import request from "@/utils/requests.ts";
import {User} from "@element-plus/icons-vue";

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

// 分页获取用户信息
export function getUserList(pageNum: number, pageSize: number){
    return request.get('/user/List', {
        params:{
            pageNum,
            pageSize,
        }
    })
}

// 更新用户信息
export function updataUser(updataUser: any){
    const updata: User = {
        id: updataUser.id,
        username: updataUser.username,
        password: updataUser.password,
        email: updataUser.email,
        inviterId: updataUser.inviterId,
        leaderId: updataUser.leaderId,
        facilityId: updataUser.facilityId,
        introduction: updataUser.introduction,
        level: updataUser.level,
    }
    return request.put('/user/updata',updata)
}
