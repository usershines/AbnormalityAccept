<template>
  <el-card style="margin-top: -20px">
    <!-- 搜索表单区域 - 暗色主题 -->
    <template #header>
      <el-form
          :model="searchForm"
          inline
          style="display: flex;margin-bottom: -20px"
      >
        <el-form-item label="姓名" class="search-item">
          <el-input
              v-model="searchForm.username"
              clearable
              class="search-input"
              prefix-icon="el-icon-user"
          ></el-input>
        </el-form-item>

        <el-form-item label="权限等级" class="search-item">
          <el-select
              v-model="searchForm.level"
              clearable
              class="search-select"
              style="width: 80px"
          >
            <el-option label="O5议会" value="5"></el-option>
            <el-option label="A级" value="4"></el-option>
            <el-option label="B级" value="3"></el-option>
            <el-option label="C级" value="2"></el-option>
            <el-option label="D级" value="1"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="所在设施ID" class="search-item">
          <el-input
              v-model="searchForm.facilityId"
              clearable
              class="search-input"
              prefix-icon="el-icon-location-outline"
          ></el-input>
        </el-form-item>

        <el-form-item label="ID" class="search-item">
          <el-input
              v-model="searchForm.id"
              clearable
              class="search-input"
              prefix-icon="el-icon-key"
          ></el-input>
        </el-form-item>

        <el-form-item style="margin-left: auto;margin-right: 10px">
          <div style="display: flex;">
            <el-button
                type="primary"
                @click="handleSearch"
                class="search-button"
            >
              <i class="iconfont icon-search"></i> 搜索
            </el-button>
            <el-button
                @click="handleReset"
                class="reset-button"
            >
              <i class="iconfont icon-reset"></i> 重置
            </el-button>
            <!-- 添加新建用户按钮 -->
            <el-button
                type="success"
                @click="handleCreate"
                class="create-button"
            >
              <i class="iconfont icon-add"></i> 新建用户
            </el-button>
          </div>

        </el-form-item>
      </el-form>
    </template>

    <!-- 数据表格区域 - 收容单元风格 -->
    <template #default>
      <el-table
          :data="tableData"
          border
          style="width: 100%;margin-top: -10px"
          max-height="600px"
          class="containment-table"
          :header-cell-style="tableHeaderStyle"
          stripe
      >
        <el-table-column prop="id" label="ID" width="50">
          <template #header>
            <span><i class="iconfont icon-id"></i> ID</span>
          </template>
        </el-table-column>

        <el-table-column prop="username" label="名称" width="150">
          <template #default="scope">
            <span class="user-name">{{ scope.row.username }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="level" label="权限等级" width="120">
          <template #header>
            <span><i class="iconfont icon-security"></i> 权限等级</span>
          </template>
          <template #default="scope">
            <el-tag :type="getLevelTagType(scope.row.level)" class="clearance-tag">
              {{ scope.row.level }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="120">
          <template #header>
            <span><i class="iconfont icon-status"></i> 状态</span>
          </template>
          <template #default="scope">
            <div class="switch-container">
              <el-switch
                  v-if="isDataReady"
                  v-model="scope.row.isActive"
                  style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
                  :active-value="1"
                  :inactive-value="0"
                  @change="handleStatusChange(scope.row)"
              />
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="location" label="所在地" width="150">
          <template #header>
            <span><i class="iconfont icon-location"></i> 所在地</span>
          </template>
        </el-table-column>

        <el-table-column prop="superior" label="上级" width="150">
          <template #header>
            <span><i class="iconfont icon-superior"></i> 上级</span>
          </template>
        </el-table-column>

        <el-table-column prop="description" label="简介" min-width="200" show-overflow-tooltip>
          <template #header>
            <span><i class="iconfont icon-description"></i> 简介</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="150" fixed="right">
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
          </template>
        </el-table-column>
      </el-table>
    </template>

    <!-- 分页组件区域 -->
    <template #footer>
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 30]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total=total
          prev-text="上一页"
          next-text="下一页"
          class="containment-pagination"
      >
      </el-pagination>
    </template>
  </el-card>

  <!-- 新建用户弹窗 -->
  <el-dialog
      v-model="createDialogVisible"
      title="新建用户"
      width="40%"
      class="containment-dialog"
  >
    <el-form
        :model="createForm"
        label-width="120px"
        label-position="left"
        :rules="createRules"
    >
      <el-form-item label="姓名" prop="username">
        <el-input v-model="createForm.username" placeholder="请输入姓名"></el-input>
      </el-form-item>

      <el-form-item label="权限等级" prop="level">
        <el-select v-model="createForm.level" placeholder="请选择权限等级">
          <el-option label="O5议会" value="5"></el-option>
          <el-option label="A级" value="4"></el-option>
          <el-option label="B级" value="3"></el-option>
          <el-option label="C级" value="2"></el-option>
          <el-option label="D级" value="1"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="邮箱" prop="email">
        <el-input v-model="createForm.email" placeholder="请输入邮箱"></el-input>
      </el-form-item>

      <el-form-item label="所在地" prop="location">
        <el-input v-model="createForm.facilityName" placeholder="请输入所在地"></el-input>
      </el-form-item>

      <el-form-item label="上级" prop="superior">
        <el-input v-model="createForm.leaderName" placeholder="请输入上级"></el-input>
      </el-form-item>

      <el-form-item label="简介" prop="description">
        <el-input
            v-model="createForm.introduction"
            type="textarea"
            :rows="3"
            placeholder="请输入简介"
        ></el-input>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="createDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitCreate">确定</el-button>
    </template>
  </el-dialog>

  <!-- 编辑用户信息 -->
  <el-dialog
      v-model="editFormVisable"
      title="编辑用户"
      width="40%"
      class="containment-dialog"
  >
    <el-form
        :model="editForm"
        label-width="120px"
        label-position="left"
        :rules="createRules"
    >
      <el-form-item label="权限等级" prop="level">
        <el-select v-model="editForm.level" placeholder="请选择权限等级">
          <el-option label="O5议会" value="5"></el-option>
          <el-option label="A级" value="4"></el-option>
          <el-option label="B级" value="3"></el-option>
          <el-option label="C级" value="2"></el-option>
          <el-option label="D级" value="1"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="上级" prop="superior">
        <el-input v-model="editForm.leaderName" placeholder="请输入上级"></el-input>
      </el-form-item>

    </el-form>

    <template #footer>
      <el-button @click="editFormVisable = false">取消</el-button>
      <el-button type="primary" @click="submitEdit">确定</el-button>
    </template>
  </el-dialog>

  <el-dialog
      v-model="detailDialogVisible"
      :title="`${selectedUser?.username} - 人员档案`"
      width="50%"
      class="containment-dialog"
  >
    <div v-if="selectedUser" class="user-detail">
      <div class="security-stamp">
        <div class="stamp-content">
          <div class="stamp-title">SCP FOUNDATION</div>
          <div class="stamp-level">机密等级: {{ selectedUser.level }}</div>
        </div>
      </div>

      <div class="detail-header">
        <div class="header-info">
          <h2 class="user-name">{{ selectedUser.username }}</h2>
          <div class="info-row">
            <span class="info-item"><i class="iconfont icon-id"></i> ID：<strong>{{ selectedUser.id }}</strong></span>
            <span class="info-item"><i class="iconfont icon-security"></i> 权限等级：
              <el-tag :type="getLevelTagType(selectedUser.level)" class="clearance-tag">
                {{ selectedUser.level }}
              </el-tag>
            </span>
          </div>
          <div class="info-row">
            <span class="info-item"><i class="iconfont icon-status"></i> 状态：
              <el-tag :type="selectedUser.status ? 'success' : 'danger'" class="status-tag">
                {{ selectedUser.status ? '启用' : '停用' }}
              </el-tag>
            </span>
            <span class="info-item"><i class="iconfont icon-location"></i> 所在地：<strong>{{ selectedUser.location }}</strong></span>
          </div>
          <div class="info-row">
            <span class="info-item"><i class="iconfont icon-superior"></i> 上级：<strong>{{ selectedUser.superior }}</strong></span>
          </div>
          <div class="info-row">
            <span class="info-item"><i class="iconfont icon-email"></i> 邮箱：<strong>{{ selectedUser.email }}</strong></span>
          </div>
          <div class="info-row description">
            <span><i class="iconfont icon-description"></i> 简介：<span class="description-text">{{ selectedUser.description }}</span></span>
          </div>
        </div>
      </div>

      <div class="detail-section">
        <h3 class="section-title"><i class="iconfont icon-security-level"></i> 安全信息</h3>
        <div class="security-info">
          <div class="info-card">
            <div class="info-label"><i class="iconfont icon-clearance"></i> 安全许可：</div>
            <div class="info-value">{{ getSecurityClearance(selectedUser.level) }}</div>
          </div>
          <div class="info-card">
            <div class="info-label"><i class="iconfont icon-access"></i> 访问权限：</div>
            <div class="info-value">{{ getAccessLevel(selectedUser.level) }}</div>
          </div>
          <div class="info-card">
            <div class="info-label"><i class="iconfont icon-login"></i> 最后登录：</div>
            <div class="info-value">2023-08-15 14:32:18</div>
          </div>
          <div class="info-card">
            <div class="info-label"><i class="iconfont icon-date"></i> 注册日期：</div>
            <div class="info-value">2022-05-10</div>
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
</template>

<script setup lang="ts">
import {reactive, ref, computed, onMounted} from 'vue';
import * as UserApi from "@/api/user.ts";
import type {UserParamsRequest,EditSubordinateRequest,User} from "@/api/user.ts";
import {ElMessage,ElMessageBox, FormInstance, type FormRules} from 'element-plus';
import * as equipmentApi from "@/api/equipment.ts";

// 新建用户弹窗相关
const createDialogVisible = ref(false);
const createForm = ref<User>({
  id: null,
  username: null,
  password: null,
  email: null,
  inviterId:  null,
  inviterName: null,
  leaderId: null,
  leaderName: null,
  facilityId: null,
  facilityName: null,
  introduction: null,
  level: null,
});

// 新建用户表单验证规则
const createRules = reactive<FormRules>({
  username: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  level: [{ required: true, message: '请选择权限等级', trigger: 'change' }],
  email: [
    { required: false, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: ['blur', 'change'] }
  ],
});

// 打开新建用户弹窗
const handleCreate = () => {
  createDialogVisible.value = true;
};

const submitCreate = () => {
  UserApi.addUser(createForm.value).then((response) => {
    if (response.code === 200) {
      console.log("新建用户成功:", createForm.value.username);
      ElMessage.success('新建用户成功');
      createDialogVisible.value = false;
      catchData()
      // 重置表单
      createForm.value={
        id: null,
        username: null,
        password: null,
        email: null,
        inviterId:  null,
        inviterName: null,
        leaderId: null,
        leaderName: null,
        facilityId: null,
        facilityName: null,
        introduction: null,
        level: null,
      }
    }
  }).catch((error) => {
    console.log('服务器错误',error);
    ElMessage.error('服务器错误')
  })
  }

// 搜索表单数据
const searchForm = ref<UserParamsRequest>({
  id: null,
  username: null,
  password: null,
  email: null,
  level: null,
  teamId: null,
  inviterId: null,
  inviterName: null,
  leaderId: null,
  leaderName: null,
  facilityId: null,
  facilityName: null,
  introduction: null,
  isActive: 1,

  // 等级范围
  minLevel: null,
  maxLevel: null,

  pageNum: null,
  pageSize: null,

});

// 原始表格数据（静态）
const tableData = ref([]);


// 分页相关数据
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 获取数据
const catchData = () => {
  UserApi.getUserList(currentPage.value, pageSize.value).then((response) => {
    if(response.code === 200) {
      tableData.value = response.data.list;
      total.value = response.data.total;
      ElMessage.success('数据更新成功')
      console.log('获取数据',response)
    }else{
      ElMessage.error('发生错误：',response.message);
    }
  }).catch((e) => {
        console.log('错误',e)
        ElMessage.error('查询数据错误：'+e.msg);
      }
  )
}

// 详情弹窗相关
const detailDialogVisible = ref(false);
const selectedUser = ref<any>(null);

// 实现搜索过滤功能
const handleSearch = () => {
  console.log('搜索表单',searchForm.value);
  searchForm.value.pageNum = currentPage.value;
  searchForm.value.pageSize = pageSize.value;
  UserApi.findUser(searchForm.value).then((response) => {
    if(response.code === 200) {
      tableData.value = response.data.list;
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

// 获取权限等级对应的标签类型
const getLevelTagType = (level: number) => {
  const types: Record<number, string> = {
    5: 'danger',
    4: 'warning',
    3: 'primary',
    2: 'success',
    1: 'info'
  };
  return types[level] || '';
};

// 根据权限等级获取安全许可
const getSecurityClearance = (level: number) => {
  const clearance: Record<number, string> = {
    5: '最高机密 (Top Secret)',
    4: '机密 (Secret)',
    3: '受限 (Restricted)',
    2: '内部 (Internal)',
    1: '公开 (Public)'
  };
  return clearance[level] || '未定义';
};

// 根据权限等级获取访问权限
const getAccessLevel = (level: number) => {
  const access: Record<number, string> = {
    5: '所有区域',
    4: 'Keter级及以下',
    2: 'Euclid级及以下',
    3: 'Safe级',
    1: '仅指定区域'
  };
  return access[level] || '未定义';
};

// 表格头部样式
const tableHeaderStyle = () => {
  return {
    background: 'linear-gradient(to bottom, #1a2a4a, #0c1a33)',
    color: '#c0d1f2',
    fontWeight: 'bold',
    borderBottom: '1px solid #304878'
  };
};

// 重置方法
const handleReset = () => {
  searchForm.value.username = null;
  searchForm.value.level = null;
  searchForm.value.facilityId = null;
  searchForm.value.id = null;
  catchData()
};

// 每页条数改变
const handleSizeChange = (val: number) => {
  pageSize.value = val;
  handleSearch()
};

// 当前页改变
const handleCurrentChange = (val: number) => {
  currentPage.value = val;
  handleSearch()
};

// 查看详情方法
const handleDetail = (row: any) => {
  selectedUser.value = row;
  detailDialogVisible.value = true;
};

// 编辑表单相关信息
const editFormVisable = ref(false);
const editForm = ref({
  id: 0,
  level: 0,
  leaderName: '',
})

// 编辑方法
const handleEdit = (row: User) => {
  if(row.level !== null)   editForm.value.level = row.level
  if(row.id !== null)   editForm.value.id = row.id ;
  if(row.leaderName !== null) editForm.value.leaderName = row.leaderName;

  editFormVisable.value = true
};
const submitEdit = () => {
  console.log('提交编辑表单',editForm.value);
  const editData: EditSubordinateRequest = {
    subordinateId : 0,
    level: editForm.value.level,
    leaderName: editForm.value.leaderName,

  }
  UserApi.editSubordinate(editForm.value.id,editData)/*.then((response) => {
    if(response.code === 200) {
      tableData.value = response.data.list;
      ElMessage.success('数据更新成功')
      console.log('获取数据',response)
    }
  }).catch((e) => {
        console.log('错误',e)
        ElMessage.error(e.msg);
      }
  )*/
}

// 添加删除方法
const handleStatusChange= (row: any) => {
  ElMessageBox.confirm(
      '是否确认停用id为' + row.id + '的用户' + row.username,
      '警告',
      {
        confirmButtonText: '停用',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
      .then(async () => {
        const index = tableData.value.findIndex(item => item.id === row.id);
        if (index !== -1) {
          const result = await UserApi.deleteUser(row.id,row.isActive);
          if (result.code === 200) {
            ElMessage.warning(`已停用用户: ${row.name}`);
            catchData();
          } else {
            ElMessage.error(`停用失败：${result.msg}`)
          }
        }
      }).catch(() => {
    ElMessage.info('已取消停用');
  });
  };


//         }
//         UserApi.deleteUser(row.id).then((response) => {
//           if(response.code === 200) {
//             ElMessage({
//               type: 'success',
//               message: '删除成功',
//             })
//           }
//           else {
//             ElMessage.error('删除失败'+response.message);
//           }
//         }).catch((e) => {
//           ElMessage.error('删除失败'+e.message);
//         })
//       })
//       .catch(() => {
//         ElMessage({
//           type: 'info',
//           message: '取消删除',
//         })
//       })
// };
const isDataReady=ref(false)
// 生命周期钩子
onMounted(async () =>{
  // 初始化表单
 await catchData();
 isDataReady.value = true;

})
</script>

<style scoped>

/* 添加新建按钮样式 */
.create-button {
  background: linear-gradient(to right, #4aaf7d, #3a8c5f);
  border: none;
  color: #fff;
  font-weight: bold;
  transition: all 0.3s ease;
  margin-left: 30px;
}

.create-button:hover {
  background: linear-gradient(to right, #5abf8d, #4a9c6f);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(74, 175, 125, 0.4);
}


/* 搜索区域样式 */
.search-form {
  background: linear-gradient(135deg, #1a2a4a, #0c1a33);
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  border: 1px solid #304878;
}


.search-input, .search-select {
  background-color: rgba(255, 255, 255, 0.1);
  border: 1px solid #304878;
  color: #e0f0ff;
  border-radius: 4px;
  width: 100px;
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

/* 表格样式 */
.containment-table {
  border: 1px solid #304878;
  border-radius: 8px;
  overflow: hidden;
}

.containment-table :deep(th) {
  background: linear-gradient(to bottom, #1a2a4a, #0c1a33) !important;
  border-bottom: 1px solid #304878;
  font-size: 15px;
}

.containment-table :deep(td) {
  background: transparent;
  border-bottom: 1px solid #304878;
  font-size: 14px;
}

.containment-table :deep(.el-table__row:hover td) {
  background-color: rgba(74, 111, 179, 0.1) !important;
}

.containment-table :deep(.el-table__body tr:hover>td) {
  background-color: rgba(74, 111, 179, 0.1) !important;
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
.user-detail {
  position: relative;
  background: rgba(15, 30, 61, 0.85);
  padding: 25px;
  border-radius: 6px;
  border: 1px solid #304878;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}

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
.user-detail {
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

.user-name {
  margin-top: 0;
  margin-bottom: 15px;
  font-size: 17px;
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

.description {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px dashed #4a6fb3;
  color: #c0d1f2;
  width: 100%;
}

.description-text {
  color: #e0f0ff;
  line-height: 1.6;
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

.security-info {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.info-card {
  background: rgba(10, 20, 41, 0.5);
  border: 1px solid #304878;
  border-radius: 8px;
  padding: 15px;
  transition: all 0.3s ease;
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
</style>