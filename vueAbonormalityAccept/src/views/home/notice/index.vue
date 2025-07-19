<script setup lang="ts">
import { ref, onMounted } from 'vue';

// 模拟公告数据
const noticeTitle = ref('重要公告');
const noticeContent = ref(`亲爱的用户们：

我们很高兴地宣布，我们即将推出一项全新的功能，这将极大地提升您在我们平台上的使用体验。以下是这项新功能的主要亮点：

1. **便捷性提升**：新功能将简化您的操作流程，让您能够更快速地完成任务。
2. **个性化体验**：根据您的使用习惯和偏好，为您提供更加个性化的服务。
3. **安全保障**：我们采用了最先进的安全技术，确保您的信息和数据得到充分的保护。

我们预计在 [具体日期] 正式上线这项新功能，届时请您留意相关通知。如果您在使用过程中有任何问题或建议，欢迎随时联系我们的客服团队。

感谢您一直以来对我们的支持与信任！

[平台名称]
[发布日期]`);

// 添加图片数据，这里需要替换为实际的图片链接
const noticeImage = ref('https://via.placeholder.com/800x400');

const isVisible = ref(false);

onMounted(() => {
  setTimeout(() => {
    isVisible.value = true;
  }, 200);
});
</script>

<template>
  <div class="notice-container" :class="{ 'fade-in': isVisible }">
    <div class="notice-header">
      <div class="header-decoration">
        <div class="decoration-circle circle-1"></div>
        <div class="decoration-circle circle-2"></div>
      </div>
      <h1 class="notice-title">{{ noticeTitle }}</h1>
      <div class="title-divider"></div>
    </div>
    
    <div class="notice-image">
      <img :src="noticeImage" alt="公告图片" />
    </div>

    <div class="notice-body">
      <div class="notice-content" v-html="noticeContent"></div>
    </div>

    <div class="notice-footer">
      <div class="footer-decoration">
        <div class="decoration-line"></div>
        <div class="decoration-dot"></div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 引入 Google Fonts */
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;700&family=Roboto:wght@300;400;500;700&display=swap');

body {
  background: linear-gradient(135deg, #e0e0ff 0%, #f0f0ff 100%);
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0;
}

.notice-container {
  max-width: 800px;
  margin: 50px auto;
  padding: 50px 40px;
  background: linear-gradient(135deg, #ffffff 0%, #f9f9ff 100%);
  border: none;
  border-radius: 18px;
  box-shadow:
    0 12px 30px rgba(0, 0, 0, 0.08),
    0 4px 12px rgba(0, 0, 0, 0.05);
  font-family: 'Noto Sans SC', 'Roboto', sans-serif;
  position: relative;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.1);
  opacity: 0;
  transform: translateY(20px);
}

.fade-in {
  opacity: 1;
  transform: translateY(0);
  transition: opacity 0.6s ease-out, transform 0.6s ease-out;
}

.notice-container:hover {
  transform: translateY(-5px);
  box-shadow:
    0 18px 40px rgba(0, 0, 0, 0.12),
    0 6px 20px rgba(0, 0, 0, 0.08);
}

.notice-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 6px;
  background: linear-gradient(90deg, #4e54c8, #8f94fb, #4e54c8);
  background-size: 200% 200%;
  animation: gradientBG 3s ease infinite;
}

@keyframes gradientBG {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.notice-header {
  text-align: center;
  margin-bottom: 40px;
  position: relative;
}

.header-decoration {
  position: absolute;
  top: -20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 12px;
}

.decoration-circle {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.circle-1 {
  background-color: #4e54c8;
}

.circle-2 {
  background-color: #8f94fb;
}

.notice-title {
  font-size: 48px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0;
  letter-spacing: -0.5px;
  background: linear-gradient(135deg, #4e54c8 0%, #8f94fb 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  position: relative;
  display: inline-block;
  padding: 0 15px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.title-divider {
  height: 3px;
  width: 150px;
  background: linear-gradient(90deg, transparent, #8f94fb, transparent);
  margin: 15px auto 0;
  border-radius: 3px;
}

/* 新增的图片上方标题样式 */
.image-notice-title {
  font-size: 32px;
  font-weight: 600;
  color: #4e54c8;
  text-align: center;
  margin-bottom: 20px;
}

/* 添加图片样式 */
.notice-image {
  margin-bottom: 40px;
  text-align: center;
}

.notice-image img {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.notice-body {
  padding: 0 20px;
}

.notice-content {
  font-size: 20px;
  line-height: 1.9;
  color: #555;
  position: relative;
}

.notice-content p {
  margin-bottom: 25px;
}

.notice-content strong {
  color: #4e54c8;
  font-weight: 600;
}

.notice-content::first-letter {
  font-size: 52px;
  font-weight: 700;
  float: left;
  line-height: 1;
  margin-right: 10px;
  margin-top: 8px;
  color: #4e54c8;
  font-family: Georgia, serif;
}

.notice-footer {
  margin-top: 40px;
  text-align: center;
}

.footer-decoration {
  position: relative;
  height: 30px;
}

.decoration-line {
  height: 1px;
  background: linear-gradient(90deg, transparent, #ddd, transparent);
  width: 100%;
  position: absolute;
  top: 50%;
}

.decoration-dot {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: #8f94fb;
  box-shadow: 0 0 0 4px rgba(143, 148, 251, 0.2);
}
</style>