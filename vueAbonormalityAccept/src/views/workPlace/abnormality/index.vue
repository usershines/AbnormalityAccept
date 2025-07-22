<template>
  <el-card style="height: 770px;margin-top: -30px">

    <!-- 搜索表单区域 -->
    <el-form :inline="true" :model="filterForm" class="search-form">

      <el-form-item label="名称" class="search-item" style="width: 180px;">
        <el-input
          v-model="filterForm.name"
          placeholder="异想体名称"
          clearable
          class="search-input"
          prefix-icon="el-icon-user"
        ></el-input>
      </el-form-item>

      <el-form-item label="危险等级" class="search-item" style="width: 230px;">
        <el-select
          v-model="filterForm.level"
          placeholder="请选择危险等级"
          clearable
          class="search-select"
        >
          <el-option label="安全" value="1" />
          <el-option label="未解明" value="2" />
          <el-option label="灭世" value="3" />
          <el-option label="机密" value="4" />
          <el-option label="无效化" value="5" />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleSearch" class="search-button">
          <i class="iconfont icon-search"></i> 搜索
        </el-button>
        <el-button @click="resetFilter" class="reset-button">
          <i class="iconfont icon-reset"></i> 重置
        </el-button>
        <el-button type="success" @click="showCreateDialog" class="create-button">
          <el-icon><Plus /></el-icon> 新建异想体
        </el-button>
      </el-form-item>
    </el-form>

    <!-- 数据表格区域 -->
    <el-table
      :data="tableData"
      border
      style="width: 100%"
      class="containment-table"
      :header-cell-style="tableHeaderStyle"
    >
    <el-table-column label="预览图" width="110" >
      <template #default="scope">

        <el-image
          :src="scope.row.imgName"
          fit="fill"
          class="abnormality-image"
        ></el-image>

      </template>
    </el-table-column>
      <el-table-column prop="name" label="名称">
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

      <el-table-column prop="facilityId" label="所在设施" width="180">
        <template #header>
          <span><i class="iconfont icon-location"></i> 所在设施</span>
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
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件区域 -->
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pageNum"
      :page-sizes="[5, 10, 15]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
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
        :model="newAbnormalityForm"
        ref="abnormalityFormRef"
        label-width="120px"
        :rules="rules"
        label-position="left"
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="newAbnormalityForm.name" placeholder="异想体名称" />
        </el-form-item>
        <el-form-item label="危险等级" prop="level">
          <el-select v-model="newAbnormalityForm.level" placeholder="请选择危险等级">
            <el-option label="灭世" value="3" />
            <el-option label="未解明" value="2" />
            <el-option label="安全" value="1" />
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
          <el-input v-model="newAbnormalityForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="管理措施" prop="managementMeasures">
          <el-input v-model="newAbnormalityForm.manageMethod" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input v-model="newAbnormalityForm.notes" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="所在设施ID" prop="facility">
          <el-input v-model="newAbnormalityForm.facilityId" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="creatDialogVisible = false" class="dialog-button">取消</el-button>
        <el-button type="primary" @click="submitAbnormality" class="dialog-button">确认</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗-->
    <el-dialog
      v-model="detailDialogVisible"
      :title="currentAbnormality.name"
      width="60%"
      class="containment-dialog"
    >
      <div class="abnormality-detail">
        <div class="security-stamp">
          <div class="stamp-content">
            <div class="stamp-title">SCP FOUNDATION</div>
            <div class="stamp-level">机密等级: {{ getClearLevel(currentAbnormality.level) }}</div>
          </div>
        </div>

        <div class="detail-header">
          <div class="header-info">
            <h2 class="abnormality-name2">{{ currentAbnormality.name }}</h2>
            <div class="info-row">
              <span class="info-item"><i class="iconfont icon-id"></i> 异想体名称：<strong>{{ currentAbnormality.name }}</strong></span>
              <span class="info-item"><i class="iconfont icon-security"></i> 危险等级：
                <el-tag :type="getLevelType(currentAbnormality.level)" effect="dark" class="clearance-tag">
                  {{ currentAbnormality.level }}
                </el-tag>
              </span>
            </div>
            <div class="info-row">
              <span class="info-item"><i class="iconfont icon-location"></i> 所在设施：<strong>{{ currentAbnormality.facilityId }}</strong></span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h3 class="section-title"><i class="iconfont icon-character"></i> 特性描述</h3>
          <div class="info-card">
            <div class="info-value">{{ currentAbnormality.description }}</div>
          </div>
        </div>

        <div class="detail-section">
          <h3 class="section-title"><i class="iconfont icon-measure"></i> 管理措施</h3>
          <div class="info-card">
            <div class="info-value">{{ currentAbnormality.manageMethod }}</div>
          </div>
        </div>

        <div class="detail-section">
          <h3 class="section-title"><i class="iconfont icon-remark"></i> 备注</h3>
          <div class="info-card">
            <div class="info-value">{{ currentAbnormality.notes }}</div>
          </div>
        </div>

        <div class="detail-section">
          <h3 class="section-title"><i class="iconfont icon-security-info"></i> 安全信息</h3>
          <div class="security-info">
            <div class="info-card">
              <div class="info-label"><i class="iconfont icon-clearance"></i> 访问权限：</div>
              <div class="info-value">{{currentAbnormality.level}}</div>
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
  </el-card>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import {getAbnormalityList, getAbnormalityById, addAbnormality, deleteAbnormality, updateAbnormality,findAbnormalityByConditions} from "@/api/abnormality.ts";
