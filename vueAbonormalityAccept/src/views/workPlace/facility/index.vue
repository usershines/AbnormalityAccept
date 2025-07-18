<template>
  <!-- 搜索表单区域 - 暗色主题 -->
  <el-form
    :model="searchForm"
    inline
    class="search-form"
    style="margin-bottom: 16px;"
  >
    <el-form-item label="设施名称" class="search-item">
      <el-input
        v-model="searchForm.facilityName"
        placeholder="请输入设施名称"
        clearable
        class="search-input"
        style="width: 125px;"
      ></el-input>
    </el-form-item>
    <el-form-item label="设施等级" class="search-item">
      <el-select
        v-model="searchForm.level"
        placeholder="请选择设施等级"
        clearable
        class="search-select"
        style="width: 150px;"
      >
        <el-option label="1级" value="1"></el-option>
        <el-option label="2级" value="2"></el-option>
        <el-option label="3级" value="3"></el-option>
        <el-option label="4级" value="4"></el-option>
        <el-option label="5级" value="5"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="所在地" class="search-item">
      <el-input
        v-model="searchForm.facilityAddress"
        placeholder="请输入所在地"
        clearable
        class="search-input"
        style="width: 120px;"
      ></el-input>
    </el-form-item>
    <el-form-item label="负责人ID" class="search-item">
      <el-input
        v-model="searchForm.managerId"
        placeholder="请输入负责人ID"
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
        type="primary"
        @click="handleCreate"
        class="create-button"
      >
        <i class="iconfont icon-plus"></i> 新建设施
      </el-button>
    </el-form-item>
  </el-form>

  <!-- 卡片列表区域 - 收容单元风格 -->
  <div class="facility-cards">
    <el-row :gutter="20">
      <el-col
        v-for="item in currentTableData"
        :key="item.id"
        :xs="24" :sm="12" :md="8" :lg="6"
        class="card-col"
      >
        <el-card
          class="containment-card"
          :body-style="{ padding: '16px' }"
          @click="handleDetail(item)"
        >
          <div class="card-header">
            <div class="avatar-container">
              <div class="security-badge">
                <i class="iconfont icon-lock"></i>
              </div>
              <el-avatar :size="60" :src="item.avatar" class="facility-avatar" />
            </div>
            <div class="card-title">
              <h3 class="facility-name">{{ item.facilityName }}</h3>
              <el-tag
                :type="getLevelTagType(item.level)"
                class="clearance-level"
              >
                <i class="iconfont icon-security"></i> {{ item.level }}级设施
              </el-tag>
            </div>
          </div>
          <div class="card-content">
            <div class="info-item">
              <span class="label"><i class="iconfont icon-id"></i> 编号：</span>
              <span class="value">{{ item.id }}</span>
            </div>
            <div class="info-item">
              <span class="label"><i class="iconfont icon-location"></i> 地址：</span>
              <span class="value address-text">{{ item.facilityAddress }}</span>
            </div>
            <div class="info-item">
              <span class="label"><i class="iconfont icon-manager"></i> 负责人ID：</span>
              <span class="value">{{ item.managerId }}</span>
            </div>
            <div class="info-item">
              <span class="label"><i class="iconfont icon-staff"></i> 人员数量：</span>
              <span class="value">{{ getStaffCount(item.id) }}人</span>
            </div>
            <div class="info-item">
              <span class="label"><i class="iconfont icon-scp"></i> 异想体数量：</span>
              <span class="value">{{ getScpCount(item.id) }}个</span>
            </div>
          </div>
          <div class="card-actions">
            <el-button
              type="text"
              size="small"
              @click.stop="handleStaffList(item.id)"
              class="staff-btn"
            >
              <i class="iconfont icon-staff-list"></i> 工作人员
            </el-button>
            <el-button
              type="text"
              size="small"
              @click.stop="handleScpList(item.id)"
              class="scp-btn"
            >
              <i class="iconfont icon-scp-list"></i> 异想体
            </el-button>
            <el-button
              type="text"
              size="small"
              @click.stop="handleEdit(item)"
              class="edit-btn"
            >
              <i class="iconfont icon-edit"></i> 编辑
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>

  <!-- 分页组件区域 -->
  <el-pagination
    @size-change="handleSizeChange"
    @current-change="handleCurrentChange"
    :current-page="currentPage"
    :page-sizes="[8, 16, 24]"
    :page-size="pageSize"
    layout="total, sizes, prev, pager, next, jumper"
    :total="filteredData.length"
    prev-text="上一页"
    next-text="下一页"
    class="containment-pagination"
  >
  </el-pagination>

  <!-- 设施详情弹窗 -->
  <el-dialog
    v-model="detailDialogVisible"
    :title="`${selectedFacility?.facilityName} - 设施档案`"
    width="70%"
    class="containment-dialog"
  >
    <div v-if="selectedFacility" class="facility-detail">
      <div class="detail-header">
        <div class="security-stamp">
          <div class="stamp-content">
            <div class="stamp-title">SCP FOUNDATION</div>
            <div class="stamp-level">设施等级: {{ selectedFacility.level }}</div>
          </div>
        </div>
        <div class="header-content">
          <el-avatar :size="80" :src="selectedFacility.avatar" class="detail-avatar" />
          <div class="header-info">
            <h2 class="facility-title">{{ selectedFacility.facilityName }}</h2>
            <div class="info-row">
              <span><i class="iconfont icon-id"></i> 设施编号：{{ selectedFacility.id }}</span>
              <span><i class="iconfont icon-security"></i> 安全等级：
                <el-tag :type="getLevelTagType(selectedFacility.level)" class="clearance-tag">
                  {{ selectedFacility.level }}级
                </el-tag>
              </span>
            </div>
            <div class="info-row">
              <span><i class="iconfont icon-location"></i> 详细地址：{{ selectedFacility.facilityAddress }}</span>
              <span><i class="iconfont icon-manager"></i> 负责人ID：{{ selectedFacility.managerId }}</span>
            </div>
            <div class="info-row">
              <span><i class="iconfont icon-staff"></i> 工作人员：{{ getStaffCount(selectedFacility.id) }}人</span>
              <span><i class="iconfont icon-scp"></i> 收容异想体：{{ getScpCount(selectedFacility.id) }}个</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <template #footer>
      <el-button
        @click="detailDialogVisible = false"
        class="dialog-button"
      >
        <i class="iconfont icon-close"></i> 关闭
      </el-button>
    </template>
  </el-dialog>

  <!-- 工作人员列表弹窗 -->
  <el-dialog
    v-model="staffDialogVisible"
    :title="`${currentFacilityName} - 工作人员列表`"
    width="70%"
    class="containment-dialog"
  >
    <el-table
      :data="filteredStaff"
      border
      style="width: 100%"
      class="containment-table"
    >
      <el-table-column prop="id" label="人员ID" width="100"></el-table-column>
      <el-table-column prop="name" label="姓名" width="150"></el-table-column>
      <el-table-column prop="role" label="职位"></el-table-column>
      <el-table-column prop="clearanceLevel" label="权限等级">
        <template #default="scope">
          <el-tag :type="getLevelTagType(scope.row.clearanceLevel)" class="clearance-tag">
            {{ scope.row.clearanceLevel }}级
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.status)" class="status-tag">
            {{ scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>
    <template #footer>
      <el-button
        @click="staffDialogVisible = false"
        class="dialog-button"
      >
        <i class="iconfont icon-close"></i> 关闭
      </el-button>
    </template>
  </el-dialog>

  <!-- 异想体列表弹窗 -->
  <el-dialog
    v-model="scpDialogVisible"
    :title="`${currentFacilityName} - 异想体收容列表`"
    width="70%"
    class="containment-dialog"
  >
    <el-table
      :data="filteredScps"
      border
      style="width: 100%"
      class="containment-table"
    >
      <el-table-column prop="scpId" label="SCP编号" width="150"></el-table-column>
      <el-table-column prop="name" label="名称" width="200"></el-table-column>
      <el-table-column prop="riskLevel" label="危险等级">
        <template #default="scope">
          <el-tag :type="getRiskTagType(scope.row.riskLevel)" class="risk-tag">
            {{ scope.row.riskLevel }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="containmentStatus" label="收容状态"></el-table-column>
      <el-table-column prop="lastCheck" label="上次检查" width="180"></el-table-column>
    </el-table>
    <template #footer>
      <el-button
        @click="scpDialogVisible = false"
        class="dialog-button"
      >
        <i class="iconfont icon-close"></i> 关闭
      </el-button>
    </template>
  </el-dialog>

  <!-- 编辑设施弹窗 -->
  <el-dialog
    v-model="editDialogVisible"
    title="编辑设施信息"
    width="50%"
    class="containment-dialog"
  >
    <el-form
      :model="editForm"
      ref="editFormRef"
      label-width="100px"
    >
      <el-form-item label="设施名称">
        <el-input v-model="editForm.facilityName"></el-input>
      </el-form-item>
      <el-form-item label="设施等级">
        <el-select v-model="editForm.level">
          <el-option label="1级" value="1"></el-option>
          <el-option label="2级" value="2"></el-option>
          <el-option label="3级" value="3"></el-option>
          <el-option label="4级" value="4"></el-option>
          <el-option label="5级" value="5"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="所在地">
        <el-input v-model="editForm.facilityAddress"></el-input>
      </el-form-item>
      <el-form-item label="负责人ID">
        <el-input v-model="editForm.managerId"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="editDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="saveEdit">保存</el-button>
    </template>
  </el-dialog>

  <!-- 新建设施弹窗 -->
  <el-dialog
    v-model="createDialogVisible"
    title="新建设施"
    width="50%"
    class="containment-dialog"
  >
    <el-form
      :model="createForm"
      ref="createFormRef"
      label-width="100px"
    >
      <el-form-item label="设施名称">
        <el-input v-model="createForm.facilityName"></el-input>
      </el-form-item>
      <el-form-item label="设施等级">
        <el-select v-model="createForm.level">
          <el-option label="1级" value="1"></el-option>
          <el-option label="2级" value="2"></el-option>
          <el-option label="3级" value="3"></el-option>
          <el-option label="4级" value="4"></el-option>
          <el-option label="5级" value="5"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="所在地">
        <el-input v-model="createForm.facilityAddress"></el-input>
      </el-form-item>
      <el-form-item label="负责人ID">
        <el-input v-model="createForm.managerId"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="createDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="saveCreate">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';

// 定义核心接口
interface Facility {
  id: number;
  facilityName: string;
  facilityAddress: string;
  level: number;
  managerId: number;
  avatar: string;
}

interface Staff {
  id: number;
  facilityId: number;
  name: string;
  role: string;
  clearanceLevel: number;
  status: string;
}

interface SCP {
  scpId: string;
  facilityId: number;
  name: string;
  riskLevel: string;
  containmentStatus: string;
  lastCheck: string;
}

// 搜索表单数据
const searchForm = reactive({
  facilityName: '',
  level: '',
  facilityAddress: '',
  managerId: ''
});

// 原始数据
const originTableData = ref<Facility[]>([]);
const staffList = ref<Staff[]>([]);
const scpList = ref<SCP[]>([]);

// 分页与弹窗状态
const currentPage = ref(1);
const pageSize = ref(8);
const detailDialogVisible = ref(false);
const staffDialogVisible = ref(false);
const scpDialogVisible = ref(false);
const selectedFacility = ref<Facility | null>(null);
const currentFacilityId = ref(0);
const currentFacilityName = ref('');
const editDialogVisible = ref(false);
const editForm = ref<Facility>({
  id: 0,
  facilityName: '',
  facilityAddress: '',
  level: 0,
  managerId: 0,
  avatar: ''
});
const editFormRef = ref();
const createDialogVisible = ref(false);
const createForm = ref<Facility>({
  id: 0,
  facilityName: '',
  facilityAddress: '',
  level: 0,
  managerId: 0,
  avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
});
const createFormRef = ref();

// 模拟数据初始化
const initData = () => {
  // 设施数据
  originTableData.value = [
    {
      id: 1,
      facilityName: 'Site-19',
      facilityAddress: '美国·罗得岛州',
      level: 5,
      managerId: 101,
      avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
    },
    {
      id: 2,
      facilityName: 'Site-17',
      facilityAddress: '美国·亚利桑那州',
      level: 4,
      managerId: 102,
      avatar: 'https://cube.elemecdn.com/e/f5/3f28f2a7e22d5c5c3d7b9e0e7b3e9png.png'
    },
    {
      id: 3,
      facilityName: 'Area-01',
      facilityAddress: '美国·华盛顿州',
      level: 5,
      managerId: 103,
      avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
    }
  ];

  // 工作人员数据
  staffList.value = [
    { id: 1001, facilityId: 1, name: 'Dr. Bright', role: '高级研究员', clearanceLevel: 4, status: '在岗' },
    { id: 1002, facilityId: 1, name: 'MTF-ε11队长', role: '安保主管', clearanceLevel: 3, status: '任务中' },
    { id: 1003, facilityId: 2, name: 'Dr. Clef', role: '异常研究员', clearanceLevel: 4, status: '在岗' },
    { id: 1004, facilityId: 3, name: 'O5-13', role: '站点主管', clearanceLevel: 5, status: '在岗' }
  ];

  // 异想体数据
  scpList.value = [
    { scpId: 'SCP-173', facilityId: 1, name: '雕塑', riskLevel: 'Euclid', containmentStatus: '稳定', lastCheck: '2023-10-01 08:30' },
    { scpId: 'SCP-682', facilityId: 1, name: '不灭蜥蜴', riskLevel: 'Keter', containmentStatus: '监控中', lastCheck: '2023-10-01 12:15' },
    { scpId: 'SCP-096', facilityId: 2, name: '羞涩的人', riskLevel: 'Euclid', containmentStatus: '稳定', lastCheck: '2023-09-29 16:45' },
    { scpId: 'SCP-999', facilityId: 3, name: '痒痒怪', riskLevel: 'Safe', containmentStatus: '稳定', lastCheck: '2023-10-01 09:20' }
  ];
};

// 初始化数据
onMounted(() => {
  initData();
  ElMessage.success('设施数据加载完成');
});

// 搜索过滤
const filteredData = computed(() => {
  return originTableData.value.filter(item => {
    const nameMatch = searchForm.facilityName
      ? item.facilityName.includes(searchForm.facilityName)
      : true;
    const levelMatch = searchForm.level
      ? item.level === parseInt(searchForm.level)
      : true;
    const addressMatch = searchForm.facilityAddress
      ? item.facilityAddress.includes(searchForm.facilityAddress)
      : true;
    const managerMatch = searchForm.managerId
      ? item.managerId.toString().includes(searchForm.managerId)
      : true;
    return nameMatch && levelMatch && addressMatch && managerMatch;
  });
});

// 分页数据
const currentTableData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredData.value.slice(start, end);
});

// 筛选当前设施的工作人员
const filteredStaff = computed(() => {
  return staffList.value.filter(item => item.facilityId === currentFacilityId.value);
});

// 筛选当前设施的异想体
const filteredScps = computed(() => {
  return scpList.value.filter(item => item.facilityId === currentFacilityId.value);
});

// 工具方法：获取人员数量
const getStaffCount = (facilityId: number) => {
  return staffList.value.filter(item => item.facilityId === facilityId).length;
};

// 工具方法：获取异想体数量
const getScpCount = (facilityId: number) => {
  return scpList.value.filter(item => item.facilityId === facilityId).length;
};

// 样式映射方法
const getLevelTagType = (level: number) => {
  const types: Record<number, string> = { 1: 'info', 2: 'success', 3: 'primary', 4: 'warning', 5: 'danger' };
  return types[level] || 'info';
};

const getStatusTagType = (status: string) => {
  const types: Record<string, string> = { '在岗': 'success', '任务中': 'warning', '休假': 'info' };
  return types[status] || 'danger';
};

const getRiskTagType = (level: string) => {
  const types: Record<string, string> = { 'Safe': 'success', 'Euclid': 'warning', 'Keter': 'danger', 'Thaumiel': 'primary' };
  return types[level] || 'info';
};

// 事件处理方法
const handleSearch = () => { currentPage.value = 1; };
const handleReset = () => {
  Object.keys(searchForm).forEach(key => searchForm[key as keyof typeof searchForm] = '');
  currentPage.value = 1;
};

const handleSizeChange = (val: number) => { pageSize.value = val; currentPage.value = 1; };
const handleCurrentChange = (val: number) => { currentPage.value = val; };

const handleDetail = (row: Facility) => {
  selectedFacility.value = row;
  detailDialogVisible.value = true;
};

const handleStaffList = (facilityId: number) => {
  currentFacilityId.value = facilityId;
  currentFacilityName.value = originTableData.value.find(f => f.id === facilityId)?.facilityName || '';
  staffDialogVisible.value = true;
};

const handleScpList = (facilityId: number) => {
  currentFacilityId.value = facilityId;
  currentFacilityName.value = originTableData.value.find(f => f.id === facilityId)?.facilityName || '';
  scpDialogVisible.value = true;
};

const handleEdit = (row: Facility) => {
  editForm.value = { ...row };
  editDialogVisible.value = true;
};

const saveEdit = () => {
  const index = originTableData.value.findIndex(item => item.id === editForm.value.id);
  if (index !== -1) {
    originTableData.value[index] = { ...editForm.value };
    ElMessage.success('设施信息更新成功');
    editDialogVisible.value = false;
  }
};

const handleCreate = () => {
  // 重置表单
  createForm.value = {
    id: 0,
    facilityName: '',
    facilityAddress: '',
    level: 0,
    managerId: 0,
    avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
  };
  createDialogVisible.value = true;
};

const saveCreate = () => {
  // 生成新ID
  const newId = Math.max(0, ...originTableData.value.map(item => item.id)) + 1;
  createForm.value.id = newId;

  // 添加到列表
  originTableData.value.push({ ...createForm.value });

  // 提示并关闭弹窗
  ElMessage.success('新建设施成功');
  createDialogVisible.value = false;
};
</script>

<style scoped>
/* 基础样式复用TeamList风格 */
.search-form {
  background: #fff; /* 修改为白色背景 */
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); /* 调整阴影 */
  border: 1px solid #e0e0e0; /* 调整边框颜色 */
  color: #333; /* 加深字体颜色 */
}

