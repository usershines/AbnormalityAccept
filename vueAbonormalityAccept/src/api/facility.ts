import request from "@/utils/requests.ts";

interface Facility{
    id: number;
    facilityName: string;
    facilityAddress: string;
    level: number;
    managerId: number;
}

// 分页查询所有设施
export function getFacilityList(pageNum: number,pageSize: number){
    return request.get('/facility/list',{
        params:{
            pageNum: pageNum,
            pageSize: pageSize,
        }
    })
}