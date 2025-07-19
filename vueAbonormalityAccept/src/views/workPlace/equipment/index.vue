<template>
  <!-- 搜索表单区域 - 保持暗色主题一致性 -->
  <el-form
    :model="searchForm"
    inline
    class="search-form"
    style="margin-bottom: 16px;"
  >
    <el-form-item label="装备名称" class="search-item">
      <el-input
        v-model="searchForm.name"
        placeholder="请输入装备名称"
        clearable
        class="search-input"
        style="width: 125px;"
      ></el-input>
    </el-form-item>
    <el-form-item label="装备类型" class="search-item">
      <el-select
        v-model="searchForm.type"
        placeholder="请选择"
        clearable
        class="search-select"
        style="width: 100px;"
      >
        <el-option label="类型1" value="1"></el-option>
        <el-option label="类型2" value="2"></el-option>
        <el-option label="类型3" value="3"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="装备状态" class="search-item">
      <el-select
        v-model="searchForm.state"
        placeholder="请选择"
        clearable
        class="search-select"
        style="width: 120px;"
      >
        <el-option label="状态1" value="1"></el-option>
        <el-option label="状态2" value="2"></el-option>
        <el-option label="状态3" value="3"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="ID" class="search-item">
      <el-input
        v-model="searchForm.id"
        placeholder="请输入装备ID"
        clearable
        class="search-input"
        style="width: 120px;"
      ></el-input>
    </el-form-item>
    <!-- 新增持有者ID搜索框 -->
    <el-form-item label="持有者ID" class="search-item">
      <el-input
        v-model="searchForm.masterId"
        placeholder="请输入持有者ID"
        clearable
        class="search-input"
        style="width: 125px;"
      ></el-input>
    </el-form-item>
    <el-form-item>
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

  <!-- 数据表格区域 - 严格展示指定属性 -->
  <el-table
    :data="currentTableData"
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
          {{ scope.row.type }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="state" label="状态" width="150">
      <template #header>
        <span><i class="iconfont icon-status"></i> 状态</span>
      </template>
      <template #default="scope">
        <el-tag :type="getStateTagType(scope.row.state)" class="state-tag">
          {{ scope.row.state }}
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

  <!-- 分页组件 - 保持收容单元风格 -->
  <el-pagination
    @size-change="handleSizeChange"
    @current-change="handleCurrentChange"
    :current-page="currentPage"
    :page-sizes="[10, 20, 30]"
    :page-size="pageSize"
    layout="total, sizes, prev, pager, next, jumper"
    :total="filteredData.length"
    prev-text="上一页"
    next-text="下一页"
    class="containment-pagination"
  >
  </el-pagination>

  <!-- 新增装备弹窗 - 严格包含指定属性 -->
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
        <el-input v-model="createForm.type"></el-input>
      </el-form-item>
      <el-form-item label="状态" prop="state">
        <el-input v-model="createForm.state"></el-input>
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
        <el-input v-model="editForm.type"></el-input>
      </el-form-item>
      <el-form-item label="状态" prop="state">
        <el-input v-model="editForm.state"></el-input>
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

  <!-- 装备详情弹窗 - 仅展示指定属性 -->
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
            <span class="info-item"><i class="iconfont icon-type"></i> 类型：<strong>{{ selectedEquipment.type }}</strong></span>
          </div>
          <div class="info-row">
            <span class="info-item"><i class="iconfont icon-status"></i> 状态：<strong>{{ selectedEquipment.state }}</strong></span>
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
import { type Equipment } from '@/api/equipment';


// 新增装备弹窗相关（严格包含指定属性）
const createDialogVisible = ref(false);
const createFormRef = ref<FormInstance>();
const createForm = ref<any>({
  name: '',
  type: 0,
  state: 0,
  applicationRequirement: '',
  masterId: 0,
  description: ''
});

// 编辑装备弹窗相关
const editDialogVisible = ref(false);
const editFormRef = ref<FormInstance>();
const editForm = ref<any>({

  name: '',
  type: '',
  state: '',
  applicationRequirement: '',
  masterId: '',
  description: ''
});

const eqList=ref<Equipment[]>([]);



// 新增装备表单验证规则（仅验证指定属性）
const createRules = reactive<FormRules>({
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  type: [{ required: true, message: '请输入类型', trigger: 'blur' }],
  state: [{ required: true, message: '请输入状态', trigger: 'blur' }],
  applicationRequirement: [{ required: true, message: '请输入使用要求', trigger: 'blur' }],
  masterId: [{ required: true, message: '请输入负责人ID', trigger: 'blur' }]
});

const initData=async()=>{
  const res=await equipmentApi.getEquipmentList(1,10);
  eqList.value=res.data.list;
  console.log(res.data.list);

}

onMounted(() => {
  initData();
}
)

// 打开新增装备弹窗
const handleCreate = () => {
  createDialogVisible.value = true;
};

// 提交新增装备表单（严格处理指定属性）
const submitCreate = async() => {
  if (!createFormRef.value) return;
  createFormRef.value.validate(async(valid) => {
    if (valid) {
      const newId = Math.max(...eqList.value.map(e => e.id), 0) + 1;
      const newEquipment = { id: newId, ...createForm.value };
      const data=(await equipmentApi.addEquipment(newEquipment)).data
      ElMessage.success('新增装备成功');
      createDialogVisible.value = false;
      createFormRef.value?.resetFields();
    } else {
      ElMessage.warning('请填写完整信息');
    }
  });
};

// 搜索表单数据（仅包含指定属性相关字段）
const searchForm = reactive({
  name: '',
  type: '',
  state: '',
  id: '',
  masterId: '' // 新增持有者ID搜索字段
});

// 原始表格数据（严格符合属性定义）
const originTableData = ref([
  {
    id: 1,
    name: '装备A',
    type: 'T1',
    state: '状态1',
    applicationRequirement: '需授权使用',
    masterId: 101,
    description: '基础作战装备'
  },
  {
    id: 2,
    name: '装备B',
    type: 'T2',
    state: '维修中',
    applicationRequirement: '仅限专业人员操作',
    masterId: 102,
    description: '精密探测装备'
  },
  {
    id: 3,
    name: '装备C',
    type: 'T3',
    state: '封存',
    applicationRequirement: '禁止私自启用',
    masterId: 103,
    description: '备用应急装备'
  }
]);

// 分页相关数据
const currentPage = ref(1);
const pageSize = ref(10);

// 详情弹窗相关
const detailDialogVisible = ref(false);
const selectedEquipment = ref<any>(null);

// 搜索过滤（仅基于指定属性）
const filteredData = computed(() => {
  const filters = {
  name: (item: Equipment) => !searchForm.name || item.name.includes(searchForm.name),
  type: (item: Equipment) => !searchForm.type || String(item.type).includes(searchForm.type),
  state: (item: Equipment) => !searchForm.state || String(item.state).includes(searchForm.state),
  id: (item: Equipment) => !searchForm.id || item.id.toString().includes(searchForm.id),
  masterId: (item: Equipment) => !searchForm.masterId || item.masterId.toString().includes(searchForm.masterId),
};

  return eqList.value.filter(item => Object.values(filters).every(filter => filter(item)));
});

// 计算当前页数据
const currentTableData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return filteredData.value.slice(start, start + pageSize.value);
});