.search-input, .search-select {
  background-color: #fff;
  border: 1px solid #ccc; /* 调整边框颜色 */
  color: #333; /* 加深字体颜色 */
  border-radius: 4px;
  width: 180px;
}

/* 卡片样式 */
.containment-card {
  background: #fff; /* 修改为白色背景 */
  border: 1px solid #e0e0e0; /* 调整边框颜色 */
  border-radius: 8px;
  color: #333; /* 加深字体颜色 */
  overflow: hidden;
}

.containment-card:hover {
  transform: none; /* 移除鼠标悬停时的颜色变化 */
  box-shadow: none;
  border-color: #e0e0e0; /* 调整边框颜色 */
}

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e0e0e0; /* 调整边框颜色 */
}

.facility-avatar {
  border: 2px solid #e0e0e0; /* 调整边框颜色 */
  background-color: #fff;
}

.card-content {
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  margin-bottom: 10px;
  font-size: 14px;
}

.label {
  color: #666; /* 加深字体颜色 */
  width: 100px;
  font-weight: bold;
  display: flex;
  align-items: center;
}

.label i {
  margin-right: 8px;
}

/* 按钮样式 */
.card-actions {
  display: flex;
  justify-content: space-between;
  padding-top: 10px;
  border-top: 1px dashed #e0e0e0; /* 调整边框颜色 */
}

