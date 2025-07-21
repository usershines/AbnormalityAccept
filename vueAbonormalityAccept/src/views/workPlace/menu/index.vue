<template>
  <el-aside width="240px" class="sidebar-container" style="height: 770px">
    <div class="system-header">
      <div class="user-info">
        <el-avatar :size="40" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
        <div class="user-details">
          <span style="color: white;font-size: 20px;font-weight: bold" >Dr.Bright</span>
          <span class="user-level">权限等级: O5</span>
        </div>
      </div>
    </div>

    <el-menu
      :default-active=defaultActive
      class="el-menu-vertical-demo"
      @open="handleOpen"
      @close="handleClose"
      router
      background-color="#0c1427"
      text-color="#a0a8c3"
      active-text-color="#5d8bf4"
    >
      <el-menu-item index="/workPlace/main">
        <el-icon><HomeFilled /></el-icon>
        <span>主页</span>
      </el-menu-item>

      <!-- 用户管理 -->
      <el-menu-item index="/workPlace/user">
        <template #title>
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </template>
      </el-menu-item>

      <!-- 异想体管理 -->
      <el-menu-item index="/workPlace/abnormality">
        <template #title>
          <el-icon><MagicStick /></el-icon>
          <span>异想体管理</span>
        </template>
      </el-menu-item>

      <!-- 机动小队管理 -->
      <el-menu-item index="/workPlace/team">
          <template #title>
            <el-icon><Position /></el-icon>
            <span>机动小队</span>
          </template>
      </el-menu-item>

      <!-- 任务管理 -->
      <el-menu-item index="/workPlace/quest">
        <template #title>
          <el-icon><Tickets /></el-icon>
          <span>任务管理</span>
        </template>
      </el-menu-item>

      <!-- 装备管理 -->
      <el-menu-item index="/workPlace/equipment">
        <template #title>
          <el-icon><SuitcaseLine /></el-icon>
          <span>装备管理</span>
        </template>
      </el-menu-item>
      <!-- 个人主页 -->
      <el-menu-item index="/workPlace/personal">
        <template #title>
          <el-icon><User /></el-icon>
          <span>个人主页</span>
        </template>
      </el-menu-item>
      <!-- 设施管理 -->
      <el-menu-item index="/workPlace/facility">
        <el-icon><OfficeBuilding /></el-icon>
        <span>设施管理</span>
      </el-menu-item>
    </el-menu>

    <div class="system-status">
      <div class="status-item">
        <div class="status-indicator active"></div>
        <span>系统状态: <strong>在线</strong></span>
      </div>
      <div class="status-item">
        <div class="status-indicator"></div>
        <span>收容状态: <strong>稳定</strong></span>
      </div>
      <div class="status-item">
        <div class="status-indicator"></div>
        <span>安保等级: <strong>LEVEL 4</strong></span>
      </div>
    </div>
  </el-aside>
</template>

<script setup lang="ts">
import {
  HomeFilled, User, MagicStick, SuitcaseLine,
  Position, Tickets, OfficeBuilding, Setting, BellFilled
} from '@element-plus/icons-vue'
import {onMounted, ref, watch} from "vue";
import {useRoute} from "vue-router";
import {findByName} from "@/api/user.ts";
import {ElMessage} from "element-plus";

const Me = ref()

// 获取当前路由
const route = useRoute()
const defaultActive = ref('main')
console.log(defaultActive.value)

//路由变化时更新
// 更新 defaultActive 的函数
const updateDefaultActive = () => {
  defaultActive.value = route.path || 'main'
  //console.log(defaultActive.value)
};

onMounted(() => {
  // 路由更新
  updateDefaultActive();
  watch(
      () => route.path,
      () => {
        updateDefaultActive();
      }
  );

  //获取信息
  const myName = localStorage.getItem('username');
  if(!myName) {
    ElMessage.error('请先登录！')
  }else {
    findByName(myName).then((res)=>{
      if(res.code === 200){
        Me.value = res.data;
        ElMessage.success(`用户信息获取成功！欢迎回来 ${Me.value?.username}`)
      }else{
        ElMessage.error(res.msg);
      }
    }).catch((err)=>{
      console.log(err);
      ElMessage.error(`发送错误：${err.msg}`)
    })
  }
})