// 类型标签样式映射
const getTypeTagType = (type: number) => {
  const typeMap: Record<number, string> = { 1: 'primary', 2: 'success', 3: 'warning' };
  return typeMap[type] || 'info';
};

// 状态标签样式映射
const getStateTagType = (state: string) => {
  const stateMap: Record<string, string> = { '正常': 'success', '维修中': 'warning', '封存': 'info' };
  return stateMap[state] || 'danger';
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
const handleSearch = () => { currentPage.value = 1; };
const handleReset = () => { Object.keys(searchForm).forEach(key => searchForm[key as keyof typeof searchForm] = ''); currentPage.value = 1; };

// 分页方法
const handleSizeChange = (val: number) => { pageSize.value = val; currentPage.value = 1; };
const handleCurrentChange = (val: number) => { currentPage.value = val; };

// 详情、编辑、删除方法
const handleDetail = (row: any) => { selectedEquipment.value = row; detailDialogVisible.value = true; };
const handleEdit = (row: any) => {
  editForm.value.id = row.id;
  editForm.value.name = row.name;
  editForm.value.type = row.type;
  editForm.value.state = row.state;
  editForm.value.applicationRequirement = row.applicationRequirement;
  editForm.value.masterId = row.masterId;
  editForm.value.description = row.description;
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
    const index = eqList.value.findIndex(item => item.id === row.id);
    if (index !== -1) {
      const result =(await equipmentApi.deleteEquipment(row.id)).data;
      if (result.code===200) {
        ElMessage.warning(`已删除装备: ${row.name}`);
        // if (currentTableData.value.length === 0 && currentPage.value > 1) currentPage.value--;
        initData();
      }
      
      
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
      const index = eqList.value.findIndex(item => item.id === editForm.value.id);
      if (index !== -1) {
        // eqList.value[index] = { ...editForm };
        const res=(await equipmentApi.updateEquipment(editForm.value)).data
        initData()
        ElMessage.success('编辑装备成功');
        editDialogVisible.value = false;
      }
    } else {
      ElMessage.warning('请填写完整信息');
    }
  });
};
</script>

