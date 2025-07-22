<template>
  <el-card style="width: 100%;height: 770px">
    <el-container>
      <el-aside width="240px" class="sidebar-container">
        <div class="system-header">
          <div class="system-logo">
            <div class="logo-icon">
              <div class="logo-inner"></div>
            </div>
            <h1>邮件系统</h1>
          </div>
        </div>

        <el-menu
            default-active="inbox"
            class="el-menu-vertical-demo"
            @open="handleOpen"
            @close="handleClose"
            router
            active-text-color="#5d8bf4"
            unique-opened
        >
          <el-menu-item index="/email/drafts">
            <el-icon><Edit /></el-icon>
            <span>写邮件</span>
            <span class="unread-badge">3</span>
          </el-menu-item>

          <el-menu-item index="/email/inbox">
            <el-icon><MessageBox /></el-icon>
            <span>收件箱</span>
            <span v-if="unreadCount > 0" class="unread-count">{{ unreadCount }}</span>
          </el-menu-item>

          <el-menu-item index="/email/sent">
            <el-icon><Promotion /></el-icon>
            <span>已发送</span>
          </el-menu-item>

          <!-- 添加分隔线 -->
          <div class="menu-divider"></div>

          <el-menu-item index="emergency" class="emergency-btn">
            <el-icon><Warning /></el-icon>
            <span>紧急通知</span>
          </el-menu-item>
        </el-menu>

        <div class="system-status">
          <div class="status-item">
            <div class="status-indicator active"></div>
            <span>系统状态: <strong>运行中</strong></span>
          </div>
          <div class="status-item">
            <div class="status-indicator"></div>
            <span>存储空间: <strong>1.2G/5G</strong></span>
          </div>
        </div>
      </el-aside>

      <el-main style="margin-top: -10px">
        <router-view/>
      </el-main>
    </el-container>
  </el-card>
</template>

<script setup lang="ts">
import {
  MessageBox, Promotion, Edit, Delete, Warning
} from '@element-plus/icons-vue'

import { countUnreadEmail } from '@/api/email'
import { ref, onMounted, onBeforeUnmount } from 'vue'

const unreadCount = ref(0) // 未读邮件数量
let timer: number // 定时器

onMounted(() => {
  fetchUnreadCount()
  // 每30秒刷新一次未读数量
  timer = setInterval(fetchUnreadCount, 5000)
})

onBeforeUnmount(() => {
  clearInterval(timer)
})

const fetchUnreadCount = () => {
  countUnreadEmail().then(res => {
    if (res.code === 200) {
      unreadCount.value = res.data
    }
  }).catch(err => {
    console.error('获取未读邮件数量失败', err)
  })
}

const handleOpen = (key: string, keyPath: string[]) => {
  console.log('展开菜单:', key, keyPath)
}
const handleClose = (key: string, keyPath: string[]) => {
  console.log('收起菜单:', key, keyPath)
}
</script>

<style scoped>
.sidebar-container {
  height: 770px;
  margin-top: -20px;
  margin-left: -20px;
  margin-bottom: -25px;
  border-right: 1px solid rgba(93, 139, 244, 0.2);
  display: flex;
  flex-direction: column;
}

.system-header {
  border-bottom: 1px solid rgba(93, 139, 244, 0.2);
  margin-left: 20px;
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
  padding: 10px;
  background: rgba(20, 25, 60, 0.4);
  border-radius: 8px;
  border: 1px solid rgba(93, 139, 244, 0.3);
  transition: all 0.3s ease;
}

.user-info:hover {
  background: rgba(20, 25, 60, 0.6);
  box-shadow: 0 0 10px rgba(93, 139, 244, 0.3);
}

.user-details {
  margin-left: 12px;
  display: flex;
  flex-direction: column;
}

.user-name {
  color: #e0e7ff;
  font-weight: 600;
  font-size: 14px;
}

.user-level {
  color: #5d8bf4;
  font-size: 12px;
  margin-top: 3px;
}

.menu-divider {
  height: 1px;
  background: rgba(93, 139, 244, 0.2);
  margin: 10px 15px;
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
  background: #5d8bf4;
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
  background: #eaeaea;
  margin-top: auto;
  margin-bottom: 0px;
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
  color: darkblue;
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