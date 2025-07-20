import request from "@/utils/requests.ts";

export interface Abnormality {
    id: number | null;
    name: string | null;
    level: number | null;
    description: string | null;
    manageMethod: string | null;
    notes: string | null;
    facilityId: number | null;
    imageName: string | null;
}

export interface AbnormalityParam{
    id: number | null;
    name: string | null;
    level: number | null;
    description: string | null;
    manageMethod: string | null;
    notes: string | null;
    facilityId: number | null;

    //等级范围查询
    minLevel: number | null;
    maxLevel: number | null;

    pageNum: number | null;
    pageSize: number | null;

}

// 添加异想体
export function addAbnormality(abnormality: any) {
    return request.post('/abnormality/add', abnormality);
}

// 分页获取异想体数据
export function getAbnormalityList(pageNum: number, pageSize: number) {
    return request.get('/abnormality/list',{
        params:{pageNum:pageNum, pageSize:pageSize}
    })
}

// id查询异想体
export function getAbnormalityById(id: number) {
    return request.get(`/abnormality/${id}`);
}

// 更新异想体信息
export function updateAbnormality(abnormality: any) {
    return request.put('abnormality/update', abnormality);
}

// 删除异想体
export function deleteAbnormality(id: number) {
    return request.delete(`/abnormality/${id}`);
}

// 条件查询异想体
export function findAbnormality(abnormality: AbnormalityParam){
    if(abnormality.minLevel === null) abnormality.minLevel = 1;
    if(abnormality.maxLevel === null) abnormality.maxLevel = 5;
    return request.get('/abnormality/conditions', {
        params:abnormality
    })
}