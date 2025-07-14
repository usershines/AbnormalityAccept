<template>
  <el-aside width="240px" class="sidebar-container">
    <div class="system-header">
      <div class="system-logo">
        <div class="logo-icon">
          <div class="logo-inner"></div>
        </div>
        <h1>异想体管理系统</h1>
      </div>
      <div class="user-info">
        <el-avatar :size="40" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
        <div class="user-details">
          <span class="user-name">O5-13</span>
          <span class="user-level">权限等级: O5</span>
        </div>
      </div>
    </div>

    <el-menu
      default-active="index"
      class="el-menu-vertical-demo"
      @open="handleOpen"
      @close="handleClose"
      router
      background-color="#0c1427"
      text-color="#a0a8c3"
      active-text-color="#5d8bf4"
    >
      <el-menu-item index="index">
        <el-icon><HomeFilled /></el-icon>
        <span>主页</span>
      </el-menu-item>

      <!-- 用户管理 -->
      <el-sub-menu index="1">
        <template #title>
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </template>
        <el-menu-item index="user-list">
          <el-icon><List /></el-icon>
          <span>用户列表</span>
        </el-menu-item>
        <el-menu-item index="create-user">
          <el-icon><CirclePlus /></el-icon>
          <span>新建用户</span>
        </el-menu-item>
        <el-menu-item index="user-permissions">
          <el-icon><Lock /></el-icon>
          <span>权限管理</span>
        </el-menu-item>
      </el-sub-menu>

      <!-- 异想体管理 -->
      <el-sub-menu index="2">
        <template #title>
          <el-icon><MagicStick /></el-icon>
          <span>异想体管理</span>
        </template>
        <el-menu-item index="abnormality-list">
          <el-icon><Search /></el-icon>
          <span>异想体查询</span>
        </el-menu-item>
        <el-menu-item index="create-abnormality">
          <el-icon><Plus /></el-icon>
          <span>新建异想体</span>
        </el-menu-item>
        <el-menu-item index="modify-abnormality">
          <el-icon><EditPen /></el-icon>
          <span>修改收容措施</span>
        </el-menu-item>
        <el-menu-item index="neutralize-abnormality">
          <el-icon><Warning /></el-icon>
          <span>异想体无效化</span>
        </el-menu-item>
      </el-sub-menu>

      <!-- 机动小队管理 -->
      <el-sub-menu index="3">
        <template #title>
          <el-icon><Position /></el-icon>
          <span>机动小队</span>
        </template>
        <el-menu-item index="team-list">
          <el-icon><List /></el-icon>
          <span>小队列表</span>
        </el-menu-item>
        <el-menu-item index="create-team">
          <el-icon><CirclePlus /></el-icon>
          <span>新建小队</span>
        </el-menu-item>
        <el-menu-item index="assign-mission">
          <el-icon><Aim /></el-icon>
          <span>任务指派</span>
        </el-menu-item>
        <el-menu-item index="team-status">
          <el-icon><DataLine /></el-icon>
          <span>状态监控</span>
        </el-menu-item>
      </el-sub-menu>

      <!-- 任务管理 -->
      <el-sub-menu index="4">
        <template #title>
          <el-icon><Tickets /></el-icon>
          <span>任务管理</span>
        </template>
        <el-menu-item index="mission-list">
          <el-icon><List /></el-icon>
          <span>任务列表</span>
        </el-menu-item>
        <el-menu-item index="create-mission">
          <el-icon><CirclePlus /></el-icon>
          <span>新建任务</span>
        </el-menu-item>
        <el-menu-item index="mission-progress">
          <el-icon><TrendCharts /></el-icon>
          <span>任务进度</span>
        </el-menu-item>
      </el-sub-menu>

      <!-- 邮件系统 -->
      <el-sub-menu index="5">
        <template #title>
          <el-icon><Message /></el-icon>
          <span>邮件系统</span>
          <span class="unread-count">3</span>
        </template>
        <el-menu-item index="inbox">
          <el-icon><MessageBox /></el-icon>
          <span>收件箱</span>
          <span class="unread-badge">3</span>
        </el-menu-item>
        <el-menu-item index="compose">
          <el-icon><Edit /></el-icon>
          <span>写邮件</span>
        </el-menu-item>
        <el-menu-item index="sent">
          <el-icon><Promotion /></el-icon>
          <span>已发送</span>
        </el-menu-item>
      </el-sub-menu>

      <!-- 绩效管理 -->
      <el-sub-menu index="6">
        <template #title>
          <el-icon><DataAnalysis /></el-icon>
          <span>绩效管理</span>
        </template>
        <el-menu-item index="performance">
          <el-icon><Histogram /></el-icon>
          <span>绩效统计</span>
        </el-menu-item>
        <el-menu-item index="evaluation">
          <el-icon><DocumentChecked /></el-icon>
          <span>人员评估</span>
        </el-menu-item>
        <el-menu-item index="reports">
          <el-icon><Document /></el-icon>
          <span>绩效报告</span>
        </el-menu-item>
      </el-sub-menu>

      <!-- 其他功能 -->
      <el-menu-item index="profile">
        <el-icon><UserFilled /></el-icon>
        <span>个人主页</span>
      </el-menu-item>

      <el-menu-item index="settings">
        <el-icon><Setting /></el-icon>
        <span>系统设置</span>
      </el-menu-item>

      <el-menu-item index="emergency">
        <el-icon><BellFilled /></el-icon>
        <span class="emergency-btn">紧急按钮</span>
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
  HomeFilled, User, List, CirclePlus, Lock, MagicStick, Search, Plus,
  EditPen, Warning, Position, Aim, DataLine, Tickets, TrendCharts,
  Message, Edit, Promotion, DataAnalysis, Histogram, DocumentChecked,
  Document, UserFilled, Setting, BellFilled, MessageBox
} from '@element-plus/icons-vue'

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
  height: 100vh;
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
  padding: 10px;
  background: rgba(20, 25, 60, 0.4);
  border-radius: 8px;
  border: 1px solid rgba(93, 139, 244, 0.3);
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