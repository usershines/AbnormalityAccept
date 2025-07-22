import request from "@/utils/requests.ts";
import type {UserParamsRequest} from "@/api/user.ts";
// 分页查询所有设施

export interface FacilityParams{
    id: number | null;
    facilityName: string | null;
    facilityAddress: string | null;
    level: number | null;
    managerId: number | null;
    managerName: string | null;

    //范围查询参数
    minLevel: number | null;
    maxLevel: number | null;

    pageNum: number | null;
    pageSize: number | null;
}








export function getFacilityList(pageNum: number,pageSize: number){
    return request.get('/facility/list',{
        params:{
            pageNum: pageNum,
            pageSize: pageSize,
        }
    })
}
// 添加设施
export function  addFacility(facility: any){
    return request.post('/facility/new',facility)
}
//更新设施
export function updateFacility(facility: any) {
    return request.put('/facility/update', facility);
}
//条件查询设施
export function findFacility(facility: FacilityParams){
    if(facility.maxLevel === null) facility.maxLevel = 5;
    if(facility.minLevel === null) facility.minLevel = 1;
    return request.post('/facility/conditions', facility)
}
