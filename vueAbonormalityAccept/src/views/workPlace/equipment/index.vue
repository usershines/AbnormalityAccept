<template>
  <div class="content-container">
    <!-- 搜索表单区域 -->
    <el-form
        :model="searchForm"
        inline
        class="search-form"
        style="margin-bottom: 16px; display: flex; flex-wrap: nowrap; overflow-x: auto;"
    >
      <el-form-item label="装备名称" class="search-item" style="margin-right: 10px; flex-shrink: 0;">
        <el-input
            v-model="searchForm.name"
            placeholder="请输入装备名称"
            clearable
            class="search-input"
            style="width: 125px;"
        ></el-input>
      </el-form-item>
      <el-form-item label="装备类型" class="search-item" style="margin-right: 10px; flex-shrink: 0;">
        <el-select
            v-model="searchForm.type"
            placeholder="请选择"
            clearable
            class="search-select"
            style="width: 90px;"
        >
          <el-option
              v-for="option in typeOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="装备状态" class="search-item" style="margin-right: 10px; flex-shrink: 0;">
        <el-select
            v-model="searchForm.state"
            placeholder="请选择"
            clearable
            class="search-select"
            style="width: 90px;"
        >
          <el-option
              v-for="option in stateOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="ID" class="search-item" style="margin-right: 10px; flex-shrink: 0;">
        <el-input
            v-model="searchForm.id"
            placeholder="请输入装备ID"
            clearable
            class="search-input"
            style="width: 110px;"
        ></el-input>
      </el-form-item>
      <!-- 持有者ID搜索框 -->
      <el-form-item label="持有者ID" class="search-item" style="margin-right: 10px; flex-shrink: 0;">
        <el-input
            v-model="searchForm.masterId"
            placeholder="请输入持有者ID"
            clearable
            class="search-input"
            style="width: 125px;"
        ></el-input>
      </el-form-item>
      <el-form-item style="margin-left: auto; flex-shrink: 0;">
        <el-button
            @click="handleSearch"
            class="reset-button"
        >
          <i class="iconfont icon-reset"></i> 搜索
        </el-button>
        <el-button
            @click="handleReset"
            class="reset-button"
        >
          <i class="iconfont icon-reset"></i> 重置
        </el-button>
        <el-button
            type="success"
            @click="handleCreate"
            class="create-button"
        >
          <i class="iconfont icon-add"></i> 新增装备
        </el-button>
      </el-form-item>
    </el-form>

    <!-- 数据表格区域 -->
    <el-table
        :data="eqList"
        border
        style="width: 100%"
        class="containment-table"
        :header-cell-style="tableHeaderStyle"
        :row-style="tableRowStyle"
    >
      <el-table-column prop="id" label="ID" width="100">
        <template #header>
          <span><i class="iconfont icon-id"></i> ID</span>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="名称" width="180">
        <template #header>
          <span><i class="iconfont icon-equipment"></i> 名称</span>
        </template>
        <template #default="scope">
          <span class="equipment-name">{{ scope.row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="type" label="类型" width="150">
        <template #header>
          <span><i class="iconfont icon-type"></i> 类型</span>
        </template>
        <template #default="scope">
          <el-tag :type="getTypeTagType(scope.row.type)" class="type-tag">
            {{ getTypeName(scope.row.type) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="state" label="状态" width="150">
        <template #header>
          <span><i class="iconfont icon-status"></i> 状态</span>
        </template>
        <template #default="scope">
          <el-tag :type="getStateTagType(scope.row.state)" class="state-tag">
            {{ getStateName(scope.row.state) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="masterId" label="持有者ID" width="150">
        <template #header>
          <span><i class="iconfont icon-master"></i> 持有者ID</span>
        </template>
      </el-table-column>
      <el-table-column prop="applicationRequirement" label="使用要求" min-width="200">
        <template #header>
          <span><i class="iconfont icon-requirement"></i> 使用要求</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" >
        <template #header>
          <span><i class="iconfont icon-operation"></i> 操作</span>
        </template>
        <template #default="scope">
          <el-button
              type="text"
              @click="handleDetail(scope.row)"
              class="detail-btn"
          >
            <i class="iconfont icon-detail"></i> 详情
          </el-button>
          <el-button
              type="text"
              @click="handleEdit(scope.row)"
              class="edit-btn"
          >
            <i class="iconfont icon-edit"></i> 编辑
          </el-button>
          <el-button
              type="text"
              class="delete-btn"
              @click="handleDelete(scope.row)"
          >
            <i class="iconfont icon-delete"></i> 删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 30]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        prev-text="上一页"
        next-text="下一页"
        class="containment-pagination"
    >
    </el-pagination>
  </div>

  <!-- 新增装备弹窗 -->
  <el-dialog
      v-model="createDialogVisible"
      title="新增装备"
      width="40%"
      class="containment-dialog"
  >
    <el-form
        :model="createForm"
        ref="createFormRef"
        label-width="120px"
        label-position="left"
        :rules="createRules"
    >
      <el-form-item label="名称" prop="name">
        <el-input v-model="createForm.name"></el-input>
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="createForm.type" placeholder="请选择装备类型">
          <el-option
              v-for="option in typeOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="state">
        <el-select v-model="createForm.state" placeholder="请选择装备状态">
          <el-option
              v-for="option in stateOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="使用要求" prop="applicationRequirement">
        <el-input
            v-model="createForm.applicationRequirement"
            type="textarea"
            :rows="2"
        ></el-input>
      </el-form-item>
      <el-form-item label="负责人ID" prop="masterId">
        <el-input v-model="createForm.masterId"></el-input>
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input
            v-model="createForm.description"
            type="textarea"
            :rows="3"
        ></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="createDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitCreate">确定</el-button>
    </template>
  </el-dialog>

  <!-- 编辑装备弹窗 -->
  <el-dialog
      v-model="editDialogVisible"
      title="编辑装备"
      width="40%"
      class="containment-dialog"
  >
    <el-form
        :model="editForm"
        ref="editFormRef"
        label-width="120px"
        label-position="left"
        :rules="createRules"
    >
      <el-form-item label="名称" prop="name">
        <el-input v-model="editForm.name"></el-input>
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="editForm.type" placeholder="请选择装备类型">
          <el-option
              v-for="option in typeOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="state">
        <el-select v-model="editForm.state" placeholder="请选择装备状态">
          <el-option
              v-for="option in stateOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="使用要求" prop="applicationRequirement">
        <el-input
            v-model="editForm.applicationRequirement"
            type="textarea"
            :rows="2"
        ></el-input>
      </el-form-item>
      <el-form-item label="负责人ID" prop="masterId">
        <el-input v-model="editForm.masterId"></el-input>
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input
            v-model="editForm.description"
            type="textarea"
            :rows="3"
        ></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="editDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitEdit">确定</el-button>
    </template>
  </el-dialog>

  <!-- 装备详情弹窗 -->
  <el-dialog
      v-model="detailDialogVisible"
      :title="`${selectedEquipment?.name} - 装备档案`"
      width="50%"
      class="containment-dialog"
  >
    <div v-if="selectedEquipment" class="equipment-detail">
      <div class="security-stamp">
        <div class="stamp-content">
          <div class="stamp-title">装备档案</div>
          <div class="stamp-level">ID: {{ selectedEquipment.id }}</div>
        </div>
      </div>
      <div class="detail-header">
        <div class="header-info">
          <h2 class="equipment-name">{{ selectedEquipment.name }}</h2>
          <div class="info-row">
            <span class="info-item"><i class="iconfont icon-id"></i> ID：<strong>{{ selectedEquipment.id }}</strong></span>
            <span class="info-item"><i class="iconfont icon-type"></i> 类型：<strong>{{ getTypeName(selectedEquipment.type) }}</strong></span>
          </div>
          <div class="info-row">
            <span class="info-item"><i class="iconfont icon-status"></i> 状态：<strong>{{ getStateName(selectedEquipment.state) }}</strong></span>
            <span class="info-item"><i class="iconfont icon-master"></i> 负责人ID：<strong>{{ selectedEquipment.masterId }}</strong></span>
          </div>
          <div class="info-row description">
            <span><i class="iconfont icon-requirement"></i> 使用要求：<span class="description-text">{{ selectedEquipment.applicationRequirement }}</span></span>
          </div>
          <div class="info-row description">
            <span><i class="iconfont icon-description"></i> 描述：<span class="description-text">{{ selectedEquipment.description }}</span></span>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <el-button @click="detailDialogVisible = false" class="dialog-button">
        <i class="iconfont icon-close"></i> 关闭
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus';
import * as equipmentApi from '@/api/equipment';
import  type { Equipment , EquipmentParam} from '@/api/equipment';

// 装备类型选项 - 前端显示文字，值存数字（与后端保持一致）
const typeOptions = [
  { label: '收容装备', value: 1 },
  { label: '防护装备', value: 2 },
  { label: '武器装备', value: 3 },
  { label: '特殊装备', value: 4 },
  { label: '探测装备', value: 5 },
  { label: '消耗品', value: 6 },
  { label: '维修装备', value: 7 },
  { label: '医疗装备', value: 8 }
];

// 装备状态选项 - 前端显示文字，值存数字（与后端保持一致）
const stateOptions = [
  { label: '启用', value: 1 },
  { label: '备用/测试', value: 2 }
];

// 类型映射表 - 用于将后端返回的数字转换为前端显示的文字
const typeMap = typeOptions.reduce((map, option) => {
  map[option.value] = option.label;
  return map;
}, {} as Record<number, string>);

// 状态映射表 - 用于将后端返回的数字转换为前端显示的文字
const stateMap = stateOptions.reduce((map, option) => {
  map[option.value] = option.label;
  return map;
}, {} as Record<number, string>);

// 新增装备弹窗相关
const createDialogVisible = ref(false);
const createFormRef = ref<FormInstance>();
const createForm = ref<any>({
  name: '',
  type: null,  // 存储数字，对应后端数据库
  state: null, // 存储数字，对应后端数据库
  applicationRequirement: '',
  masterId: 0,
  description: ''
});

// 编辑装备弹窗相关
const editDialogVisible = ref(false);
const editFormRef = ref<FormInstance>();
const editForm = ref<any>({
  id: '',
  name: '',
  type: null,  // 存储数字，对应后端数据库
  state: null, // 存储数字，对应后端数据库
  applicationRequirement: '',
  masterId: '',
  description: ''
});

const eqList = ref<Equipment[]>([]);

// 新增装备表单验证规则
const createRules = reactive<FormRules>({
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  state: [{ required: true, message: '请选择状态', trigger: 'change' }],
  applicationRequirement: [{ required: true, message: '请输入使用要求', trigger: 'blur' }],
  masterId: [{ required: true, message: '请输入负责人ID', trigger: 'blur' }]
});

// 初始化数据
const initData = async() => {
  equipmentApi.getEquipmentList(currentPage.value, pageSize.value).then(res => {
    console.log(res)
    if(res.code === 200) {
      eqList.value = res.data.list;
      total.value = res.data.total;
      ElMessage.success('装备数据获取成功')
    }
  }).catch(err => {
    console.log(err)
    ElMessage.error('装备数据获取失败' + err.msg)
  })
}

onMounted(() => {
  initData();
})

// 打开新增装备弹窗
const handleCreate = () => {
  createDialogVisible.value = true;
  // 重置表单
  createFormRef.value?.resetFields();
};

// 提交新增装备表单
const submitCreate = async() => {
  if (!createFormRef.value) return;
  createFormRef.value.validate(async(valid) => {
    if (valid) {
      // 直接提交数字类型到后端，与数据库保持一致
      const newEquipment = { ...createForm.value };
      const result = await equipmentApi.addEquipment(newEquipment);
      if (result.code === 200) {
        ElMessage.success('新增装备成功');
        createDialogVisible.value = false;
        initData(); // 重新加载数据
      } else {
        ElMessage.error(`新增失败：${result.msg}`);
      }
    } else {
      ElMessage.warning('请填写完整信息');
    }
  });
};

// 搜索表单数据
const searchForm = ref<EquipmentParam>({
  id: null,
  type: null,  // 存储数字，用于后端搜索
  name: null,
  state: null, // 存储数字，用于后端搜索
  applicationRequirement: null,
  masterId: null,
  description: null,
  stateList: null,
  typeList: null,
  pageNum: null,
  pageSize: null,
});

// 分页相关数据
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 详情弹窗相关
const detailDialogVisible = ref(false);
const selectedEquipment = ref<any>(null);

// 搜索过滤
const filteredData = computed(() => {
  const filters = {
    name: (item: Equipment) => !searchForm.value.name || item.name.includes(searchForm.value.name),
    type: (item: Equipment) => !searchForm.value.type || item.type === searchForm.value.type,
    state: (item: Equipment) => !searchForm.value.state || item.state === searchForm.value.state,
    id: (item: Equipment) => !searchForm.value.id || item.id.toString().includes(searchForm.value.id),
    masterId: (item: Equipment) => !searchForm.value.masterId || item.masterId.toString().includes(searchForm.value.masterId),
  };

  return eqList.value.filter(item => Object.values(filters).every(filter => filter(item)));
});

// 计算当前页数据
const currentTableData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return filteredData.value.slice(start, start + pageSize.value);
});

// 根据后端存储的数字获取类型名称
const getTypeName = (type: number) => {
  return typeMap[type] || '未知类型';
};

// 根据后端存储的数字获取状态名称
const getStateName = (state: number) => {
  return stateMap[state] || '未知状态';
};

// 类型标签样式映射
const getTypeTagType = (type: number) => {
  const typeStyleMap: Record<number, string> = {
    1: 'primary',
    2: 'success',
    3: 'warning',
    4: 'danger',
    5: 'info',
    6: 'default',
    7: 'info',
    8: 'success'
  };
  return typeStyleMap[type] || 'info';
};

// 状态标签样式映射
const getStateTagType = (state: number) => {
  const stateStyleMap: Record<number, string> = {
    1: 'success',
    2: 'warning'
  };
  return stateStyleMap[state] || 'danger';
};

// 表格样式控制
const tableHeaderStyle = () => ({
  background: 'linear-gradient(to bottom, #1a2a4a, #0c1a33)',
  color: '#c0d1f2',
  fontWeight: 'bold',
  borderBottom: '1px solid #304878'
});

const tableRowStyle = ({ rowIndex }: { rowIndex: number }) => ({
  background: rowIndex % 2 === 0 ? 'rgba(10, 20, 41, 0.3)' : 'rgba(15, 30, 61, 0.3)',
  color: '#e0f0ff'
});

// 搜索与重置方法
const handleSearch = () => {
  searchForm.value.pageNum = currentPage.value;
  searchForm.value.pageSize = pageSize.value;
  // 打印搜索参数，方便调试
  console.log('搜索参数:', searchForm.value);

  equipmentApi.findEquipmentByConditions(searchForm.value).then(res => {
    console.log('搜索结果:', res);
    if(res.code === 200) {
      eqList.value = res.data.list;
      total.value = res.data.total;
      ElMessage.success('搜索成功')
    } else {
      ElMessage.error(`搜索失败：${res.msg}`)
    }
  }).catch(err => {
    console.log('搜索错误:', err)
    ElMessage.error(`搜索失败${err.msg}`)
  })
};

const handleReset = () => {
  searchForm.value = {
    id: null,
    type: null,
    name: null,
    state: null,
    applicationRequirement: null,
    masterId: null,
    description: null,
    stateList: null,
    typeList: null,
    pageNum: null,
    pageSize: null,
  }
  currentPage.value = 1;
  initData();
};

// 分页方法
const handleSizeChange = (val: number) => {
  pageSize.value = val;
  currentPage.value = 1;
  initData();
};

const handleCurrentChange = (val: number) => {
  currentPage.value = val;
  initData();
};

// 详情、编辑、删除方法
const handleDetail = (row: any) => {
  selectedEquipment.value = row;
  detailDialogVisible.value = true;
};

const handleEdit = (row: any) => {
  // 直接使用后端返回的数字类型
  editForm.value = { ...row };
  editDialogVisible.value = true;
};

const handleDelete = (row: any) => {
  ElMessageBox.confirm(
      `确定要删除装备: ${row.name} 吗?`,
      '删除确认',
      {
        confirmButtonText: '确认删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
  ).then(async() => {
    const result = await equipmentApi.deleteEquipment(row.id);
    if (result.code === 200) {
      ElMessage.warning(`已删除装备: ${row.name}`);
      initData();
    } else {
      ElMessage.error(`删除失败：${result.msg}`)
    }
  }).catch(() => {
    ElMessage.info('已取消删除');
  });
};

// 提交编辑表单
const submitEdit = async() => {
  if (!editFormRef.value) return;
  editFormRef.value.validate(async(valid) => {
    if (valid) {
      // 直接提交数字类型到后端
      const result = await equipmentApi.updateEquipment(editForm.value);
      if (result.code === 200) {
        ElMessage.success('编辑装备成功');
        editDialogVisible.value = false;
        initData();
      } else {
        ElMessage.error(`编辑失败：${result.msg}`);
      }
    } else {
      ElMessage.warning('请填写完整信息');
    }
  });
};
</script>

<style scoped>
/* 保持原有样式不变 */
.search-input, .search-select {
  background-color: #ffffff;
  border: 1px solid #ccc;
  color: #333;
  border-radius: 4px;
  width: 100px;
}

.create-button {
  background: linear-gradient(to right, #4aaf7d, #3a8c5f);
  border: none;
  color: #fff;
  transition: all 0.3s ease;
  width: 100px;
}

.containment-table {
  background: #ffffff;
  border: 1px solid #ccc;
  border-radius: 8px;
  overflow: hidden;
}

.containment-table :deep(th) {
  background: #ffffff;
  color: #222;
  border-bottom: 1px solid #ccc;
}

.containment-table :deep(td) {
  background: #ffffff;
  color: #222;
  border-bottom: 1px solid #ccc;
}

.containment-pagination {
  margin-top: 16px;
  background: #ffffff;
  padding: 10px;
  border-radius: 8px;
  border: 1px solid #ccc;
  display: flex;
  justify-content: center;
}

.containment-dialog {
  background: #ffffff;
  border: 1px solid #ccc;
  border-radius: 8px;
}

.equipment-detail {
  background: #ffffff;
  padding: 25px;
  border-radius: 6px;
  border: 1px solid #ccc;
  position: relative;
}

.security-stamp {
  position: absolute;
  top: -15px;
  right: -15px;
  width: 120px;
  height: 120px;
  border: 4px solid #e63946;
  border-radius: 50%;
  background: rgba(230, 57, 70, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  transform: rotate(15deg);
}

.stamp-content {
  text-align: center;
  transform: rotate(-15deg);
  color: #e63946;
}

.detail-header .equipment-name {
  color: #333;
  font-size: 24px;
  border-bottom: 2px solid #ccc;
  padding-bottom: 10px;
  margin-bottom: 15px;
}

.info-row {
  display: flex;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.info-item {
  margin-right: 30px;
  margin-bottom: 8px;
  min-width: 45%;
  display: flex;
  align-items: center;
  color: #333;
}

.info-item i {
  margin-right: 8px;
  color: #666;
}

.description {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px dashed #ccc;
  width: 100%;
}

.description-text {
  color: #333;
  line-height: 1.6;
}

.dialog-button {
  background: linear-gradient(to right, #4a6fb3, #3a5a9c);
  border: none;
  color: white;
}

.delete-btn {
  color: red;
}

.content-container {
  background-color: #ffffff;
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
</style>
