<template>
  <!-- 搜索表单区域 - 暗色主题 -->
  <div class="content-container">

  <el-form
    :model="searchForm"
    inline
    class="search-form"
    style="margin-bottom: 8px;"
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
        placeholder="请选择"
        clearable
        class="search-select"
        style="width: 90px;"
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
        style="width: 110px;"
      ></el-input>
    </el-form-item>
    <el-form-item label="负责人姓名" class="search-item">
      <el-input
        v-model="searchForm.managerName"
        placeholder="请输入负责人姓名"
        clearable
        class="search-input"
        style="width: 135px;"
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
          @click="handleSearch"
          class="search-button"
      >搜索
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
  <div class="facility-cards" style="margin-bottom: 8px;">
    <el-row :gutter="30">
      <el-col
        v-for="item in originTableData"
        :key="item.id"
        :xs="24" :sm="12" :md="8" :lg="6"
        class="card-col"
      >
        <el-card
            :data="originTableData"
          class="containment-card"
          :body-style="{ padding: '8px' }"
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

              <el-tag
                  :type="item.isActive === 1 ? 'success' : 'danger'"
                  class="status-tag"
              >
                <i class="iconfont icon-status"></i> {{ item.isActive === 1 ? '启用' : '停用' }}
              </el-tag>

              <el-button
                  type="text"
                  size="small"
                  @click.stop="handleEdit(item)"
                  class="edit-btn"
              >
                <i class="iconfont icon-edit"></i> 编辑
              </el-button>

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
              <span class="label"><i class="iconfont icon-manager"></i> 负责人姓名：</span>
              <span class="value">{{ item.managerName }}</span>
            </div>
            <div class="info-item">
              <span class="label"><i class="iconfont icon-staff"></i> 人员数量：</span>
              <span class="value">{{ getStaffCount(item.id) === -1 ? '加载中...' : getStaffCount(item.id) }}人</span>
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
    :total="total"
    prev-text="上一页"
    next-text="下一页"
    class="containment-pagination"  style="margin-top: 8px;"
  >
  </el-pagination>
  </div>

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
      <el-table-column prop="username" label="姓名" width="150"></el-table-column>
      <el-table-column prop="level" label="权限等级">
        <template #default="scope">
          <el-tag :type="getLevelTagType(scope.row.level)" class="clearance-tag">
            {{ scope.row.level }}级
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="introduction" label="个人简介">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.introduction)" class="introduction-tag">
            {{ scope.row.introduction}}
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
      :data="filteredScp"
      border
      style="width: 100%"
      class="containment-table"
    >
      <el-table-column prop="id" label="异想体编号" width="150"></el-table-column>
      <el-table-column prop="name" label="名称" width="200"></el-table-column>
      <el-table-column prop="level" label="危险等级">
        <template #default="scope">
          <el-tag :type="getRiskTagType(scope.row.level)" class="risk-tag">
            {{ scope.row.level }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="异想体简介"></el-table-column>
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

      <div class="switch-container">
        <span class="label-text">设施状态:</span>
          <el-switch
              v-model="editForm.isActive"
              style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
              :active-value="1"
              :inactive-value="0"

          />
      </div>

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
import { reactive, ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import * as userApi from '@/api/user';
import * as facilityApi from '@/api/facility'
import * as abnormality from '@/api/abnormality'

import {findFacility} from "@/api/facility";




const total = ref(0);

// 定义核心接口
interface Facility {
  id: number;
  facilityName: string;
  facilityAddress: string;
  level: number;
  managerId: number;
  managerName: string;
  isActive: number;
  avatar: string;
}
const facility = ref<Facility>(
    {
      id: 0,
      facilityName: '',
      facilityAddress: '',
      level: 0,
      managerId: 0,
      managerName: '',
      isActive:1,
      avatar: ''
    }
);




interface Staff {
  id: number;
  facilityId: number;
  username: string;
  level: number;
  isActive: number;
}

interface SCP {
  id: number;
  facilityId: number;
  name: string;
  riskLevel: string;
  description: string;
  lastCheck: string;
}

// 搜索表单数据
const searchForm = ref({
  id:null,
  facilityName: '',
  level:null,
  facilityAddress: '',
  managerId:null,
  managerName: '',
  isActive:'',
  pageNum:1,
  pageSize:10,
  minLevel:null ,
  maxLevel:null
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
  id:0,
  facilityName: '',
  facilityAddress: '',
  level: 0,
  managerId: 0,
  managerName: '',
  isActive:1,
  avatar: ''
});
const createFormRef = ref();
const editFormRef = ref();
const createDialogVisible = ref(false);
const createForm = reactive({
  id:0 ,
  facilityName: '',
  facilityAddress: '',
  level: 0,
  managerId: 0,
  managerName: '',
  isActive:1,
  avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
});


// 数据初始化
const initData = async() => {
  try {
    const response = await facilityApi.getFacilityList(currentPage.value, pageSize.value);
    if(response.code === 200) {
      originTableData.value = response.data.list;
      total.value = response.data.total;
      ElMessage.success('数据更新成功');
      console.log('获取数据', response);
      await fetchAllStaffCounts(); // 获取所有设施的人员数量
    } else {
      ElMessage.error('发生错误：' + response.msg);
    }
  } catch (e) {
    console.log('错误', e);
    ElMessage.error(`获取数据错误${e.msg}`);
  }
};

//获取设施人员
const getFacilityUser=async(facilityId: number)=>{
  const staffRes = await userApi.findByFacilityId(facilityId,1,10)
  console.log(staffRes)
  if(staffRes.code==200){
    ElMessage.success('信息加载成功');
    filteredStaff.value = staffRes.data.list;
  }else{
    ElMessage.error('加载失败，请重试');

  }
}
//获取设施收容异想体
const getFacilityAbnormality=async(facilityId: number)=>{
  const scpRes= await abnormality.findByFacilityId(facilityId,1,10)
  console.log(scpRes)
  if(scpRes.code==200){
    ElMessage.success('信息加载成功');
    filteredScp.value =scpRes.data.list ;
  }else{
    ElMessage.error('加载失败，请重试');

  }

}
// 初始化数据
onMounted(async () => {
  await initData();
  ElMessage.success('设施数据加载完成');
});

// 搜索过滤
const handleSearch = () => {
  console.log('搜索表单',searchForm.value);
  searchForm.value.pageNum = currentPage.value;
  searchForm.value.pageSize = pageSize.value;
  findFacility(searchForm.value).then((response) => {
    if(response.code === 200) {
      originTableData.value = response.data.list;
      total.value = response.data.total;
      ElMessage.success('搜索成功')
    }else {
      ElMessage.error('搜索出错'+response.msg)
    }
  }).catch((e) => {
    console.log('error', e)
    ElMessage.error('服务器错误',e.msg);
  })
}
// 分页数据

const handleSizeChange = (val: number) => {
  pageSize.value = val;
  currentPage.value = 1;
  initData(); // 重新加载数据
};

// 当前页码改变
const handleCurrentChange = (val: number) => {
  currentPage.value = val;
  initData(); // 重新加载数据
};


// 筛选当前设施的工作人员
const filteredStaff = ref<any[]>([

])
const filteredScp = ref<any[]>([

])

const staffCounts = ref<Record<number, number>>({}); // 存储设施ID对应的人员数量
const isStaffCountLoaded = ref(false); // 人员数量是否加载完成的标志

// 提前获取所有设施的人员数量
const fetchAllStaffCounts = async () => {
  const facilityIds = originTableData.value.map(facility => facility.id);
  for (const facilityId of facilityIds) {
    staffCounts.value[facilityId] = -1; // 初始化为加载中状态
    try {
      const response = await userApi.findByFacilityId(facilityId, 1, 1);
      if (response.code === 200) {
        staffCounts.value[facilityId] = response.data.total;
      } else {
        staffCounts.value[facilityId] = 0;
      }
    } catch (error) {
      console.error('获取人员数量失败:', error);
      staffCounts.value[facilityId] = 0;
    }
  }
  isStaffCountLoaded.value = true; // 人员数量加载完成
};

// 工具方法：获取人员数量
const getStaffCount = (facilityId: number) => {
  return staffCounts.value[facilityId];
};


const abnormalityCounts = ref<Record<number, number>>({}); //存储设施ID对应的异想体数量
// 工具方法：获取异想体数量
const getScpCount = (facilityId: number) => {
  if (abnormalityCounts.value[facilityId] !== undefined) {
    return abnormalityCounts.value[facilityId];
  }

  staffCounts.value[facilityId] = 0;

  abnormality.findByFacilityId(facilityId, 1, 1).then(response => {
      if (response.code === 200) {
        abnormalityCounts.value[facilityId] = response.data.total;
      }
    }).catch((error) => {
      console.log('获取异想体数量失败', error);
      abnormalityCounts.value[facilityId] = 0;
  })
  return 0;
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
const handleReset = () => {
  searchForm.value = {
    id:null,
    facilityName: '',
    level:null,
    facilityAddress: '',
    managerId:null,
    managerName: '',
    isActive:'',
    pageNum:1,
    pageSize:10,
    minLevel:null ,
    maxLevel:null
  }
  currentPage.value = 1;
  initData()
};

const handleDetail = (row: Facility) => {
  selectedFacility.value = row;
  detailDialogVisible.value = true;
};

const handleStaffList = (facilityId: number) => {
  currentFacilityId.value = facilityId;
  currentFacilityName.value = originTableData.value.find(f => f.id === facilityId)?.facilityName || '';
  getFacilityUser(facilityId)
  staffDialogVisible.value = true;
};

const handleScpList = (facilityId: number) => {
  currentFacilityId.value = facilityId;
  currentFacilityName.value = originTableData.value.find(f => f.id === facilityId)?.facilityName || '';
  getFacilityAbnormality(facilityId)
  scpDialogVisible.value = true;
};

const handleEdit = (row: Facility) => {
  editForm.value = { ...row };
  editDialogVisible.value = true;
};

const saveEdit = () => {
  console.log('提交编辑表单',editForm.value);
  facilityApi.updateFacility(editForm.value).then((response) => {
    if(response.code === 200) {
      originTableData.value = response.data.list;
      ElMessage.success('数据更新成功')
      console.log('获取数据',response)
    }
  }).catch((e) => {
        console.log('错误',e)
        ElMessage.error(e.msg);
      }
  )
};

const handleCreate = () => {
  createDialogVisible.value = true;};

const saveCreate = () => {
  if (!createFormRef.value) return;
  createFormRef.value.validate((valid) => {
    if (valid) {
      // 创建新设施对象
      const newFacility = {
        ...createForm
      };

          facilityApi.addFacility(newFacility).then((response) => {
        if (response.code === 200) {
          console.log("新建设施成功:", newFacility);
          ElMessage.success('新建设施成功');
          createDialogVisible.value = false;
          initData()
          createFormRef.value.resetFields();
        }
          }).catch((error) => {
            console.log('服务器错误',error);
            ElMessage.error('服务器错误')
          })
    }
  })
}





</script>

<style scoped>
/* 基础样式复用TeamList风格 */
.search-form {
  background: #fff;
  padding: 4px 12px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: 1px solid #e0e0e0;
  color: #333;
}

.search-form .el-form-item {
  margin-bottom: 6px !important;
}

.search-form .el-form-item__content {
  line-height: 30px;
}

.search-form .el-input,
.search-form .el-select {
  height: 30px;
  line-height: 30px;
}

.search-form .el-input__inner,
.search-form .el-select .el-input__inner {
  height: 30px !important;
  line-height: 30px !important;
  padding: 0 10px;
  font-size: 12px;
}

.search-form .el-button {
  height: 30px;
  padding: 0 10px;
  font-size: 12px;
  line-height: 30px;
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

.search-container {
  background-color: #ffffff; /* 白色背景 */
  border: 1px solid #ccc; /* 边框 */
  border-radius: 8px; /* 圆角 */
  padding: 16px; /* 内边距 */
  margin-bottom: 16px; /* 下边距 */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* 轻微阴影 */
}

/* 原有样式保持不变 */
.search-input, .search-select {
  background-color: #ffffff;
  border: 1px solid #ccc;
  color: #333;
  border-radius: 4px;
  width: 100px;
}
.content-container {
  background-color: #ffffff; /* 白色背景 */
  border: 1px solid #ccc; /* 边框 */
  border-radius: 8px; /* 圆角 */
  padding: 16px; /* 内边距 */
  margin-bottom: 16px; /* 下边距 */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* 轻微阴影 */
}
.address-text {
  display: block; /* 确保 span 占据一行 */
  white-space: nowrap; /* 禁止换行 */
  overflow: hidden; /* 超出隐藏 */
  text-overflow: ellipsis; /* 超出部分显示省略号 */
  max-width: 200px; /* 根据实际需求调整最大宽度 */
}

.containment-card {
  width: 280px;
  height: auto;
  margin-bottom: 8px;
}

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 8px;  /* 减少顶部间距 */
  padding-bottom: 4px; /* 减少底部间距 */
  border-bottom: 1px solid #e0e0e0;
}

.card-content {
  margin-bottom: 8px;  /* 减少内容区域底部间距 */
}

.info-item {
  margin-bottom: 6px;  /* 减少每条信息之间的间距 */
}

.containment-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  border-color: #c8c8c8;
}
</style>