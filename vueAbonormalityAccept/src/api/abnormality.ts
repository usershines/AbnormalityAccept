import request from "@/utils/requests.ts";

interface abnormality {
    id: number;
    name: string;
    level: number;
    description: string;
    manageMethod: string;
    notes: number;
    facilityId: number;
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