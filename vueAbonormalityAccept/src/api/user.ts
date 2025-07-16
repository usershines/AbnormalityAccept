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

// 登录
export function login(form:any) {
    console.log(form.name)

    return request.post("/user/login", form)
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
export function updateUser(user: any){
    const update: User = {
        id: user.id,
        username: user.username,
        password: user.password,
        email: user.email,
        inviterId: user.inviterId,
        leaderId: user.leaderId,
        facilityId: user.facilityId,
        introduction: user.introduction,
        level: user.level,
    }
    return request.put('/user/updata',{update})
}

// 新建用户
export function addUser(user: any){
    const newUser: User = {
        id: 0,
        username: user.username,
        password: user.password,
        email: user.email,
        inviterId: user.inviterId,
        leaderId: user.leaderId,
        facilityId: user.facilityId,
        introduction: user.introduction,
        level: user.level,
    }
    return request.post('/user/invite', newUser)
}