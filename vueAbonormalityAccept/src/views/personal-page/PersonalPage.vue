<template>
  <el-main class="page-container personal-page">
    <div class="personal-header">
      <!-- 个人头像 -->
      <div class="personal-avatar">
        <img src="https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png" alt="个人头像">
      </div>

      <!-- 核心个人信息（带编辑切换） -->
      <div class="personal-info">
        <div class="info-row">
          <h1 class="personal-name">
            用户名：{{ user.username }}
            <el-button
              icon="Edit"
              class="edit-btn"
              @click="dialogFormVisible = true"
            >
              编辑
            </el-button>
            <el-button
              icon="Key"
              class="edit-btn"
              @click="dialogPasswordVisible = true"
            >
              修改密码
            </el-button>
          </h1>
        </div>

        <!-- 非编辑状态的基础信息 -->
        <p class="personal-id">ID：{{ user.id }}</p>
        <p class="personal-email">邮箱：{{ user.email }}</p>
        <p class="personal-level">等级：{{ user.level }}</p>
      </div>
    </div>

    <!-- 详细信息卡片 -->
    <el-card class="detail-card" :body-style="{ backgroundColor: '#ffffff', border: 'none' }">
      <!-- 非编辑状态的详细信息 -->
      <div class="detail-grid">
        <div class="detail-item">
          <span class="label">团队ID：</span>
          <span class="value">{{ user.teamId || '无' }}</span>
        </div>
        <div class="detail-item">
          <span class="label">邀请人ID：</span>
          <span class="value">{{ user.inviterId || '无' }}</span>
        </div>
        <div class="detail-item">
          <span class="label">负责人ID：</span>
          <span class="value">{{ user.leaderId || '无' }}</span>
        </div>
        <div class="detail-item">
          <span class="label">设施ID：</span>
          <span class="value">{{ user.facilityId || '无' }}</span>
        </div>
      </div>

      <!-- 个人简介（非编辑状态） -->
      <div class="introduction-section">
        <h3 class="section-title">个人简介</h3>
        <p class="introduction-content">{{ user.introduction || '该用户未填写简介' }}</p>
      </div>
    </el-card>

    <!-- 编辑信息弹窗 -->
    <el-dialog v-model="dialogFormVisible" title="编辑个人信息" width="600px">
      <el-form
        ref="editFormRef"
        :model="editUser"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="editUser.username" placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editUser.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item label="个人简介" prop="introduction">
          <el-input
            v-model="editUser.introduction"
            type="textarea"
            :rows="3"
            placeholder="请输入个人简介"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="primary" @click="handleSave">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="dialogPasswordVisible" title="修改密码" width="500px">
      <el-form
        ref="passwordFormRef"
        :model="changePwdForm"
        :rules="pwdRules"
        label-width="100px"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            v-model="changePwdForm.oldPassword"
            type="password"
            placeholder="请输入原密码"
            show-password
          />
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="changePwdForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="changePwdForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogPasswordVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSavePassword">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </el-main>
</template>

<script setup lang="ts">
import { ref, reactive,  shallowRef, onMounted } from 'vue';
import { ElMessage, type FormInstance, type FormRules } from 'element-plus';
import * as userApi from '@/api/user.ts';
import router from '@/router';




interface User {
  id: number;
  username: string;
  password: string; // 仅用于编辑，不直接展示明文
  email: string;
  level: number;
  teamId: number | null;
  inviterId: number | null;
  leaderId: number | null;
  facilityId: number | null;
  introduction: string;
}
// 模拟用户数据（实际应从接口获取）
const user = ref<User>({
  id: 0,
  username: '',
  password: '',
  email: '',
  level: 0,
  teamId: null,
  inviterId: null,
  leaderId: null,
  facilityId: null,
  introduction: ''
});

// 弹窗状态
const dialogFormVisible = ref(false); // 编辑信息弹窗
const dialogPasswordVisible = ref(false); // 修改密码弹窗

