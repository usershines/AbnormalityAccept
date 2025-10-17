<template>
  <el-main class="page-container personal-page">
    <!-- 摄像头弹窗 -->
    <el-dialog
        title="摄像头截图"
        v-model="dialogVisible"
        :width="dialogWidth"
        :before-close="handleDialogClose"
        destroy-on-close
    >
      <div class="camera-content">
        <!-- 摄像头视频流 -->
        <video
            ref="videoRef"
            class="camera-video"
            autoplay
            playsinline
            v-if="!showCapturedImage"
        ></video>

        <!-- 截取的图片 -->
        <div class="captured-image-container" v-else>
          <img
              :src="capturedImageUrl"
              alt="截取的图片"
              class="captured-image"
          >
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>

          <!-- 截图按钮 -->
          <el-button
              type="primary"
              @click="captureImage"
              :disabled="!isCameraActive || showCapturedImage"
              v-if="!showCapturedImage"
          >
            <el-icon><camera /></el-icon>
            截取画面
          </el-button>

          <!-- 重新拍摄按钮 -->
          <el-button
              type="warning"
              @click="resetCapture"
              v-if="showCapturedImage"
          >
            <el-icon><refresh-left /></el-icon>
            重新拍摄
          </el-button>

          <!-- 上传按钮 -->
          <el-button
              type="success"
              @click="handleUpload"
              v-if="showCapturedImage && !isUploading"
          >
            <el-icon v-if="!isUploading"><upload /></el-icon>
            <el-icon v-if="isUploading"><loading /></el-icon>
            {{ isUploading ? '上传中...' : '上传图片' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

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
              :icon="Edit"
              class="edit-btn"
              @click="dialogFormVisible = true"
            >
              编辑
            </el-button>
            <el-button
              :icon="Key"
              class="edit-btn"
              @click="dialogPasswordVisible = true"
            >
              修改密码
            </el-button>
            <!--- 打开摄像头 -->
            <el-button type="primary" @click="dialogVisible = true" class="access-btn" >
              <el-icon><camera-filled /></el-icon>
              注册人脸登录
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
          <span class="label">上级ID：</span>
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
import {ref, reactive, shallowRef, onMounted, onUnmounted, watch} from 'vue';
import { ElMessage, type FormInstance, type FormRules } from 'element-plus';
import * as userApi from '@/api/user.ts';
import {Camera, CameraFilled, Edit, Key, RefreshLeft, Upload} from "@element-plus/icons-vue";
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
  editUser.value = {...user.value};
}

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

// 弹窗状态
const dialogVisible = ref(false);
const dialogWidth = ref('800px');

// 摄像头相关
const videoRef = ref(null);
const stream = ref(null);
const isCameraActive = ref(false);
const canvas = ref(document.createElement('canvas'));

// 截图相关
const capturedImageUrl = ref('');
const showCapturedImage = ref(false);

// 上传相关
const isUploading = ref(false);

// 打开摄像头
const startCamera = async () => {
  try {
    // 请求摄像头权限
    stream.value = await navigator.mediaDevices.getUserMedia({
      video: {
        width: { ideal: 1280 },
        height: { ideal: 720 },
        facingMode: 'user' // 使用前置摄像头
      }
    });

    // 将视频流绑定到video元素
    if (videoRef.value) {
      videoRef.value.srcObject = stream.value;
      isCameraActive.value = true;

      // 等待视频加载完成后设置canvas尺寸
      videoRef.value.onloadedmetadata = () => {
        canvas.value.width = videoRef.value.videoWidth;
        canvas.value.height = videoRef.value.videoHeight;
      };
    }
  } catch (error) {
    console.error('摄像头访问失败:', error);
    ElMessage.error('无法访问摄像头，请检查权限设置');
    dialogVisible.value = false;
  }
};

// 关闭摄像头
const stopCamera = () => {
  if (stream.value) {
    stream.value.getTracks().forEach(track => {
      track.stop();
    });
    stream.value = null;
    isCameraActive.value = false;
  }
};

// 截取当前画面
const captureImage = () => {
  if (!videoRef.value || !stream.value) return;

  // 将视频当前帧绘制到canvas
  const ctx = canvas.value.getContext('2d');
  ctx.drawImage(
      videoRef.value,
      0, 0,
      canvas.value.width,
      canvas.value.height
  );

  // 将canvas内容转换为图片URL
  capturedImageUrl.value = canvas.value.toDataURL('image/png');
  showCapturedImage.value = true;

  ElMessage.success('已成功截取画面');
};

// 重置截图，重新拍摄
const resetCapture = () => {
  capturedImageUrl.value = '';
  showCapturedImage.value = false;
  startCamera(); // 重新启动摄像头
};

// 处理图片上传（适配仅接受图片数据的POST接口）
const handleUpload = async () => {
  if (!capturedImageUrl.value) {
    ElMessage.warning('请先截取图片');
    return;
  }

  try {
    isUploading.value = true;

    // 将dataURL转换为Blob对象（图片二进制数据）
    const blob = dataURLToBlob(capturedImageUrl.value);

    // 创建FormData
    const formData = new FormData();
    // 注意：这里的键名（如'image'）需要与后端接口要求的参数名一致
    formData.append('image', blob, 'captured-image.png');

    let userID;
    userID = localStorage.getItem('userid')
    if(!userID) {
      ElMessage.error('请先登录');
      return
    }

    const res = await userApi.faceReg(userID,formData);

    // 4. 处理成功响应
    if (res.code === 200) {
      ElMessage.success('图片上传成功');
      console.log('上传成功响应:', res.data);
      dialogVisible.value = false; // 上传成功后关闭弹窗
    } else {
      ElMessage.error(`上传失败: ${res.msg}`);
    }
  } catch (error) {
    console.error('上传出错:', error);
    if (error.response) {
      ElMessage.error(`上传失败: ${error.response.data?.message || '服务器错误'}`);
    } else if (error.request) {
      ElMessage.error('上传超时，请检查网络');
    } else {
      ElMessage.error('上传过程中发生错误');
    }
  } finally {
    isUploading.value = false;
  }
};

// 辅助函数：将dataURL转换为Blob对象
const dataURLToBlob = (dataUrl:string) => {
  const arr = dataUrl.split(',');
  const mime = arr[0].match(/:(.*?);/)[1];
  const bstr = atob(arr[1]);
  let n = bstr.length;
  const u8arr = new Uint8Array(n);

  while (n--) {
    u8arr[n] = bstr.charCodeAt(n);
  }

  return new Blob([u8arr], { type: mime });
};

// 处理弹窗关闭
const handleDialogClose = () => {
  stopCamera();
  capturedImageUrl.value = '';
  showCapturedImage.value = false;
  dialogVisible.value = false;
};

// 监听弹窗显示状态，控制摄像头开关
watch(dialogVisible, (newVal) => {
  if (newVal) {
    startCamera();
  } else {
    stopCamera();
  }
});

onMounted(()=>{
  initData()
})


onUnmounted(()=>{
  stopCamera();
});
</script>

<style scoped>
.page-container {
  padding: 20px;
  background-color: #ffffff;
  min-height: calc(100vh - 240px);
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

.camera-content {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 10px 0;
}

.camera-video {
  max-width: 100%;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.captured-image-container {
  width: 100%;
  text-align: center;
}

.captured-image {
  max-width: 100%;
  max-height: 500px;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.dialog-footer {
  display: flex;
  gap: 8px;
}
</style>