import { ElMessage, ElMessageBox, type UploadProps} from 'element-plus';
import type { FormInstance } from 'element-plus';
// 表格数据表单
const tableData = ref<abnormality[]>([

]);

//页面数据
const pageNum = ref(1)
const pageSize = ref(5)
const total = ref(0)
const creatDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const dialogTitle = ref('新建异想体')
const currentAbnormality = ref({
  id: 0,
  name: '',
  level: 0,
  description: '',
  manageMethod: '',
  notes: '',
  facilityId: 0,
  imageName: "",
})
// 定义表单引用，指定类型为 FormInstance | null
const abnormalityFormRef = ref<FormInstance | null>(null);


// 筛选条件表单
const filterForm = ref({
  id: null,
  name: '',
  level: null,
  pageNum: 1,
  pageSize: 10
  // description: '',
  // manageMethod: '',
  // notes: '',
  // facilityId: 0,
  // imageName: "",
})

// 定义异想体
interface abnormality {
  id: number;
  name: string;
  level: number;
  description: string;
  manageMethod: string;
  notes: string;
  facilityId: number;
  imageName: string;
}

// 新异想体表单
const newAbnormalityForm  = ref({
  // id: 0,
  name: '',
  level: 0,
  description: '',
  manageMethod: '',
  notes: '',
  facilityId: 0,
  imgeName: "",
})

// 异想体表单规则
const rules = {
  name: [{ required: true, message: '请输入异想体名称', trigger: 'blur' }],
  level: [{ required: true, message: '请选择危险等级', trigger: 'change' }],
  description: [{ required: true, message: '请输入特性描述', trigger: 'blur' }],
  managementMethod: [{ required: true, message: '请输入管理措施', trigger: 'blur' }],
}

