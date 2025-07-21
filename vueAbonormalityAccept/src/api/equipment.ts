import request from "@/utils/requests.ts";

// 装备类型
export interface Equipment {
  id: number;
  type: number;
  name: string;
  state: number;
  applicationRequirement: string;
  masterId: number;
  description: string;
}
export interface EquipmentParam {
  id: number | null;
  type: number | null;
  name: string | null;
  state: number | null;
  applicationRequirement: string | null;
  masterId: number | null;
  description: string | null;

  // 多值查询参数
  stateList: number[] | null;
  typeList: number[] | null;

  pageNum: number | null;
  pageSize: number | null;
}


// 分页查询所有装备
export function getEquipmentList(pageNum: number, pageSize: number) {
  return request.get('/equipment/list', {
    params: {
      pageNum: pageNum,
      pageSize: pageSize,
    }
  });
}

// 根据ID查询装备
export function getEquipmentById(id: number) {
  return request.get(`/equipment/${id}`, {
    params: {
      id: id,
    }
  });
}

// 删除装备
export function deleteEquipment(id: number) {
  return request.delete(`/equipment/${id}`);
}

// 添加装备
export function addEquipment(equipment: any) {
  return request.post('/equipment/add', equipment);
}

// 更新装备
export function updateEquipment(equipment: any) {
  return request.put('/equipment/update', equipment);
}

// 根据装备状态查询装备
export function getEquipmentByState(state: string) {
  return request.get(`/equipment/state/${state}`);
}

// 批量更新装备状态
export function batchUpdateEquipmentState(ids: number[], state: string) {
  return request.put('/equipment/batch/state', null, {
    params: {
      ids: ids,
      state: state,
    }
  });
}

// 批量删除装备
export function batchDeleteEquipment(ids: number[]) {
  return request.delete('/equipment/batch', {
    params: {
      ids: ids,
    }
  });
}

// 模糊查询（name）
export function searchEquipmentByName(name: string) {
  return request.get('/equipment/search/name', {
    params: {
      name: name,
    }
  });
}

export function findEquipmentByConditions(Params: EquipmentParam){
  return request.get('/equipment/conditions', {
    params: Params,
  })
}