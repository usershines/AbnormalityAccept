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

// 分页查询所有装备
export function getEquipmentList(pageNum: number, pageSize: number) {
  return request.get('/Equipment/findAllEquipment', {
    params: {
      pageNum: pageNum,
      pageSize: pageSize,
    }
  });
}

// 根据ID查询装备
export function getEquipmentById(id: number) {
  return request.get(`/Equipment/${id}`, {
    params: {
      id: id,
    }
  });
}

// 删除装备
export function deleteEquipment(id: number) {
  return request.delete(`/Equipment/${id}`);
}

// 添加装备
export function addEquipment(equipment: Equipment) {
  return request.post('/Equipment/add', equipment);
}

// 更新装备
export function updateEquipment(equipment: Equipment) {
  return request.put('/Equipment/update', equipment);
}

// 根据装备状态查询装备
export function getEquipmentByState(state: string) {
  return request.get(`/Equipment/state/${state}`);
}

// 批量更新装备状态
export function batchUpdateEquipmentState(ids: number[], state: string) {
  return request.put('/Equipment/batch/state', null, {
    params: {
      ids: ids,
      state: state,
    }
  });
}

// 批量删除装备
export function batchDeleteEquipment(ids: number[]) {
  return request.delete('/Equipment/batch', {
    params: {
      ids: ids,
    }
  });
}

// 模糊查询（name）
export function searchEquipmentByName(name: string) {
  return request.get('/Equipment/search/name', {
    params: {
      name: name,
    }
  });
}