// 获取数据 刷新
const catchData = () =>{
  ElMessage.info('正在获取数据')
  getAbnormalityList(pageNum.value,pageSize.value).then((res)=>{
    console.log(res)
    if(res.code===200){
      tableData.value =  res.data.list
      total.value = res.data.total
      ElMessage.success('更新成功')
    }
  }).catch((err)=>{
    console.log(err)
    ElMessage.error(err.msg)
  })
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

const resetFilter = () => {
  filterForm.value = {
    id: null,
    name: '',
    level: null,
    pageNum: 1,
    pageSize: 10
    // description: '',
    // manageMethod: '',
    // notes: '',
    // facilityId: 0,
    // imageName: "",
  }
  catchData()
}

const showCreateDialog = () => {
  creatDialogVisible.value = true
}

const editAbnormality = (item :any) => {
  dialogTitle.value = '编辑异想体'
  newAbnormalityForm.value = { ...item }
  creatDialogVisible.value = true
}

const viewDetail = (item:any) => {
  currentAbnormality.value = { ...item }
  detailDialogVisible.value = true
}

const handleDeleteAbnormality = (item:any) => {
  ElMessageBox.confirm(`确定要删除异想体 ${item.name} 吗?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    center: true,
    customClass: 'delete-confirm-box'
  }).then(() => {
    deleteAbnormality(item.id).then((res)=>{
      if(res.code === 200){
        ElMessage.success('删除成功')
        catchData()
      }else{
        ElMessage.error('发生错误'+res.msg)
      }
    }).catch(() => {
      ElMessage.info('已取消删除')
    })
})}

const submitAbnormality = async() => {
  if (!abnormalityFormRef.value) return;

  // 先验证表单
  abnormalityFormRef.value.validate(async (valid: boolean) => {
    if (!valid) {
      ElMessage.warning('请填写完整信息');
      return;
    }
    console.log('提交参数:', newAbnormalityForm.value);
    try {
      // 提交数据
      const res = await addAbnormality(newAbnormalityForm.value);

      if (res.code === 200) {
        ElMessage.success('添加成功');
        catchData(); // 刷新数据
        creatDialogVisible.value = false;

        // // 重置表单，避免下次打开时保留旧数据
        // abnormalityFormRef.value.resetFields();
      } else {
        ElMessage.error('添加失败: ' + res.msg);
      }
    } catch (error) {
      ElMessage.error('服务器错误: ' + (error as any).msg || '未知错误');
    }
  });
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  console.log(val)
  // catchData()
  const hasSearch = !!filterForm.value.id || !!filterForm.value.name || filterForm.value.level !== null;
  hasSearch ? handleSearch() : catchData();
}

const handleCurrentChange = (val: number) => {
  console.log(val)
  pageNum.value = val
  // catchData()
  const hasSearch = !!filterForm.value.id || !!filterForm.value.name || filterForm.value.level !== null;
  hasSearch ? handleSearch() : catchData();
}

const getClearLevel = (value: number) => {
  switch (value) {
    case 0: return '无效化';
    case 1: return '安全';
    case 2: return '未解明';
    case 3: return '灭世';
    case 4: return '机密';

  }
}

// 初始化
onMounted(() => {
  catchData()
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
    newAbnormalityForm.value.imgeName = response.data
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

// 级别tag
const getLevelType = (value:number) => {
  switch (value) {
    case 0: return 'primary'
    case 1: return 'success'
    case 2: return 'warning'
    case 3: return 'error'
    case 4: return 'error'
  }
}

// 搜索
const handleSearch = () => {
  console.log('搜索表单', filterForm.value);
  filterForm.value.pageNum = pageNum.value;
  filterForm.value.pageSize = pageSize.value;
  findAbnormalityByConditions(filterForm.value).then((response) => {
    if (response.code === 200) {
      tableData.value = response.data.list;
      total.value = response.data.total;
      ElMessage.success('搜索成功');
    } else {
      ElMessage.error('搜索出错' + response.msg);
    }
  }).catch((e) => {
    console.log('error', e);
    ElMessage.error('服务器错误', e.msg);
  });
}

</script>

<style scoped>
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

/* 修改表格单元格文字颜色为深色 */
.containment-table :deep(td) {
  background: transparent;
  color: #333; /* 修改为深色 */
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
  color: black;
  font-size: 24px;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
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
.abnormality-image {
  width: 80px;
  height: 80px;
}
.abnormality-name2 {
  color: #ffffff; /* 修改为你想要的颜色，例如红色 */
  font-size: 24px;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
  border-bottom: 2px solid #4a6fb3;
  padding-bottom: 10px;
}

</style>