const handleOpen = (key: string, keyPath: string[]) => {
  console.log('展开菜单:', key, keyPath)
}
const handleClose = (key: string, keyPath: string[]) => {
  console.log('收起菜单:', key, keyPath)
}
</script>

<style scoped>
.sidebar-container {
  background: linear-gradient(135deg, #0c1427 0%, #0d1830 100%);
  border-right: 1px solid rgba(93, 139, 244, 0.2);
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
}

.system-header {
  padding: 20px 15px;
  border-bottom: 1px solid rgba(93, 139, 244, 0.2);
}

.system-logo {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.logo-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #3a5bcf 0%, #5d8bf4 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 12px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 0 15px rgba(93, 139, 244, 0.5);
}

.logo-inner {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  position: relative;
}

.logo-inner::before {
  content: "";
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #5d8bf4;
  box-shadow: 0 0 10px #5d8bf4;
}

.system-logo h1 {
  color: #e0e7ff;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 0.5px;
  text-shadow: 0 0 10px rgba(93, 139, 244, 0.7);
  background: linear-gradient(90deg, #5d8bf4, #aab6ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.user-info {
  display: flex;
  align-items: center;
  padding: 5px;
  background: rgba(20, 25, 60, 0.4);
  border-radius: 8px;
  border: 1px solid rgba(93, 139, 244, 0.3);
}

.user-details {
  margin-left: 12px;
  display: flex;
  flex-direction: column;
}

.user-level {
  color: #5d8bf4;
  font-size: 12px;
  margin-top: 3px;
}

.el-menu {
  border-right: none;
  flex: 1;
}

.el-menu-item, .el-sub-menu {
  transition: all 0.3s;
}

.el-menu-item:hover, .el-sub-menu:hover {
  background: rgba(93, 139, 244, 0.1) !important;
}

.el-menu-item.is-active {
  background: linear-gradient(90deg, rgba(93, 139, 244, 0.2), transparent) !important;
  border-left: 3px solid #5d8bf4;
}

.el-sub-menu__title:hover {
  background: rgba(93, 139, 244, 0.1) !important;
}

.el-sub-menu__title {
  display: flex;
  align-items: center;
}

.el-menu-item {
  display: flex;
  align-items: center;
}

.unread-count {
  background: #ff6b6b;
  color: white;
  border-radius: 10px;
  padding: 0 6px;
  font-size: 12px;
  margin-left: 8px;
  height: 18px;
  line-height: 18px;
}

.unread-badge {
  background: #ff6b6b;
  color: white;
  border-radius: 10px;
  padding: 0 6px;
  font-size: 12px;
  margin-left: auto;
  height: 18px;
  line-height: 18px;
}

.emergency-btn {
  color: #ff6b6b;
  font-weight: bold;
  text-shadow: 0 0 5px rgba(255, 107, 107, 0.7);
}

.system-status {
  padding: 15px;
  border-top: 1px solid rgba(93, 139, 244, 0.2);
  background: rgba(13, 18, 48, 0.6);
}

.status-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  font-size: 13px;
  color: #a0a8c3;
}

.status-item:last-child {
  margin-bottom: 0;
}

.status-item strong {
  color: #e0e7ff;
  margin-left: 5px;
}

.status-indicator {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #4caf50;
  margin-right: 10px;
  position: relative;
}

.status-indicator::after {
  content: "";
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  border-radius: 50%;
  border: 1px solid #4caf50;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { opacity: 0.8; transform: scale(1); }
  50% { opacity: 0; transform: scale(1.5); }
  100% { opacity: 0; transform: scale(1.5); }
}

.status-indicator.active::after {
  border-color: #4caf50;
}
</style>