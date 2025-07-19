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

// 登录
export function login(form:any) {
    console.log(form.name)

    return request.post("/user/login", form)
}

// 分页获取用户信息
export function getUserList(pageNum: number, pageSize: number){
    return request.get('/user/list', {
        params:{
            pageNum: pageNum,
            pageSize: pageSize,
        }
    })
}

// 更新用户信息
export function updateUser(user: any){
    return request.put('/user/update',user)
}

// 新建用户
export function addUser(user: any){
    const newUser = {
        id: 0,
        ...user,
    }
    return request.post('/user/invite', newUser)
}

// 删除用户
export function deleteUser(id:number){
    return request.delete(`/user/${id}` )
}

// 用户登出
export function logout(){
        return request.post('/user/logout')
}

// 条件查询
export function findUser(user: any, pageNum: number, pageSize: number){
    return request.post('/user/findUserByCondition', user, {
        params:{
            pageNum: pageNum,
            pageSize: pageSize,
        }
    })
}