// 编辑相关状态
const editFormRef = shallowRef<FormInstance>(); // 表单引用
const passwordFormRef = shallowRef<FormInstance>(); // 密码表单引用
const editUser = ref<User>({ ...user.value }); // 编辑用的临时数据

// 修改密码表单数据
const changePwdForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 表单验证规则
const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在 2-20 之间', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  introduction: [
    { max: 500, message: '简介长度不超过 500 字', trigger: 'blur' }
  ]
};

// 密码验证规则
const pwdRules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      trigger: 'blur'
    }
  ]
};

const initData=async()=>{
  const name=localStorage.getItem('username')
  if(name == null){
    return
  }
  const response = await userApi.findByName(name);
  if (response.code === 200){
    user.value = response.data
  }
}

onMounted(()=>{
  initData()
})



// 取消编辑
const handleCancel = () => {
  dialogFormVisible.value = false;
  editFormRef.value?.resetFields();
};

// 保存修改
const handleSave = async () => {
  // 表单验证
  const valid = await editFormRef.value?.validate();
  if (!valid) return;

  // 模拟API请求保存（实际项目中替换为真实接口）

    // 更新原始用户数据
    const res=await userApi.updateUser(editUser.value)
    console.log(res)
    if(res.code==200){
      ElMessage.success('信息修改成功');
      initData()
      dialogFormVisible.value = false;
    }else{
      ElMessage.error('修改失败，请重试');

    }
    

};

// 保存密码修改
const handleSavePassword = async () => {
  // 表单验证
  const valid = await passwordFormRef.value?.validate();
  if (!valid) return;

  // 验证原密码是否正确（模拟）
  const body = {
    oldPassword: changePwdForm.value.oldPassword,
    newPassword: changePwdForm.value.newPassword,
    confirmPassword: changePwdForm.value.confirmPassword,
  };
  const res=await userApi.updatePassword(body);
  if (res.code === 200) { 
    ElMessage.success("修改密码成功！");
    userApi.logout();
    dialogPasswordVisible.value = false;
    router.push("/");
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    
  }else{
    ElMessage.error(res.msg);
  }
};
</script>

<style scoped>
.page-container {
  padding: 20px;
  background-color: #ffffff;
  min-height: calc(100vh - 60px);
  color: #333333;
}

.personal-header {
  display: flex;
  align-items: flex-start;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #304878;
}

.personal-avatar {
  width: 120px;
  height: 120px;
  border: 3px solid #4a6fb3;
  background-color: #0a1429;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 25px;
  flex-shrink: 0;
}

.personal-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.personal-info .personal-name {
  margin: 0 0 10px 0;
  color: #333333;
  font-size: 28px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.edit-btn {
  display: flex;
  justify-content: center;
  align-items: center;
}

.edit-btn:hover {
  background-color: #f0f0f0;
  border-color: #73a6ff;
}

.personal-info .personal-id,
.personal-info .personal-email,
.personal-info .personal-level {
  margin: 5px 0;
  color: #333333;
  font-size: 16px;
}

.detail-card {
  border: 1px solid #304878;
  background: #ffffff;
  margin-bottom: 20px;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px 20px;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px dashed #304878;
}

.detail-item {
  display: flex;
  align-items: center;
}

.detail-item .label {
  color: #333333;
  width: 80px;
}

.detail-item .value {
  color: #333333;
  flex: 1;
}

.introduction-section .section-title {
  color: #333333;
  font-size: 18px;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
}

.introduction-section .section-title::before {
  content: '';
  display: inline-block;
  width: 4px;
  height: 18px;
  background-color: #4a9cf0;
  margin-right: 8px;
  border-radius: 2px;
}

.introduction-content {
  color: #333333;
  line-height: 1.8;
  padding: 10px;
  background-color: rgba(10, 20, 41, 0.3);
  border-radius: 4px;
}

/* 弹窗底部按钮样式 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>