<script setup lang="ts">
import { reactive, onMounted, ref,toRaw} from 'vue'
import { useRouter } from 'vue-router' // 引入路由
import { login} from "@/api/user.ts";
import { ElMessage } from 'element-plus';
// 表单响应式数据
const form = reactive({
  name: "",
  password: "",
})
// 获取路由实例
const router = useRouter()
// 登录状态
const isLoading = ref(false)

// 登录事件处理
const onSubmit = () => {
  isLoading.value = true;
  ElMessage.success("系统认证中...")

  // 在控制台打印提交的表单数据
  console.log("提交的表单数据:", toRaw(form))
  login(toRaw(form)).then((res)=>{
    if(res.code === 200){
      localStorage.setItem('token',res.data.token);
      localStorage.setItem('username',res.data.name);
      ElMessage.success("认证成功! 正在进入系统...")
      router.push("/start")
    }
    console.log(res)
  }).catch((err)=>{
    ElMessage.warning("认证失败! 请检查您的身份凭证")
    console.log(err)
  })

}
// 粒子数据
const particles = ref<Array<{x: number, y: number, size: number, delay: number}>>([])

// 初始化粒子
const initParticles = () => {
  const count = 50;
  const newParticles = [];

  for (let i = 0; i < count; i++) {
    newParticles.push({
      x: Math.random() * 100,
      y: Math.random() * 100,
      size: Math.random() * 6 + 2,
      delay: Math.random() * 3
    });
  }

  particles.value = newParticles;
}

onMounted(() => {
  initParticles();

  // 添加窗口大小变化监听
  window.addEventListener('resize', initParticles);
});
</script>

<template>
  <div class="login-container">
    <!-- Vue实现的粒子背景 -->
    <div class="particles-container">
      <div
        v-for="(particle, index) in particles"
        :key="index"
        class="particle"
        :style="{
        left: `${particle.x}%`,
        top: `${particle.y}%`,
        width: `${particle.size}px`,
        height: `${particle.size}px`,
        animationDelay: `${particle.delay}s`
      }"
      ></div>
    </div>

    <!-- 扫描线 -->
    <div class="scan-line"></div>

    <div class="login-card">
      <div class="system-title">
        <h1>异想体收容管理系统</h1>
        <div class="divider"></div>
      </div>

      <el-form :model="form" label-width="120px" label-position="top" size="large">
        <h2 class="login-title">安全访问协议</h2>

        <el-form-item label="身份识别码">
          <el-input
            v-model="form.name"
            placeholder="请输入SCP识别码"
            class="scp-input"
          >
            <template #prefix>
              <svg class="icon" viewBox="0 0 24 24">
                <path d="M12 12a5 5 0 1 0 0-10 5 5 0 0 0 0 10zm0 2c-4.42 0-8 3.58-8 8h16c0-4.42-3.58-8-8-8z"/>
              </svg>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="生物密钥">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入生物密钥"
            @keyup.enter="onSubmit"
            show-password
            class="scp-input"
          >
            <template #prefix>
              <svg class="icon" viewBox="0 0 24 24">
                <path d="M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 1.39-3.1 3.1-3.1 1.71 0 3.1 1.39 3.1 3.1v2z"/>
              </svg>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            @click="onSubmit"
            class="access-btn"
          >
            确认访问
          </el-button>
        </el-form-item>
      </el-form>

      <div class="security-footer">
        <div class="warning-text">
          <svg class="icon warning-icon" viewBox="0 0 24 24">
            <path d="M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z"/>
          </svg>
          警告：未经授权的访问将被记录并上报
        </div>
      </div>
    </div>

    <!-- 底部扫描线 -->
    <div class="bottom-scan"></div>
  </div>
</template>

<style scoped>
.login-container {
  position: relative;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #0c0e2a 0%, #1a1f4b 100%);
  overflow: hidden;
  font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
}