<style scoped>
/* 复用UserList.vue的样式体系，保持风格一致 */
/* 进一步减小搜索框宽度 */
.search-input, .search-select {
  background-color: #ffffff; /* 白色背景 */
  border: 1px solid #ccc; /* 加深边框颜色 */
  color: #333; /* 加深字体颜色 */
  border-radius: 4px;
  width: 100px; /* 进一步减小宽度 */
}

/* 减小按钮宽度 */

.create-button {
  background: linear-gradient(to right, #4aaf7d, #3a8c5f);
  border: none;
  color: #fff;
  transition: all 0.3s ease;
  width: 100px; /* 减小宽度 */
}

.containment-table {
  background: #ffffff; /* 白色背景 */
  border: 1px solid #ccc; /* 加深边框颜色 */
  border-radius: 8px;
  overflow: hidden;
}

.containment-table :deep(th) {
  background: #ffffff; /* 白色背景 */
  color: #222; /* 进一步加深表头字体颜色 */
  border-bottom: 1px solid #ccc; /* 加深边框颜色 */
}

.containment-table :deep(td) {
  background: #ffffff;
  color: #222; /* 进一步加深表格内容字体颜色 */
  border-bottom: 1px solid #ccc; /* 加深边框颜色 */
}

.containment-pagination {
  margin-top: 16px;
  background: #ffffff; /* 白色背景 */
  padding: 10px;
  border-radius: 8px;
  border: 1px solid #ccc; /* 加深边框颜色 */
  display: flex;
  justify-content: center;
}

.containment-dialog {
  background: #ffffff; /* 白色背景 */
  border: 1px solid #ccc; /* 加深边框颜色 */
  border-radius: 8px;
}

.equipment-detail {
  background: #ffffff; /* 白色背景 */
  padding: 25px;
  border-radius: 6px;
  border: 1px solid #ccc; /* 加深边框颜色 */
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
  color: #333; /* 加深字体颜色 */
  font-size: 24px;
  border-bottom: 2px solid #ccc; /* 加深边框颜色 */
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
  color: #333; /* 加深字体颜色 */
}

.info-item i {
  margin-right: 8px;
  color: #666; /* 加深图标颜色 */
}

.description {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px dashed #ccc; /* 加深边框颜色 */
  width: 100%;
}

.description-text {
  color: #333; /* 加深字体颜色 */
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
</style>