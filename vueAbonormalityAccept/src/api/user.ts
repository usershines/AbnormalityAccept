import request from "@/utils/requests.ts";

//用户类型
export interface User {

    id: number | null,
    username: string | null,
    password: string | null,
    email: string | null,
    inviterId: number | null,
    inviterName: string | null,
    leaderId: number | null,
    leaderName: string | null,
    facilityId: number | null,
    facilityName: string | null,
    introduction: string | null,
    level: number | null,

}

// 用户条件查询
export interface UserParamsRequest{
    id: number | null,
    username: string | null,
    password: string | null,
    email: string | null,
    level: number | null,
    teamId: number | null,
    inviterId: number | null,
    inviterName: string | null,
    leaderId: number | null,
    leaderName: string | null,
    facilityId: number | null,
    facilityName: string | null,
    introduction: string | null,
    isActive: number | null,

    // 等级范围
    minLevel: number | null,
    maxLevel: number | null,

    pageNum: number | null,
    pageSize: number | null,
}

// 编辑下属用户表单
export interface EditSubordinateRequest {
    subordinateId: number | null,
    level: number | null,
    leaderName: string | null,
}

// 登录
export function login(form:any) {
    console.log(form.name)
    return request.post("/user/login", form)
}

// 人脸登录
export function faceRecognize(faceImg:any){
    return request.post('/user/face', faceImg)
}

// 注册人脸
export function faceReg(userID:any, faceImg:any){
    return request.post('/user/face/reg', faceImg, {
        params:{
            id: userID,
        }
    })
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
export function updateUser(userProfile: any){
    return request.post('/user/update',userProfile)
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
export function deleteUser(id:number,isActive:number){
    return request.delete(`/user/${id}` )
}

// 启停用户
export function userIsActive(id: number, isActive: number){
    return request.post(`/user/isActive/${id}`, {},{
        params:{
            isActive: isActive,
        }
    })
}

// 用户登出
export function logout(){
        return request.post('/user/logout')
}

// 条件查询
export function findUser(user: UserParamsRequest){
    if(user.maxLevel === null) user.maxLevel = 5;
    if(user.minLevel === null) user.minLevel = 1;
    return request.post('/user/conditions', user)
}

// 名称查询
export const findByName=async (name:string)=>{
    return request.get('/user/findByName', {
        params:{
            name: name,
        }
    })
}



// 更新密码
export const updatePassword=async(body:any)=>{
    return request.post('/user/updatePassword', body)
}

// id查询
export function findUserById(id:number){
    return request.get(`/user/${id}`)
}
export const findByFacilityId  =async (id:number,pageNum: number, pageSize: number)=>{
    return request.get(`/user/findByFacilityId`,{
        params:{
            facilityId: id,
            pageNum: pageNum,
            pageSize: pageSize,
        }
    })
}

// 编辑下属用户
export function editSubordinate(id: number,data: EditSubordinateRequest){
    request.post(`/user/editSubordinate/${id}`, data).then(r => {return r})
}