/* 粒子容器 */
.particles-container {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  z-index: 0;
}

.particle {
  position: absolute;
  background: #5d8bf4;
  border-radius: 50%;
  box-shadow: 0 0 10px 2px rgba(93, 139, 244, 0.8);
  opacity: 0.7;
  transform: translate(-50%, -50%);
  animation: pulse 3s infinite alternate;
}

@keyframes pulse {
  0% { opacity: 0.3; transform: translate(-50%, -50%) scale(1); }
  100% { opacity: 0.8; transform: translate(-50%, -50%) scale(1.5); }
}

.scan-line {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, transparent, rgba(93, 139, 244, 0.8), transparent);
  animation: scan 5s linear infinite;
  box-shadow: 0 0 10px rgba(93, 139, 244, 0.7);
  z-index: 1;
}

@keyframes scan {
  0% { top: 0; }
  100% { top: 100%; }
}

.bottom-scan {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background: linear-gradient(90deg, transparent, #ff6b6b, transparent);
  box-shadow: 0 0 15px rgba(255, 107, 107, 0.7);
  animation: scan-horizontal 10s linear infinite;
  z-index: 1;
}

@keyframes scan-horizontal {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

.login-card {
  position: relative;
  width: 380px;
  background: rgba(13, 18, 48, 0.8);
  padding: 40px 30px;
  border-radius: 12px;
  box-shadow:
    0 0 25px rgba(93, 139, 244, 0.5),
    inset 0 0 15px rgba(93, 139, 244, 0.2);
  border: 1px solid rgba(93, 139, 244, 0.3);
  z-index: 1;
  backdrop-filter: blur(5px);
}

.system-title {
  text-align: center;
  margin-bottom: 30px;
}

.system-title h1 {
  color: #e0e7ff;
  font-size: 22px;
  font-weight: 600;
  letter-spacing: 1px;
  text-shadow: 0 0 10px rgba(93, 139, 244, 0.7);
}

.divider {
  height: 2px;
  background: linear-gradient(90deg, transparent, #5d8bf4, transparent);
  margin: 15px 0;
}

.login-title {
  color: #5d8bf4;
  text-align: center;
  font-size: 24px;
  margin-bottom: 25px;
  text-shadow: 0 0 8px rgba(93, 139, 244, 0.6);
}

:deep(.scp-input .el-input__wrapper) {
  background: rgba(20, 25, 60, 0.6);
  box-shadow:
    0 0 0 1px rgba(93, 139, 244, 0.4) inset,
    0 0 8px rgba(93, 139, 244, 0.3);
  border-radius: 6px;
}

:deep(.scp-input .el-input__inner) {
  color: #aab6ff;
}

:deep(.scp-input .el-input__wrapper:hover) {
  box-shadow:
    0 0 0 1px rgba(93, 139, 244, 0.7) inset,
    0 0 12px rgba(93, 139, 244, 0.5);
}

:deep(.scp-input .el-input__prefix) {
  color: #5d8bf4;
  margin-right: 10px;
  display: flex;
  align-items: center;
}

.access-btn {
  width: 100%;
  background: linear-gradient(90deg, #3a5bcf, #5d8bf4);
  border: none;
  border-radius: 6px;
  font-weight: 600;
  letter-spacing: 1px;
  margin-top: 20px;
  transition: all 0.3s;
  box-shadow: 0 0 15px rgba(93, 139, 244, 0.4);
}

.access-btn:hover {
  transform: translateY(-2px);
  box-shadow:
    0 5px 20px rgba(93, 139, 244, 0.6),
    0 0 15px rgba(93, 139, 244, 0.4);
}

.security-footer {
  margin-top: 25px;
  text-align: center;
}

.warning-text {
  color: #ff6b6b;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}

.icon {
  width: 16px;
  height: 16px;
  fill: currentColor;
}

.warning-icon {
  fill: #ff6b6b;
}
</style>