<template>
  <el-card style="height: 770px;margin-top: -40px">
    <el-container>
      <el-aside>
        <el-card style="height: 500px;margin-top: 40px">
          <template #header >
            <div style="height: 15px">
              <h3 style="text-align: center;margin-top: -10px">欢迎回来</h3>
            </div>
          </template>
          <template #default>
            <div style="display: flex;flex-direction: column;align-items: center">
              <h2>Dr.bright</h2>
              <h4>职务: 研究员</h4>
            </div>
          </template>
        </el-card>
      </el-aside>
      <el-main>
        <div>
          <h3>部分监控画面</h3>
        </div>
        <div class="scroll-container">
          <div class="video-grid">
            <el-card
                v-for="(video, index) in videos"
                :key="index"
                @click="enlargeVideo(video)"
                :class="`level-${video.level}`"
            >
              <template v-slot:header>
                <div class="card-header">
                  <div>
                    <span>监控编号: {{ video.id }}</span>
                    <span class="level-badge" :class="`level-${video.level}-badge`">
                      {{ getLevelText(video.level) }}
                    </span>
                  </div>
                  <span class="status-badge" :class="`status-${video.status}`">
                    {{ video.status }}
                  </span>
                </div>
              </template>
              <div class="video-container">
                <video
                    :src="video.src"
                    muted
                    autoplay
                    loop
                    :width="videoWidth"
                    :height="videoHeight"
                ></video>
              </div>
            </el-card>
          </div>
        </div>
      </el-main>
    </el-container>
    <el-dialog
        v-model="dialogVisible"
        width="80%"
        center
        @close="handleDialogClose"
        style="align-items: center;display: flex;flex-direction: column"
    >
      <template #title>
        <div>
          <span>监控编号: {{ selectedVideo.id }}</span>
          <span class="level-badge ml-2" :class="`level-${selectedVideo.level}-badge`">
            {{ getLevelText(selectedVideo.level) }}
          </span>
          <span class="status-badge ml-2" :class="`status-${selectedVideo.status}`">
            {{ selectedVideo.status }}
          </span>
        </div>
      </template>
      <template #default>
        <video
            :src="selectedVideo.src || ''"
            autoplay
            loop
            muted
            :width="dialogVideoWidth"
            :height="dialogVideoHeight"
        ></video>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {findByName} from "@/api/user.ts";
import type {User} from "@/api/user.ts";
import {ElMessage} from "element-plus";

const dialogVisible = ref(false);
const selectedVideo = ref({ id: '', status: '', src: '', level: '' });
const videos = ref([
  { id: 1, status: '正常', src: '/src/assets/video/999.mp4', level: 'high' },
  { id: 2, status: '警告', src: '/src/assets/video/999.mp4', level: 'medium' },
  { id: 3, status: '正常', src: 'path/to/video3.mp4', level: 'low' },
  { id: 4, status: '异常', src: 'path/to/video4.mp4', level: 'high' },
  { id: 5, status: '正常', src: 'path/to/video5.mp5', level: 'medium' },
  { id: 6, status: '正常', src: 'path/to/video6.mp6', level: 'low' },
  { id: 1, status: '正常', src: '/src/assets/video/999.mp4', level: 'high' },
  { id: 2, status: '警告', src: '/src/assets/video/999.mp4', level: 'medium' },
  { id: 3, status: '正常', src: 'path/to/video3.mp4', level: 'low' },
  { id: 4, status: '异常', src: 'path/to/video4.mp4', level: 'high' },
  { id: 5, status: '正常', src: 'path/to/video5.mp5', level: 'medium' },
  { id: 6, status: '正常', src: 'path/to/video6.mp6', level: 'low' },
]);

// 视频尺寸配置
const videoWidth = ref(320);
const videoHeight = ref(240);
const dialogVideoWidth = ref(800);
const dialogVideoHeight = ref(600);

const Me = ref<User>()

// 获取重要度文本
const getLevelText = (level: string) => {
  const levelMap = {
    'high': '高',
    'medium': '中',
    'low': '低'
  };
  return levelMap[level] || '未知';
};

function enlargeVideo(video: any) {
  selectedVideo.value = video;
  dialogVisible.value = true;
}

const handleDialogClose = () => {
  dialogVisible.value = false;
};

onMounted(() =>{
  const myName = localStorage.getItem('username');
  if(!myName) {
    ElMessage.error('请先登录！')
  }else {
    Me.value = findByName(myName)
  }
})
</script>

<style scoped>
/* 调整滚动容器样式 */
.scroll-container {
  height: 700px;
  overflow-y: auto;
  padding: 0 4px; /* 增加左右内边距 */
}

/* 自定义滚动条样式 */
.scroll-container::-webkit-scrollbar {
  width: 6px;
}

.scroll-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.scroll-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.scroll-container::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 调整视频网格样式 */
.video-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr)); /* 使用minmax确保网格项不溢出 */
  gap: 1rem;
  justify-items: center;
  padding: 8px; /* 增加内边距 */
}

/* 确保卡片不会超出网格项 */
.video-grid .el-card {
  width: 100%; /* 让卡片宽度适应网格项 */
  max-width: 360px; /* 设置最大宽度 */
  box-sizing: border-box; /* 确保内边距和边框包含在宽度内 */
}

.video-container {
  display: flex;
  justify-content: center;
  padding: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.level-badge, .status-badge {
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
  margin-left: 8px;
}

/* 重要度样式 */
.level-high-badge {
  background-color: #f56c6c;
  color: white;
}

.level-medium-badge {
  background-color: #e6a23c;
  color: white;
}

.level-low-badge {
  background-color: #909399;
  color: white;
}

/* 状态样式 */
.status-正常 {
  background-color: #67c23a;
  color: white;
}

.status-警告 {
  background-color: #e6a23c;
  color: white;
}

.status-异常 {
  background-color: #f56c6c;
  color: white;
}

/* 卡片边框样式 */
.level-high {
  border: 1px solid #f56c6c;
}

.level-medium {
  border: 1px solid #e6a23c;
}

.level-low {
  border: 1px solid #909399;
}

/* 响应式调整 */
@media (max-width: 992px) {
  .video-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 576px) {
  .video-grid {
    grid-template-columns: 1fr;
  }
}
</style>