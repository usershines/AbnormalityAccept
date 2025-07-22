import request from "@/utils/requests.ts";

interface Email{
    id: number,
    state: number,
    senderId: number,
    senderName: string,
    senderLevel: number,
    receiverId: number,
    receiverName: string,
    receiverLevel: number,
    theme:  string,
    content: string,
    sendTime: Date,
}


export function findAllEmail(pageNumber: number, pageSize: number){
    return request.get('/email/list',{
        params:{
            pageNum: pageNumber,
            pageSize: pageSize,
        }
    })
}

export function findEmailById(emailId: number){
    return request.get(`/email/${emailId}`);
}

export function sendEmail(email:Email){
    return request.post('/email/send',email);
}

export function deleteEmail(emailId:number){
    return request.delete(`/email/${emailId}`);
}

export function findEmailBySender(senderName:string , pageNumber:number, pageSize:number){
    return request.get(`/email/sender/`,{
        params:{
            senderName: senderName,
            pageNum: pageNumber,
            pageSize: pageSize,
        }
    });
}



export function updateEmailState(emailId: number,state:number){
    return request.put(`/email/${emailId}/state?state=${state}`, )
}

//查询自己已发送的历史邮件
export function findEmailOneself(pageNumber:number, pageSize:number){
    return request.get(`/email/history`,{
        params:{
            pageNum: pageNumber,
            pageSize: pageSize,
        }
    })
}

//统计未读邮件数量
export function countUnreadEmail(){
    return request.get(`/email/countUnRead`)
}
//一键已读邮件
export function readAllEmail(){
    return request.get('/email/readAll')
}

//新增删除邮件
export function deleteEmailById(id:number){
    return request.delete(`/email/${id}`,)
}

//新增按照发送者等级查询邮件
export function findEmailBySenderLevel(level:number, pageNumber:number, pageSize:number){
    return request.get('/email/senderLevel',{
        params:{
            level: level,
            pageNum: pageNumber,
            pageSize: pageSize,
        }
    })
}

//按照已读未读查询邮件
export function findEmailByState(state:number, pageNumber:number, pageSize:number){
    return request.get(`/email/state`,{
        params:{
            state: state,
            pageNum: pageNumber,
            pageSize: pageSize,
        }
    })
}