.staff-btn {
  color: #007bff; /* 调整按钮颜色 */
}

.scp-btn {
  color: #ff9800; /* 调整按钮颜色 */
}

.edit-btn {
  color: #28a745; /* 调整按钮颜色 */
}

.create-button {
  background-color: #28a745;
  border-color: #28a745;
}

.create-button:hover {
  background-color: #218838;
  border-color: #218838;
}

/* 弹窗与表格样式 */
.containment-dialog {
  background: #fff; /* 修改为白色背景 */
  border: 1px solid #e0e0e0; /* 调整边框颜色 */
  border-radius: 8px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1); /* 调整阴影 */
  color: #333; /* 加深字体颜色 */
}

.containment-table {
  background: #fff; /* 修改为白色背景 */
  border: 1px solid #e0e0e0; /* 调整边框颜色 */
  border-radius: 8px;
  color: #333; /* 加深字体颜色 */
}

.containment-table :deep(th) {
  background: #f5f5f5 !important; /* 调整表头背景颜色 */
  color: #333; /* 加深字体颜色 */
  border-bottom: 1px solid #e0e0e0; /* 调整边框颜色 */
}

.containment-table :deep(td) {
  background: #fff;
  color: #333; /* 加深字体颜色 */
  border-bottom: 1px solid #e0e0e0; /* 调整边框颜色 */
}

/* 分页样式 */
.containment-pagination {
  margin-top: 16px;
  background: #fff; /* 修改为白色背景 */
  padding: 10px;
  border-radius: 8px;
  border: 1px solid #e0e0e0; /* 调整边框颜色 */
  display: flex;
  justify-content: center;
  color: #333; /* 加深字体颜色 */
}

/* 标签样式 */
.clearance-tag, .status-tag, .risk-tag {
  font-weight: bold;
  border-radius: 12px;
  border: none;
}

</style>