<template>
  <el-main class="page-container">
    <div class="page-header">
      <h2>异想体列表</h2>
      <div>
        <el-button type="success" @click="showCreateDialog" class="create-button">
          <el-icon><Plus /></el-icon> 新建异想体
        </el-button>
      </div>
    </div>

    <el-row :gutter="20" class="mb-20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <h3>Keter 级</h3>
            <p class="stat-value">{{ levelCounts.Keter }} <span class="stat-label">个</span></p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <h3>Euclid 级</h3>
            <p class="stat-value">{{ levelCounts.Euclid }} <span class="stat-label">个</span></p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <h3>Safe 级</h3>
            <p class="stat-value">{{ levelCounts.Safe }} <span class="stat-label">个</span></p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <h3>Thaumiel 级</h3>
            <p class="stat-value">{{ levelCounts.Thaumiel }} <span class="stat-label">个</span></p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索表单区域 -->
    <el-form :inline="true" :model="filterForm" class="search-form">
      <el-form-item label="SCP编号" class="search-item">
        <el-input
          v-model="filterForm.number"
          placeholder="SCP-XXX"
          clearable
          class="search-input"
          prefix-icon="el-icon-key"
        ></el-input>
      </el-form-item>

      <el-form-item label="名称" class="search-item">
        <el-input
          v-model="filterForm.name"
          placeholder="异想体名称"
          clearable
          class="search-input"
          prefix-icon="el-icon-user"
        ></el-input>
      </el-form-item>

      <el-form-item label="危险等级" class="search-item">
        <el-select
          v-model="filterForm.level"
          placeholder="请选择危险等级"
          clearable
          class="search-select"
        >
          <el-option label="Keter" value="Keter" />
          <el-option label="Euclid" value="Euclid" />
          <el-option label="Safe" value="Safe" />
          <el-option label="Thaumiel" value="Thaumiel" />
          <el-option label="Neutralized" value="Neutralized" />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="searchAbnormalities" class="search-button">
          <i class="iconfont icon-search"></i> 搜索
        </el-button>
        <el-button @click="resetFilter" class="reset-button">
          <i class="iconfont icon-reset"></i> 重置
        </el-button>
      </el-form-item>
    </el-form>

    <!-- 数据表格区域 -->
    <el-table
      :data="filteredAbnormalityList"
      border
      style="width: 100%"
      class="containment-table"
      :header-cell-style="tableHeaderStyle"
      :row-style="tableRowStyle"
    >
      <el-table-column prop="number" label="SCP编号" width="120">
        <template #header>
          <span><i class="iconfont icon-id"></i> SCP编号</span>
        </template>
      </el-table-column>

      <el-table-column prop="name" label="名称" width="200">
        <template #header>
          <span><i class="iconfont icon-user"></i> 名称</span>
        </template>
        <template #default="scope">
          <span class="abnormality-name">{{ scope.row.name }}</span>
        </template>
      </el-table-column>

      <el-table-column prop="level" label="危险等级" width="120">
        <template #header>
          <span><i class="iconfont icon-security"></i> 危险等级</span>
        </template>
        <template #default="scope">
          <el-tag :type="getLevelType(scope.row.level)" effect="dark" class="clearance-tag">
            {{ scope.row.level }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="facility" label="所在设施" width="180">
        <template #header>
          <span><i class="iconfont icon-location"></i> 所在设施</span>
        </template>
      </el-table-column>

      <el-table-column prop="status" label="收容状态" width="120">
        <template #header>
          <span><i class="iconfont icon-status"></i> 收容状态</span>
        </template>
        <template #default="scope">
          <el-tag :type="scope.row.status === '稳定' ? 'success' : 'danger'" class="status-tag">
            {{ scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="220" fixed="right">
        <template #header>
          <span><i class="iconfont icon-operation"></i> 操作</span>
        </template>
        <template #default="scope">
          <el-button
            type="text"
            @click="viewDetail(scope.row)"
            class="detail-btn"
          >
            <i class="iconfont icon-detail"></i> 详情
          </el-button>
          <el-button
            type="text"
            @click="editAbnormality(scope.row)"
            class="edit-btn"
          >
            <i class="iconfont icon-edit"></i> 编辑
          </el-button>
          <el-button
            type="text"
            class="delete-btn"
            @click="deleteAbnormality(scope.row)"
          >
            <i class="iconfont icon-delete"></i> 删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件区域 -->
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pagination.currentPage"
      :page-sizes="[10, 20, 30]"
      :page-size="pagination.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="pagination.total"
      prev-text="上一页"
      next-text="下一页"
      class="containment-pagination"
    />

    <!-- 新建/编辑对话框 -->
    <el-dialog
      v-model="creatDialogVisible"
      title= "新建异想体"
      width="50%"
      class="containment-dialog"
    >
      <el-form
        :model="abnormalityForm"
        ref="abnormalityFormRef"
        label-width="120px"
        :rules="rules"
        label-position="left"
      >
        <el-form-item label="异想体编号" prop="number">
          <el-input v-model="abnormalityForm.number" placeholder="异想体编号(请和ID区分)" />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="abnormalityForm.name" placeholder="异想体名称" />
        </el-form-item>
        <el-form-item label="危险等级" prop="level">
          <el-select v-model="abnormalityForm.level" placeholder="请选择危险等级">
            <el-option label="灭世" value="1" />
            <el-option label="未解明" value="2" />
            <el-option label="安全" value="3" />
            <el-option label="机密" value="4" />
            <el-option label="无效化" value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="异想体图片">
          <el-upload
              class="avatar-uploader"
              action="http://localhost:8080/file/upload"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
              :headers="headers"
          >
            <img v-if="imageUrl" :src="imageUrl" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="特性" prop="characteristics">
          <el-input v-model="abnormalityForm.characteristics" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="管理措施" prop="managementMeasures">
          <el-input v-model="abnormalityForm.managementMeasures" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="特殊事项" prop="specialNotes">
          <el-input v-model="abnormalityForm.specialNotes" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input v-model="abnormalityForm.remarks" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="所在设施" prop="facility">
          <el-input v-model="abnormalityForm.facility" />
        </el-form-item>
        <el-form-item label="收容状态" prop="status">
          <el-select v-model="abnormalityForm.status" placeholder="请选择收容状态">
            <el-option label="稳定" value="稳定" />
            <el-option label="异常" value="异常" />
            <el-option label="收容失效" value="收容失效" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="creatDialogVisible = false" class="dialog-button">取消</el-button>
        <el-button type="primary" @click="submitAbnormality" class="dialog-button">确认</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="`${currentAbnormality.number} - ${currentAbnormality.name}`"
      width="60%"
      class="containment-dialog"
    >
      <div class="abnormality-detail">
        <div class="security-stamp">
          <div class="stamp-content">
            <div class="stamp-title">SCP FOUNDATION</div>
            <div class="stamp-level">机密等级: {{ getClearanceLevel(currentAbnormality.level) }}</div>
          </div>
        </div>

        <div class="detail-header">
          <div class="header-info">
            <h2 class="abnormality-name">{{ currentAbnormality.name }}</h2>
            <div class="info-row">
              <span class="info-item"><i class="iconfont icon-id"></i> SCP编号：<strong>{{ currentAbnormality.number }}</strong></span>
              <span class="info-item"><i class="iconfont icon-security"></i> 危险等级：
                <el-tag :type="getLevelType(currentAbnormality.level)" effect="dark" class="clearance-tag">
                  {{ currentAbnormality.level }}
                </el-tag>
              </span>
            </div>
            <div class="info-row">
              <span class="info-item"><i class="iconfont icon-status"></i> 收容状态：
                <el-tag :type="currentAbnormality.status === '稳定' ? 'success' : 'danger'" class="status-tag">
                  {{ currentAbnormality.status }}
                </el-tag>
              </span>
              <span class="info-item"><i class="iconfont icon-location"></i> 所在设施：<strong>{{ currentAbnormality.facility }}</strong></span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h3 class="section-title"><i class="iconfont icon-character"></i> 特性描述</h3>
          <div class="info-card">
            <div class="info-value">{{ currentAbnormality.characteristics }}</div>
          </div>
        </div>

        <div class="detail-section">
          <h3 class="section-title"><i class="iconfont icon-measure"></i> 管理措施</h3>
          <div class="info-card">
            <div class="info-value">{{ currentAbnormality.managementMeasures }}</div>
          </div>
        </div>

        <div class="detail-section">
          <h3 class="section-title"><i class="iconfont icon-note"></i> 特殊事项</h3>
          <div class="info-card">
            <div class="info-value">{{ currentAbnormality.specialNotes }}</div>
          </div>
        </div>

        <div class="detail-section">
          <h3 class="section-title"><i class="iconfont icon-remark"></i> 备注</h3>
          <div class="info-card">
            <div class="info-value">{{ currentAbnormality.remarks }}</div>
          </div>
        </div>

        <div class="detail-section">
          <h3 class="section-title"><i class="iconfont icon-security-info"></i> 安全信息</h3>
          <div class="security-info">
            <div class="info-card">
              <div class="info-label"><i class="iconfont icon-clearance"></i> 访问权限：</div>
              <div class="info-value">{{ getAccessLevel(currentAbnormality.level) }}</div>
            </div>
            <div class="info-card">
              <div class="info-label"><i class="iconfont icon-date"></i> 收容日期：</div>
              <div class="info-value">2023-08-15</div>
            </div>
            <div class="info-card">
              <div class="info-label"><i class="iconfont icon-update"></i> 最后更新：</div>
              <div class="info-value">2023-10-20 14:32:18</div>
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
  </el-main>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import {ElMessage} from "element-plus";

// 模拟数据
const abnormalityData = ref([
  {
    id: 1,
    number: 'SCP-173',
    name: '雕像 - "The Sculpture"',
    level: 'Euclid',
    characteristics: '由混凝土和钢筋建造的雕像，当不被直接注视时会以极快速度移动并折断观察者的颈部。',
    managementMeasures: '必须时刻保持至少两名人员同时注视SCP-173，清洁时需在至少三名人员的监督下进行。',
    specialNotes: '任何情况下不得单独与SCP-173处于同一空间。',
    remarks: '最初在Site-19发现',
    facility: 'Site-19 收容区',
    status: '稳定'
  },
  {
    id: 2,
    number: 'SCP-682',
    name: '不灭孽蜥',
    level: 'Keter',
    characteristics: '一只巨大、起源未知的爬行动物，表现出极高的智能和近乎无法摧毁的特性，对所有生命形式怀有极端的敌意。',
    managementMeasures: 'SCP-682必须被持续浸泡在盐酸池中，任何情况下不得将其移出收容。',
    specialNotes: '已记录到SCP-682能够适应并抵抗多种消灭手段。',
    remarks: '与SCP-053、SCP-999等有特殊互动记录',
    facility: 'Site-19 酸性池',
    status: '异常'
  },
  {
    id: 3,
    number: 'SCP-049',
    name: '瘟疫医生',
    level: 'Euclid',
    characteristics: '人形实体，身着中世纪瘟疫医生服装，认为人类感染了"瘟疫"，试图通过外科手术"治愈"他们。',
    managementMeasures: '收容于标准人形收容单元，每周提供一次D级人员作为"病人"。',
    specialNotes: '被049"治愈"的个体会转化为SCP-049-2实例。',
    remarks: '在法国乡村发现',
    facility: 'Site-19 医疗区',
    status: '稳定'
  },
  {
    id: 4,
    number: 'SCP-999',
    name: '痒痒怪',
    level: 'Safe',
    characteristics: '橙色粘稠胶状物，具有愉快的气味和温暖的触感，能引发幸福感并中和有害情绪。',
    managementMeasures: '收容于10m×10m的强化玻璃容器中，每天提供糖类食物。',
    specialNotes: '对酸性物质敏感，需保持pH值中性环境。',
    remarks: '已知最友好的SCP之一',
    facility: 'Site-19 安全区',
    status: '稳定'
  },
  {
    id: 5,
    number: 'SCP-106',
    name: '恐怖老人',
    level: 'Keter',
    characteristics: '人形实体，外观为严重腐烂的老人，能腐蚀任何接触的表面并穿越固体物质。',
    managementMeasures: '使用特殊设计的"少女之盒"收容装置，每24小时更换一次诱饵。',
    specialNotes: '受害者会被拖入106的"口袋次元"，无法生还。',
    remarks: '收容失效频率最高的Keter级实体之一',
    facility: 'Site-19 高危区',
    status: '稳定'
  }
])

const filterForm = ref({
  number: '',
  name: '',
  level: ''
})

const abnormalityForm = ref({
  id: null,
  number: '',
  name: '',
  level: '',
  characteristics: '',
  managementMeasures: '',
  specialNotes: '',
  remarks: '',
  facility: '',
  status: '稳定',
  imageName: "",
})

const rules = {
  number: [{ required: true, message: '请输入SCP编号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入异想体名称', trigger: 'blur' }],
  level: [{ required: true, message: '请选择危险等级', trigger: 'change' }],
  characteristics: [{ required: true, message: '请输入特性描述', trigger: 'blur' }],
  managementMeasures: [{ required: true, message: '请输入管理措施', trigger: 'blur' }],
  facility: [{ required: true, message: '请输入所在设施', trigger: 'blur' }],
  status: [{ required: true, message: '请选择收容状态', trigger: 'change' }]
}

const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const creatDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const dialogTitle = ref('新建异想体')
const currentAbnormality = ref({})
const abnormalityFormRef = ref(null)

// 计算属性
const filteredAbnormalityList = computed(() => {
  let result = [...abnormalityData.value]

  if (filterForm.value.number) {
    result = result.filter(item =>
      item.number.toLowerCase().includes(filterForm.value.number.toLowerCase()))
  }

  if (filterForm.value.name) {
    result = result.filter(item =>
      item.name.toLowerCase().includes(filterForm.value.name.toLowerCase()))
  }

  if (filterForm.value.level) {
    result = result.filter(item => item.level === filterForm.value.level)
  }

  // 更新分页总数
  pagination.value.total = result.length

  // 分页处理
  const start = (pagination.value.currentPage - 1) * pagination.value.pageSize
  const end = start + pagination.value.pageSize
  return result.slice(start, end)
})

const levelCounts = computed(() => {
  const counts = {
    Keter: 0,
    Euclid: 0,
    Safe: 0,
    Thaumiel: 0,
    Neutralized: 0
  }

  abnormalityData.value.forEach(item => {
    if (counts.hasOwnProperty(item.level)) {
      counts[item.level]++
    }
  })

  return counts
})

// 方法
const getLevelType = (level) => {
  const levelMap = {
    'Keter': 'danger',
    'Euclid': 'warning',
    'Safe': 'success',
    'Thaumiel': 'primary',
    'Neutralized': 'info'
  }
  return levelMap[level] || 'info'
}

// 获取机密等级
const getClearanceLevel = (level) => {
  const clearanceMap = {
    'Keter': '5级',
    'Euclid': '4级',
    'Safe': '3级',
    'Thaumiel': '6级',
    'Neutralized': '1级'
  }
  return clearanceMap[level] || '未知'
}

// 获取访问权限
const getAccessLevel = (level) => {
  const accessMap = {
    'Keter': 'O5议会及指定人员',
    'Euclid': '4级及以上人员',
    'Safe': '2级及以上人员',
    'Thaumiel': '仅O5议会',
    'Neutralized': '所有人员'
  }
  return accessMap[level] || '未定义'
}

// 表格头部样式
const tableHeaderStyle = () => {
  return {
    background: 'linear-gradient(to bottom, #1a2a4a, #0c1a33)',
    color: '#c0d1f2',
    fontWeight: 'bold',
    borderBottom: '1px solid #304878'
  }
}

// 表格行样式
const tableRowStyle = ({ rowIndex }) => {
  return {
    background: rowIndex % 2 === 0 ? 'rgba(10, 20, 41, 0.3)' : 'rgba(15, 30, 61, 0.3)',
    color: '#e0f0ff'
  }
}

const searchAbnormalities = () => {
  pagination.value.currentPage = 1
}

const resetFilter = () => {
  filterForm.value = {
    number: '',
    name: '',
    level: ''
  }
  searchAbnormalities()
}

const showCreateDialog = () => {
  creatDialogVisible.value = true
}

const editAbnormality = (item) => {
  dialogTitle.value = '编辑异想体'
  abnormalityForm.value = { ...item }
  creatDialogVisible.value = true
}

const viewDetail = (item) => {
  currentAbnormality.value = { ...item }
  detailDialogVisible.value = true
}

const deleteAbnormality = (item) => {
  ElMessageBox.confirm(`确定要删除异想体 ${item.number} - ${item.name} 吗?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    center: true,
    customClass: 'delete-confirm-box'
  }).then(() => {
    const index = abnormalityData.value.findIndex(ab => ab.id === item.id)
    if (index !== -1) {
      abnormalityData.value.splice(index, 1)
      ElMessage.success('删除成功')
      // 如果删除的是当前页最后一条且不是第一页，则跳转到上一页
      if (filteredAbnormalityList.value.length === 0 && pagination.value.currentPage > 1) {
        pagination.value.currentPage -= 1
      }
    }
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

const submitAbnormality = () => {
  console.log(abnormalityForm.value)
}

const handleSizeChange = (val) => {
  pagination.value.pageSize = val
  pagination.value.currentPage = 1
}

const handleCurrentChange = (val) => {
  pagination.value.currentPage = val
}

// 初始化
onMounted(() => {
  pagination.value.total = abnormalityData.value.length
})


// 上传图片
const imageUrl = ref('')

const handleAvatarSuccess: UploadProps['onSuccess'] = (
    response,
    uploadFile
) => {
  imageUrl.value = URL.createObjectURL(uploadFile.raw!)
  console.log(response)
  if(response.code === 200) {
    abnormalityForm.value.imageName = response.data
    ElMessage.success("图片上传成功")
  }else{
    ElMessage.error(response.msg)
  }
}

const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
  if (rawFile.type !== 'image/jpeg') {
    ElMessage.error('Avatar picture must be JPG format!')
    return false
  } else if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('Avatar picture size can not exceed 2MB!')
    return false
  }
  return true
}

const headers = ref({
  Authorization: 'Bearer ' + localStorage.getItem('token')
})
</script>

<style scoped>
.page-container {
  padding: 20px;
  background-color: #0c1427;
  min-height: calc(100vh - 60px);
  color: #e0e7ff;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #e0e7ff;
}

.mb-20 {
  margin-bottom: 20px;
}

.stat-card {
  background-color: #0d1830;
  border: 1px solid rgba(93, 139, 244, 0.2);
}

.stat-content h3 {
  color: #a0a8c3;
  font-size: 14px;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #e0e7ff;
  margin: 0;
}

.stat-label {
  font-size: 14px;
  color: #a0a8c3;
  font-weight: normal;
}

/* 搜索表单样式 */
.search-form {
  background: linear-gradient(135deg, #1a2a4a, #0c1a33);
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  border: 1px solid #304878;
  margin-bottom: 20px;
}

.search-input, .search-select {
  background-color: rgba(255, 255, 255, 0.1);
  border: 1px solid #304878;
  color: #e0f0ff;
  border-radius: 4px;
  width: 180px;
}

.search-input:hover, .search-select:hover {
  border-color: #4a6fb3;
}

.search-button {
  background: linear-gradient(to right, #4a6fb3, #3a5a9c);
  border: none;
  color: #fff;
  font-weight: bold;
  transition: all 0.3s ease;
}

.search-button:hover {
  background: linear-gradient(to right, #5a7fc3, #4a6aac);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(74, 111, 179, 0.4);
}

.reset-button {
  background: linear-gradient(to right, #5a5a7c, #4a4a6c);
  border: none;
  color: #e0e0ff;
  font-weight: bold;
  transition: all 0.3s ease;
}

.reset-button:hover {
  background: linear-gradient(to right, #6a6a8c, #5a5a7c);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(90, 90, 140, 0.4);
}

.create-button {
  background: linear-gradient(to right, #4aaf7d, #3a8c5f);
  border: none;
  color: #fff;
  font-weight: bold;
  transition: all 0.3s ease;
}

.create-button:hover {
  background: linear-gradient(to right, #5abf8d, #4a9c6f);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(74, 175, 125, 0.4);
}

/* 表格样式 */
.containment-table {
  background: rgba(10, 20, 41, 0.5);
  border: 1px solid #304878;
  border-radius: 8px;
  overflow: hidden;
}

.containment-table :deep(th) {
  background: linear-gradient(to bottom, #1a2a4a, #0c1a33) !important;
  color: #c0d1f2;
  border-bottom: 1px solid #304878;
  font-size: 15px;
}

.containment-table :deep(td) {
  background: transparent;
  color: #e0f0ff;
  border-bottom: 1px solid #304878;
  font-size: 14px;
}

.containment-table :deep(.el-table__row:hover td) {
  background-color: rgba(74, 111, 179, 0.2) !important;
}

.abnormality-name {
  font-weight: bold;
}

.clearance-tag {
  font-weight: bold;
  border-radius: 12px;
  padding: 0 10px;
  height: 24px;
  line-height: 24px;
  border: none;
}

.status-tag {
  font-weight: bold;
  border-radius: 12px;
  padding: 0 10px;
  height: 24px;
  line-height: 24px;
  border: none;
}

.detail-btn {
  color: #4a9cf0;
  font-weight: bold;
}

.detail-btn:hover {
  color: #6ab6ff;
}

.edit-btn {
  color: #4a9cf0;
  font-weight: bold;
}

.edit-btn:hover {
  color: #6ab6ff;
}

.delete-btn {
  color: #e63946;
  font-weight: bold;
}

.delete-btn:hover {
  color: #ff5a6e;
}

/* 分页样式 */
.containment-pagination {
  margin-top: 16px;
  display: flex;
  justify-content: center;
  background: rgba(10, 20, 41, 0.3);
  padding: 10px;
  border-radius: 8px;
  border: 1px solid #304878;
}

.containment-pagination :deep(.el-pager li) {
  background-color: #1a2a4a;
  color: #c0d1f2;
  border: 1px solid #304878;
  border-radius: 4px;
  margin: 0 4px;
}

/* 弹窗样式 */
.containment-dialog {
  background: linear-gradient(135deg, #0c1a33, #1a2a4a);
  border: 1px solid #4a6fb3;
  border-radius: 8px;
  box-shadow: 0 0 20px rgba(74, 111, 179, 0.5);
}

.dialog-button {
  background: linear-gradient(to right, #4a6fb3, #3a5a9c);
  border: none;
  color: white;
  font-weight: bold;
  transition: all 0.3s ease;
}

.dialog-button:hover {
  background: linear-gradient(to right, #5a7fc3, #4a6aac);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(74, 111, 179, 0.4);
}

/* 详情页样式 */
.abnormality-detail {
  position: relative;
  background: rgba(15, 30, 61, 0.85);
  padding: 25px;
  border-radius: 6px;
  border: 1px solid #304878;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
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
  z-index: 10;
  overflow: hidden;
}

.stamp-content {
  text-align: center;
  transform: rotate(-15deg);
}

.stamp-title {
  font-size: 12px;
  font-weight: bold;
  color: #e63946;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.stamp-level {
  font-size: 10px;
  color: #ff9aa2;
  margin-top: 3px;
}

.detail-header {
  margin-bottom: 20px;
}

.abnormality-name {
  margin-top: 0;
  margin-bottom: 15px;
  color: #e0f0ff;
  font-size: 24px;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.5);
  border-bottom: 2px solid #4a6fb3;
  padding-bottom: 10px;
}

.info-row {
  display: flex;
  margin-bottom: 12px;
  font-size: 14px;
  flex-wrap: wrap;
}

.info-item {
  margin-right: 30px;
  margin-bottom: 8px;
  min-width: 45%;
  display: flex;
  align-items: center;
}

.info-item i {
  margin-right: 8px;
  color: #a0b9e0;
  font-size: 16px;
}

.info-item strong {
  color: #ffffff;
  margin-left: 5px;
  font-weight: 500;
}

.detail-section {
  margin-top: 25px;
  padding-top: 20px;
  border-top: 1px solid #304878;
}

.section-title {
  color: #e0f0ff;
  font-size: 18px;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.section-title i {
  margin-right: 8px;
  color: #4a9cf0;
  font-size: 20px;
}

.info-card {
  background: rgba(10, 20, 41, 0.5);
  border: 1px solid #304878;
  border-radius: 8px;
  padding: 15px;
  transition: all 0.3s ease;
  margin-bottom: 15px;
}

.info-card:hover {
  border-color: #4a6fb3;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(74, 111, 179, 0.2);
}

.info-label {
  color: #a0b9e0;
  font-weight: bold;
  margin-bottom: 8px;
  font-size: 14px;
  display: flex;
  align-items: center;
}

.info-label i {
  margin-right: 8px;
  font-size: 16px;
}

.info-value {
  color: #ffffff;
  font-size: 15px;
  font-weight: 500;
  line-height: 1.5;
}

.security-info {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>

<style>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}
</style>