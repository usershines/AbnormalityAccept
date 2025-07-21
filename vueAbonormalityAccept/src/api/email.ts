import request from "@/utils/requests.ts";

interface Email{
    id: number,
    state: number,
    senderId: number,
    senderName: string,
    receiverId: number,
    receiverName: string,
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